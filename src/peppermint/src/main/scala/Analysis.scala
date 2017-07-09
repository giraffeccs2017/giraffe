package peppermint.Analysis
import java.io.PrintWriter

import peppermint.CMTAnalysis.CMTStatistics
import peppermint.ILAST._
import peppermint.IntervalAnalysis._

import sys.process._
import peppermint.Parser._
import peppermint.PepperVerifierInputGen.PepperVerifierInputGen
//import scala.collection.immutable.Vector._

import java.io._
import scala.collection.immutable.Map
import scala.collection.immutable.Vector

object Utility {

  def invertMap[a,b](m:Map[a,b]) : Map[b,a] = {
    m.foldLeft[Map[b,a]](Map()){
      case (acc,(x,y)) => acc.updated(y,x)
    }
  }

  def markOutsource(stmts : Vector[Statement], chunkIDs : Vector[(Int, Int,Double,CMTStatistics)]) : Vector[Statement] = {
    val x = chunkIDs.foldLeft((stmts, Vector() : Vector[Statement]))(
      (acc, chunkid) => {
        val (pre, preRest) = acc._1.span(_.identity < chunkid._1)
        val (mid, post) = preRest.span(_.identity <= chunkid._2)
        (post, acc._2 ++ (pre :+ SOut( SLiteral("//" + chunkid._3.toString + "\n") +: mid)))
      }
    )
    x._2 ++ x._1
  }

  def findMain(prog : Program) : FunctionDef = {
    prog.funcDefs.find(x => x.funcName == "compute").get
  }
  def stripArrIndex(lval : LValue) : LValue = spanArrIndex(lval)._1

  def spanArrIndex(lval : LValue) : (LValue, Vector[LValPath]) = lval match {
      // assuming that the tails of the lval is LVPArray
    case VIdent(n, path) => {
      val (pre, post) = path.span({
        case LVPArray(_) => false
        case _ => true
      })
      (VIdent(n, pre), post.asInstanceOf[Vector[LVPArray]])
    }
  }

  def findEnvIndex(name: String, env: Vector[Map[String, Value]]): (Int, Value) = {
    val i = env.indexWhere(_.contains(name))
    if (i == -1) {
    }
    (i, env(i)(name))
  }

  def isArrayAccess(lval : LValue) : Boolean = {
    lval match {
      case VIdent(_,p) => p.exists({
        case LVPArray(_) => true
        case _ => false
      })
    }
  }

  def binopToFunction(binop: BinOp): (BigInt, BigInt) => BigInt = (x, y) => binop match {
    case BAnd() => x * y
    case BOr() => if (x != 0) 1 else if (y != 0) 1 else 0
    case BEqual() => if (x == y) 1 else 0
    case BNEqual() => if (x != y) 0 else 1
    case BLE() => if (x <= y) 1 else 0
    case BLT() => if (x < y) 1 else 0
    case BGE() => if (x >= y) 1 else 0
    case BGT() => if (x > y) 1 else 0
    case BArithOp(aop) => aop match {
      case BPlus() => x + y
      case BMinus() => x - y
      case BMul() => x * y
      case BDiv() => x / y
      case BMod() => x % y
    }
  }
}
abstract class Value
case class VConst(value : BigInt) extends Value
case class VLValue(value : LValue) extends Value
case class VStruct(valueMap : Map[String, Value]) extends Value
case class VArray(length : Int, values: Vector[Value]) extends Value
case class VUnknown() extends Value

class Accounting {
  var readSet : Set[LValue] = Set()
  var readArrSet : Set[LValue] = Set()
  var writeSet : Set[LValue] = Set()
  var tentWriteSet : Set[LValue] = Set()
  var binopCount : Int = 0
  var ramputCount : Int = 0
  var ramgetCount : Int = 0
  var declared: Map[String, Type] = Map()
  var lvalToType : Map[LValue, Type] = Map()

  override def toString : String = {
    readSet.toString + "\n" + writeSet.toString + "\n" + tentWriteSet + "\n" +
      "%d, %d, %d".format(binopCount, ramgetCount, ramputCount)
  }

  def declareLValue(n:String, ty : Type) = {
    declared = declared.updated(n, ty)
  }

  def makeWriteTentative(): Unit = {
    tentWriteSet = tentWriteSet.union(writeSet)
    writeSet = Set()
  }

  def binopInc() : Accounting = {
    binopCount = binopCount + 1
    this
  }

  def ramputInc(): Accounting = {
    ramputCount = ramputCount + 1
    this
  }

  def ramgetInc() : Accounting = {
    ramgetCount = ramgetCount + 1
    this
  }

  def outputSet : Set[LValue] = writeSet.union(tentWriteSet)

  def inputSet : Set[LValue] = readSet.union(readArrSet)

  def readWrapper(lval : LValue) = {
    if (Utility.isArrayAccess(lval)) {
      readArr(Utility.stripArrIndex(lval))
    } else {
      read(lval)
    }
  }

  def readArr(lval:LValue) = {
    val n = lval match {
      case VIdent(n,_) => n
    }
    if (!declared.keySet(n)) readSet = readSet + Utility.stripArrIndex(lval)
  }

  def read(lval:LValue) = {
    val n = lval match {
      case VIdent(nn, _) => nn
    }
    if (!declared.keySet(n) && !writeSet(lval)) readSet = readSet + lval
  }

  def write(lval:LValue) = {
    writeSet = writeSet + Utility.stripArrIndex(lval)
  }

  // *this* happens immediately before *that*
  def merge(that : Accounting) : Accounting = {
    that.readSet.foreach( rv => {
      this.read(rv)
    })
    that.readArrSet.foreach( rv =>
      this.readArr(rv)
    )
    writeSet = writeSet.union(that.writeSet)
    tentWriteSet = tentWriteSet.union(that.tentWriteSet).diff(this.writeSet)
    declared = declared ++ that.declared
    binopCount = binopCount + that.binopCount
    ramputCount = ramputCount + that.ramputCount
    ramgetCount = ramgetCount + that.ramgetCount
    this
  }
}

class Cleaner(p:Program) {
  // rid of all "side-effect"y expressions, except for function calls
  val definedConst:Map[String,BigInt] = p.constantDefs.foldLeft[Map[String,BigInt]](Map())(
    (acc, cdef) => acc.updated(cdef.name, cdef.value)
  )
  println(definedConst)

  private var counter : Int = 0
  def nextCounter() : Int = {
    counter += 1
    counter
  }
  def newName() : String = {
    "c_%d".format(nextCounter())
  }

  def clean(l: Vector[Statement]) : Vector[Statement] = l.flatMap(cleanOne)

  def cleanFunctions : Program = {
    val funcs = p.funcDefs.map({
      case FunctionDef(n,t,args,body) => FunctionDef(n,t,args,clean(body))
    })
    p.copy(funcDefs = funcs)
  }

  private def cleanExpr(expr : Expression) : (Expression, Vector[Statement]) = expr match {
    case ELValue(VIdent(n,Vector())) => (definedConst.get(n).map(EConstant) getOrElse expr, Vector())
    case ELValue(VIdent(n,vv)) => (expr, Vector())
    case EBinOp(op, x, y) => {
      val (xp, xl) = cleanExpr(x)
      val (yp, yl) = cleanExpr(y)
      (EBinOp(op, xp,yp), xl++yl)
    }
    case EUniOp(op, x) => {
      val (xp, xl) = cleanExpr(x)
      val succX = EBinOp(BArithOp(BPlus()), xp, EConstant(1))
      val predX = EBinOp(BArithOp(BMinus()),xp,EConstant(1))
      op match {
        case UDeref() => (EUniOp(op, xp), xl)
        case UGetRef() => (EUniOp(op, xp), xl)
        case ULNegate() => (EUniOp(op,xp), xl)
        case UANegate() => (EUniOp(op,xp), xl)
        case _ => {
          val xlval = xp match {
            case ELValue(l) => l
          }
          op match {
            case UInc() => (predX, xl :+ SAssign(xlval, succX).setLNumber(expr.lineNumber))
            case UDec() => (succX, xl :+ SAssign(xlval, predX).setLNumber(expr.lineNumber))
            case UIncPre() => (x, xl :+ SAssign(xlval, succX).setLNumber(expr.lineNumber))
            case UDecPre() => (x, xl :+ SAssign(xlval, predX).setLNumber(expr.lineNumber))
          }
        }
      }
    }
    case ESelect(cond, x, y) => {
      val (cp, cl) = cleanExpr(cond)
      val (xp, xl) = cleanExpr(x)
      val (yp, yl) = cleanExpr(y)
      (ESelect(cp,xp,yp), cl++xl++yl)
    }
    case EConstant(_) => (expr, Vector())
    case EFunctionCall(n, args) => {
      val pl = args.map(cleanExpr)
      val ls = pl.flatMap(_._2).to[Vector]
//      val func = p.funcDefs.find(_.funcName == n).get
//      val newVar = newName()
//      val vardec = SVarDec(TInt(), Vector((newVar, None)))
//      val decs : Vector[Statement] = func.args.zip(args).flatMap(x =>
//          Vector(SVarDec(x._1._2, Vector((x._1._1, Some(x._2)))),
//            SAssign(VIdent(x._1._1,Vector()),x._2))
//       )
//      val stmts = decs ++ func.body.init
//      val assignVarDec = func.body.last.match {
//        case SReturn(Some(expr)) =>
//      }
      (EFunctionCall(n, pl.map(_._1)), ls)
    }
  }

  private def cleanOne(s : Statement) : Vector[Statement] = s match {
    case SBlank() => Vector()
    case SAssign(l,r) => {
      val (rp, rl) = cleanExpr(r)
      rl :+ SAssign(l, rp)
    }
    case SArithAssign(l, op, r) => {
      val (rp, rl) = cleanExpr(r)
      rl :+ SAssign(l, EBinOp(BArithOp(op), ELValue(l),rp))
    }
    case SExpression(r) => {
      val (rp, rl) = cleanExpr(r)
      rl :+ SExpression(rp)
    }
    case SIf(r, ts, es) => {
      val (rp, rl) = cleanExpr(r)
      rl :+ SIf(rp, ts.flatMap(cleanOne), es.flatMap(cleanOne))
    }
    case SLoop(pre, cond, post, body) => {
      val prel = cleanOne(pre)
      val (condp, condl) = cleanExpr(cond)
      val postl = cleanOne(post)
      val bl = body.flatMap(cleanOne)
      (prel ++ condl) :+ SWhile(condp, bl ++ postl ++ condl)
    }
    case SOFor(n, pre, cond, post, body) => {
      val prel = cleanOne(pre)
      val (condPure, condSide) = cleanExpr(cond)
      val postl = cleanOne(post)
      val bl = body.flatMap(cleanOne)
      val init = prel ++ condSide
      val identI = VIdent("__i",Vector())
      val innerBody = Vector(
        SVarDec(TInt(), Vector(("__i", Some(EConstant(0))))),
        SWhile(EBinOp(BLT(), ELValue(identI), EConstant(n)),
          Vector(
            SIf(condPure,
              bl ++ postl ++ condSide,
              Vector()
            ),
            SAssign(identI, EBinOp(BArithOp(BPlus()), ELValue(identI), EConstant(1)))
          ))
      )
      val outSource = SOut(innerBody)
      val newLoop = SWhile(condPure,
        Vector(outSource)
      )
      prel ++ condSide :+ newLoop
    }
    case SWhile(cond, body) => {
      val (condp, condl) = cleanExpr(cond)
      val bl = body.flatMap(cleanOne)
      condl :+ SWhile(condp, bl ++ condl)
    }
    case SVarDec(t, nl) => nl.flatMap {
      case (n, Some(expr)) => {
        val (ep, el) = cleanExpr(expr)
        val assign = SAssign(VIdent(n,Vector()), ep)
        el :+ SVarDec(t, Vector((n, None))) :+ assign
      }
      case (n, None) => {
        Vector(SVarDec(t, Vector((n,None))))
      }
    }
    case SReturn(oexpr) => {
      oexpr match {
        case Some(expr) =>
          val (ep, el) = cleanExpr(expr)
          el :+ SReturn(Some(ep))
        case _ => Vector(SReturn(None))
      }
    }
    case SOut(s) => Vector(SOut(s.flatMap(cleanOne)))
    case SGrouped(s) => Vector(SGrouped(s.flatMap(cleanOne)))
    case _ => Vector(s)
  }

}


object Executor {


}

class Executor(pre: Vector[Statement],
               mid: Vector[Statement],
               post: Vector[Statement],
               program: Program) {
  import Utility._


  type Env = Vector[Map[String, Value]]

  var accRegister : Map[Int, Accounting] = Map()
  var outEnvRegister : Map[Int, Map[String,BigInt]] = Map()
  var potentialOutputRegister : Map[Int, Set[LValue]] = Map()

  var outputConfirmRegister : Map[Int, Set[LValue]] = Map()

  def registerLValRead(lval : LValue) = {
    val confids = potentialOutputRegister.foldLeft[Vector[Int]](Vector():Vector[Int]) {
      (acc, x) => if (x._2(lval)) x._1 +: acc else acc
    }
    outputConfirmRegister = confids.foldLeft(outputConfirmRegister) {
      (acc, idx) => {
        val s = acc.getOrElse(idx,Set()) + lval
        acc.updated(idx, s)
      }
    }
  }

  var env: Vector[Map[String, Value]] = Vector(
    program.constantDefs.foldRight[
      Map[String, Value]](Map())(
      (c, m) => m.+((c.name, VConst(c.value)))))

  var binopCount: Int = 0

  def valueToSize(v : Value) : Int = v match {
    case VUnknown() => 1
    case VConst(_) => 1
    case VStruct(m) => m.values.to[Vector].map(valueToSize).sum
    case VLValue(VIdent(n,_)) => nameToSize(n)
    case VArray(i, vv) => i * valueToSize(vv(0))
  }

  def nameToSize(n : String) : Int = {
    val (_,v) = findEnvIndex(n, env)
    valueToSize(v)
  }

  def namesToSize(ns : Vector[String]) : Int = ns.map(nameToSize).sum

  def resetCounters() = {
    binopCount = 0; ramGetCount = 0; ramPutcount = 0;
    evalLValueNames = Set()
    updateLValueSet = Set()
  }

  // IO happens in here
  private var osc = 0
  def outsourceCounter() : Int = {
    osc += 1
    osc
  }


  def rewriteArrAccess(lval : LValue, target : LValue, offset : BigInt) : LValue = {
    val (fst, snd) = Utility.spanArrIndex(lval)
    if (fst == target) {
      val acc = snd.head.asInstanceOf[LVPArray].index
      val ff = fst.asInstanceOf[VIdent]
      VIdent(fst.asInstanceOf[VIdent].name, ff.path ++ snd.updated(0, LVPArray(EBinOp(BArithOp(BMinus()),acc,EConstant(offset)))))
    } else {
      lval
    }
  }

  def rewriteArrAccess(expr : Expression, target : LValue, offset : BigInt) : Expression = expr match {
    case EConstant(_) => expr
    case ELValue(lval) => ELValue(rewriteArrAccess(lval, target, offset))
    case EUniOp(op, x) => EUniOp(op, rewriteArrAccess(x, target, offset))
    case EBinOp(op, x, y) => EBinOp(op, rewriteArrAccess(x, target, offset), rewriteArrAccess(y, target, offset))
    case _ => ???
  }

  def rewriteArrAccess(stmt : Statement, target : LValue, offset : BigInt) : Statement = stmt match {
    case SVarDec(t, Vector((n,oexpr))) => {
      SVarDec(t, Vector((n, oexpr.map(rewriteArrAccess(_,target, offset)))))
    }
    case SAssign(l, r) => SAssign(rewriteArrAccess(l,target,offset),rewriteArrAccess(r,target,offset))
    case SExpression(expr) => SExpression(rewriteArrAccess(expr,target,offset))
    case SIf(b,ts,es) => SIf(rewriteArrAccess(b,target,offset),ts.map(rewriteArrAccess(_,target,offset)),es.map(rewriteArrAccess(_,target,offset)))
    case SWhile(cond,body) => SWhile(rewriteArrAccess(cond,target,offset),body.map(rewriteArrAccess(_,target,offset)))
    case SGrouped(body) => SGrouped(body.map(rewriteArrAccess(_,target,offset)))
    case SReturn(oexpr) => SReturn(oexpr.map(rewriteArrAccess(_,target,offset)))
    case SLoop(p1,cond,p2,body) =>
      SLoop(rewriteArrAccess(p1,target,offset),rewriteArrAccess(cond,target,offset),rewriteArrAccess(p2,target,offset),
        body.map(rewriteArrAccess(_,target,offset))
      )
    case _ => ???
  }

  def rewriteOutSource(stmt : Statement) : Statement = stmt match {
    case SOut(stmts) => {
      val acc : Accounting = accRegister(stmt.identity)
      println(outputConfirmRegister)
      val outputSet = outputConfirmRegister(stmt.identity).union(acc.outputSet.filter({
        case VIdent(n,_) => n == "output"
      }))
      val lvaltoty : LValue => Type = {x => acc.lvalToType(x)}
      val arrOutput = outputSet.filter(x =>
        lvaltoty(x) match {
          case TArr(_,_) => true
          case _ => true // maybe we can get away with using just acc.tentWriteSet
        }
      )
      val constantMap = outEnvRegister(stmt.identity)
      val constantStmts = constantMap.filter(p => !definedNames.contains(p._1)).map(p =>
        SAssign(VIdent(p._1,Vector()),EConstant(p._2))
      ).toVector
      val inputSet = acc.inputSet.union(arrOutput).diff(constantMap.keySet.map(s=>new VIdent(s,Vector())))
      val xxx = new InterProcAnalyzer(stmts, constantMap, inputSet, outputSet).run()
      println(xxx)
      val rewriter = new Rewriter(program, constantStmts ++ stmts, inputSet, acc.outputSet, outputSet, lvaltoty)
      val newProg = rewriter.rewrite
      val ccode = CPrinter.print(newProg)
      //println(ccode)
      val counter = outsourceCounter()
      val className = "outsource_%d".format(counter)
      val cfileName = "apps/%s.c".format(className)
      val inputFile = "%s.inputs".format(className)
      val outputFile = "%s.outputs".format(className)
      Plumbing.writeStringToFile(ccode, cfileName)
      ("./pepper_compile_and_setup_V.sh %s %s.vk %s.pk".format(className, className, className)) !

      ("./pepper_compile_and_setup_P.sh %s".format(className)) !

     /*
     ("./run/compile_pepper.sh %s > /dev/null".format(className)) !


      val inputGen = PepperVerifierInputGen.classNameToCPPCode(className, "bin/"+inputFile)
      Plumbing.writeStringToFile(inputGen, "apps_sfdl_gen/%s_v_inp_gen.cpp".format(className))
      ("./run/compile_pepper.sh %s > /dev/null".format(className)) !
     */

      SOutsource(inputSet.to[Vector],
        outputSet.to[Vector], className, inputFile, outputFile, acc.lvalToType)
    }
    case SIf(e,ts,es) => SIf(e, ts.map(rewriteOutSource), es.map(rewriteOutSource))
    case SLoop(x,e,y,z) => SLoop(x,e,y,z.map(rewriteOutSource))
    case SWhile(e,b) => SWhile(e, b.map(rewriteOutSource))
    case _ => stmt
  }



  def compileForInterpreter() : Program = {
    val main = program.funcDefs.find(_.funcName == "compute").get
    val bootStrapping = Plumbing.pluginExtraVarDeclarationForMain(main)
    bootStrapping.foreach(executeStatement)
    executeOneBlock(main.body)
    val newMain = main match {
      case FunctionDef(n, ret, args, b) => FunctionDef(n, ret, args, b.map(rewriteOutSource))
    }
    program match {
      case Program(x,y,z) =>
        Program(x, y.filter(_.funcName != "compute") :+ newMain, z)
    }
  }



  def intervalAnalysis() : Unit = {
    val main = program.funcDefs.find(_.funcName == "compute").get
    val bootStrapping = Plumbing.pluginExtraVarDeclarationForMain(main)
    bootStrapping.foreach(executeStatement)
    executeOneBlock(main.body.map(x => SGrouped(Vector(x))))
  }


  def run(): (Set[LValue], Set[LValue], Set[LValue], LValue => Type) = {
    val accPre = executeOneBlock(pre)
    resetCounters()
    val midEntrySet = env.last.keySet
    val accMid = executeOneBlock(mid)
    val midOutputCand = updateLValueSet
    val midBinopCount = binopCount
    val midInput = namesToSize(evalLValueNames.intersect(midEntrySet).to[Vector])
    val midRamPutCount = ramPutcount
    val midRamGetCount = ramGetCount
    resetCounters()
    val accPost = executeOneBlock(post)

    val midOutput = namesToSize(midOutputCand.intersect(evalLValueNames).union(Set("@output")).to[Vector])
    printf("\n\n\nInput size: %d, output size: %d, arithmetic count: %d, ram put count: %d, ram get count: %d\n\n\n\n",
      midInput, midOutput, midBinopCount, midRamPutCount, midRamGetCount)
    val tentOutputSet : Set[LValue] = accMid.outputSet
    val lvalWithOutput = tentOutputSet.filter({case VIdent(x,_) => x == "output"})
    (accMid.inputSet, tentOutputSet, accMid.outputSet.intersect(accPost.inputSet).union(lvalWithOutput),
      v => lvalToType(v))
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





  def lvalToType(lval : LValue) : Type = {
    // used for converting accounting info into types
    //  ultimately used for rewriting AST into C.
    def valToType(v : Value) : Type = {
      v match {
        case VUnknown() => TInt()
        case VArray(l, vals) => TArr(valToType(vals(0)), l)
        case VConst(_) => TInt()
        case _ => sys.error("bad value") // lvalue and others are forbidden
      }
    }
    valToType(evalLValue(lval)._1)
    // TODO: structure assignments are not currently supported
  }


  def uniopToFunction(uniop: UniOp): Int => Int = x => uniop match {
    case ULNegate() => -x
    case UANegate() => if (x == 0) 1 else 0
  }

  def forceInt(value: Value) = value match {
    case VConst(a) => Some(a)
    case _ => None
  }


  var evalLValueNames: Set[String] = Set()

  var ramGetCount : Int = 0


  def evalLValue(lval: LValue): (Value, Accounting) = lval match {
    case VIdent(x, l) =>
      val (envIdx, v) = findEnvIndex(x, env)
      var ramGet = false
      val (vv, acc) : (Value, Accounting) = l.foldLeft((v, new Accounting)) {
        case ((vp:Value, a:Accounting), pe: LValPath) =>
          vp match {
            case VUnknown() => (VUnknown(), a)
            case _ => pe match {
              case LVPDeref() => vp match {
                // TODO: dereference might not be to a "simple" name
                case VLValue(VIdent(n,_)) => {
                  val (_, vll) = findEnvIndex(n, env)
                  //               val (vll, all) = evalLValue(ll)
                  (vll, a)
                }
              }
              case LVPArrow(c) => vp match {
                // TODO: arrow might not be to a "simple" name
                case VLValue(ll) =>
                  evalLValue(ll) match {
                    case (VUnknown(), ap) => (VUnknown(), a)
                    case (VStruct(m), ap) => (m(c), a)
                  }
              }
              case LVPDot(c) => vp match {
                case VStruct(m) => (m(c), a)
              }
              case LVPArray(i) => vp match {
                case VArray(_, arr) => {
                  val (ival, ai) = evalExpression(i)
                  ival match {
                    case VUnknown() => ramGet = true; (VUnknown(), a.merge(ai))
                    case VConst(ii) => (arr(ii.toInt), a.merge(ai))
                  }
                }
              }

            }
          }
      }
      vv match {
        case VUnknown() => {
          if (envIdx == env.size - 1) evalLValueNames = evalLValueNames + x
        }
        case _ =>
      }
      acc.readWrapper(lval)
      registerLValRead(Utility.stripArrIndex(lval))
      if (ramGet) {
        ramGetCount = ramGetCount + 1
        acc.ramgetInc()
      }
      (vv, acc)
  }


  def evalExpression(expr: Expression): (Value, Accounting) = expr match {
    case EConstant(x) => (VConst(x), new Accounting)
    case ELValue(lval) => evalLValue(lval)
    case EBinOp(op, x, y) => {
      val (ex, ax) = evalExpression(x)
      val (ey, ay) = evalExpression(y)
      val aa = ax.merge(ay)
      (ex, ey) match {
        case (VConst(xval), VConst(yval)) =>
          (VConst(binopToFunction(op)(xval, yval)), new Accounting)
        case _ => {
          binopCount = binopCount + 1
          aa.binopCount = aa.binopCount + 1
          (VUnknown(), aa)
        }
      }
    }
    case EUniOp(UGetRef(), ELValue(v)) => {
      (VLValue(v), new Accounting)
    }
    case EUniOp(op, ex) => {
      val (v, ax) = evalExpression(ex)
      v match {
        case VUnknown() => (v, ax)
        case VConst(vp) => op match {
          case ULNegate() => (VConst(if (vp == 0) 1 else 0), ax)
          case UANegate() => (VConst(-vp), ax)
          case _ => sys.error("Type error " + op + " " + vp)
        }
        case VLValue(l) => op match {
          case UDeref() => {
            val (vv, aa) = evalLValue(l)
            (vv, ax.merge(aa))
          }
        }
        case VArray(_, av) => op match {
          case UDeref() => (av(0), ax)
        }
        case VStruct(_) => sys.error("type error")
      }
    }
    case ESelect(bin, x, y) => {
      val (vv, aa) = evalExpression(bin)
      vv match {
        case VUnknown() => {
          val (xv, ax) = evalExpression(x)
          val (yv, ay) = evalExpression(y)
          (VUnknown(), aa.merge(ax.merge(ay)))
        }
        case VConst(v) => {
          val (yv, ay) = if (v == 0) evalExpression(y) else evalExpression(x)
          (yv, aa.merge(ay))
        }
        case _ => {
          val (xv, ax) = evalExpression(x)
          (xv, aa.merge(ax))
        }
      }
    }
    case EFunctionCall(ident, args) => {
      val func = program.funcDefs.find(_.funcName == ident).get
      executeFunction(func, args.to[Vector])
    }
  }

  def updateValue(path: Vector[LValPath], old: Value, newV: Value): Either[Vector[LValPath], Value] = path match {
    case Vector() => Right(newV)
    case _ => path.last match {
      case LVPArrow(_) => Left(path)
      case LVPDeref() => Left(path)
      case LVPDot(c) => old match {
        case VStruct(m) =>
          val res = updateValue(path.init, m.get(c).get, newV)
          res match {
            case Right(v) => Right(VStruct(m.updated(c, v)))
            case Left(_) => res
          }
      }
      case LVPArray(expr) => evalExpression(expr) match {
        case _ => ???
      }
    }
  }

  var updateLValueSet: Set[String] = Set()

  var ramPutcount : Int = 0

  def updateLValue(lval: LValue, value: Value, env: Env): (Env, Accounting) = lval match {
    case VIdent(x, Vector()) => {
      val (idx, v) = findEnvIndex(x, env)
      val frame = env(idx)
      val acc = new Accounting
      value match {
        case VUnknown() => {
          updateLValueSet = updateLValueSet + x
        }
        case _ =>
      }
      acc.write(lval)
      potentialOutputRegister = potentialOutputRegister.mapValues(s => s - (lval))
      (env.updated(idx, frame.updated(x, value)), acc)
    }
    case VIdent(x, l) => {
      val acc = new Accounting
      // assuming this is a "data" update, where it is already in unknown state
      // TODO: fix the above assumption...
      // find out if any of the indices are unknown and mark as RAMPUT
      var ramput = false
      l.foreach({
        case LVPArray(idx) =>
          val (v, va) = evalExpression(idx)
          acc.merge(va)
          v match {
            case VUnknown() => ramput = true
            case _ =>
          }
        case _ =>
      })
      if (ramput) {
        acc.ramputInc()
        ramPutcount = ramPutcount + 1
      }
      val (_,v) = findEnvIndex(x, env)
      v match {
        case VLValue(VIdent(n,_)) => updateLValueSet = updateLValueSet + n
        case _ => updateLValueSet = updateLValueSet + x
      }
      acc.write(lval)
      (env, acc)
    }
  }

  def executeFunction(functionDef: FunctionDef, args : Vector[Expression]) : (Value, Accounting) = {
    if (functionDef.args.length != args.length) {
      sys.error("wrong number of inputs provided for function " + functionDef.funcName)
    }
    val (vals, accs) = args.map(evalExpression(_)).unzip
    val argac = accs.foldLeft[Accounting](new Accounting){
      (l, r) => l.merge(r)
    }
    val newFrame = functionDef.args.map(_._1).zip(vals).toMap
    env = newFrame +: env
    //    val decs : Vector[SVarDec] = functionDef.args.zip(args).map(x =>
    //      SVarDec(x._1._2, Vector((x._1._1, Some(x._2))))
    //    )
    //    val stmts = decs ++ functionDef.body
    val (mr, acc) = executeStatements(functionDef.body)
    argac.merge(acc)
    env = env.tail
    (mr getOrElse VUnknown(), argac)
  }

  def executeOneBlock(block: Vector[Statement]): Accounting = {
    block.foldLeft(new Accounting){
      (acc, stmt) => {
        val (_, accp) = executeStatement(stmt)
        acc.merge(accp)
      }
    }
  }


  def executeStatements(stmts: Vector[Statement]): (Option[Value], Accounting) = {
    env = (Map(): Map[String, Value]) +: env
    val (ret, ac) = stmts.foldLeft((None:Option[Value], new Accounting))(
      (acc, stmt) =>
        acc match {
          case (Some(_), _) => acc
          case _ => {
            val (rr, aa) = executeStatement(stmt)
            val newAA = acc._2.merge(aa)
            (rr, newAA)
          }
        })
    // rid of all of the writes that do not survive the scope
    val h:Set[String] = env.head.keySet
    val ff : LValue => Boolean = {
      case VIdent(n,_) => !h.contains(n)
    }
    ac.writeSet = ac.writeSet.filter(ff)
    ac.tentWriteSet = ac.tentWriteSet.filter(ff)
    env = env.tail
    (ret, ac)
  }

  val definedNames : Set[String] = program.constantDefs.map(c => c.name).to[Set]

  // returns a single map that only contains constant scalars
  def collapseEnv(e : Env) : Map[String,BigInt] = {
    e.foldRight(Map():Map[String,BigInt])(
      (a, acc) => {
        a.foldRight(acc)(
          (s, aa) => s._2 match {
            case VConst(x) => aa.updated(s._1,x)
            case VUnknown() => aa.updated(s._1, Interval.infinity)
            case _ => aa
          }
        )
      }
    )
  }


  def executeStatement(stmt: Statement): (Option[Value], Accounting) = stmt match {
    case SGrouped(stmts) => executeStatements(stmts)
    case SOut(blk) => {
      outEnvRegister = outEnvRegister.updated(stmt.identity, collapseEnv(env))
      val ret = executeOneBlock(blk)
      val acc = ret
      acc.lvalToType = acc.inputSet.union(acc.outputSet).foldLeft(Map():Map[LValue,Type]) {
        (acc, l) => acc.updated(l, lvalToType(l))
      }
      potentialOutputRegister = potentialOutputRegister.updated(stmt.identity, acc.outputSet)
      outputConfirmRegister = outputConfirmRegister.updated(stmt.identity, Set())
      accRegister = accRegister.updated(stmt.identity, acc)
      (None,ret)
    }
    case SBlank() => (None, new Accounting)
    case SAssign(lval, rval) => {
      val (rr, acc) = evalExpression(rval)
      val envAcc = updateLValue(lval, rr, env)
      env = envAcc._1
      (None, acc.merge(envAcc._2))
    }
    case SArithAssign(_, _, _) => sys.error("")
    case SExpression(e) => {
      val ret = evalExpression(e)
      (None, ret._2)
    }
    case SIf(b, ts, es) => {
      val (bv, ba) = evalExpression(b)
      bv match {
        case _ => {
          // we assume `if` branches cannot be decided at static time
          val eh = env.head
          val (tv, ta) = executeStatements(ts)
          env = eh +: env.tail
          val (ev, ea) = executeStatements(es)
          val ret = (tv,ev) match {
            case (Some(_), Some(_)) => Some(VUnknown())
            case _ => None
          }
          ta.writeSet.union(ea.writeSet).foreach({
            lval =>
              val (envp, _) = updateLValue(lval, unknownValueForType(lvalToType(lval)), env)
              env = envp
          })
          ea.makeWriteTentative()
          ta.makeWriteTentative()
          (ret, ba.merge(ea.merge(ta)))
        }
//        case VConst(0) => {
//          val (ret, acc) = executeStatements(es)
//          (ret, ba.merge(acc))
//        }
//        case _ => {
//          val (ret, acc) = executeStatements(ts)
//          (ret, ba.merge(acc))
//        }
      }
    }
    case SLoop(_, _, _, _) => sys.error("loop constructs should be removed using cleaner class")
    case SWhile(cond, body) =>
      // we only `execute` the loop body once
      val ret = None
      val acc = new Accounting
      var loopc = true
      while(loopc) {
        val (cv, ca) = evalExpression(cond)
        cv match {
//          case VUnknown() => sys.error("bad loop " + cond)
//          case VConst(0) => loopc = false
          case _ => {
            val (ret, ba) = executeStatements(body)
            val (ret2, ba2) = executeStatements(body)
            // exeucte a second time to compute a "fixed point" of the I/O set of the body
            acc.merge(ba)
            loopc = false
          }
        }
      }
      (ret, acc)
    case SVarDec(t, Vector((n, oexpr))) =>
      val acc = new Accounting
      acc.declareLValue(n, t)
      val (v, accp) = oexpr match {
        case Some(expr) => acc.write(VIdent(n,Vector())); evalExpression(expr)
        case None => (unknownValueForType(t), new Accounting)
      }
      val h = env.head.updated(n, v)
      env = env.updated(0, h)
      (None, acc)
    case SReturn(Some(expr)) => {
      val (ret, acc) = evalExpression(expr)
      (Some(ret), acc)
    }
    case SReturn(None) => (Some(VUnknown()), new Accounting)
    case _ => ???
  }

}

object Plumbing {

  def pluginExtraVarDeclarationForMain(func : FunctionDef) : Vector[Statement] = {
    def ptrToStmts(newS : String, s:String, ptrType : TPtr) : Vector[Statement] = {
      val dec = SVarDec(ptrType.elemType, Vector((newS, None)))
      val ptrDec = SVarDec(ptrType, Vector((s, None)))
      val assin = SAssign(VIdent(s,Vector()), EUniOp(UGetRef(), ELValue(VIdent(newS, Vector()))))
      Vector(dec,ptrDec,assin)
    }
    ptrToStmts("@input", func.args(0)._1, func.args(0)._2.asInstanceOf[TPtr]) ++
      ptrToStmts("@output", func.args(1)._1, func.args(1)._2.asInstanceOf[TPtr])
  }

  def expressionHasFunctionCall(expr : Expression) : Boolean = {
    expr match {
      case ELValue(VIdent(_,p)) => p.foldLeft(false)((b,p) =>
        b || (p match {
          case LVPArray(e) => expressionHasFunctionCall(e)
          case LVPArrow(_) => false
          case LVPDot(_) => false
        })
      )
      case EBinOp(_, x, y) => expressionHasFunctionCall(x) || expressionHasFunctionCall(y)
      case EUniOp(_, x) => expressionHasFunctionCall(x)
      case ESelect(x,y,z) => expressionHasFunctionCall(x) || expressionHasFunctionCall(y) || expressionHasFunctionCall(z)
      case EConstant(_) => false
      case EFunctionCall(_,_) => true
    }
  }

  def statementHasFunctionCall(s : Statement) : Boolean = {
    def statementsHaveFunctionCall(s: Vector[Statement]) : Boolean = {
      s.foldLeft(false)((b,s) => b || statementHasFunctionCall(s))
    }
    s match {
      case SBlank() => false
      case SAssign(_, e) => expressionHasFunctionCall(e)
      case SArithAssign(_,_,e) => expressionHasFunctionCall(e)
      case SExpression(e) => expressionHasFunctionCall(e)
      case SIf(e, ts, es) => expressionHasFunctionCall(e) || statementsHaveFunctionCall(ts) || statementsHaveFunctionCall(es)
      case SLoop(x,e,y,z) => {
        expressionHasFunctionCall(e) || statementHasFunctionCall(x) ||
          statementHasFunctionCall(y) || statementsHaveFunctionCall(z)
      }
      case SWhile(_,b) => statementsHaveFunctionCall(b)
      case SVarDec(_,nvl) => nvl.foldLeft(false)((b, nv) => b ||
        nv._2.exists(expressionHasFunctionCall))
      case SReturn(oexpr) => oexpr.exists(expressionHasFunctionCall)
      case _ => false
    }
  }

  def isGoodStartingPoint(s : Statement) : Boolean = s match {
    case SWhile(_,_) => true
    case SLoop(_,_,_,_) => true
    case _ => statementHasFunctionCall(s)
  }

  def splitupStatements(s : Vector[Statement]) : List[(Vector[Statement],Vector[Statement],Vector[Statement])] = {
    def endsWithGoodStartingPoint(s : Vector[Statement]) : List[(Vector[Statement], Vector[Statement])] = {
      // not sure how to use lazy...
      lazy val inits : List[Vector[Statement]] = s.inits.toList.reverse
      lazy val tails = s.tails.toList
      lazy val zipped : List[(Vector[Statement],Vector[Statement])] = inits.zip(tails)
      zipped.filter(p => p._1.nonEmpty && isGoodStartingPoint(p._1.last))
    }
    val (p, q) = s.span(!isGoodStartingPoint(_))
    if (q.isEmpty)
      List()
    else {
      val p1 = endsWithGoodStartingPoint(q).map(x => (p, x._1, x._2))
      val p2 = splitupStatements(q.tail).map(x =>
        (p ++ (q.head +: x._1), x._2, x._3)
      )
      p1 ++ p2
    }
  }

  def writeStringToFile(string : String, fileName : String, append : Boolean = false) = {
    val out = new PrintWriter(new FileWriter(fileName, append))
    out.print(string)
    out.close()
  }




  def compile(prog : Program, outputName : String) = {
    val progC = new Cleaner(prog).cleanFunctions
    val newProg = new Executor(Vector(),Vector(),Vector(),progC).compileForInterpreter()
    val newProgBin = CBinaryRewriter.rewriteProg(newProg)
    val str = "#include <stdio.h>\n#include <stdlib.h>\n" + CPrinter.print(newProgBin)
    val cFileName = outputName + ".c"
    writeStringToFile(str, cFileName)
    "gcc %s -o %s".format(cFileName, outputName) !
  }

  def main2(args : Array[String]) : Unit = {
    // compile c-file-name output-file-name
    // run file-name input-file-name
    if (args.length != 3) {
      println("USAGE: ./bin compile c-file-name bin-name")
     // println("or     ./bin run bin-name input-file-name")
      return
    }
    println("hello world??")
    if (args(0) == "compile") {
      val prog = CParserPlus.parseFile(args(1))
      compile(prog, args(2))
    } else if (args(0) == "run") {
      val prog = CParserPlus.parseFile(args(1))
      import peppermint.Runner._
      val r = new Runner(prog, args(2))
      r.run()
    }
  }



  def main_(args : Array[String]): Unit = {
    if (args.length != 1 && args.length != 3) {
      println("Usage: ./bin filename begin-line-number end-line-number\n")
      println("or     ./bin filename to automatically compute based on loops and function calls")
      return
    }
    val filename = args(0)
    val prog = CParserPlus.parseFile(filename)
    val mainFunc = prog.funcDefs.find(_.funcName == "compute").get
    val stmts = pluginExtraVarDeclarationForMain(mainFunc) ++ mainFunc.body
    val cleaner = new Cleaner(prog)
    if (args.length == 3) {
      val b = args(1).toInt
      val e = args(2).toInt
      val (pre, mid, post) = CParserPlus.divide(b, e, stmts)
      val executor = new Executor(
        cleaner.clean(pre), cleaner.clean(mid),
        cleaner.clean(post), cleaner.cleanFunctions)
      executor.run()
    } else if (args.length == 1) {
      val splitted = splitupStatements(stmts)
      val progp = cleaner.cleanFunctions
      splitted.foreach({
        case (pre, mid, post) =>
          val executor = new Executor(cleaner.clean(pre), cleaner.clean(mid), cleaner.clean(post),
            progp)
          executor.run()
      })
    }
  }
}



class Rewriter(program: Program
               , stmts : Vector[Statement]
               , readSet : Set[LValue]
               , writeSet : Set[LValue]
               , outputSet : Set[LValue]
               , lvalToTypeFunc : LValue => Type
               , cmt : Boolean = false
              ) {
  val totalSet = readSet ++ writeSet ++ outputSet
  val newNamesMap : Map[LValue, String]  =
    totalSet.toVector.zip(List.range(0,totalSet.size).toVector).map({case (lval,n)=>(lval, "_vv" + n.toString)}).toMap

  def rewriteExpression(expr : Expression) : Expression = expr match {
    case ELValue(lvl) => ELValue(rewriteLValue(lvl))
    case EBinOp(op, x , y) => EBinOp(op, rewriteExpression(x), rewriteExpression(y))
    case EUniOp(op, x) => EUniOp(op, rewriteExpression(x))
    case ESelect(cond, x, y) => ESelect(rewriteExpression(cond), rewriteExpression(x), rewriteExpression(y))
    case EConstant(x) => EConstant(x)
    case EFunctionCall(n, args) => EFunctionCall(n, args.map(rewriteExpression))
  }

  def rewriteLValue(lval : LValue) : LValue = {
    val (name,path) = lval match {
      case VIdent(n, p) => (n,p)
    }
    val newPath = path.map({
      case LVPArray(e) => LVPArray(rewriteExpression(e))
      case a => a
    })
    val lvalTmp = VIdent(name, newPath)
    val (slval, rest) = if (!cmt) {
      Utility.spanArrIndex(lvalTmp)
    } else {
      (lvalTmp, Vector())
    }
    val inRead : Boolean = readSet(slval)
    val inWrite : Boolean = writeSet(slval)
    val inOutput : Boolean = outputSet(slval)
    (inRead, inWrite, inOutput) match {
      case (true, _, _) => VIdent("input", LVPArrow(newNamesMap(slval)) +: rest)
      case (_, _, true) => VIdent("output", LVPArrow(newNamesMap(slval)) +: rest)
      case (_, true, _) => VIdent(newNamesMap(slval), rest)
      case _ => lval
    }
  }

  def rewriteStatement(stmt: Statement) : Statement = stmt match {
    case SAssign(lval, rval) => SAssign(rewriteLValue(lval), rewriteExpression(rval))
    case SArithAssign(lval, op, rval) => SArithAssign(rewriteLValue(lval), op, rewriteExpression(rval))
    case SExpression(expr) => SExpression(rewriteExpression(expr))
    case SIf(b, t, e) => SIf(rewriteExpression(b), t.map(rewriteStatement), e.map(rewriteStatement))
    case SLoop(pre,c,post,body) => SLoop(rewriteStatement(pre), rewriteExpression(c), rewriteStatement(post),
      body.map(rewriteStatement))
    case SWhile(cond, body) => SWhile(rewriteExpression(cond), body.map(rewriteStatement))
    case SVarDec(ty: Type, nameValList) => SVarDec(ty, nameValList.map({
      case (s, oexpr) => (s, oexpr.map(rewriteExpression))
    }))
    case SReturn(oexpr) => SReturn(oexpr.map(rewriteExpression))
    case SGrouped(b) => SGrouped(b.map(rewriteStatement))
    case x => x
  }

  def typeToCopyFunc(ty: Type, r : LValue, iterStr : String) :  Vector[Statement] = {
    def arrayLengths(ty:Type) : Vector[Int] = ty match {
      case TInt() => Vector()
      case TArr(t, n) => n +: arrayLengths(t)
    }
    val alengths = arrayLengths(ty)
    val iters = List.range(0, alengths.length).map(iterStr + _.toString)
    val arrayAccess = iters.map(
      n => LVPArray(ELValue(VIdent(n,Vector())))
    )
    def appendArrayAccess(lval : LValue) = lval match {
      case VIdent(n, p) => VIdent(n, p ++ arrayAccess)
    }
    val l = r match {
      case VIdent(_, p) => VIdent("output", p)
    }
    val al = appendArrayAccess(l)
    val ar = appendArrayAccess(r)
    val stmt = SAssign(al, ELValue(ar))
    val decs = iters.map( n =>
      SVarDec(TInt(), Vector((n,None)))
    ).to[Vector]
    val forLoop:Statement = iters.zip(alengths).foldRight[Statement](stmt){
      (iter, acc) => {
        val liter = VIdent(iter._1, Vector())
        val limit = EConstant(iter._2)
        SLoop(
          SAssign(liter, EConstant(0)),
          EBinOp(BLT(), ELValue(liter), limit),
          SExpression(EUniOp(UInc(), ELValue(liter))),
          Vector(acc)
        )
      }
    }
    decs :+ forLoop
  }



  def rewrite: Program = {
    val inputStructType = TStruct("input_gen")
    val outputStructType = TStruct("output_gen")
    val wDiffO = writeSet.diff(outputSet.union(readSet))
//    println(readSet)
//    println(writeSet)
//    println(outputSet)
//    println(wDiffO)
    val wDiffODecl = wDiffO.to[Vector].map(
      lval => SVarDec(lvalToTypeFunc(lval), Vector((newNamesMap(lval), None)))
    )
    val copyIOSet = readSet.intersect(outputSet)
    var cioCounter = 0
    val copyIOStmts = copyIOSet.to[Vector].flatMap(
      lval => {
        cioCounter = cioCounter + 1
        val ty = lvalToTypeFunc(lval)
        typeToCopyFunc(ty, rewriteLValue(lval), "_i" + cioCounter.toString)
      }
    )
    val newBody = wDiffODecl ++ stmts.map(rewriteStatement) ++ copyIOStmts
    val newComputeFun = FunctionDef("compute", TVoid(), Vector(("input", TPtr(inputStructType)),
      ("output", TPtr(outputStructType))), newBody)
    val inputTypeDef= TypeDef(
      "input_gen",
      readSet.to[Vector].map(
        lval =>
          {
//            println("INPUT_SET_GEN")
//            println(lval, newNamesMap(lval))
            (newNamesMap(lval), lvalToTypeFunc(lval))
          }
      )
    )
    val outputTypeDef= TypeDef(
      "output_gen",
      outputSet.to[Vector].map(
        lval =>
        {
//          println("OUTPUT_SET_GEN:")
//          println(lval, newNamesMap(lval))
          (newNamesMap(lval), lvalToTypeFunc(lval))
        }
      )
    )
    program match {
      case Program(cdef, funDef, tyDef) =>
        Program(cdef, funDef.filterNot(_.funcName == "compute"):+newComputeFun,
          inputTypeDef +: outputTypeDef +: tyDef)
    }
  }
}



object CPrinter {

  def binopToString(binop : BinOp) : String = binop match {
    case BArithOp(arithop) => arithop match {
      case BMinus() => "-"
      case BPlus() => "+"
      case BMul() => "*"
      case BDiv() => "/"
      case BMod() => "%"
    }
    case BAnd() => "&&"
    case BOr() => "||"
    case BEqual() => "=="
    case BNEqual() => "!="
    case BLT() => "<"
    case BGT() => ">"
    case BLE() => "<="
    case BGE() => ">="
  }

  def printExpr(expr : Expression) : String = expr match {
    case ELValue(lval) => printLVal(lval)
    case EBinOp(op, x, y) =>
      "(%s)%s(%s)".format(printExpr(x), binopToString(op), printExpr(y))
    case EUniOp(op, x) => {
      val sx = "(%s)".format( printExpr(x))
      op match {
        case UInc() => sx + "++"
        case UDec() => sx + "--"
        case UIncPre() => "++" + sx
        case UDecPre() => "--" + sx
        case UDeref() => "*" + sx
        case UGetRef() => "&" + sx
        case ULNegate() => "!" + sx
        case UANegate() => "-" + sx
      }
    }
    case ESelect(cond, x, y) => {
      "(%s)?(%s):(%s)".format(printExpr(cond), printExpr(x), printExpr(y))
    }
    case EConstant(i) => i.toString
    case EFunctionCall(fname, args) => {
      val argsStrL = args.map(printExpr)
      val argsStr = if (argsStrL.nonEmpty) {
        val argsStr = argsStrL.tail.foldLeft(argsStrL.head) {
          (acc, y) => acc + ", " + y
        }
      } else ""
      "%s(%s)".format(fname, argsStr)
    }
    case EFetch(VIdent(n, p), v) => {
      printLVal(VIdent(n, p ++ v.map(LVPArray(_))))
    }
  }

  def printLVal(lval : LValue) : String = lval match {
    case VIdent(n, pl) => pl.foldLeft(n){
      (acc, p) => p match {
        case LVPArrow(c) => acc + "->" + c
        case LVPDot(c) => acc + "." + c
        case LVPArray(idxE) => acc + "[" + printExpr(idxE) + "]"
        case LVPDeref() => "*(%s)".format(acc)
      }
    }
    case VIdentSSA(v,i) => {
      printLVal(v) //+ "_" + i
    }
    case VIdentName(n,idx) => n + idx.map(x => "[%d]".format(x)).getOrElse("")
  }

  def printStatements(stmts : Vector[Statement]) : String = {
    stmts.map(printStatement(_) + "\n").foldLeft("")((acc,s) => acc+s)
  }

  def nameTypeToStr(n : String, ty : Type) : String = {
    def typeToStrToAppend(ty : Type) : String = ty match {
      case TArr(t, k) => "[%d]".format(k) + typeToStrToAppend(t)
      case _ => ""
    }
    n + typeToStrToAppend(ty)
  }

  def typeToStr(ty : Type) : String = ty match {
    case TInt() => "int"
    case TStruct(x) => "struct " + x
    case TPtr(t) => typeToStr(t) + " *"
    case TArr(t, _) => typeToStr(t)
    case TVoid() => "void"
  }

  def printStatement(stmt:Statement) : String = stmt match {
    case SBlank() => ""
    case SAssign(l,r) => {
      if (stmt.comment.length > 0)
        "%s = %s; \n/*\n%s\n*/".format(printLVal(l), printExpr(r),stmt.comment)
      else
        "%s = %s;".format(printLVal(l), printExpr(r))
    }
    case SArithAssign(l,op,r) => "%s %s= %s;".format(printLVal(l), binopToString(BArithOp(op)),
      printExpr(r))
    case SExpression(expr) => printExpr(expr) + ";"
    case SIf(b, tb, eb) => "if (%s) {\n%s\n} else {\n%s\n}".format(printExpr(b),
      printStatements(tb), printStatements(eb))
    case SLoop(pre, cond, post, body) =>
      "%s;\nfor (; %s;) {\n%s\n}".format(printStatement(pre), printExpr(cond),
        printStatements(body :+ post))
    case SWhile(cond, body) =>
      "while(%s) {\n%s\n}".format(printExpr(cond), printStatements(body))
    case SVarDec(ty, nameValList) => {
      val tyStr = typeToStr(ty)
      val valStr = nameValList.map({
        case (n, oexpr) => nameTypeToStr(n, ty) + (oexpr.map(" = " + printExpr(_)) getOrElse "")
      }).reduceLeft((acc,str) => acc + ", " + str)
      tyStr + " " + valStr + ";"
    }
    case SReturn(oexpr) => "return %s;".format(oexpr.map(printExpr) getOrElse "")
    case SBreak() => "break;"
    case SContinue() => "continue;"
    case SBegin() => ""
    case SEnd() => ""
    case SOutsource(input, output, n1, n2, n3, _) => {
      "outsource {\n %s \n} {\n %s \n} {%s \n %s \n %s}".format(
        input.map(printLVal).reduce((x,y) => x + "\n" + y),
        output.map(printLVal).reduce((x,y) => x + "\n" + y),
        n1, n2, n3
      )
    }
    case _:SZebraOutsource => {
      "outsource to Giraffe: result.pws"
    }
    case SOut(stmts) => {
      "out {\n %s \n}".format(printStatements(stmts))
    }
    case SLiteral(str) => str
    case SGrouped(body) => {
      "{\n %s \n}".format(printStatements(body))
    }
    case SForGrouped(body) => {
      "{\n %s \n}".format(printStatements(body))
    }
    case SCondStore(VIdent(n,p), inds, v, cond) => {
      val left = VIdent(n, p ++ inds.map(LVPArray(_)))
      val assn = SAssign(left, v)
      printStatement(SIf(cond.getOrElse(EConstant(1)),Vector(assn),Vector()))
    }
  }

  def typeNameString(ty:Type, name:String) : String = {
    "%s %s".format(typeToStr(ty), nameTypeToStr(name, ty))
  }
  def printFuncDef(funcDef : FunctionDef) : String = funcDef match {
    case FunctionDef(n, ret, args, body) => {
      val argS = if (args.isEmpty) "" else args.map(x => typeNameString(x._2,x._1)).reduceLeft((acc,s)=>acc+", "+s)
      "%s %s(%s) {\n%s\n}".format(
        typeToStr(ret), n, argS, printStatements(body)
      )
    }
  }

  def seperateByNewLine(x:String, y: String) : String = {
    x + "\n" + y
  }

  def printTypeDef(x : TypeDef):String = x match {
    case TypeDef(n, mems) => {
      val memsNew = if (mems.isEmpty) {
        Vector(("dummy", TInt()))
      } else {
        mems
      }
      val memStr = memsNew.map(x=>typeNameString(x._2,x._1)+";").reduceLeft(seperateByNewLine)
      "struct %s {\n%s\n};".format(n, memStr)
    }
  }


  def print(prog : Program) : String = {
    val constantDefL:List[String] = prog.constantDefs.map( {
      case ConstantDef(a,b) => "#define %s %d".format( a, b) : String}
    )
    val constantDef : String = ("" :: constantDefL).reduce(seperateByNewLine)
    val structs : String = prog.typeDefs.map(printTypeDef).reduceLeft[String](seperateByNewLine)
    val funcs : String = prog.funcDefs.map(printFuncDef).reduceLeft[String](seperateByNewLine)
    constantDef + "\n" + structs + "\n" + funcs
  }
}

object CBinaryRewriter {
  def arrayLengths(ty: Type): Vector[Int] = ty match {
    case TInt() => Vector()
    case TArr(t, n) => n +: arrayLengths(t)
  }

  def typeToWriteStatements(ty : Type, lval : LValue, iterStr : String) : Vector[Statement] = {
    val alengths = arrayLengths(ty)
    val iters = List.range(0, alengths.length).map(iterStr + _.toString)
    val arrayAccess = iters.map(
      n => LVPArray(ELValue(VIdent(n, Vector())))
    )
    def appendArrayAccess(lval: LValue) = lval match {
      case VIdent(n, p) => VIdent(n, p ++ arrayAccess)
    }
    val ar = appendArrayAccess(lval)
    val stmt = SLiteral(
      "printf(\"%%d\\n\", %s);".format(CPrinter.printLVal(ar))
    )
    val decs = iters.map(n =>
      SVarDec(TInt(), Vector((n, None)))
    ).to[Vector]
    val forLoop: Statement = iters.zip(alengths).foldRight[Statement](stmt) {
      (iter, acc) => {
        val liter = VIdent(iter._1, Vector())
        val limit = EConstant(iter._2)
        SLoop(
          SAssign(liter, EConstant(0)),
          EBinOp(BLT(), ELValue(liter), limit),
          SExpression(EUniOp(UInc(), ELValue(liter))),
          Vector(acc)
        )
      }
    }
    decs :+ forLoop
  }

  def typeToReadStatements(ty : Type, lval : LValue, iterStr : String) : Vector[Statement] = {

    val alengths = arrayLengths(ty)
    val iters = List.range(0, alengths.length).map(iterStr + _.toString)
    val arrayAccess = iters.map(
      n => LVPArray(ELValue(VIdent(n, Vector())))
    )
    def appendArrayAccess(lval: LValue) = lval match {
      case VIdent(n, p) => VIdent(n, p ++ arrayAccess)
    }
    val ar = appendArrayAccess(lval)
    val stmt = SLiteral(
      "scanf(\"%%d\", &(%s));".format(CPrinter.printLVal(ar))
    )
    val decs = iters.map(n =>
      SVarDec(TInt(), Vector((n, None)))
    ).to[Vector]
    val forLoop: Statement = iters.zip(alengths).foldRight[Statement](stmt) {
      (iter, acc) => {
        val liter = VIdent(iter._1, Vector())
        val limit = EConstant(iter._2)
        SLoop(
          SAssign(liter, EConstant(0)),
          EBinOp(BLT(), ELValue(liter), limit),
          SExpression(EUniOp(UInc(), ELValue(liter))),
          Vector(acc)
        )
      }
    }
    decs :+ forLoop
  }

  private var counter = 0
  def nextCounter() : Int = {
    counter = counter + 1
    counter
  }


  def rewriteStatement(stmt : Statement) : Vector[Statement] = stmt match {
    case SOutsource(input, output, cName, inputFileName, outputFileName, tyMap) => {
      val reopenStdOut = SLiteral("freopen(\"prover_verifier_shared/%s\", \"w\", stdout);".format(inputFileName))
      val inputStmts = input.flatMap(
        lval => {
          val iterStr = "__citer" + nextCounter
          typeToWriteStatements(tyMap(lval), lval, iterStr)
        } )
      val closeStdOut = SLiteral("fclose(stdout);")


     /* val callPepper = SLiteral(
        """system(\"./run/run_pepper.sh %s > /dev/null\");
        """.stripMargin.format(cName))
        */

      val proofName = ".%s".format(cName)
      val callPepper = SLiteral(
       """
         | system("./bin/pepper_prover_%s prove %s.pk %s %s %s");
         | system("./bin/pepper_verifier_%s verify %s.vk %s %s %s");
       """.stripMargin.format(
         cName, cName, inputFileName, outputFileName, proofName,
         cName, cName, inputFileName, outputFileName, proofName
       ))

      val reopenStdIn =  SLiteral("freopen(\"prover_verifier_shared/%s\", \"r\", stdin);".format(outputFileName))
      val tmpCharArray = "__tmp" + nextCounter()
      val tmpdec = SLiteral("char %s[100];".format(tmpCharArray))
      val drop1 = SLiteral("scanf(\"%%s\", %s);".format(tmpCharArray))
      val outputStmts = output.flatMap(
        lval => {
          val iterStr = "__citer" + nextCounter
          typeToReadStatements(tyMap(lval), lval, iterStr)
        } )
      val closeStdIn = SLiteral("fclose(stdin);")

      (reopenStdOut +: inputStmts :+ closeStdOut) ++ Vector(callPepper, reopenStdIn) ++
        ((Vector(tmpdec, drop1) : Vector[Statement])
          ++ outputStmts :+ closeStdIn)
    }
    case SIf(b, ts, es) => Vector(SIf(b, ts.flatMap(rewriteStatement), es.flatMap(rewriteStatement)))
    case SWhile(c, b) => Vector(SWhile(c, b.flatMap(rewriteStatement)))
    case SLoop(pre,cond,post,body) => Vector(SLoop(pre, cond, post, body.flatMap(rewriteStatement)))
    case _ => Vector(stmt)
  }

  // takes the result of "compileForInterpreter" and rewrite it for GCC to consume
  def rewriteProg(prog : Program) : Program = {
    def changeDotToArrow(path : Vector[LValPath]) = {
      if (path.isEmpty)
        path
      else {
        path.head match {
          case LVPDot(c) => LVPArrow(c) +: path.tail
          case _ => path
        }
      }

    }
    def typeToPaths(ty : Type) : Vector[(Type, Vector[LValPath])] = ty match {
      case TStruct(name) => {
        prog.typeDefs.to[Vector].find(_.name==name).get.members.flatMap(
          {
            case (s, t@TStruct(_)) => for ((tt,p) <- typeToPaths(t)) yield (tt,LVPDot(s)+:p)
            case (s, t) => Vector((t, Vector(LVPDot(s))))
          }
        )
      }
    }

    val main = Utility.findMain(prog)
    val inputStructName = main.args(0)._2.asInstanceOf[TPtr].elemType.asInstanceOf[TStruct].name
    val outputStructName = main.args(1)._2.asInstanceOf[TPtr].elemType.asInstanceOf[TStruct].name

    val decs = Vector(
      SVarDec(main.args(0)._2, Vector(("input", None))),
      SVarDec(main.args(1)._2, Vector(("output", None)))
    )
    val inputMalloc = SLiteral("input = malloc(sizeof(struct %s));".format(inputStructName))
    val outputMalloc = SLiteral("output = malloc(sizeof(struct %s));".format(outputStructName))
    val inputStmts : Vector[Statement] = typeToPaths(TStruct(inputStructName)).flatMap(
      p => {
        val iterStr = "__tmp" + nextCounter()
        val lval = VIdent("input", changeDotToArrow(p._2))
        val typ = p._1
        typeToReadStatements(typ, lval, iterStr)
      })
    // read from stdin

    val callCompute = SLiteral("compute(input, output);")

    val openOutput = SLiteral("freopen(\"output\", \"w\", stdout);")

    // write to file "output"
    val outputStmts : Vector[Statement] = typeToPaths(TStruct(outputStructName)).flatMap(
      p => {
        val iterStr = "__tmp" + nextCounter()
        val lval = VIdent("output", changeDotToArrow(p._2))
        val typ = p._1
        typeToWriteStatements(typ, lval, iterStr)
      })
    val closStdout = SLiteral("fclose(stdout);")

    val binMainBody = decs ++ Vector(inputMalloc, outputMalloc) ++ inputStmts ++
      Vector(callCompute, openOutput) ++ outputStmts :+ closStdout

    val binMainFunc = FunctionDef("main", TInt(), Vector(), binMainBody)

    Program(prog.constantDefs, prog.funcDefs.map(
      {
        case FunctionDef(n,ret,args,body) => FunctionDef(n, ret, args, body.flatMap(rewriteStatement))
      }
    ) :+ binMainFunc,
      prog.typeDefs
    )
  }
}


