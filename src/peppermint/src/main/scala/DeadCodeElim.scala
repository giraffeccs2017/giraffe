import peppermint.Analysis.CPrinter
import peppermint.ILAST._

object ComputeIgnoreSet {
  def collectSet(expr : Expression) : Set[LValue] = expr match {
    case EConstant(_) => Set()
    case ELValue(l) => Set(l)
    case EBinOp(_,x,y) => collectSet(x) ++ collectSet(y)
    case EUniOp(_,x) => collectSet(x)
    case EFetch(_,Vector(e)) => collectSet(e)
  }

  def multiple(stmts : Vector[Statement], imset:Set[LValue], ignore:Set[LValue]):(Set[LValue],Set[LValue]) = {
    stmts.foldRight((imset,ignore))({
      case (stmt, (acc1,acc2)) => one(stmt,acc1,acc2)
    })
  }

  def one(stmt:Statement, imset:Set[LValue], ignore:Set[LValue]) : (Set[LValue], Set[LValue]) = stmt match {
    case SAssign(x,r) => {
      if (imset.contains(x)) {
        (imset ++ collectSet(r), ignore)
      } else {
        (imset, ignore + x)
      }
    }
    case SCondStore(_,Vector(e),v,cond) => {
      (imset ++ collectSet(e) ++ collectSet(v) ++ cond.map(collectSet).getOrElse(Set()), ignore)
    }
    case SForGrouped(v) => multiple(v,imset,ignore)
    case SGrouped(v) => multiple(v,imset,ignore)
  }

  // first return value is the VIP set, second return value is the ignore set
  def compute(comp : Vector[Statement], importantSet : Set[LValue]) : (Set[LValue],Set[LValue]) = {
    multiple(comp, importantSet, Set())
  }
}

class DeadCodeElim(chunk : Vector[Statement], outputSet : LValue => Boolean) {
  // might have to run this module multiple times due to the condition of if statements
  import ComputeIgnoreSet._
  import scala.collection.mutable.{Set => MSet}

  val stmtDelId : collection.mutable.Set[Int] = collection.mutable.Set()

  def findVIP(stmts : Vector[Statement], vipSet : Set[LValue]) : Set[LValue] = {
    stmts.foldRight(vipSet)(findVIP)
  }

  def findVIP(stmts : Vector[Statement], vipSet : MSet[LValue]) : Unit = {
    stmts.reverseIterator.foreach(findVIP(_, vipSet))
  }

  def findVIP(stmt : Statement, vipSet : MSet[LValue]) : Unit = stmt match {
    case SBlank() => ()
    case SExpression(_) => ()
    case SAssign(l, r) => {
      if (vipSet.contains(l) || outputSet(l)) {
        vipSet.update(l, false)
        collectSet(r).foreach(vipSet.update(_,true))
      } else {
        stmtDelId.update(stmt.identity, true)
      }
    }
    case SCondStore(_,Vector(e),v,cond) => {
      (collectSet(e) ++ collectSet(v) ++ cond.map(collectSet).getOrElse(Set())).foreach(vipSet.update(_,true))
    }
    case SIf(b,t,e) => {
      // TODO: figure out how to make this more efficient?
      val tdce = new DeadCodeElim(t, vipSet)
      val edce = new DeadCodeElim(e, vipSet)
      tdce.stmtDelId.foreach(stmtDelId.update(_,true))
      edce.stmtDelId.foreach(stmtDelId.update(_,true))
      tdce.vipSet.foreach(vipSet.update(_,true))
      edce.vipSet.foreach(vipSet.update(_,true))
      collectSet(b).foreach(vipSet.update(_,true))
    }
    case SForGrouped(v) => findVIP(v,vipSet)
    case SGrouped(v) => findVIP(v, vipSet)
    case _:SVarDec => ()
  }

  def findVIP(stmt : Statement, vipSet : Set[LValue]) : Set[LValue] = stmt match {
    case SBlank() => vipSet
    case SExpression(_) => vipSet
    case SAssign(l, r) => {
      if (vipSet.contains(l) || outputSet(l)) {
        (vipSet - l) union collectSet(r)
      } else {
        stmtDelId.update(stmt.identity, true)
        vipSet
      }
    }
    case SCondStore(_,Vector(e),v,cond) => {
      vipSet ++ collectSet(e) ++ collectSet(v) ++ cond.map(collectSet).getOrElse(Set())
    }
    case SIf(b,t,e) => {
      findVIP(t,vipSet) union findVIP(e,vipSet) union collectSet(b)
    }
    case SForGrouped(v) => findVIP(v,vipSet)
    case SGrouped(v) => findVIP(v, vipSet)
    case _:SVarDec => vipSet
  }

  def optionVecToVec[a](as : Vector[Option[a]]):Vector[a] = {
    as.foldLeft(Vector[a]())((acc,oa) => oa match {
      case None => acc
      case Some(a) => acc :+ a
    })
  }

  def elim(stmts:Vector[Statement]) : Vector[Statement] = {
    optionVecToVec(stmts.map(elim))
  }

  def elim(stmt : Statement) : Option[Statement] = stmt match {
    case SBlank() => None
    case _:SAssign => if (stmtDelId.contains(stmt.identity)) None else Some(stmt)
    case _:SExpression => None
    case _:SCondStore => Some(stmt)
    case SIf(b,t,e) => {
      val tt = elim(t)
      val ee = elim(e)
      if (tt.isEmpty && ee.isEmpty) {
        None
      } else {
        Some(SIf(b, tt, ee))
      }
    }
    case _:SVarDec => None
    case SForGrouped(stmts) => {
      val gg = elim(stmts)
      if (gg.isEmpty) None else Some(SForGrouped(gg))
    }
    case SGrouped(stmts) => {
      val gg = elim(stmts)
      if (gg.isEmpty) None else Some(SGrouped(gg))
    }
  }

  val (result, vipSet) : (Vector[Statement], MSet[LValue]) = {
    val set = MSet[LValue]()
    findVIP(chunk, set)
    (elim(chunk), set)
  }
}
