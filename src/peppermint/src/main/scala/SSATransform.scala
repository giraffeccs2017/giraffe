
package peppermint.SSATransform

import peppermint.ILAST._
import peppermint.Analysis._

class MarkDynamicArray(chunk : Vector[Statement], canonicalName : LValue => String = CPrinter.printLVal) {
  val (dynArrs, staArrs) = new FindDynamicArray(chunk).run()

  val result = chunk.map(rewriteStmt)

  def isDynamic(lval : LValue) : Boolean = dynArrs.contains(Utility.stripArrIndex(lval))

  def rewriteLVPath(path : Vector[LValPath]) : (Vector[Expression]) = {
    val x = path.map({
      case LVPArray(e) => {
        val ee = rewriteExpression(e)
        ee
      }
    })
    x
  }

  def videntName(lval : LValue) : VIdentName = {
    val (front,end) = Utility.spanArrIndex(lval)
    val idx = end match {
      case Vector() => None
      case Vector(LVPArray(EConstant(i))) => Some(i.toInt)
    }
    VIdentName(canonicalName(front), idx)
  }

  def rewriteExpression(expr : Expression) : Expression = expr match {
    case EConstant(_) => expr
    case ELValue(lval) if !isDynamic(lval) => ELValue(videntName(lval))
    case ELValue(lval) => {
      val (fst, path) = Utility.spanArrIndex(lval)
//      println(path)
      EFetch(fst, rewriteLVPath(path))
    }
    case EBinOp(op,x,y) => EBinOp(op, rewriteExpression(x), rewriteExpression(y))
    case EUniOp(op, x) => EUniOp(op, rewriteExpression(x))
  }

  def rewriteStmt(stmt : Statement) : Statement = stmt match {
    case SBlank() => stmt
    case SExpression(_) => stmt
    case SGrouped(v) => SGrouped(v.map(rewriteStmt))
    case SForGrouped(v) => SForGrouped(v.map(rewriteStmt))
    case SOut(v) => SOut(v.map(rewriteStmt))
    case SIf(b,ts,es) => SIf(rewriteExpression(b),ts.map(rewriteStmt),es.map(rewriteStmt))
    case SVarDec(_,_) => stmt
    case SAssign(l,r) if !isDynamic(l) => {
      SAssign(videntName(l), rewriteExpression(r))
    }
    case SAssign(lval, rval) => {
      val (ll, p) = Utility.spanArrIndex(lval)
      val rr = rewriteExpression(rval)
      val indV = rewriteLVPath(p)
      SCondStore(ll.asInstanceOf[VIdent], indV, rr, None)
    }
  }

}

class FindDynamicArray(chunk : Vector[Statement]) {
  private var dynamic : Set[LValue] = Set()
  private var static : Set[LValue] = Set()

  private def dfs(vLValue: LValue) : Unit = vLValue match {
    case VIdent(_, p) => {
      val (fst, snd) = Utility.spanArrIndex(vLValue)
      if (snd.nonEmpty) {
        val dyn = p.foldLeft(false)(
          {
            case (acc, LVPArray(EConstant(_))) => acc
            case (acc, LVPArray(k)) => {
              dfs(k)
              true
            }
            case (acc, _) => acc
          }
        )
        if (dyn) {
          dynamic = dynamic + fst
          static = static - fst
        } else {
          if (!dynamic.contains(fst)) {
            static = static + fst
          }
        }
      }
    }
  }

  private def dfs(expression: Expression) : Unit = expression match {
    case ELValue(lval) => dfs(lval)
    case EBinOp(op,x,y) => {dfs(x);dfs(y)}
    case EUniOp(op,x) => dfs(x)
    case ESelect(b,x,y) => {dfs(b);dfs(x);dfs(y)}
    case _ => ()
  }

  private def dfs(stmt:Statement) : Unit = stmt match {
    case SAssign(lval,rval) => {dfs(lval);dfs(rval)}
    case SExpression(e) => dfs(e);
    case SGrouped(s) => s.foreach(dfs)
    case SForGrouped(s) => s.foreach(dfs)
    case SOut(s) => s.foreach(dfs)
    case SIf(c,x,y) => {dfs(c);x.foreach(dfs);y.foreach(dfs)}
    case _ => ()
  }

  def run() : (Set[LValue],Set[LValue]) = {
    chunk.foreach(dfs)
    (dynamic,static)
  }
}

class SSATransform(chunk : Vector[Statement]) {
  import scala.collection.mutable._
  import scala.collection.immutable.{Set => _, Map => _}
//  val (dynArrs, staArrs) = new FindDynamicArray(chunk).run()

  var counter : Int = 0

  def nextCounter():Int = {
    counter+=1
    counter
  }

//  def isDynamic(lval : LValue) : Boolean = dynArrs.contains(Utility.stripArrIndex(lval))

  type Env = scala.collection.mutable.Map[LValue, VIdentSSA] // lval -> latest version map

  def rewriteVIdent(vident : VIdentName, env : Env) : (VIdentSSA, Env) = {
    // assuming vident is a static array element name, or a scalar name
    env.get(vident).map(v => (v, env)).getOrElse(incCounter(vident, env))
  }

  def rewriteLVPath(path : Vector[LValPath], env : Env) : (Vector[Expression], Env) = {
    var newEnv = env
    val x = path.map({
      case LVPArray(e) => {
        val (ee, tmpEnv)= rewriteExpression(e, newEnv)
        newEnv = tmpEnv
        ee
      }
    })
    (x, newEnv)
  }

  def rewriteExpression(expr : Expression, env : Env) : (Expression, Env) = expr match {
    case EConstant(_) => (expr, env)
//    case ELValue(lval : VIdent) if isDynamic(lval)=> {
//      val (fst, path) = Utility.spanArrIndex(lval)
//      val (x, newEnv) = rewriteLVPath(path, env)
//      val (ll, retEnv) = rewriteVIdent(fst.asInstanceOf[VIdent], newEnv)
//      (EFetch(ll, x), retEnv)
//    }
    case ELValue(lval:VIdentName)  => {
      val (lvalp, envNew) = rewriteVIdent(lval, env)
      (ELValue(lvalp), envNew)
    }
    case EBinOp(op, x, y) => {
      val (xx, ex) = rewriteExpression(x, env)
      val (yy, ey) = rewriteExpression(y, ex)
      (EBinOp(op, xx, yy), ey)
    }
    case EUniOp(op, x) => {
      val (xx, ex) =  rewriteExpression(x, env)
      (EUniOp(op, xx), ex)
    }
    case ESelect(cond, x, y) => {
      val (cc, ec) = rewriteExpression(cond, env)
      val (xx, ex) = rewriteExpression(x, ec)
      val (yy, ey) = rewriteExpression(y, ex)
      (ESelect(cc, xx, yy), ey)
    }
    case EFetch(lval, Vector(e)) => {
      val (x ,ee) = rewriteExpression(e, env)
      (EFetch(lval, Vector(x)), ee)
    }
  }

  def rewriteStmts(stmts : Vector[Statement], env: Env) : (Vector[Statement], Env, Set[VIdentName]) = {
    stmts.foldLeft[(Vector[Statement],Env,Set[VIdentName])]((Vector(),env, Set()))(
      {
        case ((va, enva, svia), stmt) => {
          val (vs, newEnv, ss) = rewriteStmt(stmt, enva)
          ss.foreach(svia.update(_, true))
          (va ++ vs, newEnv, svia)
        }
      }
    )
  }

  def nextTmpVar() : VIdentSSA = {
    val i = nextCounter()
    VIdentSSA(VIdentName("_ssa_%d".format(i), None), i)
  }

  def markCondStore(stmt : Statement, cond : Expression) : Statement = stmt match {
    case SCondStore(l,is,v,m) => SCondStore(l,is,v,Some(m.map(e => EBinOp(BArithOp(BMul()),cond,e)).getOrElse(cond)))
    case SGrouped(b) => SGrouped(b.map(markCondStore(_,cond)))
    case SForGrouped(b) => SForGrouped(b.map(markCondStore(_,cond)))
    case SOut(b) => SOut(b.map(markCondStore(_,cond)))
    case _ => stmt
  }

  // the last return value does NOT include dynamic arrays
  def rewriteStmt(stmt : Statement, env : Env) : (Vector[Statement], Env, Set[VIdentName]) = stmt match {
    case SExpression(_) => (Vector(),env,Set())
    case SBlank() => (Vector(), env, Set())
    case SVarDec(_,_) => (Vector(stmt),env, Set())
    case SCondStore(lval, Vector(e), v, cond) => {
      val (ee, eEnv) = rewriteExpression(e, env)
      val (vv, vEnv) = rewriteExpression(v, eEnv)
      val (cc, cEnv) =
      cond match {
        case Some(c) => {
          val (ccc, ccEnv) = rewriteExpression(c,vEnv)
          (Some(ccc), ccEnv)
        }
        case None => (None, vEnv)
      }
      (Vector(SCondStore(lval, Vector(ee), vv, cc)), cEnv, Set())
    }
    case SGrouped(stmts) => {
      val (g, ret, ss) = rewriteStmts(stmts, env)
      (Vector(SGrouped(g)), ret, ss)
    }
    case SForGrouped(stmts) => {
      val (g, ret, ss) = rewriteStmts(stmts, env)
      (Vector(SForGrouped(g)), ret, ss)
    }
    case SOut(stmts) => {
      val (g, ret, ss) = rewriteStmts(stmts, env)
      (Vector(SOut(g)), ret, ss)
    }
//    case SAssign(lval:VIdent,rval) if isDynamic(lval) => {
//      val (ll, p) = Utility.spanArrIndex(lval)
//      val (rr, re) = rewriteExpression(rval, env)
//      val (indV, indvE) = rewriteLVPath(p, re)
//      (Vector(SCondStore(ll.asInstanceOf[VIdent], indV, rr, None)), indvE, Set())
//    }
    case SAssign(lval:VIdentName, rval) => {
      val (rr, re) = rewriteExpression(rval, env)
      val (ll, retenv) = incCounter(lval, re)
      (Vector(SAssign(ll,rr)),retenv, Set(lval))
    }
    case SIf(cond, ts, es) => {
      val (cc, cenv) = rewriteExpression(cond, env)
      val condVar = nextTmpVar()
      val condExpr = ELValue(condVar)
      val negCond = EConstant(1) - condExpr
      val assn = SAssign(condVar, cc)
      val (tsstmp, tsEnv, tsC) = rewriteStmts(ts, cenv)
      val tss = tsstmp.map(markCondStore(_,condExpr))
      val (esstmp, esEnv, esC) = rewriteStmts(es, cenv)
      val ess = esstmp.map(markCondStore(_, negCond))
      var retenv = cenv
      val changedSet = tsC ++ esC
      val changed = (tsC ++ esC).to[Vector].map(ident =>
        {
          val (ii, tmp) = incCounter(ident, retenv)
          retenv = tmp
          val tlval = ELValue(tsEnv(ident))
          val elval = ELValue(esEnv(ident))
          SAssign(ii, (condExpr * (tlval - elval)) + elval)
        }
      )
      (Vector(SGrouped(assn +: (tss ++ ess ++ changed))), retenv, changedSet)
    }
  }

  def incCounter(lval : VIdentName, env : Env) : (VIdentSSA, Env) = {
    val i = VIdentSSA(lval,  nextCounter())
    // mutable
    env.update(lval, i)
    (i, env)
  }

  var endEnv : Env = scala.collection.mutable.Map()

  def run() : Vector[Statement] = {
    // TODO: setup the initial environment
    val (ret, tmp, _) = rewriteStmts(chunk, scala.collection.mutable.Map())
    endEnv = tmp
    ret
  }

}
