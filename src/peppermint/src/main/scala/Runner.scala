
package peppermint.Runner

import peppermint.ILAST._
import peppermint.Analysis._
import peppermint.Analysis.Utility._

import sys.process._
import scala.collection.immutable.Vector

object Runner {
  def fileToInts(file : String) : Vector[Int] = {
    scala.io.Source.fromFile(file).mkString.split("""\s+""").map( x =>
      if (x.length > 5) -1 else x.toInt).to[Vector]
  }
}

class Runner(prog : Program, inputFile : String) {
  // Assuming a "cleaned" program

  def defaultValueForType(ty : Type) : Value = ty match {
    case TInt() => VConst(0)
    case TArr(t, n) => VArray(n, Vector.fill(n)(defaultValueForType(t)))
    case TStruct(name) => {
      val tt = prog.typeDefs.find(_.name == name).get
      val m = tt.members.foldLeft[Map[String,Value]](Map())({
        case (acc, (n, t)) => acc.updated(n, defaultValueForType(t))
      })
      VStruct(m)
    }
    case TPtr(_) => VUnknown()
  }

  def newValueFromIntsLVal(ints : Vector[Int], oldVals : Vector[LValue]) : Vector[Value] = {
    oldVals.foldLeft((Vector():Vector[Value], ints))({
      case ((acc, s), lval) =>
        val (newS, v) = newValueFromInts(s, evalLValue(lval))
        (acc :+ v, newS)
    }
    )._1
  }

  def newValueFromInts(ints : Vector[Int], oldVal : Value) : (Vector[Int], Value) = {
    // returns rest of the ints and new value
    oldVal match {
      case VConst(_) => (ints.tail, VConst(ints.head))
      case VArray(n, vecv) =>
        val (rest, result) : (Vector[Int], Vector[Value]) = vecv.foldLeft((ints,Vector():Vector[Value]))({
          case ((s,acc), v) => {
            val (newS, newV) : (Vector[Int], Value) = newValueFromInts(s, v)
            (newS,  acc :+ newV)
          }
        })
        (rest, VArray(n, result))
    }
  }

  var env : Vector[Map[String, Value]] = Vector(Map())

  def executeStatements(stmts : Vector[Statement], frame  : Map[String,Value]= Map()) : Option[Value] = {
    env = frame +: env
    val ret = stmts.foldLeft[Option[Value]](None) {
      (acc, stmt) => acc match {
        case Some(_) => acc
        case None => executeStatement(stmt)
      }
    }
    env = env.tail
    ret
  }

  def valToVector(value : Value) : Vector[Int] = value match {
    case VConst(i) => Vector(i.toInt)
      // what about the order?
    case VStruct(m) => m.values.to[Vector].flatMap(valToVector)
    case VArray(_, vv) => vv.flatMap(valToVector)
  }

  def lvalToVector(lval : LValue) : Vector[Int] = {
    valToVector(evalLValue(lval))
  }


  def executeStatement(stmt : Statement) : Option[Value] = stmt match {
    case SBlank() => None
    case SAssign(lval, rval) => {
      val rr = evalExpression(rval)
      updateLValue(lval, rr)
      None
    }
    case SArithAssign(_, _, _) => sys.error("arithmetic assignments should be removed in cleaning phase")
    case SExpression(e) => {
      val ret = evalExpression(e)
      None
    }
    case SIf(b, ts, es) => {
      val bv = evalExpression(b)
      bv match {
        case VConst(v) if v == 0 => executeStatements(es)
        case _ => executeStatements(ts)
      }
    }
    case SLoop(_, _, _, _) => sys.error("loop constructs should be removed using cleaner class")
    case SWhile(cond, body) =>
      var ret :Option[Value]= None
      var loopc = true
      while(loopc) {
        val cv = evalExpression(cond)
        cv match {
          case VUnknown() => sys.error("bad loop " + cond)
          case VConst(v) if v == 0 => loopc = false
          case _ => executeStatements(body) match {
            case Some(v) => ret = Some(v); loopc = false
            case None =>
          }
        }
      }
      ret
    case SVarDec(t, Vector((n, oexpr))) =>
      val v = oexpr match {
        case Some(expr) => evalExpression(expr)
        case None => defaultValueForType(t)
      }
      val h = env.head.updated(n, v)
      env = env.updated(0, h)
      None
    case SReturn(Some(expr)) => Some(evalExpression(expr))
    case SReturn(None) => Some(VUnknown())
    case SOutsource(input, output, cfile, inputFileName, outputFile, _) => {
      println(env.last("@output"))
      //return None
      val inputVals = input.flatMap(lvalToVector)
      val inputStr = inputVals.foldLeft("")({
        (acc, n) => acc + "\n" + n.toString
      })
      Plumbing.writeStringToFile(inputStr, "bin/"+inputFileName)
      println("outsourcing....")
      ("./run/run_pepper.sh %s > /dev/null".format(cfile)) !
      val ints = Runner.fileToInts("bin/"+outputFile).drop(2) // Buffet will output a default '0' in the "head"
      newValueFromIntsLVal(ints, output).zip(output).foreach(
        x => updateLValue(x._2,x._1)
      )
      None
    }
    case _ => None
  }

  def executeFunction(functionDef : FunctionDef, args : Vector[Expression]) : Value = {
    if (functionDef.args.length != args.length) {
      sys.error("wrong number of inputs provided for function " + functionDef.funcName)
    }
    val vals = args.map(evalExpression)
    val newFrame = functionDef.args.map(_._1).zip(vals).toMap
    //    val decs : Vector[SVarDec] = functionDef.args.zip(args).map(x =>
    //      SVarDec(x._1._2, Vector((x._1._1, Some(x._2))))
    //    )
    //    val stmts = decs ++ functionDef.body
    val mr = executeStatements(functionDef.body, newFrame)
    mr getOrElse VUnknown()
  }

  def evalExpression(expr : Expression) : Value = expr match {
    case EConstant(x) => VConst(x)
    case ELValue(lval) => evalLValue(lval)
    case EBinOp(op, x, y) => {
      val ex = evalExpression(x)
      val ey = evalExpression(y)
      (ex, ey) match {
        case (VConst(xval), VConst(yval)) =>
          VConst(binopToFunction(op)(xval, yval))
      }
    }
    case EUniOp(UGetRef(), ELValue(v)) => VLValue(v)
    case EUniOp(op, ex) => {
      val v = evalExpression(ex)
      v match {
        case VConst(vp) => op match {
          case ULNegate() => VConst(if (vp == 0) 1 else 0)
          case UANegate() => VConst(-vp)
          case _ => sys.error("Type error " + op + " " + vp)
        }
        case VLValue(l) => op match {
          case UDeref() => {
            evalLValue(l)
          }
        }
        case VArray(_, av) => op match {
          case UDeref() => av(0)
        }
        case VStruct(_) => sys.error("type error")
      }
    }
    case ESelect(bin, x, y) => {
      val vv = evalExpression(bin)
      vv match {
        case VConst(v) if v == 0 => {
          evalExpression(y)
        }
        case _ => {
          evalExpression(x)
        }
      }
    }
    case EFunctionCall(ident, args) => {
      val func = prog.funcDefs.find(_.funcName == ident).get
      executeFunction(func, args.to[Vector])
    }
  }


  def updateLValue(lval : LValue, rval : Value) = {
    //println(lval, rval)
    def valTrace(value : Value, steps : Vector[LValPath]) : Value => Value = {
      steps.foldLeft(({(x:Value) => x},value))({
        case ((acc, cc), pe) => pe match {
          case LVPDot(c) => cc match {
            case VStruct(m) => val newCC = m(c); (acc compose {(newVal:Value) => VStruct(m.updated(c,newVal))}, newCC)
          }
          case LVPArray(x) => evalExpression(x) match {
            case VConst(i) => cc match {
              case VArray(n, vv) => val newCC = vv(i.toInt); (acc compose {(newVal:Value) => VArray(n, vv.updated(i.toInt,newVal))}, newCC)
            }
          }
        }
      })._1
    }

    def ridOfArrow(steps : Vector[LValPath]) : Vector[LValPath] = steps.foldLeft[Vector[LValPath]](Vector():Vector[LValPath]) {
      (acc, x) => x match {
        case LVPArrow(c) => acc ++ Vector(LVPDeref(), LVPDot(c))
        case _ => acc :+ x
      }
    }

    val (ln, steps) = lval match {
      case VIdent(n, s) => (n, ridOfArrow(s))
    }

    val (rpTmp, dpTmp) = steps.reverse.span( {
      case LVPDeref() => false
      case LVPArrow(_) => false
      case _ => true
    })

    val realPath = rpTmp.reverse
    val derefPath = dpTmp.reverse

    val derefed : VIdent = if (derefPath.isEmpty) lval.asInstanceOf[VIdent]
      else evalLValue(VIdent(ln, derefPath.init)) match {
      case VLValue(l@VIdent(n,pp)) => VIdent(n, pp ++ realPath)
    }
//    println(derefPath)
//    println("Original: ")
//    println(lval)
//    println("Derefed: ")
//    println(derefed)

    val (idx, v) = findEnvIndex(derefed.name, env)
    val bc = valTrace(v, derefed.path)(rval)
//    println(bc)
//    println("\n\n\n")

    val frame = env(idx).updated(derefed.name, bc)
    env = env.updated(idx, frame)
  }

  def evalLValue(lval : LValue) : Value = lval match {
    case VIdent(x, l) =>
      val (envIdx, v) = findEnvIndex(x, env)
      l.foldLeft(v) {
        case (vp, pe: LValPath) => pe match {
          case LVPDeref() => vp match {
            case VLValue(VIdent(n,_)) => {
              val (_, vll) = findEnvIndex(n, env)
              vll
            }
          }
          case LVPArrow(c) => vp match {
            case VLValue(ll) =>
              evalLValue(ll) match {
                case VStruct(m) => m(c)
//                case _ => println(ll); println(env); ???
              }
          }
          case LVPDot(c) => vp match {
            case VStruct(m) => m(c)
          }
          case LVPArray(i) => vp match {
            case VArray(_, arr) => {
              val ival = evalExpression(i)
              ival match {
                case VConst(ii) => {
                  if (ii >= arr.length) VConst(0)//{println(lval, ii); ???}
                  else arr(ii.toInt)
                }
              }
            }
          }

        }
      }
  }

  def typeToPaths(ty : Type) : Vector[Vector[LValPath]] = ty match {
    case TStruct(name) => {
      prog.typeDefs.to[Vector].find(_.name==name).get.members.flatMap(
        {
          case (s, t) => for (p <- typeToPaths(t)) yield LVPDot(s)+:p
        }
      )
    }
    case _ => Vector(Vector():Vector[LValPath]):Vector[Vector[LValPath]]
  }

  def lvalsForPtrTy(name : String, ty : TPtr) : Vector[LValue] = {
    typeToPaths(ty.elemType).map(VIdent(name, _))
  }

  def run(): Unit = {
    val main = prog.funcDefs.find(x => x.funcName == "compute").get
    val mainStmts = main.body
    val extras = Plumbing.pluginExtraVarDeclarationForMain(main)
    extras.foreach(executeStatement)
    val inputStruct = VIdent("@input",Vector())
    val inputPtrType : Type = main.args(0)._2
    val inputLVals = lvalsForPtrTy("@input", inputPtrType.asInstanceOf[TPtr])
    val inputInts = Runner.fileToInts(inputFile)
    newValueFromIntsLVal(inputInts, inputLVals).zip(inputLVals).foreach( {
      case (v, lvl) => updateLValue(lvl, v)
    })
    //println(env)
    main.body.foreach(executeStatement)
    val outputStruct = evalLValue(VIdent("@output", Vector()))
    println(outputStruct)
  }


}
