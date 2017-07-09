package peppermint.IntervalAnalysis
import scala.math.BigInt._
import peppermint.ILAST._
import peppermint.Analysis._
import sys.process._



class Interval(_a : BigInt, _b : BigInt) {
  import Interval._

  val a : BigInt = _a.max(negInfinity)
  val b : BigInt = _b.min(infinity)

  override def toString : String = {
    "[%d, %d]".format(a, b)
  }

  def +(that : Interval) : Interval = {
    new Interval(a + that.a, b + that.b)
  }

  def -(that: Interval) : Interval = {
    new Interval(a - that.b, b - that.a)
  }

  def *(x : Int) : Interval = {
    if (x >= 0)
      new Interval(x * this.a, x * this.b)
    else
      new Interval(x * this.b, x * this.a)
  }

  def *(that : Interval) : Interval = {
    val s = List(a*that.a,a*that.b,b*that.a,b*that.b)
    new Interval(s.min,s.max)
  }

  def intersect(that : Interval) : Option[Interval] = {
    (a > that.b, that.a > b) match {
      case (true, _) => None
      case (_, true) => None
      case _ => Some(new Interval(a.max(that.a), b.min(that.b)))
    }
  }

  def union(that : Interval) : Interval = {
    if (that.a > that.b)
      this
    else if(this.a > this.b)
      that
    else
      new Interval(a.min(that.a),b.max(that.b))
  }

  def /(that:Interval) : Interval = {
    assert(that.a == that.b && that.a != 0)
    new Interval(a/that.a, b/that.b)
  }

  def <(that:Interval) : Interval = {
    if (b < that.a) {
      new Interval(1,1)
    } else if (a >= that.b) {
      new Interval(0,0)
    } else {
      new Interval(0,1)
    }
  }

  def >(that:Interval) : Interval = {
    if (a > that.b) {
      new Interval(1,1)
    } else if (b <= that.a) {
      new Interval(0,0)
    } else {
      new Interval(0,1)
    }
  }

  def <=(that : Interval) : Interval = {
    if (b <= that.a) {
      new Interval(1,1)
    } else if (that.b < a) {
      new Interval(0,0)
    } else {
      new Interval(0,1)
    }
  }


  def >=(that : Interval) : Interval = {
    if (a >= that.b) {
      new Interval(1,1)
    } else if (b < that.a) {
      new Interval(0,0)
    } else {
      new Interval(0,1)
    }
  }

  def !=(that : Interval) : Interval = {
    this.intersect(that) match {
      case None => new Interval(1,1)
      case _ => new Interval(0,1)
    }
  }

  def ==(that:Interval) : Interval = {
    this.intersect(that) match {
      case None => new Interval(0,0)
      case _ => new Interval(0,1)
    }
  }

  def contains(i : BigInt) : Boolean = {
    a <= i && i <= b
  }
}

object Interval {
  val infinity : BigInt = 999999999
  val negInfinity : BigInt = -infinity
  def defaultInterval : Interval = new Interval(negInfinity, infinity)
  def constantInterval(a:BigInt) : Interval = new Interval(a, a)
}

case class ERandom() extends Expression
case class EBRandom() extends Expression
case class SPrint(tag : String, expr : Expression) extends Statement

object InterProcPrinter {
  // assume all lvalues are only scalars, i.e., no struct references

  def printlval(lval : LValue) : String = lval match {
    case VIdent(n, _) => n
  }

  def printExpr(expr : Expression) : String = expr match {
    case ERandom() => "random"
    case EBRandom() => "brandom"
    case EConstant(v) => v.toString()
    case EBinOp(op, x, y) => {
      val ops = op match {
        case BAnd() => "and"
        case BOr() => "or"
        case BArithOp(BDiv()) => "/_i,0" // see interproc documentation....
        case _ => CPrinter.binopToString(op)
      }
      "(%s) %s (%s)".format(printExpr(x), ops, printExpr(y))
    }
    case EUniOp(op, x) => op match {
      case UANegate() => "-(%s)".format(printExpr(x))
      case ULNegate() => "not(%s)".format(printExpr(x))
    }
    case ELValue(lval) => printlval(lval)
  }

  def printStatements(stmts:Vector[Statement]) : String = {
    if (stmts.isEmpty)
      "skip;"
    else
      stmts.map(printStatement).reduce((x,y)=>x+y)
  }

  def printStatement(stmt: Statement) : String = stmt match {
    case SExpression(_) => "skip;"
    case SBlank() => "skip;"
    case SAssign(lval, rval) => "%s = %s;\n".format(printlval(lval),printExpr(rval))
    case SIf(bool, ts, es) =>
      "if (%s) then\n%s\nelse\n%s\nendif;\n".format(printExpr(bool),printStatements(ts),printStatements(es))
    case SWhile(cond,b) => "while (%s) do\n %s\ndone;\n".format(printExpr(cond),printStatements(b))
    case SGrouped(stmts) => printStatements(stmts)
    case SPrint(tag,expr) => "print %s %s;\n".format(tag, printExpr(expr))
    case SVarDec(typ, Vector((n,None))) => "skip;"
    case SVarDec(typ, Vector((n,Some(expr)))) => "%s = %s;\n".format(n, printExpr(expr))
  }

  def printDecs(decs : Vector[String]) : String = {
    println(decs)
    if (decs.isEmpty) "" else {
      val dl = decs.map(s => s + ": int")
      val dli: String = dl.reduce((x, y) => x + ", " + y)
      "var %s;".format(dli)
    }
  }

  def print(decs : Vector[String], chunk:Vector[Statement]) : String = {
    "%s\nbegin\n%s\nend\n".format(printDecs(decs),printStatements(chunk))
  }
}

object ReadInterprocOutput {
  def toBigInt(str : String, roundUp : Boolean) : BigInt = {
    val ll = str.split("/")
    if (ll.length == 2) {
      val num = BigInt(ll(0))
      val denom = BigInt(ll(1))
      if (denom == 0) {
        if (num > 0)
          Interval.infinity
        else
          Interval.negInfinity
      } else {
        num / denom + (if (roundUp) 1 else 0)
      }
    } else {
      BigInt(ll(0))
    }
  }


  def toInterval(inf: String, sup : String) : Interval = {
    new Interval(toBigInt(inf,true),toBigInt(sup,false))
  }

  def read(file:String) : Map[String, Interval] = {
    var ret : Map[String, Interval] = Map()
    val lines = scala.io.Source.fromFile(file).getLines()
    lines.foreach(str => {
      val f = str.split(" ")
      if (f.length == 3) {
        val tag = f(0)
        val infs = f(1)
        val sups = f(2)
        ret = ret.updated(tag, toInterval(infs,sups))
      }
    })
    ret
  }
}

object LValueToInterprocVarString {
  def convert(lval:LValue) : String = lval match {
    case VIdent(n,path) => {
      n + path.foldLeft("")(
        (s,p) => p match {
          case LVPArray(_) => s+"AA"
          case LVPArrow(c) => s+"Arr"+c
          case LVPDot(c) => s+"Dot"+c
          case LVPDeref() => s+"Deref"
        }
      )
    }
  }
}

object CollectVarDecs {
  def collectOne(stmt : Statement) : Vector[String] = stmt match {
    case SBlank() => Vector()
    case SExpression(_) => Vector()
    case SAssign(_,_) => Vector()
    case SIf(_, ts, es) => collect(ts) ++ collect(es)
    case SWhile(_, b) => collect(b)
    case SGrouped(b) => collect(b)
    case SVarDec(ty, Vector((n,_))) => ty match {
      case TInt() => println(n);Vector(n)
      case _ => Vector()
    }
  }

  def collect(chunk : Vector[Statement]) : Vector[String] = chunk.flatMap(collectOne)
}


class InterProcRewriter(chunk : Vector[Statement]) {
  // replace all array accesses with a corresponding (PRINT tag index)
  // also rewrites all struct accesses with a scalar
  var ret : Map[String, LValue] = Map()

  private var counter : Int = 0
  private def nextCounter : String = {
    counter+=1
    "t"+counter
  }

  def isArrAccess(lval:LValue) : Boolean = lval match {
    case VIdent(_, p) => p.foldLeft(false)(
      (b, l) => l match {
        case LVPArray(_) => true
        case _ => b
      }
    )
  }


  def splitArrAccess(lval : LValue) : Vector[(LValue, Expression)] = {
    val (h,t) = Utility.spanArrIndex(lval)
    val tc : Vector[(LValue,Expression)] = t.tail.filter({
      case LVPArray(_) => true
      case _ => false
    }).asInstanceOf[Vector[LVPArray]].flatMap(x=>collectArrAccess(x.index))
    val expr = t.head.asInstanceOf[LVPArray].index
    val ce = collectArrAccess(expr)
    val texpr = if (ce.isEmpty) {
      expr
    } else {
      ERandom()
    }
    (h,texpr) +: (ce ++ tc)
  }

  def collectArrAccess(lval : LValue): Vector[(LValue,Expression)] = {
    if (isArrAccess(lval)) {
      splitArrAccess(lval)
    } else {
      Vector()
    }
  }

  def collectArrAccess(expr : Expression) : Vector[(LValue,Expression)] = expr match {
    case EConstant(_) => Vector()
    case EUniOp(_, e) => collectArrAccess(expr)
    case EBinOp(_,x,y) => collectArrAccess(x) ++ collectArrAccess(y)
    case ELValue(lval) => collectArrAccess(lval)
  }

  def generatePrintStmt(ps:Vector[(LValue,Expression)]) :Vector[SPrint] = {
    ps.map(p => {
      val x = nextCounter
      ret = ret.updated(x, p._1)
      SPrint(x, p._2)
    })
  }

  def rewriteExpr(expr:Expression) : Expression = expr match {
    case EConstant(_) => expr
    case ELValue(lval) => ELValue(rewriteLVal(lval))
    case EUniOp(op,x) => EUniOp(op,rewriteExpr(expr))
    case EBinOp(op,x,y) => EBinOp(op,rewriteExpr(x),rewriteExpr(y))
  }


  def rewriteLVal(lval: LValue) : LValue = VIdent(LValueToInterprocVarString.convert(lval),Vector())

  def rewriteStmt(stmt : Statement) : Vector[Statement] = stmt match {
    case SExpression(_) => Vector()
    case SBlank() => Vector()
    case SAssign(lval, rval) => {
      val vl = collectArrAccess(lval)
      val vr = collectArrAccess(rval)
      val vaa = vl ++ vr
      val pstmts = generatePrintStmt(vaa)
      val tt = (vl.isEmpty, vr.isEmpty) match {
        case (true, true) => SAssign(rewriteLVal(lval), rewriteExpr(rval))
        case (true, false) => SAssign(rewriteLVal(lval), ERandom())
        case (false, true) => SBlank()
        case (false, false) => SBlank()
      }
      pstmts :+ tt
    }
    case SIf(b, ts, es) => {
      val vb = collectArrAccess(b)
      val pvb = generatePrintStmt(vb)
      val newB = if (vb.isEmpty) rewriteExpr(b) else EBRandom()
      pvb :+ SIf(newB, rewriteStmts(ts), rewriteStmts(es))
    }
    case SWhile(b, body) => {
      val vb = collectArrAccess(b)
      val pvb = generatePrintStmt(vb)
      val newB = if (vb.isEmpty) rewriteExpr(b) else EBRandom()
      pvb :+ SWhile(newB, rewriteStmts(body))
    }
    case SGrouped(body) => rewriteStmts(body)
    case SVarDec(_,Vector((n,Some(e)))) => rewriteStmt(SAssign(VIdent(n,Vector()),e))
    case SVarDec(_,_) => Vector()
  }

  def rewriteStmts(stmts:Vector[Statement]) : Vector[Statement] = stmts.flatMap(rewriteStmt)


  def rewrite() : (Map[String, LValue], Vector[Statement]) = {
    val x = rewriteStmts(chunk)
    (ret, x)
  }
}


class InterProcAnalyzer(chunk : Vector[Statement],
                       entryEnv : Map[String, BigInt],
                       readSet : Set[LValue],
                       writeSet : Set[LValue]) {

  def run() : Map[LValue, Interval] = {
    val varDecs = entryEnv.to[Vector].map(
      x => SVarDec(TInt(), Vector((x._1,Some(EConstant(x._2)))))
    )
    val prog = varDecs ++ chunk
    val (map, newChunk) = new InterProcRewriter(prog).rewrite()
    val cdecs = CollectVarDecs.collect(prog)
    val interProcStr = InterProcPrinter.print(cdecs, newChunk)
    val fileName = "interproc.txt"
    val outputName = "interproc.output"
    Plumbing.writeStringToFile(interProcStr, fileName)
    Seq("sh","-c","./interproc.opt %s > %s".format(fileName, outputName)).!
    val ret = ReadInterprocOutput.read(outputName)
    println(ret)
    println(map)
    val result = map.toVector.map(
    {
      case (str, lval) => (lval, ret(str))
    }
    ).foldLeft(Map():Map[LValue,Interval])(
      (acc, lvint) => {
        val cint = acc.getOrElse(lvint._1, lvint._2)
        acc.updated(lvint._1, lvint._2.union(cint))
      }
    )
    println(result)
    result
  }
}


class IntervalAnalysis(chunk : Vector[Statement],
                       entryEnv : Map[String, BigInt],
                       readSet : Set[LValue],
                       writeSet : Set[LValue]) {

  val ioset = readSet.union(writeSet)
  var ret : Map[LValue, Interval] = Map()

  type IntervalMap = Vector[Map[String, Interval]]


  def registerAccess(x : LValue, interval : Interval) = {
    ret = ret.get(x).map(int => ret.updated(x, int.union(interval))) getOrElse ret.updated(x, interval)
  }

  //  class Accounting {
  //    // only record the first dimension here
  //    var readMap : Map[LValue, Interval] = Map()
  //    var writeMap : Map[LValue, Interval] = Map()
  //    var tentWriteMap : Map[LValue, Interval] = Map()
  //    var declared : Map[String, Type] = Map()
  //    var lvalToType : Map[LValue, Type] = Map()
  //  }

  def findIntervalIdx(name : String, im : IntervalMap) : (Int, Interval) = {
    val i = im.indexWhere(_.contains(name))
    if (i == -1) {
      sys.error("name not found %s".format(name))
    }
    (i, im(i)(name))
  }

  def evalExpression(imap : IntervalMap, expr : Expression) : Interval = expr match {
    case ELValue(l) => evalLValue(imap, l)
    case EConstant(c) => Interval.constantInterval(c)
    case EUniOp(op, x) => {
      val i = evalExpression(imap, x)
      op match {
        case UANegate() => i * (-1)
        case ULNegate() =>
          if (i.a >= 0 && i.b <= 1) {
            new Interval(1-i.b,1-i.a)
          } else {
            i
          }
      }
    }
    case EBinOp(bop, x, y) => {
      val xx = evalExpression(imap, x)
      val yy = evalExpression(imap, y)
      bop match {
        case BArithOp(aop) => aop match {
          case BPlus() => xx + yy
          case BMinus() => xx - yy
          case BMul() => xx * yy
          case BDiv() => xx / yy
          case BMod() => ???
        }
        case BNEqual() => xx != yy
        case BEqual() => xx == yy
        case BLT() => xx < yy
        case BLE() => xx <= yy
        case BGT() => xx > yy
        case BGE() => xx >= yy
        case BAnd() =>
          if (xx.a == 1 && xx.b == 1) {
            yy
          } else if (yy.a == 1 && yy.b == 1) {
            xx
          } else if (xx.a == 0 && xx.b == 0) {
            xx
          } else if (yy.a == 0 && yy.b == 0) {
            yy
          } else {
            xx
          }
        case BOr() =>
          if (xx.a == 1 && xx.b == 1) {
            xx
          } else if (yy.a == 1 && yy.b == 1) {
            yy
          } else if (xx.a == 0 && xx.b == 0) {
            yy
          } else if (yy.a == 0 && yy.b == 0) {
            xx
          } else {
            xx
          }

      }
    }
    case ESelect(_,_,_) => ???
    case EFunctionCall(_,_) => ???
  }

  def evalLValue(imap : IntervalMap, x : LValue) : Interval = x match {
    case VIdent(s, Vector()) => {
      findIntervalIdx(s, imap)._2
    }
    case VIdent(s, vec) => {
      val (xx, p) = Utility.spanArrIndex(x)
      if (ioset.contains(xx)) {
        p.headOption.foreach({
          case LVPArray(expr) => {
            registerAccess(xx, evalExpression(imap, expr))
          }
        })
      }
      Interval.defaultInterval
    }
  }

  def updateInterval(imap : IntervalMap, x : LValue, int : Interval) : IntervalMap = x match {
    case VIdent(s, Vector()) => {
      val (idx, _) = findIntervalIdx(s, imap)
      imap.updated(idx, imap(idx).updated(s, int))
    }
    case VIdent(s, vec) => {
      val (xx, p) = Utility.spanArrIndex(x)
      if (ioset.contains(xx)) {
        p.headOption.foreach({
          case LVPArray(expr) => {
            registerAccess(xx, evalExpression(imap, expr))
          }
        })
      }
      imap
    }
  }

  def executeStatements(imap : IntervalMap, stmts : Vector[Statement]) : IntervalMap = {
    val empty : Map[String,Interval] = Map()
    stmts.foldLeft(empty+:imap)(
      (im, stmt) => executeStatement(im, stmt)
    ).tail
  }

  def executeStatement(imap : IntervalMap, stmt : Statement) : IntervalMap = stmt match {
    case SBlank() => imap
    case SGrouped(stmts) => executeStatements(imap, stmts)
    case SAssign(lval, rval) => updateInterval(imap, lval, evalExpression(imap, rval))
    case SExpression(_) => imap // assuming no function calls
    case SIf(b, ts, es) => {
      val bi = evalExpression(imap, b)
      if (bi.a == 1 && bi.b == 1) {
        executeStatements(imap, ts)
      } else if (bi.a == 0 && bi.b == 0) {
        executeStatements(imap, es)
      } else {
        val timap = executeStatements(imap, ts)
        val eimap = executeStatements(imap, es)
        // TODO: merge the two vectors
//        println(b)
//        println(imap)
//        println("\n\n\n")
        timap.zip(eimap).map({
          case (m1:Map[String,Interval],m2:Map[String,Interval]) =>
            m1.toList.zip(m2.toList).map({
              case (x1, x2) => {(x1._1, x1._2.union(x2._2))}
            }).foldLeft[Map[String,Interval]](Map())((acc,x) => acc.updated(x._1,x._2))
        })
      }
    }
    case SWhile(cond, body) => {
      var loopc = true
      var ret = imap
      while (loopc) {
        val ci = evalExpression(ret, cond)
        if (ci.a == 0 && ci.b == 0) {
          loopc = false
        } else if (ci.a == 1 && ci.b == 1) {
          ret = executeStatements(ret, body)
        } else {
          sys.error("bad loop condition %s\n%s".format(cond, ret))
        }
      }
      ret
    }
    case SVarDec(t, Vector((n,oexpr))) => t match {
      case TInt() => {
        val it = oexpr.map(evalExpression(imap,_)) getOrElse Interval.defaultInterval
        imap.updated(0, imap(0).updated(n, it))
      }
      case _ => imap
    }
  }

  def run() : Map[LValue, Interval] = {
    println(entryEnv)
    val imap : IntervalMap = Vector(entryEnv.mapValues(Interval.constantInterval))
    executeStatements(imap, chunk)
    ret
  }
}
