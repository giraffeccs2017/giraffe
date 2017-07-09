import peppermint.Analysis.CPrinter
import peppermint.CircuitCompilation._
import peppermint.ILAST._

object PythonStitchingV2 {
  def pureNameWithoutArray(lval : LValue) : String = {
    lval match {
      case VIdentSSA(l,v) => pureNameWithoutArray(l)+v
      case VIdent(n, path) => {
        val ss = path.foldLeft("")({
          case (s, LVPArrow(c)) => s + "_A_" + c
          case (s, LVPDeref()) => s + "D"
          case (s, LVPDot(c)) => s + "_D_" + c
        })
        n + ss
      }
      case VIdentName(n, idx) => {
        n
      }
    }
  }
  def binopToString(op : BinOp) : String = op match {
    case BAnd() => "and"
    case BOr() => "or"
    case _ => CPrinter.binopToString(op)
  }

  def shift(strs:Vector[String]) : Vector[String] = {
    strs.map(s => "  " + s)
  }
}

class PythonStitchingV2(comp:Vector[Statement],prog:FlattenArrayAccesses) {
  import PythonStitchingV2._

  val verifierStateName : String = "vs"
  val proverStateName : String = "ps"

  val vpStateInit : String = {
    val vals = prog.lvalToSizesVec.map({
      case (v, sz) => {
        val name = pureNameWithoutArray(v)
        val initVal = sz match {
          case Vector() => "0"
          case _ => {
            val ss = sz.product
            "[0] * %d".format(ss)
          }
        }
        "\"%s\" : %s".format(name, initVal)
      }}
    )
    val oneStrVals = vals.reduce((acc,str) => {
      acc + ",\n  " + str
    })

    """
      |%s = {
      |  %s
      |}
      |
      |%s = {
      |  %s
      |}
    """.stripMargin.format(
      verifierStateName,
      oneStrVals,
      proverStateName,
      oneStrVals
    )
  }

  val initStmt : Vector[String] = {
    prog.lvalToSizesVec.flatMap({
      case (v,sz) if v.name != "input" => Vector()
      case (v,sz) => Vector({
        // call read_input function to put initial data into V/P's state
        "read_input(%s, %s, %d)".format(lvalInV(v), lvalInP(v), sz.product)
      })
    })
  }

  val finalStmt : Vector[String] = {
    prog.lvalToSizesVec.flatMap({
      case (v,_) if v.name != "output" => Vector()
      case (v,sz) => Vector({
        "write_output(%s, %d)".format(lvalInV(v), sz.product)
      })
    })
  }

  def lvalInPVS(lval : LValue, prefix:String) : String = lval match {
    case x:VIdent => {
      "%s[\"%s\"]%s".format(
        prefix,
        pureNameWithoutArray(lval), "")
    }
    case x:VIdentName => {
      "%s[\"%s\"]%s".format(
        prefix,
        pureNameWithoutArray(x), x.idx.map("[%d]".format(_)).getOrElse(""))
    }
    case x:VIdentSSA => lvalInPVS(x.ident, prefix)
  }

  def lvalInP(lval:LValue) : String = lvalInPVS(lval, proverStateName)
  def lvalInV(lval:LValue) : String = lvalInPVS(lval, verifierStateName)

  def exprToString(expression : Expression, prefix:String) : String = expression match {
    case EConstant(i) => i.toString
    case ELValue(lval) => lvalInPVS(lval, prefix)
    case EBinOp(op, x, y) =>
      "(%s) %s (%s)".format(exprToString(x,prefix), binopToString(op), exprToString(y,prefix))
    case EUniOp(_:UANegate, x) => "(-(%s))".format(exprToString(x,prefix))
    case EUniOp(_:ULNegate, x) => "(not(%s))".format(exprToString(x,prefix))
    case EFetch(l,Vector(e)) => lvalInPVS(l, prefix) + "[%s]".format(exprToString(e,prefix))
  }

  def stmtsToString(stmts:Vector[Statement], prefix:String) : Vector[String] = {
    def tailRec(acc : Vector[String], rest : Vector[Statement]) :Vector[String] = {
      if (rest.isEmpty) {
        acc
      } else {
        val h = stmtToString(rest.head, prefix)
        val next = acc ++ h
        tailRec(next, rest.tail)
      }
    }
    tailRec(Vector(), stmts)
  }

  def stmtToString(stmt : Statement, prefix : String) : Vector[String] = stmt match {
    case SAssign(lval, rval) => {
      Vector(
        "%s = %s %% Defs.prime".format(lvalInPVS(lval, prefix), exprToString(rval, prefix))
      )
    }
    case SIf(b, ts, es) => {
      val ifs = "if %s:".format(exprToString(b, prefix))
      val elses = "else:"
      val pass = "pass"
      val tstr = shift(pass +: stmtsToString(ts, prefix))
      val estr = shift(pass +: stmtsToString(es, prefix))
      (ifs +: tstr) ++ (elses +: estr)
    }
    case SGrouped(v) => stmtsToString(v,prefix)
    case SForGrouped(v) => stmtsToString(v,prefix)
    case SCondStore(lval,Vector(e),v,cond) => {
      val cc : String = cond.map(x => exprToString(x,prefix)).getOrElse("True")
      val ifs = "if %s:".format(cc)
      val stmt = "  %s[%s] = %s".format(lvalInPVS(lval,prefix),
        exprToString(e,prefix),exprToString(v,prefix))
      Vector(ifs,stmt)
    }
    case SWhile(c,b) => {
      val whiles = "while %s:".format(exprToString(c,prefix))
      val pass = "pass"
      val bs = pass +: stmtsToString(b,prefix)
      whiles +: shift(bs)
    }
    case zo:SZebraOutsource => zebraoutsourceToString(zo)
  }

  def zebraoutsourceToString(zo:SZebraOutsource):Vector[String] = {
    // four things that we need here:
    // 1. make a function that generates all the input given the "original" input, using P's state
    // 2. call outsource_to_giraffe("pwsfile", copies, input)
    // 3. check the output from the call, using V's state
    // 4. read stuff into verifier state from the output

    def proverLocalStackName(lval : LValue) : String = lval match {
      case VIdentSSA(v:VIdentName,_) => {
        v.name + v.idx.map(x => x.toString).getOrElse("")
      }
    }

    def proverLocalStackExpr(expr : Expression) : String = expr match {
      case EConstant(x) => x.toString
      case ELValue(lval) => proverLocalStackName(lval)
      case EBinOp(op, x, y) => {
        "(%s) %s (%s)".format(proverLocalStackExpr(x), binopToString(op), proverLocalStackExpr(y))
      }
    }

    val inputSize = {
      zo.inputSet.size
    }

    val genInputFunc : Vector[String] = {
      val genInput : Vector[String] = {
        // first read V's input into P's local stack
        val vIntoP = zo.originalInputSet.toVector.map(v =>
              "%s = %s".format(proverLocalStackName(v), lvalInV(v))
        )
        val computationNeeded :Vector[String] = zo.pwis.flatMap({
          // use P's state for computation
          case PWIOOBC(x) => x match {
            case MemStore(l, Vector(idx), v, cond) => {
              Vector("if %s:".format(proverLocalStackName(cond)),
                "  %s[%s] = %s".format(lvalInP(l), proverLocalStackName(idx), proverLocalStackName(v))
              )
            }
            case MemUncondStore(l, Vector(idx), v) => Vector({
              "%s[%s] = %s".format(lvalInP(l), proverLocalStackName(idx), proverLocalStackName(v))
            })
            case MemFetch(w,l,Vector(i)) => Vector({
              "%s = %s[%s]".format(proverLocalStackName(w), lvalInP(l), proverLocalStackName(i))
            })
            case WireConst(w, v) => Vector({
              "%s = %s".format(proverLocalStackName(w), v.toString)
            })
            case WireCopy(n, o) => Vector("%s = %s".format(proverLocalStackName(n),proverLocalStackName(o)))
            case BinopOOBC(op, out, xx, y) => Vector({
              "%s = (%s) %s (%s)".format(proverLocalStackName(out),
                proverLocalStackName(xx),
                binopToString(op),
                proverLocalStackName(y)
              )
            })
          }
          case PWIAssn(SAssign(lval, rval)) => Vector({
            "%s = %s %% Defs.prime".format(proverLocalStackName(lval), proverLocalStackExpr(rval))
          })
        })
        val initRetVec : String = "tmp = [0] * %s".format(inputSize) // need input size
        val popRetVec : Vector[String] = zo.inputSet.zipWithIndex.map(
            x => {
              "tmp[%d] = %s".format(x._2, proverLocalStackName(x._1))
            }
        )
        val populateRetVec : Vector[String] = initRetVec +: popRetVec :+ "return tmp"
        // stitch everything together
        vIntoP ++ computationNeeded ++ populateRetVec
      }
      val funcHead : String = "def genInput%d():".format(zo.identity)
      val funcBody : Vector[String] = shift(genInput)
//      funcHead :+ funcBody
      val callGenInputFunc : String = "input_to_giraffe = genInput%d()".format(zo.identity)
      funcHead +: funcBody :+ callGenInputFunc
    }


    val getOutput : String =
      "output_from_giraffe = outsource_to_giraffe(\"%s\", %s, %s)".format(zo.pwsFileName,zo.numOfCopies, "input_to_giraffe")

    def findVidentSSAFromOutput(v:VIdentSSA):String = {
      "output_from_giraffe[%d]".format(zo.indexMapping(v))
    }

    def findVidentSSAFromInput(v:VIdentSSA):String = {
      "input_to_giraffe[%d]".format(zo.indexMapping(v))
    }

    val checkIO : Vector[String] = {
      zo.pwis.flatMap({
        case PWIAssn(_) => Vector()
        case PWIOOBC(w) => w match {
          case MemStore(l,Vector(i),v,c) => Vector(
            "if %s".format(findVidentSSAFromOutput(c)),
            "  %s[%s] = %s".format(lvalInV(l), findVidentSSAFromOutput(i), findVidentSSAFromOutput(v))
          )
          case MemUncondStore(l,Vector(i),v) => Vector(
            "%s[%s] = %s".format(lvalInV(l), findVidentSSAFromOutput(i), findVidentSSAFromOutput(v))
          )
          case MemFetch(w,l,Vector(i)) => Vector(
            "assert %s == %s[%s]".format(findVidentSSAFromInput(w),
              lvalInV(l), findVidentSSAFromOutput(i)
            ))
          case WireConst(w, v) => Vector("assert %s == %s".format(findVidentSSAFromInput(w),v.toString))
          case WireCopy(n,o) => Vector("assert %s == %s".format(findVidentSSAFromInput(n),findVidentSSAFromOutput(o)))
          case BinopOOBC(op, o, x, y) => Vector(
            "assert %s == %s %s %s".format(
              findVidentSSAFromOutput(o),
              findVidentSSAFromOutput(x),
              binopToString(op),
              findVidentSSAFromOutput(y)
            )
          )
        }
      })
    }

    val readOutputIntoLocalState : Vector[String] = {
      zo.originalOutputSet.toVector.map(v => {
        "%s = %s".format(lvalInV(v), findVidentSSAFromOutput(v))
      })
    }

    (genInputFunc :+ getOutput) ++ checkIO ++ readOutputIntoLocalState
  }
  val header =
    """
      |from ppmtLib import read_input, write_output, outsource_to_giraffe
      |from giraffelib.defs import Defs
      |
    """.stripMargin

  val ret : Vector[String] = Vector(header, vpStateInit , "\n\n\ndef main():") ++
    shift(initStmt ++ stmtsToString(comp,verifierStateName) ++ finalStmt)
}
