import peppermint.CMTAnalysis.CMTStatistics
import peppermint.CircuitCompilation._
import peppermint.ILAST._
import peppermint.Parser.CParserPlus
import peppermint.RWSetCompute.{RWSetCompute, RWSetComputeMutable}

import scala.math._
import peppermint.Analysis.{CPrinter, Cleaner, Plumbing, Utility}
import peppermint.SSATransform._

object Timer {
  def time[R](block: => R, comment : String = ""): R = {
    val t0 = System.currentTimeMillis()
    println("Enter block, ", comment)
    val result = block // call-by-name
    val t1 = System.currentTimeMillis()
    println("Elapsed time: " + (t1 - t0) / 1000 + "s, ", comment)
    result
  }
}

object Config {
  var reportFailure : Boolean = false
}


object Divider {
//  def isOutput(lval: VIdentSSA): Boolean = lval match {
//    //    case VIdent(n, _) => n == "output"
//    case VIdentSSA(VIdent(n, _), _) => n == "output"
//  }

  def constProp(prog:Program):Vector[Statement] = {
    val flattened = new FlattenArrayAccesses(prog)
    //    print(CPrinter.printStatements(flattened.newMainBody))
    val defs : Map[String,BigInt] = prog.constantDefs.map(x => (x.name,x.value)).toMap[String,BigInt]
    Timer.time(ConstProp.rewriteStmts(flattened.newMainBody, defs)._1, "Const prop")
  }

  def dynArrAnalyze(comp:Vector[Statement]):Vector[Statement] = {
    Timer.time(new MarkDynamicArray(comp, PythonStitchingV2.pureNameWithoutArray).result, "Mark Dynamic array")
  }

  def deadcodeElim(comp:Vector[Statement]):Vector[Statement] = {
    Timer.time(new DeadCodeElim(comp,RWSetCompute.isOutput).result, "Dead code elim")
  }

  def preprocessing(prog:Program) :  Vector[Statement] = deadcodeElim(dynArrAnalyze(constProp(prog)))

  def outputPythonStitching(dived:Vector[Statement], flattened: FlattenArrayAccesses) = {
    val py : String = new PythonStitchingV2(dived, flattened).ret.mkString("\n")
    Plumbing.writeStringToFile(py, "result.py")
  }

  def run(prog : Program) : Vector[Statement] = {

    val flattened = new FlattenArrayAccesses(prog)
    val dived = new DividerWithIfs(preprocessing(prog)).ret

//    outputPythonStitching(dived, flattened)

    dived
  }

  def main(args : Array[String]) = {
    //runOne(CParserPlus.parseFile(args(0)))
    val progName = if (args.isEmpty) "apps/fft.c" else args(0)

    if (args.length > 1) {
      args(1) match {
        case "GSquash" => Config.reportFailure = true
      }
    }

    val ret = run(new Cleaner(CParserPlus.parseFilePreprocess(progName)).cleanFunctions)

    if (args.length > 1) {
      args(1) match {
        case "GSquash" => GiraffeSquashingBenchmark.benchmark(ret, args(2), args.drop(3).toVector)
      }
    }

  }
}

class DividerWithIfs(chunk : Vector[Statement], externOut : LValue => Boolean = RWSetCompute.isOutput) {
  // assume input is DYNArray Marked
//  val (rc,wc) = Timer.time( new RWSetCompute(chunk).run(), "Compute RWSet 1")
//  val recordc = Timer.time(RWSetCompute.computeRWSetRecordWithRW(chunk,rc,wc,externOut), "Compute RWSetRecordWithRW")
//  val outputWhole = Timer.time(recordc.findOutputSet(0,chunk.length-1), "Find output set")
  val comp = chunk //Timer.time(new DeadCodeElim(chunk,RWSetCompute.isOutput).result, "Dead code elim")


  val record = {
    val (r,w) = Timer.time( new RWSetComputeMutable(comp).run(), "compute RWSet after dead code elim")
//    Timer.time(RWSetCompute.computeRWSetRecordWithRW(comp,r,w,externOut), "compute RWSetRecordWithRW after elim")
    RWSetCompute.findOutputSet(comp, externOut, r, w)
  }

  val ret : Vector[Statement] = comp.map({ stmt =>
    stmt match {
      case SForGrouped(v) => {
        //        val out = record.findOutputSetOfIdentity(stmt.identity)
        val out = record(stmt.identity)
        val ssaTrans = new SSATransform(v)
        val ssa = Timer.time(ssaTrans.run(), "SSA transform")
        val endEnv = ssaTrans.endEnv
        val isOutput : LValue => Boolean = {
          case y@VIdentSSA(x,_) => out.contains(x) && endEnv(x) == y
        }
        println("Divider with if loop count ", ssa.length)
        val xx = Timer.time(OneLoopDivider.divide(ssa, isOutput), "One loop divider")
        //        val circ = Timer.time(new StraighLineDivider(ssa, isOutput).circuits, "straight line divider")
        println("SFG original:", v.size)
        println("SFG new:", xx.size)
        SGrouped(xx)
        //        SGrouped(circ)
      }
      case SIf(b,ts,es) => {
        val out = record(stmt.identity)
        val tts = new DividerWithIfs(ts, out).ret
        val ees = new DividerWithIfs(es, out).ret
        SIf(b,tts,ees)
      }
      case _ => stmt
    }
  })
}


object OneLoopDivider {

  def divideI[A](i : Int, vs : Vector[A], acc : Vector[Vector[A]]) : Vector[Vector[A]] = {
    if (vs.isEmpty)
      acc
    else {
      val (h,t) = vs.splitAt(i)
      divideI(i, t, acc :+ h)
    }
  }

  def divideIntoGroup(i : Int, v : Vector[Statement]) : Vector[Statement] = {
    divideI(i, v, Vector()).map(SGrouped(_))
  }

  def depthsToTry(d : Int) : Vector[Int] = {
    // assuming d is powers of 2, basically returns all proper divisors of d
    val logd = (math.log(d) / math.log(2)).toInt
    Vector.range(0,logd).map(1 << _)
  }

  def divide(stmts : Vector[Statement], outputSet : LValue => Boolean) : Vector[Statement] = {
    // assuming loopdepth is powers of 2, as restricted by the current backend
    val loopdepth = stmts.length
    var bestD = 1
    var bestSavings = -1.0
    var bestSplit : Option[Int] = None
    var bestRet : (Option[Int],(Double,Double),String) = (None,(0,0),"")
    val depthChoices = depthsToTry(loopdepth)

    /*
    case class DivResult(savings:Double,d:Int,ret:(Option[Int],(Double,Double),String))

    var cache : Map[Int,DivResult] = Map()

    def bsearch(b : Int, e : Int) : DivResult = {
      def compute(idx: Int): DivResult = {
        val x = depthChoices(idx)
        cache.getOrElse(x, {
          println("trying depth", x)
          val ss = divideIntoGroup(x, stmts)
          val ret@(split, cost, _) = OneBodyCostAnalysis.costOfLast(ss, outputSet)
          val savings = cost._2 - cost._1
          val dret = DivResult(savings, x, ret)
          cache = cache.updated(x, dret)
          dret
        })
      }
      if (b == e) {
        compute(b)
      } else if (b + 1 == e) {
        val br = compute(b)
        val er = compute(e)
        if (br.savings > er.savings) br else er
      } else {
        val mid = (b + e) / 2
        val mm1R = compute(mid - 1)
        val midR = compute(mid)
        val mp1 = compute(mid + 1)
        if (midR.savings >= mp1.savings && midR.savings >= mm1R.savings) {
          midR
        } else if (mm1R.savings <= midR.savings && midR.savings <= mp1.savings) {
          bsearch(mid+1,e)
        } else if (mm1R.savings >= midR.savings && midR.savings >= mp1.savings) {
          bsearch(b,mid-1)
        } else {
          // indicate that this should not happen...
          ???
        }
      }
    }

    // assuming the savings is an increasing function first, then decreasing function according to depth
    val dr = bsearch(0, depthChoices.length - 1)
    */


    depthChoices.foreach(
      x => {
        println("trying depth", x)
        val ss = divideIntoGroup(x, stmts)
        val ret@(split,cost,_) = OneBodyCostAnalysis.costOfLast(ss, outputSet)
        val savings = cost._2 - cost._1
        if (savings > bestSavings) {
          bestSavings = savings
          bestSplit = split
          bestD = x
          bestRet = ret
        }
      }
    )
    Timer.time(divideHelper(divideIntoGroup(bestD,stmts),outputSet,bestRet), "Divider helper")
  }

  type MIntSSA = Map[Int,Set[VIdentSSA]]
  def divideHelper(stmts : Vector[Statement], outputSet : LValue => Boolean, costAnalysisRet:(Option[Int],(Double,Double),String)) : Vector[Statement] = {
    // assuming that stmts are bodies of the same loop
    val (ii, (oCost,locCost), fileName) = costAnalysisRet
    println(costAnalysisRet)
    ii.map({
      val (rsm,wsm) = Timer.time(new RWSetComputeMutable(stmts).run(), "OLDiv computeRWSet")
      val osm = Timer.time(RWSetCompute.findOutputSet(stmts, outputSet, rsm, wsm)).asInstanceOf[MIntSSA]
      val os = osm.asInstanceOf[MIntSSA](stmts.last.identity)
      divideI(_, stmts, Vector()).map(x => {
        Timer.time({
          val c = Compile.compileSquash(SOut(x), rsm.asInstanceOf[MIntSSA], osm.asInstanceOf[MIntSSA])
          c.pwsFileName = fileName
          c.costForLocalExec = locCost
          c.costForOutsource = oCost
          c
        },
          "Circuite Compile Squash")
      })
    }
    ).getOrElse(stmts)
  }
}


object OneBodyCostAnalysis {
  val upperLimitThatWeCanHandle : Int = 2000000000
  val mulAddRatio : Int = 70

  def nearest2sPower(size : Int) : Int = {
    var t = 1
    var r = 1
    for (i <- 1 to 64 if t * size <= upperLimitThatWeCanHandle) {
      r = t
      t = t * 2
    }
    r
  }

  type MIntSSA = Map[Int,Set[VIdentSSA]]
  // Second argument is (outsource cost, local exec cost)
  def costOfLast(stmt:Vector[Statement], outputSet : LValue => Boolean) : (Option[Int], (Double,Double), String) = {
    val loopcount = stmt.size
    println("Loop count:", stmt.size)
    val ss = Vector(SGrouped(stmt.init), stmt.last)
    val (rsm,wsm) = Timer.time(new RWSetComputeMutable(ss).run(), "OLDiv computeRWSet")
    val osm = Timer.time(RWSetCompute.findOutputSet(ss, outputSet, rsm, wsm)).asInstanceOf[MIntSSA]
    val os = osm.asInstanceOf[MIntSSA](ss.last.identity)
    val (pwis, rr, ww) =
       new CircuitCompilation(Vector(ss.last),rsm.asInstanceOf[MIntSSA](ss.last.identity),os,Set()).run()
    val www = ww.toSet.union(os)
    val osize = www.size
    val isize = rr.size
    val (tmp,_) = new PWSGen(pwis, rr.toSet, www).run()
    val tmp_assn = CircuitCompilationPar.split(tmp)._1.map(PWIAssn(_))
    val strVec :Vector[String]= PWSPrinter.printStmts(tmp_assn)
    val str :String = strVec.mkString("\n")
    val fileName = "%d.pws".format(math.abs(str.hashCode()))
    Plumbing.writeStringToFile(str, fileName)
    val assn = CircuitCompilationPar.split(tmp)._1
    val (split, c) = cost(assn, isize, osize, loopcount,fileName)
    (split, c, fileName)
  }

  // Second argument is (outsource cost, local exec cost)
  def cost(assn : Vector[SAssign], iSize : Int, oSize : Int, loopcount : Int, fileName:String = "") : (Option[Int], (Double,Double)) = {
    // assuming loopcount is also 2^x
    val stats = CircuitAnalysis.runWithPWSInfo(assn)
    val width = stats.widths.max
    val size =  stats.widths.sum
    val copies = min(loopcount, nearest2sPower(size))
    val originalSize = copies * size
    val originalMulCount = stats.mulCount * copies
    val originalAddCount = stats.addCount + stats.subCount * 2

    def oneOrZero(x:Int) : Int = if (x==0) 1 else 1

    val typeCount = oneOrZero(stats.mulCount)+oneOrZero(stats.addCount)+oneOrZero(stats.subCount)

    val costForOutSourcing = GiraffeVCost.costWithWidths2(stats.depth, stats.widths, iSize, oSize, copies, typeCount)
    val costForOutsourceInAdds = costForOutSourcing._1 * mulAddRatio + costForOutSourcing._2
    val costForLocalInAdds = originalMulCount * mulAddRatio + originalAddCount
    val costSavings = costForLocalInAdds - costForOutsourceInAdds
    println("I/O size", iSize, oSize, "depth",
      stats.depth, "max width", width, "mulCount", originalMulCount, "addCount", originalAddCount,
      "type of gates", typeCount,
      "# of copies",
      loopcount, costForOutSourcing)
    printf("Saves: %.2f%%\n", costSavings.toDouble / costForLocalInAdds.toDouble * 100)
    val outsourceAndLocalExecCost : (Double,Double) = (costForOutsourceInAdds, costForLocalInAdds)

    if (costSavings > 0 || Config.reportFailure)
      (Some(copies), outsourceAndLocalExecCost)
    else
      (None, outsourceAndLocalExecCost)
  }
}


case class CircuitStatistics(
                              addCount : Int,
                              mulCount : Int,
                              depth : Int,
                              widths : Vector[Int],
                              inputCount:Int,
                              subCount : Int
                            )



object CircuitAnalysis
{


  abstract class Edge
  case class EAdd() extends Edge
  case class EMul() extends Edge
  case class ESub() extends Edge
  case class ELT() extends Edge
  case class ENEQ() extends Edge
  type Graph = Map[String, (Edge, String, String)]

  def lvalToName(lval : LValue) :String = lval match {
    case VIdent(s,_) => s
    case VIdentSSA(VIdent(n,_), k) => n + k
    case VIdentName(n,idx) => n + idx.getOrElse("")
  }

  var counter = 0

  def nextCounter() : Int = {
    counter += 1
    counter
  }

  def cleanExpr(expr : Expression) : Expression = expr match {
    case EUniOp(_, x) => EBinOp(BArithOp(BMinus()), EConstant(0), cleanExpr(x))
    case EBinOp(op, x, y) => EBinOp(op, cleanExpr(x), cleanExpr(y))
    case x => x
  }

  def binopToEdge(op : BinOp) : Edge = op match {
    case BArithOp(BMinus()) => ESub()
    case BArithOp(BPlus()) => EAdd()
    case BArithOp(BMul()) => EMul()
    case BLT() => ELT()
    case BNEqual() => ENEQ()
  }

  def newNode() : String = {
    "X" + nextCounter()
  }

  def exprToNode(expr : Expression) : (String, Graph) = expr match {
    case EConstant(i) => (i.toString, Map())
    case EBinOp(op, x, y) => {
      val (xn, xg) = exprToNode(x)
      val (yn, yg) = exprToNode(y)
      val thisNode = newNode()
      (thisNode, (xg ++ yg).updated(thisNode, (binopToEdge(op), xn, yn)))
    }
    case ELValue(lval) => {
      (lvalToName(lval), Map())
    }
  }

  def exprToEdge(expr : Expression) : ((Edge, String, String), Map[String,(Edge,String,String)]) = expr match {
    case EConstant(i) => ((EAdd(), "0", i.toString()), Map())
    case EBinOp(op, x, y) => {
      val edge = binopToEdge(op)
      val (xn, xg) = exprToNode(x)
      val (yn, yg) = exprToNode(y)
      ((edge,xn,yn), xg++yg)
    }
    case ELValue(lval) => {
      ((EAdd(), "0", lvalToName(lval)), Map())
    }
  }

  def assnToGraph(assn : SAssign) : Graph = {
    val (edge, g) = exprToEdge(assn.rval)
    val n = lvalToName(assn.lval)
    g.updated(n, edge)
  }

  def assnsToGraph(assns : Vector[SAssign]) : Graph = {
    assns.foldLeft(Map():Graph)(
      (g, assn) => {
        val cassn = SAssign(assn.lval, cleanExpr(assn.rval))
        g ++ assnToGraph(cassn)
      }
    )
  }

  def computeLayers(graph : Graph) : Map[String, Int] = {
    var result = Map() : Map[String,Int]
    def dfs(node : String) : Int = {
      if (result.contains(node)) {
        result(node)
      } else {
        if (node.head != 'X' && node.head != 'V' && node.head != 'O') {
          result = result.updated(node, 0)
          0
        } else {
          val (_, l, r) = graph(node)
          val depth = max(dfs(l), dfs(r)) + 1
          result = result.updated(node, depth)
          depth
        }
      }
    }
    graph.keys.foreach(dfs)
    graph.keys.filter(_.head == 'O').foreach(dfs)
    //result.foreach(k => println(k._1, k._2))
    result
  }

  def computeMaxWidthAndLayerCount(graph : Graph, layerMap : Map[String,Int]) : (Vector[Int],Int,Int) = {
    val highestLayer = layerMap.values.max
    val newLayerMap : Map[String,Int] = layerMap.keys.filter(_.head == 'O').foldLeft(layerMap)(
      (m, s) => m.updated(s, highestLayer)
    )
    var reverseLayerArray : Array[Set[String]] = Array.fill(highestLayer + 1)(Set())
    layerMap.foreach(
      elem => {
        val l = elem._2
        reverseLayerArray.update(l, reverseLayerArray(l) + elem._1)
      }
    )
    var neededVars : Array[Set[String]] = Array.fill(highestLayer + 1)(Set())
    layerMap.filter(s => s._1.head == 'O').foreach(
      elem => neededVars.update(elem._2, neededVars(elem._2) + elem._1)
    )
    var maxWidth = reverseLayerArray(0).size
    val vv : Vector[Int] = Vector.range(highestLayer, 0, -1).map(
      x => {
        val nodes = reverseLayerArray(x)
        val width = nodes.size + neededVars.slice(0, x).foldLeft(0)(
          (acc, s) => acc + s.size
        )
        maxWidth = max(width, maxWidth)
        val y = nodes.foldLeft[Vector[(Int,String)]](Vector())(
          (acc, node) => {
            val (_, x, y) = graph(node)
            acc :+ (layerMap(x), x) :+ (layerMap(y), y)
          }
        ).foreach(
          elem => {
            val idx = elem._1
            val s = elem._2
            neededVars.update(idx, neededVars(idx)+s)
          }
        )
        width
      }
    )
    (vv.reverse, highestLayer + 1, maxWidth)
  }


  def pwsToGraph(fileName : String) : Graph = {
    val assns = CParserPlus.parsePWSFile(fileName)
    assnsToGraph(assns)
  }

  def computeStats(graph : Graph) : CircuitStatistics = {
    val (ac, sc, mc, ltc, neqc) = graph.values.map(_._1).foldLeft((0,0,0,0,0))(
      (x, e) => e match {
        case EAdd() => (x._1+1,x._2, x._3, x._4,x._5)
        case ESub() => (x._1,x._2+1,x._3,x._4,x._5)
        case EMul() => (x._1,x._2, x._3+1,x._4,x._5)
        case ELT() => (x._1,x._2,x._3,x._4+1,x._5)
        case ENEQ() => (x._1,x._2,x._3,x._4,x._5+1)
      }
    )

    val layerMap = computeLayers(graph)
    val (vec, d, g) = computeMaxWidthAndLayerCount(graph, layerMap)
    CircuitStatistics(ac, mc, d, vec, vec(0),sc)
  }


  def runWithPWSInfo(assns:Vector[SAssign]) : CircuitStatistics = {
    import sys.process._
    val s = PWSPrinter.printStmts(assns.map(PWIAssn))
    Plumbing.writeStringToFile(s.mkString("\n"),"tmp")
    // TODO: might need to change directory of pws_info.py
    val ret = Seq("../giraffe/pws_info.py", "tmp").lineStream
    val inputC = ret(0).toInt
    val depth = ret(1).toInt
    val widths = ret.slice(2,2+depth).toVector.map(_.toInt)
    val lastLine = ret(2+depth).split("\\s+")
    val mulCount = lastLine(0).toInt
    val addCount = lastLine(1).toInt
    val subCount = lastLine(2).toInt
    CircuitStatistics(addCount,mulCount,depth,widths,inputC,subCount)
  }

  def runWithAssns(assns : Vector[SAssign]) : CircuitStatistics = {
    computeStats(assnsToGraph(assns))
  }
}
