import peppermint.ILAST._
import peppermint.Analysis._

object GroupLocal {
  def groupLocal(stmts : Vector[Statement]) : Vector[Statement] = {
    def isLocal(stmt : Statement) : Boolean = stmt match {
      case SOut(_) => false
      case _ => true
    }
    val (loc, r) = stmts.span(isLocal)
    val t = SGrouped(loc)
    if (r.isEmpty) {
      Vector(t)
    } else {
      Vector(t, r.head) ++ groupLocal(r.tail)
    }
  }
}

class FlattenArrayAccesses(prog : Program) {
  val main = Utility.findMain(prog)
  val inputTypeName = main.args.head match {
    case (_, TPtr(TStruct(n))) => n
  }
  val outputTypeName = main.args.last match {
    case (_, TPtr(TStruct(n))) => n
  }

  val inputMembers = prog.typeDefs.find(_.name == inputTypeName).get.members
  val outputMembers = prog.typeDefs.find(_.name == outputTypeName).get.members


  private var tmp : Map[VIdent, Vector[Int]] = Map()

  val (inputSize, outputSize, lvalToSizes, lvalToSizesVec) : (Int, Int, Map[VIdent,Vector[Int]], Vector[(VIdent, Vector[Int])]) = {
    val vinput : Vector[(VIdent, Vector[Int])]=
    inputMembers.map({
      case (str,typ) => {
        val vi = typeToVInt(typ)
        tmp = tmp.updated(VIdent("input",Vector(LVPArrow(str))), vi)
        (VIdent("input",Vector(LVPArrow(str))), vi)
      }
    })
    val voutput : Vector[(VIdent, Vector[Int])] = outputMembers.map({
      case (str,typ) => {
        val vi = typeToVInt(typ)
        tmp = tmp.updated(VIdent("output",Vector(LVPArrow(str))), vi)
        (VIdent("output",Vector(LVPArrow(str))), vi)
      }
    })


    dfs(main.body)
    val tt = tmp.filterKeys(v => v.name != "output" && v.name != "input")
    (vinput.length, voutput.length, tmp, vinput ++ voutput ++ tt.toVector)
  }



  val newMainBody : Vector[Statement] = main.body.map(rewriteStmt)

  def rewriteExpr(expr : Expression) : Expression = expr match {
    case ELValue(lval) => ELValue(rewriteLValue(lval))
    case EConstant(_) => expr
    case EBinOp(op,x,y) => EBinOp(op, rewriteExpr(x),rewriteExpr(y))
    case EUniOp(op,x) => EUniOp(op,rewriteExpr(x))
    case ESelect(b,x,y) => ESelect(rewriteExpr(b),rewriteExpr(x),rewriteExpr(y))
  }

  def rewriteStmt(stmt : Statement) : Statement = stmt match {
    case SAssign(x, y) => SAssign(rewriteLValue(x), rewriteExpr(y))
    case SExpression(e) => SExpression(rewriteExpr(e))
    case SGrouped(b) => SGrouped(b.map(rewriteStmt))
    case SOut(b) => SOut(b.map(rewriteStmt))
    case SWhile(c,b) => SWhile(rewriteExpr(c),b.map(rewriteStmt))
    case SIf(b,ts,es) => SIf(rewriteExpr(b), ts.map(rewriteStmt), es.map(rewriteStmt))
    case SBlank() => SBlank()
    case SVarDec(_,_) => stmt
  }

  def rewriteLValue(lval : LValue) : LValue = {
    val (l:VIdent, indsTmp) = Utility.spanArrIndex(lval)
    if (indsTmp.isEmpty)
      lval
    else {
      val inds = indsTmp.asInstanceOf[Vector[LVPArray]].map(_.index)
      lvalToSizes.get(l) match {
        case None => lval
        case Some(vi) => {
          val expr: Expression = inds.zip(vi).init.foldRight[(Int, Expression)]((vi.last, inds.last)) {
            case ((ind, len), (currentMultiplier, acc)) => {
              (len * currentMultiplier, acc + ind * EConstant(currentMultiplier))
            }
          }._2
          l match {
            case VIdent(n, r) => VIdent(n, r :+ LVPArray(expr))
          }
        }
      }
    }
  }


  def typeToVInt(t : Type) : Vector[Int] = t match {
    case TInt() => Vector()
    case TArr(tt, i) => i +: typeToVInt(tt)
  }

  def dfs(stmt : Statement) : Unit = stmt match {
    case SVarDec(t, Vector((n,_))) => t match {
      case TArr(_,_) => {
        val x = typeToVInt(t)
        tmp = tmp.updated(VIdent(n,Vector()),x)
      }
      case TInt() => {
        val x = typeToVInt(t)
        tmp = tmp.updated(VIdent(n,Vector()),x)
      }
    }
    case SGrouped(b) => dfs(b)
    case SOut(b) => dfs(b)
    case SIf(b,ts,es) => {dfs(ts); dfs(es)}
    case SWhile(_,b) => dfs(b)
    case _ => ()
  }

  def dfs(stmts: Vector[Statement]) : Unit = stmts.foreach(dfs)
}

object ConstProp {

  def cleanConstAssn(stmts : Vector[Statement]) : Vector[Statement] = {
    stmts.flatMap(x => x match {
      case SAssign(_,EConstant(_)) => Vector[Statement]()
      case SAssign(_,_) => Vector(x)
      case SExpression(_) => Vector[Statement]()
      case SIf(_,_,_) => Vector(x)
      case SGrouped(v) => Vector(SGrouped(cleanConstAssn(v)))
      case SForGrouped(v) => Vector(SForGrouped(cleanConstAssn(v)))
      case SOut(v) => Vector(SOut(cleanConstAssn(v)))
      case SVarDec(_,_) => Vector()
    })
  }

  def rewriteStmts(stmts : Vector[Statement]) : Vector[Statement] = {
    cleanConstAssn(rewriteStmts(stmts, Map())._1)
  }

  type Env = Map[String, BigInt]

  def rewriteLVal(lval : LValue, env : Env) : Option[BigInt] = lval match {
    case VIdent(n, Vector()) => env.get(n)
    case _ => None
  }

  def rewriteLValArr(lval : LValue, env : Env) : LValue = lval match {
    case VIdent(n, v) =>
      val vv = VIdent(n, v.map({
        case LVPArray(e) => {
          val ee = rewriteExpr(e, env)
          LVPArray(ee)
        }
        case x => x
      }))
      // use string instead of vector to save space??
      vv
  }

  def rewriteExprLvalArr(expr : Expression, env : Env) : Expression = expr match {
    case EConstant(_) => expr
    case ELValue(VIdent(_,Vector())) => expr
    case ELValue(x) => ELValue(rewriteLValArr(x, env))
    case EBinOp(op, x, y) => {
      EBinOp(op, rewriteExprLvalArr(x,env), rewriteExprLvalArr(y, env))
    }
    case EUniOp(op, x) => {
      EUniOp(op, rewriteExprLvalArr(x, env))
    }
    case ESelect(cond,x,y) => {
      ESelect(rewriteExprLvalArr(cond,env), rewriteExprLvalArr(x, env), rewriteExprLvalArr(y,env))
    }
  }

  def rewriteExpr(expr : Expression, env : Env) : Expression =
    expr match {
      case EConstant(x) => expr
      case ELValue(lval) => {
        val x = rewriteLVal(lval, env).map(EConstant(_)).getOrElse(
          ELValue(rewriteLValArr(lval,env)))
        x
      }
      case EBinOp(op, x, y) => {
        val f = Utility.binopToFunction(op)
        (rewriteExpr(x, env), rewriteExpr(y, env)) match {
          case (EConstant(xx), EConstant(yy)) => EConstant(f(xx, yy))
          case (xx, yy) => EBinOp(op, xx, yy)
        }
      }
      case EUniOp(op, x) => {
        val xx = rewriteExpr(x, env)
        xx match {
          case EConstant(y) =>
            op match {
              case UANegate() => EConstant(-y)
              case ULNegate() => EConstant(if (y == 0) 0 else 1)
            }
          case _ => EUniOp(op, xx)
        }
      }
      case ESelect(cond, x ,y) => {
        val c = rewriteExpr(cond, env)
        val xx = rewriteExpr(x, env)
        val yy = rewriteExpr(y, env)
        c match {
          case EConstant(b) if b == 0 => yy
          case EConstant(_) => xx
          case _ => ESelect(c, xx, yy)
        }
      }
    }


  def rewriteStmts(stmts : Vector[Statement], env : Env) : (Vector[Statement], Env) = {
    stmts.foldLeft[(Vector[Statement],Env)]((Vector(),env))(
      (acc, stmt) => {
        val (vs, newEnv) = rewriteStmt(stmt, acc._2)
        (acc._1 :+ vs, newEnv)
      }
    )
  }

  def rewriteStmt(stmt : Statement, env : Env) : (Statement, Env) = stmt match {
    case SExpression(e) => (SExpression(rewriteExprLvalArr(e,env)), env)
    case SBlank() => (SBlank(), env)
    case SGrouped(stmts) => {
      val (v, e) = rewriteStmts(stmts, env)
      (SGrouped(v), e)
    }
    case SOut(stmts) => {
      val (v, e) = rewriteStmts(stmts, env)
      (SOut(v), e)
    }
    case SAssign(VIdent(n, Vector()), rval) => {
      // need to remove the key if rval is None
      val rr = rewriteExpr(rval, env)
      val assn = SAssign(VIdent(n,Vector()), rewriteExprLvalArr(rval, env))
      rr match {
        case EConstant(c) => (SBlank(), env.updated(n,c))
        case _ => {
          (assn, env - n)
        }
      }
    }
    case SAssign(lval, rval) => {
      val assn = SAssign(rewriteLValArr(lval,env),rewriteExprLvalArr(rval,env))
      (assn,env)
    }
    case SIf(cond, ts, es) => {
      val c = rewriteExpr(cond,env)
      c match {
        case EConstant(b) if b == 0 => rewriteStmt(SGrouped(es), env)
        case EConstant(_) => rewriteStmt(SGrouped(ts), env)
        case _ => {
          val (vts, ets) = rewriteStmts(ts, env)
          val (ves, ees) = rewriteStmts(es, env)
          // here we should do a 3-way merge....
          val ks = ets.keySet.intersect(ees.keySet)
          val newEnv = ks.foldLeft[Env](Map())(
            (e, s) => {
              val x = ets(s)
              val y = ets(s)
              if (x == y)
                e.updated(s, x)
              else
                e
            }
          )
          (SIf(rewriteExprLvalArr(cond, env),vts,ves),newEnv)
        }
      }
    }
    case SVarDec(_, _) => {
      (stmt, env)
    }
    case SWhile(cond, body) => {
      var acc : Vector[Statement] = Vector()
      var retEnv = env
      var cc = true
      while(cc) {
        val condV = rewriteExpr(cond, retEnv)
        condV match {
          case EConstant(tmp) if tmp == 0 => cc = false
          case EConstant(_) => {
            val (vb, newEnv) = rewriteStmts(body, retEnv)
            retEnv = newEnv
            acc = acc :+ SGrouped(vb)
          }
          case _ => sys.error("loop depth unknown")
        }
      }
      (SForGrouped(acc), retEnv)
    }
  }
}
