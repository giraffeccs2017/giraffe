import peppermint.ILAST._
import peppermint.Analysis._
import peppermint.CircuitCompilation._



class FindCallbacks(comp : Vector[Statement]) {
  var outsource : Vector[SZebraOutsource] = Vector()
  var vCallbacks : Map[Int, Vector[Statement]] = Map()
  var initStatements : Vector[Statement] = constructCallBack(comp)
  var terminates : Map[Int, Boolean] = Map()

  def terminates(stmts : Vector[Statement]) : Boolean = stmts.foldLeft(false)((b,s)=>b || terminates(s))

  def terminates(stmt : Statement) : Boolean = {
    terminates.get(stmt.identity) match {
      case Some(x) => x
      case None => {
        val ret = stmt match {
          case _:SAssign => false
          case SGrouped(v) => terminates(v)
          case SIf(_,ts,es) => {
            val tt = terminates(ts)
            val ee = terminates(es)
            tt && ee
          }
          case SForGrouped(v) => terminates(v)
          case _:SZebraOutsource => true
          case _:SCondStore => false
          case _:SBlank => false
          case _:SExpression => false
        }
        terminates = terminates.updated(stmt.identity,ret)
        ret
      }
    }
  }


  def constructCallBack(input : Vector[Statement]) : Vector[Statement] = {
    def tailRec(acc : Vector[Statement], vec : Vector[Statement]) : Vector[Statement] = {
      if (vec.isEmpty)
        Vector()
      else if (terminates(vec.head))
        Vector(vec.head)
      else
        tailRec(acc :+ vec.head, vec.tail)
    }
    tailRec(Vector(),input)
  }

  def dfs(vec : Vector[Statement], post : Vector[Statement]) : Unit = {
    vec.tails.filter(_.nonEmpty).foreach(
      x => {
        x.head match {
          case y:SZebraOutsource => {
            outsource = outsource :+ y
            val callback = constructCallBack(x.tail ++ post)
            vCallbacks = vCallbacks.updated(y.identity, callback)
          }
          case SGrouped(v) => {
            if (terminates(x)) dfs(v, x.tail ++ post)
          }
          case SIf(b,ts,es) => {
            val pp = x.tail ++ post
            dfs(ts,pp)
            dfs(es,pp)
          }
          case SForGrouped(vv) => {
            if (terminates(x)) dfs(vv, x.tail++post)
          }
          case _:SAssign => ()
          case _:SCondStore => ()
          case _:SBlank => ()
          case _:SExpression => ()
        }
      }
    )
  }
}

object GenPythonCode {
  def binopToString(op : BinOp) : String = op match {
    case BAnd() => "and"
    case BOr() => "or"
    case _ => CPrinter.binopToString(op)
  }


  def pureNameWithoutArray(lval : LValue) : String = {
    lval match {
      case VIdentSSA(l,v) => pureNameWithoutArray(l)+v
      case VIdent(n, path) => {
        val ss = path.foldLeft("")({
          case (s, LVPArrow(c)) => s + "Arrow" + c
          case (s, LVPDeref()) => s + "deref"
          case (s, LVPDot(c)) => s + "Child" + c
        })
        n + ss
      }
      case VIdentName(n, idx) => {
        n
      }
    }
  }

  def exprToString(expr : Expression, lfun : LValue=>String = lvalToClassMem) : String = {
    expr match {
      case ELValue(lval) => lfun(lval)
      case EBinOp(op,x,y) =>
          "(%s) %s (%s)".format(exprToString(x,lfun), binopToString(op), exprToString(y,lfun))
      case EUniOp(_:UANegate, x) => "(-(%s))".format(exprToString(x,lfun))
      case EUniOp(_:ULNegate, x) => "(not(%s))".format(exprToString(x,lfun))
      case EFetch(l,Vector(e)) => lvalToClassMem(l) + "[%s]".format(exprToString(e,lfun))
    }
  }

  def ssaName(vIdentSSA: VIdentSSA):String = {
    pureNameWithoutArray(vIdentSSA.ident) + vIdentSSA.version
  }

  def lvalToClassMem(lval : LValue) : String = {
    val prefix = "self."
    val (pure,Vector(LVPArray(e))) = Utility.spanArrIndex(lval)
    prefix + pureNameWithoutArray(pure) + "[%s]".format(exprToString(e))
  }

  def shift(strs:Vector[String]) : Vector[String] = {
    strs.map(s => "  " + s)
  }

  def stmtsToString(stmts:Vector[Statement]) : Vector[String] = {
    def tailRec(acc : Vector[String], rest : Vector[Statement]) :Vector[String] = {
      if (rest.isEmpty) {
        acc
      } else {
        val h = stmtToString(rest.head)
        val next = acc ++ h
        rest.head match {
          case _:SZebraOutsource => next
          case _ => tailRec(next, rest.tail)
        }
      }
    }
    tailRec(Vector(), stmts)
  }

  def stmtToString(stmt : Statement) : Vector[String] = stmt match {
    case SAssign(l,r) => {
      Vector("%s = %s %% Defs.prime".format(lvalToClassMem(l), exprToString(r)))
    }
    case SIf(b,t,e) => {
      val ifs = "if %s:".format(exprToString(b))
      val elses = "else:"
      val pass = "pass"
      val tstr = shift(pass +: stmtsToString(t))
      val estr = shift(pass +: stmtsToString(e))
      (ifs +: tstr) ++ (elses +: estr)
    }
    case SGrouped(v) => stmtsToString(v)
    case SCondStore(lval,Vector(e),v,cond) => {
      val cc : String = cond.map(x => exprToString(x)).getOrElse("True")
      val ifs = "if %s:".format(cc)
      val stmt = "  %s[%s] = %s".format(lvalToClassMem(lval),exprToString(e),exprToString(v))
      Vector(ifs,stmt)
    }
    case SWhile(c,b) => {
      val whiles = "while %s:".format(exprToString(c))
      val pass = "pass"
      val bs = pass +: stmtsToString(b)
      whiles +: shift(bs)
    }
    case zo:SZebraOutsource => {
      // return identity and a list of values, aka next input
      val origInputVec = zo.originalInputSet.toVector
      val tmp = "tmp = []"
      val inserts = origInputVec.map(vssa => "tmp.append(%s)".format(lvalToClassMem(vssa)))
      val ret = "return (%d, tmp)".format(zo.identity)
      tmp +: inserts :+ ret
    }
  }
}

class ProverInputGen(zo : SZebraOutsource) {
  import GenPythonCode._
  private val inputFromVerifier = "input"
  private val nonDeterm :String = "result"

  private val initCode : Vector[String] = {
    val inputVec = zo.originalInputSet.toVector
    inputVec.zipWithIndex.map({
      case (ssa, i) => "%s = %s[%s]".format(ssaName(ssa), inputFromVerifier, i)
    })
  }

  private val genInputCode : Vector[String] = zo.pwis.flatMap {
    case PWIAssn(SAssign(l,r)) =>
      Vector("%s = %s".format(pureNameWithoutArray(l),
        GenPythonCode.exprToString(r,pureNameWithoutArray)))
    case PWIOOBC(oobc) => oobc match {
      case MemStore(l,Vector(e),v,cond) => {
        val ifs = "if %s:".format(pureNameWithoutArray(cond))
        val assn = "  %s[%s] = %s".format(lvalToClassMem(l), pureNameWithoutArray(e),
          pureNameWithoutArray(v))
        Vector(ifs,assn)
      }
      case MemFetch(w,l,Vector(e)) => {
        Vector("%s = %s[%s]".format(pureNameWithoutArray(w),lvalToClassMem(l),pureNameWithoutArray(e)))
      }
      case WireConst(w,i) => {
        Vector("%s = %s".format(pureNameWithoutArray(w),i.toString()))
      }
      case wc:WireCopy => {
        Vector("%s = %s".format(pureNameWithoutArray(wc.newWire),
          pureNameWithoutArray(wc.oldWire)))
      }
      case boobc:BinopOOBC => {
        Vector("%s = %s %s %s".format(
          pureNameWithoutArray(boobc.output),
          pureNameWithoutArray(boobc.x),
          binopToString(boobc.op),
          pureNameWithoutArray(boobc.y)))
      }
    }
  }

  private val putInputIntoPlace : Vector[String] = {
    val inputVec = zo.inputSet.toVector
    inputVec.map(
      v => {
        val ind = zo.indexMapping(v)
        "%s[%d] = %s".format(nonDeterm,ind,pureNameWithoutArray(v))
      }
    )
  }

  val funBody = {
    initCode ++ genInputCode ++ putInputIntoPlace :+ "return %s".format(nonDeterm)
  }
}

class GenVerifierCheck(zo : SZebraOutsource) {
  import GenPythonCode._

  private val prevInput : String = "prevInput"
  private val prevOutput : String = "prevOutput"

  private def getPrevOutput(ov:VIdentSSA) : String = {
    prevOutput + zo.indexMapping(ov)
  }
  private def getPrevInput(iv:VIdentSSA) : String = {
    prevInput + zo.indexMapping(iv)
  }

  val checkIO : Vector[String] = zo.pwis.flatMap {
    case PWIAssn(_) => Vector()
    case PWIOOBC(oobc) => oobc match {
      case MemStore(l, Vector(e), v, cond) => {
        val ifs = "if %s:".format(getPrevOutput(cond))
        val assn = "  %s[%s] = %s".format(lvalToClassMem(l),
          getPrevOutput(e),
          getPrevOutput(v))
        Vector(ifs,assn)
      }
      case MemUncondStore(l,Vector(e),v) => {
         Vector("%s[%s] = %s".format(lvalToClassMem(l),
          getPrevOutput(e),
          getPrevInput(v)))
      }
      case MemFetch(wire,l,Vector(e)) => {
        Vector("assert %s[%s] == %s".format(lvalToClassMem(l),
          getPrevOutput(e),
          getPrevInput(wire)))
      }
      case wc:WireCopy => {
        Vector("assert %s == %s".format(getPrevOutput(wc.oldWire), getPrevInput(wc.newWire)))
      }
      case wc:WireConst => {
        Vector("assert %s == %s".format(getPrevInput(wc.wire),wc.value.toString()))
      }
      case boobc:BinopOOBC => {
        Vector("assert %s == %s %s %s".format(
          getPrevInput(boobc.output),
          getPrevOutput(boobc.x),
          binopToString(boobc.op),
          getPrevOutput(boobc.y)))
      }
    }
  }
}


class GenPythonCode(comp : Vector[Statement], prog : FlattenArrayAccesses) {
  import GenPythonCode._
  val fcb = new FindCallbacks(comp)

  val initCode : Vector[String] = stmtsToString(fcb.initStatements)

  val vCallbackCodes : Vector[(Int,Vector[String])] = fcb.vCallbacks.toVector.map({
    case (zoIdx,rest) => {
      val restStr = stmtsToString(rest)
      val zo = fcb.outsource.find(_.identity == zoIdx).get
      val checker = new GenVerifierCheck(zo).checkIO
      (zoIdx,checker ++ restStr)
    }}
  )
  val pCallbackCodes : Vector[(Int,Vector[String])] = fcb.outsource.map(
    x => (x.identity, new ProverInputGen(x).funBody)
  )
}
