
package peppermint.CMTAnalysis

import peppermint.ILAST._
import scala.math._
import peppermint.Analysis._
import peppermint.Analysis.Utility._
import sys.process._
import peppermint.Parser.CParserPlus

class Accounting {
  var readSet : Set[LValue] = Set()
  var writeSet : Set[LValue] = Set()
  var writeKnownSet : Map[LValue,Value] = Map()
  var tentWriteSet : Set[LValue] = Set()
  //var outputSet : Set[LValue] = Set()
  var binopCount : Int = 0
  var orderCompCount : Int = 0
  var eqCount : Int = 0
  var declared: Map[String, Type] = Map()
  var confirmedOutput : Set[LValue] = Set()
  var numOfLayers : Int = 0
  var circuitWidth : Int = 0

  def writeKnown(lval : LValue, value : Value) = {
    writeKnownSet = writeKnownSet.updated(lval,value)
  }

  def read(lval : LValue) = {
    if (!writeSet(lval)) readSet = readSet + lval
  }

  def write(lval : LValue) = {
    writeSet = writeSet + lval
  }

  def makeWriteTentative() = {
    tentWriteSet = tentWriteSet.union(writeSet).union(writeKnownSet.keySet)
    writeSet = Set()
    writeKnownSet = Map()
  }

  def merge(that : Accounting) : Accounting = {
    var ret = new Accounting
    var thisOutput = this.writeSet.union(this.tentWriteSet)
    if (thisOutput.intersect(that.readSet).isEmpty) {
      // parallelizable
      ret.readSet = this.readSet.union(that.readSet)
      ret.writeSet = this.writeSet.union(that.writeSet)
      ret.writeKnownSet = this.writeKnownSet++(that.writeKnownSet)
      ret.tentWriteSet = this.tentWriteSet.union(that.tentWriteSet).diff(this.writeSet)
      ret.binopCount = this.binopCount + that.binopCount
      ret.orderCompCount = this.orderCompCount + that.orderCompCount
      ret.eqCount = this.eqCount + that.eqCount
      ret.numOfLayers = max(this.numOfLayers, that.numOfLayers)
      ret.circuitWidth = this.circuitWidth + that.circuitWidth
    } else {
      ret.readSet = this.readSet.union(that.readSet.diff(thisOutput))
      ret.writeSet = this.writeSet.union(that.writeSet)
      ret.writeKnownSet = this.writeKnownSet++(that.writeKnownSet)
      //ret.tentWriteSet = this.tentWriteSet.union(that.tentWriteSet)
      ret.tentWriteSet = this.tentWriteSet.union(that.tentWriteSet).diff(this.writeSet)
      ret.binopCount = this.binopCount + that.binopCount
      ret.orderCompCount = this.orderCompCount + that.orderCompCount
      ret.eqCount = this.eqCount + that.eqCount
      ret.numOfLayers = this.numOfLayers + that.numOfLayers
      ret.circuitWidth = max(this.circuitWidth, that.circuitWidth)
    }
    ret
  }
}

abstract class ExpandedStatement(acc : Accounting) {
  def toVector : Vector[Statement]
}
case class OneStmt(stmt : Statement, acc : Accounting) extends ExpandedStatement(acc : Accounting) {
  def toVector = Vector(stmt)
}
case class MultiStmts(stmts : Vector[Statement], acc : Accounting) extends ExpandedStatement(acc : Accounting) {
  def toVector = stmts
}

object CMTMain {
  def main2(args : Array[String]) : Unit = {
    val file = args(0)
    val prog = CParserPlus.parseFile(file)
    val progC = new Cleaner(prog).cleanFunctions
    val res = new CMTAnalylsis(progC,
      ZebraEnergyCost.savings
    ).run2()
    Plumbing.writeStringToFile(CPrinter.print(res), args(1))
  }
}

case class CMTStatistics(
                          addCount : Int
                        , mulCount : Int
                        , ltCount : Int
                        , neqCount : Int
                        , depth : Int
                        , width : Int
                     ) {

  def ratio : Double = {
    mulCount.toDouble / ((depth.toDouble - 1) * width.toDouble)
  }
  override def toString = {
    ("+ count: %d, x count: %d, != count : %d, < count : %d, depth: %d, width: %d, x ratio: %.3f").
      format(addCount, mulCount, neqCount, ltCount, depth, width, ratio)
  }
}

class CircuitAnalysis(program: Program
                      , stmts : Vector[Statement]
                      , readSet : Set[LValue]
                      , writeSet : Set[LValue]
                      , outputSet : Set[LValue])
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
    graph.keys.filter(_.head == 'O').foreach(dfs)
    //result.foreach(k => println(k._1, k._2))
    result
  }

  def computeMaxWidthAndLayerCount(graph : Graph, layerMap : Map[String,Int]) : (Int,Int) = {
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
    Iterator.range(highestLayer, 0, -1).foreach(
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
      }
    )
    (highestLayer + 1, maxWidth)
  }


  def pwsToGraph(fileName : String) : Graph = {
    val assns = CParserPlus.parsePWSFile(fileName)
    assnsToGraph(assns)
  }

  def computeStats(graph : Graph) : CMTStatistics = {
    val (ac, mc, ltc, neqc) = graph.values.map(_._1).foldLeft((0,0,0,0))(
      (x, e) => e match {
        case EAdd() => (x._1+1,x._2, x._3, x._4)
        case ESub() => (x._1+1,x._2,x._3,x._4)
        case EMul() => (x._1,x._2+1, x._3,x._4)
        case ELT() => (x._1,x._2,x._3+1,x._4)
        case ENEQ() => (x._1,x._2,x._3,x._4+1)
      }
    )

    val layerMap = computeLayers(graph)
    val (d, g) = computeMaxWidthAndLayerCount(graph, layerMap)
    CMTStatistics(ac, mc, ltc, neqc, d, g)
  }


  def callZCC(computeName : String) : Graph = {
    val zccProgram : Program = new Rewriter(program, stmts, readSet, writeSet, outputSet, _ => TInt(), true).rewrite
    val str : String = CPrinter.print(zccProgram)
    Plumbing.writeStringToFile(str, "apps/%s.c".format(computeName))
    println("Compiling %s.c....".format(computeName))
    Seq("sh", "-c","make bin/%s.params > /dev/null".format(computeName)).!
    println("Compilation done.")

    val graph = pwsToGraph("bin/%s.pws".format(computeName))
    graph
  }

  def runWithAssns(assns : Vector[SAssign]) : CMTStatistics = {
    computeStats(assnsToGraph(assns))
  }


  def run(computeName : String, comment:String = "") : CMTStatistics = {
//    val graph = callZCC(computeName)
//    println(CPrinter.printStatements(assns))
    val assns = new CMTPWSGen(program, readSet, outputSet, stmts).run()
    val graph = assnsToGraph(assns)
    computeStats(graph)
  }
}

class CMTAnalylsis(program : Program, objectiveFunction : CMTStatistics => Double) {

  def run2() : Program = {
    val mainFunc = findMain(program)
    val bootstrap = Plumbing.pluginExtraVarDeclarationForMain(mainFunc)
    bootstrap.foreach(executeStatement)
    val body = mainFunc.body
    var counter = 0
    def nextCounter : Int = {
      counter += 1
      counter
    }
    val (execed, newBody) = body.map(s => {
      val (ss, _, acc) = executeStatement(s)
      ss.identity = counter
      s.identity = counter
      counter += 1
      ((ss, acc), s)
    }).unzip

    val scores = execed.tails.takeWhile(!_.isEmpty).map(
      v => {
        if (!v.head._1.isInstanceOf[SGrouped]) Vector() else {
        v.to[Iterator].zip(v.tails.drop(1)).
          foldLeft[(Vector[Statement],Accounting,Vector[(Int,Int,Double,CMTStatistics)])](Vector(), new Accounting, Vector())({
          case ((accStmts, accountingSoFar, scoresAcc), ((stmt,stmtAcc), rest)) => {
            val newAccounting = accountingSoFar.merge(stmtAcc)
            newAccounting.confirmedOutput = Set()
            rest.map(_._2.readSet).foreach(
              s => s.foreach(
                elem =>
                  if (newAccounting.writeSet(elem) || newAccounting.tentWriteSet(elem))
                    newAccounting.confirmedOutput += elem
              )
            )
            newAccounting.confirmedOutput = newAccounting.confirmedOutput ++ newAccounting.writeSet.union(newAccounting.tentWriteSet).filter(
              {
                case VIdent(x, p) => x == "output"
              })
            val newAccStmts = accStmts :+ stmt
            val actualInputSet = newAccounting.readSet.union(newAccounting.tentWriteSet)
            if (actualInputSet.isEmpty || newAccounting.confirmedOutput.isEmpty || !stmt.isInstanceOf[SGrouped]) {
              (newAccStmts, newAccounting, scoresAcc)
            } else {
              val computeName = "outsource" + nextCounter
              val stats = new CircuitAnalysis(
                  program,
                  newAccStmts,
                  actualInputSet,
                  newAccounting.writeSet.union(newAccounting.tentWriteSet),
                  newAccounting.confirmedOutput).run(computeName)
              val score = objectiveFunction(stats)
              //if (score > 0.0) {
                // only include chunks that have a high score
                (newAccStmts, newAccounting,
                  scoresAcc :+(newAccStmts.head.identity, newAccStmts.last.identity, score, stats))
//              } else {
//                (newAccStmts, newAccounting, scoresAcc)
//              }
            }
          }
        }
        )._3
      }}
    )
    val sv = scores.to[Vector].flatten
    sv.foreach(println)
    val chunkIds  = Vector.range(0, sv.length).foldLeft(
      (Vector() : Vector[(Int,Int,Double,CMTStatistics)], sv : Vector[(Int,Int,Double,CMTStatistics)])
    )(
      (acc, _) => {
        if (acc._2.isEmpty) {
          acc
        } else {
          val (id1, id2, s, cmt) = acc._2.reduce(
            (a1,a2) => if (a1._3 >= a2._3) a1 else a2
          )
          if (s < 0)
            acc
          else {
            val filtered = acc._2.filter(
              {
                case (b, e, _, _) => id2 < b || id1 > e
              }
            )
            (acc._1 :+(id1, id2, s,cmt), filtered)
          }
        }
      }
    )._1
    val newB = markOutsource(newBody, chunkIds)
    program match {
        case Program(c, f, t) => Program(c,
          f.filter(_.funcName != "compute") :+ FunctionDef("compute", mainFunc.retType, mainFunc.args, newB),
          t
        )
      }
  }

  //  def run() : Program = {
  //    val mainFunc = findMain(program)
  //    val bootstrap = Plumbing.pluginExtraVarDeclarationForMain(mainFunc)
  //    bootstrap.foreach(executeStatement)
  //    var counter = 0
  //    val chunkIDs = bruteForce(mainFunc.body.map(stmt => {
  //      stmt.identity = counter
  //      counter += 1
  //      stmt
  //    }), 1).map(x => (x._1, x._2))
  //    val newBody = markOutsource(mainFunc.body, chunkIDs)
  //    program match {
  //      case Program(c, f, t) => Program(c,
  //        f.filter(_.funcName != "compute") :+ FunctionDef("compute", mainFunc.retType, mainFunc.args, newBody),
  //        t
  //      )
  //    }
  //  }

  type Env = Vector[Map[String, Value]]


  //  def bruteForce(stmts : Vector[Statement], numOfChunks : Int) : Vector[(Int,Int,Double)] = {
  //    // n^2 algorithm to find a good
  //    // assuming stmts are already arranged in increasing order in their ids
  //    val accountings = stmts.map(executeStatement(_)._3).foldLeft(
  //      Vector() : Vector[Accounting]
  //    )(
  //      (acc, accounting) => {
  //        acc.map(acc2 => {
  //          accounting.readSet.foreach(
  //            lvl => if (acc2.writeSet(lvl) || acc2.tentWriteSet(lvl)) acc2.confirmedOutput += lvl
  //          )
  //          acc2
  //        }) :+ accounting
  //      }
  //    ).map(
  //      x => {
  //        x.confirmedOutput = x.confirmedOutput ++ x.writeSet.union(x.tentWriteSet).filter(
  //          {
  //            case VIdent(x, p) => x == "output"
  //          }
  //        )
  //        x
  //      }
  //    )
  //
  //
  //    val scores : Vector[(Int, Int, Double)] = stmts.zip(accountings).foldLeft(
  //      (Vector() : Vector[Vector[(Int, Int, Accounting)]], Vector():Vector[(Int, Int, Accounting)])
  //    )(
  //      (acc, elem) => {
  //        val acc1 = acc._1
  //        val front = acc._2
  //        val accounting = elem._2
  //        val stmt = elem._1
  //        val newFront = (stmt.identity, stmt.identity, accounting) +: front.map(
  //          x => (x._1, stmt.identity, x._3.merge(accounting))
  //        )
  //        (acc1 :+ newFront, newFront)
  //      }
  //    )._1.flatten.map(
  //      x => (x._1,x._2,objectiveFunction(x._3))
  //    )
  //    //    println(scores)
  //    val ret = List.range(0, numOfChunks).foldLeft(
  //      (Vector() : Vector[(Int,Int,Double)], scores : Vector[(Int,Int,Double)])
  //    )(
  //      (acc, _) => {
  //        if (acc._2.isEmpty) {
  //          acc
  //        } else {
  //          val (id1, id2, s) : (Int, Int, Double) = acc._2.reduce(
  //            (a1,a2) => if (a1._3 >= a2._3) a1 else a2
  //          )
  //          val filtered = acc._2.filter(
  //            {
  //              case (b, e, _) => id2 < b || id1 > e
  //            }
  //          )
  //          (acc._1 :+ (id1,id2,s), filtered)
  //        }
  //      }
  //    )
  //    ret._1
  //  }

  var env: Vector[Map[String, Value]] = Vector(
    program.constantDefs.foldRight[
      Map[String, Value]](Map("NULL" -> VConst(0)))(
      (c, m) => m.+((c.name, VConst(c.value)))))

  def evalLValue(lval : LValue) : (LValue, Vector[Statement], Value, Accounting) = lval match {
    case VIdent(x, l) =>
      val (envIdx, v) = findEnvIndex(x, env)
      val (ll, vs, vv, acc) : (Vector[LValPath], Vector[Statement], Value, Accounting) =
        l.foldLeft((Vector():Vector[LValPath], Vector():Vector[Statement], v, new Accounting)) {
        case ((pacc, vsacc, vp:Value, a:Accounting), pe: LValPath) =>
          vp match {
            case VUnknown() => (pacc :+ pe, vsacc, VUnknown(), a)
            case _ => pe match {
              case LVPDeref() => vp match {
                // TODO: dereference might not be to a "simple" name
                case VLValue(VIdent(n,_)) => {
                  val (_, vll) = findEnvIndex(n, env)
                  //               val (vll, all) = evalLValue(ll)
                  (pacc :+ pe, vsacc, vll, a)
                }
              }
              case LVPArrow(c) => vp match {
                // TODO: arrow might not be to a "simple" name
                case VLValue(ll) =>
                  evalLValue(ll) match {
                    case (_, vsll, VStruct(m), ap) => (pacc :+ pe, vsacc ++ vsll, m(c), a)
                  }
              }
              case LVPDot(c) => vp match {
                case VStruct(m) => (pacc :+ pe, vsacc, m(c), a)
              }
              case LVPArray(i) => vp match {
                case VArray(_, arr) => {
                  val (_, vsi, ival, ai) = evalExpression(i)
                  ival match {
                    case VConst(ii) => (pacc :+ LVPArray(EConstant(ii)), vsacc ++ vsi, arr(ii.toInt), a.merge(ai))
                  }
                }
              }

            }
          }
      }
      val newlval = VIdent(x, ll)
      if (vv == VUnknown()) acc.read(newlval)
      acc.circuitWidth = 1
      (newlval, vs, vv, acc)
  }

  def evalExpression(expr : Expression) : (Expression, Vector[Statement], Value, Accounting) = expr match {
    case EConstant(x) => (expr, Vector(), VConst(x), new Accounting)
    case ELValue(lval) => {
      val (ll, vslval, value, acc) = evalLValue(lval)
      value match {
        case VConst(x) => (EConstant(x), vslval, value, acc)
        case _ => (ELValue(ll), vslval, value, acc)
      }
    }
    case EBinOp(op, x, y) => {
      val (ex, vsx, vx, ax) = evalExpression(x)
      val (ey, vsy, vy, ay) = evalExpression(y)
      val aa = ax.merge(ay)
      val vs = vsx ++ vsy
      (vx, vy) match {
        case (VConst(xval), VConst(yval)) => {
          val result = binopToFunction(op)(xval, yval)
          (EConstant(result), vs, VConst(result), new Accounting)
        }
        case _ => {
          op match {
            case BEqual() => aa.eqCount = aa.eqCount + 1
            case BNEqual() => aa.eqCount = aa.eqCount + 1
            case BArithOp(_) => aa.binopCount = aa.binopCount + 1
            case BAnd() => aa.binopCount = aa.binopCount + 1
            case BOr() =>  aa.binopCount = aa.binopCount + 1
            case _ => aa.orderCompCount = aa.orderCompCount + 1
          }
          aa.numOfLayers = aa.numOfLayers + 1
          (EBinOp(op, ex, ey), vs, VUnknown(), aa)
        }
      }
    }
    case EUniOp(UGetRef(), ELValue(v)) => {
      (expr, Vector(), VLValue(v), new Accounting)
    }
    case EUniOp(op, ex) => {
      val (e, vs, v, ax) = evalExpression(ex)
      v match {
        case VUnknown() => (EUniOp(op, e), vs, v, ax)
        case VConst(vp) => op match {
          case ULNegate() => {
            val x = if (vp == 0) 1 else 0
            (EConstant(x), vs, VConst(x), ax)
          }
          case UANegate() => {
            (EConstant(-vp),vs, VConst(-vp), ax)
          }
          case _ => sys.error("Type error " + op + " " + vp)
        }
        case VLValue(l) => op match {
          case UDeref() => {
            val (lval, vslval, vv, aa) = evalLValue(l)
            (ELValue(lval), vs ++ vslval, vv, ax.merge(aa))
          }
        }
        case VArray(_, av) => op match {
          case UDeref() => (EUniOp(op, e), vs, av(0), ax)
        }
        case VStruct(_) => sys.error("type error")
      }
    }
    case ESelect(bin, x, y) => {
      val (eb, vsbin, vv, aa) = evalExpression(bin)
      vv match {
        case VUnknown() => {
          val (xe, vsx, xv, ax) = evalExpression(x)
          val (ye, vsy, yv, ay) = evalExpression(y)
          (ESelect(eb, xe, ye), vsbin ++ vsx ++ vsy, VUnknown(), aa.merge(ax.merge(ay)))
        }
        case VConst(v) => {
          val (ye, vsy, yv, ay) = if (v == 0) evalExpression(y) else evalExpression(x)
          (ye, vsbin ++ vsy, yv, aa.merge(ay))
        }
        case _ => {
          val (xe, vsx, xv, ax) = evalExpression(x)
          (xe, vsbin ++ vsx, xv, aa.merge(ax))
        }
      }
    }
    case EFunctionCall(ident, args) => {
      val func = program.funcDefs.find(_.funcName == ident).get
      val (vs, vv, acc) = executeFunction(func, args.to[Vector])
      ???
    }
  }


  def executeFunction(functionDef: FunctionDef, args : Vector[Expression]) : (Vector[Statement], (Expression,Value), Accounting) = {
    if (functionDef.args.length != args.length) {
      sys.error("wrong number of inputs provided for function " + functionDef.funcName)
    }
    val decs : Vector[Statement] = functionDef.args.zip(args).flatMap(x =>
          Vector(SVarDec(x._1._2, Vector((x._1._1, Some(x._2)))),
            SAssign(VIdent(x._1._1,Vector()),x._2))
       )
    val stmts = decs ++ functionDef.body
    val (vs, mr, acc) = executeStatements(stmts)
    ??? //(vs, mr getOrElse VUnknown(), acc)
  }

  def executeStatements(stmts: Vector[Statement]): (Vector[Statement], Option[(Expression,Value)], Accounting) = {
    env = (Map(): Map[String, Value]) +: env
    val (newStmts,ret, ac) = stmts.foldLeft((Vector():Vector[Statement],None:Option[(Expression,Value)], new Accounting))(
      (acc, stmt) =>
        acc match {
          case (_,Some(_), _) => acc
          case _ => {
            val (newStmt,rr, aa) = executeStatement(stmt)
            val newAA = acc._3.merge(aa)
            (acc._1:+newStmt,rr, newAA)
          }
        })
    // rid of all of the writes that do not survive the scope
    val h:Set[String] = env.head.keySet
    val ff : LValue => Boolean = {
      case VIdent(n,_) => !h.contains(n)
    }
    ac.writeSet = ac.writeSet.filter(ff)
    ac.tentWriteSet = ac.tentWriteSet.filter(ff)
    ac.writeKnownSet = ac.writeKnownSet.filter(x => ff(x._1))
    env = env.tail
    (newStmts,ret, ac)
  }

  def updateLValue(lval: LValue, value: Value, env: Env): (Env, Vector[Statement], Accounting) = lval match {
    case VIdent(x, Vector()) => {
      val (idx, v) = findEnvIndex(x, env)
      val frame = env(idx)
      val acc = new Accounting
      if (value == VUnknown()) {
        acc.write(lval)
        acc.writeKnown(lval,value)
      } else {
        //acc.write(lval)
        acc.writeKnown(lval,value)
      }
      (env.updated(idx, frame.updated(x, value)), Vector(), acc)
    }
    case VIdent(x, l) => {
      val acc = new Accounting
      // assuming this is a "data" update, where it is already in unknown state
      // TODO: fix the above assumption...
      // find out if any of the indices are unknown and mark as RAMPUT
      var vs : Vector[Statement] = Vector()
      val ll = l.map({
        case LVPArray(idx) =>
          val (_, vsidx, v, va) = evalExpression(idx)
          acc.merge(va)
          vs = vs ++ vsidx
          v match {
            case VConst(i) => LVPArray(EConstant(i))
          }
        case lvpath => lvpath
      })
      if (value == VUnknown()) {
        acc.write(VIdent(x,ll))
        acc.writeKnown(VIdent(x,ll),value)
      } else {
      //  acc.write(VIdent(x,ll))
        acc.writeKnown(VIdent(x,ll),value)
      }
      (env, vs, acc)
    }
  }

  def groupIfNonEmpty(vs : Vector[Statement], stmt : Statement) : Statement = {
    if (vs.isEmpty) {
      stmt
    } else {
      SGrouped(vs :+ stmt)
    }
  }

  def executeStatement(stmt : Statement) : (Statement, Option[(Expression,Value)], Accounting) = stmt match {
    case SBlank() => (SBlank(), None, new Accounting)
    case SAssign(lval, rval) => {
      val (er, rvs, rr, acc) = evalExpression(rval)
      val (ee, vsulval, vsacc) = updateLValue(lval, rr, env)
      val ll = evalLValue(lval)._1
      env = ee
      rr match {
        case VUnknown() => {
          val stmtNew = SAssign(ll, er)
          val vsOfExpr = rvs ++ vsulval
          val vs = if (vsOfExpr.isEmpty) {
            stmtNew
          } else {
            SGrouped(vsOfExpr :+ stmtNew)
          }
          stmtNew.comment = "Line %d, %s".format(stmt.lineNumber, CPrinter.printStatement(stmt))
          (vs, None, acc.merge(vsacc))
        }
        case _ => (SBlank(), None, acc.merge(vsacc))
      }
    }
    case SArithAssign(_, _, _) => sys.error("")
    case SExpression(e) => {
      val ret = evalExpression(e)
      val newS = ret._1 match {
        case EConstant(_) => SBlank()
        case _ => SExpression(ret._1)
      }
      (newS, None, ret._4)
    }
    case SIf(b, ts, es) => {
      val (be, bvs, bv, ba) = evalExpression(b)
      bv match {
        case VUnknown() => {
          val eh = env.head
          val (tts, tv, ta) = executeStatements(ts)
          val taWKSet = ta.writeKnownSet
          val taKnAssn = taWKSet.map(
            lval => {
              val x = lval._2
              x match {
                case VConst(i) => SAssign(lval._1, EConstant(i))
                case _ => SBlank()
              }
            }
          ).toVector

          env = eh +: env.tail

          val (ees, ev, ea) = executeStatements(es)
          val ret = (tv,ev) match {
            case (Some(_), Some(_)) => Some(EConstant(0),VUnknown())
            case _ => None
          }
          val eaWKSet = ea.writeKnownSet
          val eaKnAssn = eaWKSet.map(
            lval => {
              val x = lval._2
              x match {
                case VConst(i) => SAssign(lval._1, EConstant(i))
                case _ => SBlank()
              }
            }
          ).toVector
          ta.writeSet.union(ea.writeSet).foreach({
            lval =>
              val (envp, _, _) = updateLValue(lval, VUnknown(), env)
              env = envp
          })
          val resetSet = ea.writeKnownSet.keySet.union(ta.writeKnownSet.keySet)
          resetSet.foreach(x => {
            val (e, _, _) = updateLValue(x, VUnknown(), env)
            env = e
          })
          ta.makeWriteTentative()
          ea.makeWriteTentative()
          (groupIfNonEmpty(bvs,SIf(be, tts ++ taKnAssn, ees ++ eaKnAssn)), ret, ba.merge(ea.merge(ta)))
        }
        case VConst(v) if v == 0 => {
          val (bunch, ret, acc) = executeStatements(es)
          (SGrouped(bvs++bunch), ret, ba.merge(acc))
        }
        case _ => {
          val (bunch, ret, acc) = executeStatements(ts)
          (SGrouped(bvs++bunch), ret, ba.merge(acc))
        }
      }
    }
    case SWhile(cond, body) =>
      val ret = None
      var acc = new Accounting
      var loopc = true
      var newGroup : Vector[Statement] = Vector()
      while(loopc) {
        val (_, vs, cv, _) = evalExpression(cond)
        cv match {
          case VUnknown() => sys.error("bad loop " + cond)
          case VConst(v) if v == 0 => {
            loopc = false
            newGroup = newGroup ++ vs
          }
          case _ => {
            val (newBody, _, ba) = executeStatements(body)
            acc = acc.merge(ba)
            newGroup = (newGroup ++ vs) :+ SGrouped(newBody)
            //            println(acc.numOfLayers)
          }
        }
      }
      (SGrouped(newGroup), ret, acc)
    case SVarDec(t, Vector((n, oexpr))) =>
      val acc = new Accounting
      val (e, v, accp) = oexpr match {
        case None => (None, unknownValueForType(t), new Accounting)
      }
      acc.write(VIdent(n,Vector()))
      val h = env.head.updated(n, v)
      env = env.updated(0, h)
      (SVarDec(t, Vector((n, e))),None, acc.merge(accp))
    case SReturn(Some(expr)) => {
      val (ee, vs, ret, acc) = evalExpression(expr)
      (groupIfNonEmpty(vs,SBlank()), Some(ee,ret), acc)
    }
    case SReturn(None) => (SReturn(None), Some((EConstant(0),VUnknown())), new Accounting)
  }

  def unknownValueForType(ty: Type): Value = ty match {
    case TInt() => VUnknown()
    case TArr(t, s) => VArray(s, Vector.fill(s)(unknownValueForType(t)))
    case TStruct(n) => {
      val nl: Vector[(String, Type)] = program.typeDefs.find(_.name == n).get.members
      val k: Vector[(String, Value)] = nl.map(x => (x._1, unknownValueForType(x._2)))
      val m: Map[String, Value] = k.foldRight[Map[String, Value]](Map())((x, acc) =>
        acc.updated(x._1, x._2)
      )
      VStruct(m)
    }
    case TPtr(_) => VUnknown()
  }
}


class ZebraVEnergy( mulEnergy : Double
                    , addEnergy : Double
                    , txEnergy : Double
                    , stoEnergy : Double
                    , prngEnergy : Double
                    , ioEnergy : Double
                  ) {
  import Utility._
  def energy(d : Int, G : Int) : Double = {
    val logg = log2(G)
    val compute = (7.0 * d * logg + 6 * G) * mulEnergy + (15 * d * logg + 2 * G) * addEnergy
    val others = (2*d * logg + G) * txEnergy + d * logg * stoEnergy + 2 * d * logg * prngEnergy + 2 * G * ioEnergy
    compute + others
  }

  def baseLineOnDG(d : Int, G : Int, ratio : Double) : Double = {
    baseLine((d*G*(1-ratio)).toInt, (d*G*ratio).toInt) + G * 2 * ioEnergy
  }

  def baseLine(addCount : Int, mulCount : Int) : Double = {
    addCount * addEnergy + mulCount * mulEnergy
  }
}

object Utility {
  def log2(x: Double): Double = {
    log(x) / log(2)
  }
  def log2(x: Int): Double = {
    log(x.toDouble) / log(2)
  }
}

class ZebraVCostModel(
                       nvsc : Int
                       , mulDelay : Double
                       , addDelay : Double
                       , nvio : Int
                       //                     , mulArea : Double
                       //                     , addArea : Double
                       //                     , txArea : Double
                       //                     , stoArea : Double
                       //                     , prngArea : Double
                       //                     , ioArea : Double
                       , mulEnergy : Double
                       , addEnergy : Double
                       , txEnergy : Double
                       , stoEnergy : Double
                       , prngEnergy : Double
                       , ioEnergy : Double
                     ) {
  import Utility._
  def sumCheckDelay(d : Int, G : Int) : Double = {
    d.toDouble / nvsc * (2 * log2(G) * (mulDelay + 2 * addDelay) + ceil((7 + log2(G)) / 2) * mulDelay + 4 * addDelay)
  }
  def ioDelay(G:Int) : Double = {
    ceil(3 * G.toDouble / nvio + log2(nvio.toDouble)) * mulDelay + ceil(log2(nvio)) * addDelay
  }
  def delay(d : Int, G : Int) : Double = {
    max(sumCheckDelay(d, G), ioDelay(G))
  }

  def energy(d : Int, G : Int) : Double = {
    val logg = log2(G)
    val compute = (7.0 * d * logg + 6 * G) * mulEnergy + (15 * d * logg + 2 * G) * addEnergy
    val others = (2*d * logg + G) * txEnergy + d * logg * stoEnergy + 2 * d * logg * prngEnergy + 2 * G * ioEnergy
    compute + others
  }
}

class ZebraProverEnergy(mulEnergy : Double
                        , addEnergy : Double
                        , txEnergy : Double
                        , stoEnergy : Double
                       ) {
  import Utility._
  def energy(d : Int, G : Int, delta : Double) : Double = {
    val nppl = d // per section 7.4
    val eAverge = mulEnergy * delta + addEnergy * (1 - delta)
    val logG = log2(G)
    val compute = d * G * logG * logG * mulEnergy + 9 * d * G * logG * addEnergy + 4 * d * G * logG * eAverge
    val others = txEnergy * (7 * d * logG + G) + d * G * nppl * stoEnergy
    compute + others
  }

}

class ZebraProverModel(
                        npsc : Int
                        , addDelay : Double
                        , mulDelay : Double
                        , mulArea : Double
                        //                        , nppl : Int
                        //                      , addArea : Double
                        //                      , txArea : Double
                        //                      , stoArea : Double
                        , mulEnergy : Double
                        , addEnergy : Double
                        , txEnergy : Double
                        , stoEnergy : Double
                      ) {
  import Utility._
  val zpe = new ZebraProverEnergy(mulEnergy,addEnergy,txEnergy,stoEnergy)
  def ioDelay(d : Int, G : Int) : Double = {
    val logG = log2(G)
    d.toDouble / npsc * (3 * logG * logG * addDelay + 18 * logG * (mulDelay + addDelay))
  }

  def energy(d : Int, G : Int, delta : Double) : Double = {
    zpe.energy(d,G,delta)
  }
}

object ZebraEnergyCost {
  def savings(stats : CMTStatistics) : Double = {
    val ratio = stats.ratio
    val eaddt = 3.1
    val emult = 220
    val etxt : Double = 1.1
    val estot = 48
    val eprngt = 8.7
    val eiot = 4.2

    val sp = 100
    val eaddu = 0.006 / sp
    val emulu = 0.21 / sp
    val etxu = 0.6 / sp
    val estou = 4.2 * 0.001 / sp

//    val eaddu = 0.0
//    val emulu = 0.0
//    val etxu = 0.0
//    val estou = 4.2 * 0.000

    val vModel = new ZebraVEnergy(emult, eaddt, etxt, estot, eprngt, eiot)
    val pModel = new ZebraProverEnergy(emulu,eaddu,etxu,estou)


    val fieldSize = 64
    val zebraWidth = stats.neqCount * 2 + stats.ltCount * (fieldSize + 7) + stats.width
    // TODO: fix prover's ratio
    val outsource = vModel.energy(stats.depth, zebraWidth) + pModel.energy(stats.depth,zebraWidth,ratio)
    val baseline = vModel.baseLineOnDG(stats.depth, stats.width, ratio)
    //val baseline = vModel.baseLine(stats.addCount, stats.mulCount)
    val score = baseline - outsource
    score / baseline
  }
}


class CMTPWSGen(
               program : Program
               , readSet : Set[LValue]
               , outputSet : Set[LValue]
               , stmts : Vector[Statement]
               ) {
  type Env = Vector[Map[LValue, VIdent]]

  var env : Env = Vector(readSet.to[List].map((_,fresh("I"))).toMap)

  var counter = 0
  def nextCounter : Int = {
    counter += 1
    counter
  }

  def fresh(prefix : String) : VIdent = {
    VIdent(prefix + nextCounter, Vector())
  }

  def freshLValFirst(lval : LValue) : VIdent = {
    val newIdent = fresh("V")
    updateEnv(0, lval, newIdent)
    newIdent
  }

  def freshLValLast(lval : LValue) : VIdent = {
    val idx = env.indexWhere(_.contains(lval))
    val newIdent = fresh("V")
    val ii = if (idx == -1) {
      env.length - 1
    } else idx
    updateEnv(ii, lval, newIdent)
    newIdent
  }

  def updateEnv(idx : Int, lval : LValue, newIdent : VIdent) : Unit = {
    val m = env(idx).updated(lval, newIdent)
    env = env.updated(idx, m)
  }

  def updateEnv(lval : LValue, newIdent : VIdent) : Unit = {
    updateEnv(findCurrentIdent(lval)._1, lval, newIdent)
  }

  def findCurrentIdent(lval : LValue) : (Int, VIdent) = {
    val i = env.indexWhere(_.contains(lval))
    //println("FIND IDENT %d".format(i), CPrinter.printLVal(lval))
    (i, env(i)(lval))
  }

  def expr2expr(expr : Expression) : Expression = expr match {
    case ELValue(l) => ELValue(findCurrentIdent(l)._2)
    case EConstant(_) => expr
    case EBinOp(op, x, y) => EBinOp(op, expr2expr(x), expr2expr(y))
    case EUniOp(op, x) => EUniOp(op, expr2expr(x))
  }

  def type2lvalpaths(ty : Type) : Vector[Vector[LValPath]] = ty match {
    case TInt() => Vector(Vector())
    case TArr(t, n) => {
      val tval = type2lvalpaths(t)
      Vector.range(0,n).flatMap(x => tval.map(LVPArray(EConstant(x)) +: _))
    }
    case TStruct(_) => ??? // TODO fix this
  }


  def stmt2assigns(stmt : Statement) : Vector[SAssign] = stmt match {
    case SAssign(l, r) => {
      val expr = expr2expr(r)
      val x = freshLValLast(l)
      Vector(SAssign(x, expr))
    }
    case SVarDec(ty, ns) => {
//      println(ns)
      val x = ns match {
        case Vector((a,oexpr)) =>
          val paths = type2lvalpaths(ty)
          val lvals = paths.map(p => freshLValFirst(VIdent(a, p)))
          oexpr match {
            case Some(EConstant(_)) => {
              Vector()
            }
            case Some(e) => {
              Vector(SAssign(lvals(0), expr2expr(e)))
            }
            case None => {
              Vector()
            }
          }
      }
//      println(CPrinter.printStatement(stmt),x.map(CPrinter.printStatement(_)))
      x
    }
    case SBlank() => Vector()
    case SGrouped(vs) => {
      env = (Map() : Map[LValue,VIdent]) +: env
      val r = vs.flatMap(stmt2assigns(_))
      env = env.tail
      r
    }
  }

  def handleOutput(stmts : Vector[SAssign]) : Vector[SAssign] = {
//    println(env.last)
    val outputThenAndNow = outputSet.toList.map(
      l => SAssign(fresh("O"), ELValue(env.last(l)))
    )
    stmts ++ outputThenAndNow
  }

  def run() : Vector[SAssign] = {
//    stmts.foreach(x=>println(CPrinter.printStatement(x)))
    val ret = handleOutput(stmts.flatMap(stmt2assigns))
    println("\n\n\n\n*****************")
    stmts.foreach(x => println(CPrinter.printStatement(x)))
    println("\n\n\n ==> \n\n\n")
    ret.foreach(x => println(CPrinter.printStatement(x)))
    println("*****************\n\n\n\n")
    ret
  }
}
