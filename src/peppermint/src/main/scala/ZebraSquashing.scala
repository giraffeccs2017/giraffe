
import peppermint.Analysis.Cleaner
import peppermint.CMTAnalysis.CMTStatistics
import peppermint.CircuitCompilation._
import peppermint.ILAST._
import peppermint.Parser.CParserPlus
import peppermint.RWSetCompute.{RWSetCompute, RWSetRecord}
import peppermint.SSATransform.{MarkDynamicArray, SSATransform}

case class DepthMarkerResult(
                              identToDepth : Map[VIdentSSA, Int],
                              depthToIdents : Array[Set[VIdentSSA]],
                              depthToAssn : Vector[Statement],
                              maxDepth : Int
                            )

object DepthMarker {
  def markMinDepth(assn : Vector[SAssign], inputSet : Set[VIdentSSA]) : DepthMarkerResult = {
    var identToDepth = inputSet.foldLeft(Map[VIdentSSA,Int]())((acc,i) => acc.updated(i,0))

    def depthOfLVal(lval : LValue) : Int = lval match {
      case (x:VIdentSSA) => identToDepth(x)
    }
    def depthOfExpr(expr : Expression) : Int = expr match {
      case ELValue(x) => depthOfLVal(x)
      case EConstant(_) => 0
      case EBinOp(_,x,y) => math.max(depthOfExpr(x), depthOfExpr(y))
//      case EUniOp(_,x) => depthOfExpr(x)
    }
    var maxDepth = 0
    assn.foreach({
      case SAssign(x:VIdentSSA,y) => {
        val d = depthOfExpr(y) + 1
        maxDepth = math.max(d, maxDepth)
        identToDepth = identToDepth.updated(x, d)
      }
    })

    val depthToIdents : Array[Set[VIdentSSA]] = Array.fill(maxDepth+1)(Set[VIdentSSA]())

    identToDepth.foreach(x => {
      depthToIdents.update(x._2, depthToIdents(x._2) + x._1)
    })


    val depthToAssn : Array[Vector[SAssign]] = Array.fill(maxDepth + 1)(Vector())

    assn.foreach(x => {
      val d = depthOfLVal(x.lval)
      depthToAssn.update(d, depthToAssn(d) :+ x)
    })

    val depthToAssnVec = depthToAssn.map(SGrouped(_)).to[Vector]

    DepthMarkerResult(identToDepth, depthToIdents, depthToAssnVec, maxDepth)
  }
}

object SplitByLayer {
  def splitByLayer(depthToAssn : Vector[Statement],
                   depth : Int,
                   rwRecord : RWSetRecord) : (SOut, Int=>Set[VIdentSSA], Int=>Set[VIdentSSA]) = {
    // rwRecord is computed on depthToAssn
    println("trying depth " + depth)
    var outVec = Vector[Statement]()
    var isets : Map[Int,Set[VIdentSSA]] = Map()
    var osets : Map[Int,Set[VIdentSSA]] = Map()
    var i = 1
    while (i <= depthToAssn.size) {
      val slice = SGrouped(depthToAssn.slice(i, i + depth - 1))
      outVec = outVec :+ slice
      isets = isets.updated(slice.identity, rwRecord.findReadWriteSet(i, i + depth - 1)._1.asInstanceOf[Set[VIdentSSA]])
      osets = isets.updated(slice.identity, rwRecord.findOutputSet(i, i + depth - 1).asInstanceOf[Set[VIdentSSA]])
      i = i + depth
    }
    var sout = SOut(outVec)
    isets = isets.updated(sout.identity,
      rwRecord.findReadWriteSet(0, depthToAssn.size - 1)._1.asInstanceOf[Set[VIdentSSA]])
    osets = isets.updated(sout.identity,
      rwRecord.findOutputSet(0, depthToAssn.size - 1).asInstanceOf[Set[VIdentSSA]])
    (sout, isets, osets)
  }
}

object SquashByLayer {

  val minDepth = 10


  def squashByLayer(depthToAssn : Vector[Statement],
                    rwRecord : RWSetRecord,
                    objectiveFunction : SZebraOutsource => Double
                   ) : Option[(SZebraOutsource, Double)] = {
    val maxDepth = depthToAssn.size + 1
    val depthsToTry : Vector[Int] =
      Iterator.range(2, maxDepth).map(x => 1 + ((maxDepth-1)/x)).takeWhile(d => d >= minDepth).toSet.toVector
    val tmp = SplitByLayer.splitByLayer(depthToAssn, maxDepth, rwRecord)
    var bestCircuit : SZebraOutsource = Compile.compileSquash(tmp._1, tmp._2, tmp._3)
    var maxScore : Double = objectiveFunction(bestCircuit)
    depthsToTry.foreach(d => {
      val (out, isets, osets) = SplitByLayer.splitByLayer(depthToAssn, d, rwRecord)
      val zout = Compile.compileSquash(out, isets, osets)
      val score = objectiveFunction(zout)
      if (score > maxScore) {
        maxScore = score
        bestCircuit = zout
      }
    })
    if (maxScore > 0) {
      Some((bestCircuit, maxScore))
    } else {
      None
    }
  }
}

object ZebraSquashing {
  // assuming stmt is SSAed
  def compile(stmt : Vector[Statement],
              inputSet : Set[VIdentSSA],
              outputSet : Set[VIdentSSA],
              objectiveFunction : SZebraOutsource => Double
             ) : Option[(SZebraOutsource, Double)] = {
    val sz = Compile.compileSimple(stmt, inputSet, outputSet)
    val circ = CircuitCompilationPar.split(sz.pwis)._1
    val mdr = DepthMarker.markMinDepth(circ, sz.inputSet.toSet)
    val assns = mdr.depthToAssn
    println(assns.size)
    val record = RWSetCompute.computeRWSet(assns, {case x:VIdentSSA => outputSet.contains(x)})
    SquashByLayer.squashByLayer(assns, record, objectiveFunction)
  }
}

class ZebraSquashing(chunk : Vector[Statement],
                     externOutput : LValue => Boolean,
                     objectiveFunction : SZebraOutsource => Double
                    ) {
  // return a new vec with SZebraoutsouce
  // need to do SSA first
  val ssaTrans = new SSATransform(chunk)
  val ssa = ssaTrans.run()
  val endEnv = ssaTrans.endEnv
  val isOutput : LValue => Boolean = {
    case y@VIdentSSA(x,_) => externOutput(x) && endEnv(x) == y
  }
  val rWSetRecord = RWSetCompute.computeRWSet(ssa, isOutput)
  val rset = rWSetRecord.findReadWriteSet(0, ssa.length-1)._1
  val wset = rWSetRecord.findOutputSet(0, ssa.length-1)
  val ret = ZebraSquashing.compile(
    ssa,
    rset.asInstanceOf[Set[VIdentSSA]],
    wset.asInstanceOf[Set[VIdentSSA]], objectiveFunction)
}

class ZebraSquashingWithIf(chunk : Vector[Statement], externOutput : LValue => Boolean = RWSetCompute.isOutput,
  objectiveFunction : SZebraOutsource => Double) {
  // uses ZebraSquashing to compute over one "continuous" chunk, and sweep up any remaining if-statements
  // input is un-SSAed chunk
  val rWSetRecord = RWSetCompute.computeRWSet(chunk, externOutput)

  // do pairwise things
  val sforLocs : Vector[Int] = chunk.zipWithIndex.foldLeft(Vector[Int]())((acc, stmtIdx) => stmtIdx._1 match {
    case _:SForGrouped => acc :+ stmtIdx._2
    case _ => acc
  })

  var outs : Vector[(Int,Int,SZebraOutsource,Double)] = Vector()

  def sliceOnInds(resultCands : Vector[(Int,Int,SZebraOutsource,Double)]) : Vector[Statement] = {
    var rr : Vector[Statement] = Vector()
    var current = 0
    for (i <- resultCands) {
      if (current < i._1) {
        rr = rr ++ chunk.slice(current, i._1)
      }
      rr = rr ++ chunk.slice(i._1,i._2+1)
      current = i._2+1
    }
    if (current < chunk.size) {
      rr = rr ++ chunk.slice(current, chunk.size)
    }
    rr
  }

  val ret : Vector[Statement] = {
    for (
      i <- sforLocs.indices;
      j <- i until sforLocs.length
    ) {
      val ii = sforLocs(i)
      val jj = sforLocs(j)
      val cc = chunk.slice(ii, jj + 1)
      val output = rWSetRecord.findOutputSet(ii, jj)
      val ozout = new ZebraSquashing(cc, output, objectiveFunction).ret
      ozout match {
        case Some((zo, s)) => outs = outs :+(ii, jj, zo, s)
        case _ =>
      }
    }
    val cands = outs.sortBy(x => -x._4)

    def cleanCands(rr : Vector[(Int,Int,SZebraOutsource,Double)],
                   acc : Vector[(Int,Int,SZebraOutsource,Double)]) : Vector[(Int,Int,SZebraOutsource,Double)] = {
      if (rr.isEmpty) acc else {
        val t = rr.head
        val nextRR = rr.filter(x =>  x._2 < t._1 || x._1 > t._2)
        cleanCands(nextRR, acc :+ t)
      }
    }
    val resultCands = cleanCands(cands, Vector()).sortBy(x => x._1)
    resultCands.foreach(x => println(x._1,x._2))
    val sliced = sliceOnInds(resultCands)
    sliced.map({
      case y:SIf => {
        val output = rWSetRecord.findOutputSetOfIdentity(y.identity)
        val ts = y.thenS
        val es = y.elseS
        SIf(y.bool,
          new ZebraSquashingWithIf(ts, output, objectiveFunction).ret,
          new ZebraSquashingWithIf(es, output, objectiveFunction).ret)
      }
      case x => x
    })
  }
}


object ZebraCost {
  def vmulcount(d : Int, inputS : Int, outputS : Int, w : Vector[Int])  : Int = {
    ZebraVCost.costWithWidths(d, w, inputS, outputS, 1, 3)._1
  }

  def costFunction(out : SZebraOutsource) : Double = {
    val assn = CircuitCompilationPar.split(out.pwis)._1
    val iset = out.inputSet
    val oset = out.outputSet
    val ppp = new PWSGen(assn.map(PWIAssn), iset.toSet, oset.toSet).run()._1.map({
      case PWIAssn(a) => a
    })
    val stats = CircuitAnalysis.runWithAssns(ppp)
    val outsourceCost = vmulcount(stats.depth, iset.size, oset.size, stats.widths)
    val localCost = stats.mulCount
    val ret = localCost - outsourceCost
    println(stats.depth, ret)
    ret
  }
}


object ZebraSquashingRun {
  def preprocessing(prog : Program) : Vector[Statement] = {
    // const prop, mark dynamic array, dead code elim
    val flattened = new FlattenArrayAccesses(prog)
//    print(CPrinter.printStatements(flattened.newMainBody))
    val defs : Map[String,BigInt] = prog.constantDefs.map(x => (x.name,x.value)).toMap[String,BigInt]
    val constprop = Timer.time(ConstProp.rewriteStmts(flattened.newMainBody, defs)._1, "Const prop")
    //val ssa = new SSATransform(constprop).run()
    val dynArrAnalyzed = Timer.time(new MarkDynamicArray(constprop).result, "Mark Dynamic array")
    val comp = Timer.time(new DeadCodeElim(dynArrAnalyzed,RWSetCompute.isOutput).result, "Dead code elim")
    comp
  }
  def main(args : Array[String]) : Unit = {
    val progName = if (args.isEmpty) "apps/hello_world.c" else args(0)
    val prog = new Cleaner(CParserPlus.parseFile(progName)).cleanFunctions
    val comp = preprocessing(prog)

    new ZebraSquashingWithIf(comp, RWSetCompute.isOutput, ZebraCost.costFunction).ret
  }

}
