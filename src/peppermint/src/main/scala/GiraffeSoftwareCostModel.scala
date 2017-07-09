import peppermint.CircuitCompilation._
import peppermint.ILAST._


case class ProverCost(
  ramRead : Int = 0,
  ramWrite : Int = 0,
  division : Int = 0,
  mod : Int = 0,
  and : Int = 0,
  or : Int = 0,
  neqComp : Int = 0,
  ltComp : Int = 0
  ) {
  def +(other : ProverCost) : ProverCost = {
    ProverCost(other.ramRead+ramRead,
      other.ramWrite+ramWrite,
      other.division+division,
      other.mod + mod,
      other.and + and,
      other.or + or,
      other.neqComp+neqComp,
      other.ltComp+ltComp
    )
  }
}

case class VerifierCost(
                       mul : Int,
                       add : Int,
                       sub : Int,
                       ramRead : Int,
                       ramWrite : Int,
                       division : Int,
                       mod : Int,
                       and : Int,
                       or : Int,
                       neqComp : Int,
                       ltComp : Int
                       ) {
  def +(other : VerifierCost) : VerifierCost = {
    VerifierCost(
      other.mul + mul,
      other.add + add,
      other.sub + sub,
      other.ramRead+ramRead,
      other.ramWrite+ramWrite,
      other.division+division,
      other.mod + mod,
      other.and + and,
      other.or + or,
      other.neqComp+neqComp,
      other.ltComp+ltComp
    )
  }
}

class ProverCostForOOBC(stmts : Vector[OutOfBandComputation]) {
  var ramRead : Int = 0
  var ramWrite : Int = 0
  var division : Int = 0
  var mod : Int = 0
  var and : Int = 0
  var or : Int = 0
  var neqComp : Int = 0
  var ltComp : Int = 0

  def forOne(stmt : OutOfBandComputation) = stmt match {
    case _:MemStore => ramWrite = ramWrite + 1
    case _:MemUncondStore => ramWrite = ramWrite + 1
    case _:MemFetch => ramRead = ramRead + 1
    case _:WireConst => ()
    case _:WireCopy => ()
    case BinopOOBC(op,_,_,_) => {
      op match {
        case BArithOp(oo) => oo match {
          case BDiv() => division = division + 1
        }
        case BEqual() => neqComp = neqComp + 1
        case BNEqual() => neqComp = neqComp + 1
        case BAnd() => and = and + 1
        case BOr() => or = or + 1
        case _ => ltComp = ltComp + 1
      }
    }
  }

  val result : ProverCost = {
    stmts.foreach(forOne)
    ProverCost(ramRead,ramWrite,division,mod,and,or,neqComp,ltComp)
  }
}

class GiraffeSoftwareCostModel(stmts : Vector[Statement]) {
  // requires the stmts are loop-unrolled
  var mul : Int = 0
  var add : Int = 0
  var sub : Int = 0
  var ramRead : Int = 0
  var ramWrite : Int = 0
  var division : Int = 0
  var mod : Int = 0
  var and : Int = 0
  var or : Int = 0
  var neqComp : Int = 0
  var ltComp : Int = 0

  var proverCost : ProverCost = ProverCost()


  val result : (VerifierCost,ProverCost) = {
    costOfStmts(stmts)
    (VerifierCost(mul,add,sub,ramRead,ramWrite,division,mod,and,or,neqComp,ltComp),
      proverCost)
  }

  def costOfExpr(expr : Expression):Unit  = {
    expr match {
      case EConstant(_) => ()
      case ELValue(_) => ()
      case EBinOp(op, x, y) => {
        costOfExpr(x)
        costOfExpr(y)
        op match {
          case BArithOp(oo) => oo match {
            case BPlus() => add = add + 1
            case BMul() => mul = mul + 1
            case BDiv() => division = division + 1
            case BMinus() => sub = sub + 1
          }
          case BEqual() => neqComp = neqComp + 1
          case BNEqual() => neqComp = neqComp + 1
          case BAnd() => and = and + 1
          case BOr() => or = or + 1
          case _ => ltComp = ltComp + 1
        }
      }
      case EFetch(_,inds) => {
        inds.foreach(costOfExpr)
        ramRead = ramRead + 1
      }
      case EUniOp(op,x) => {
        sub = sub + 1
        costOfExpr(x)
      }
    }
  }

  def costOfStmts(stmts:Vector[Statement]):Unit = stmts.foreach(costOfStmt)

  def costOfPWSOOBC(stmts:Vector[OutOfBandComputation]) : Unit = {
    val pc = new ProverCostForOOBC(stmts).result
    proverCost = pc + proverCost
    ramRead = ramRead + pc.ramRead
    ramWrite = ramWrite + pc.ramWrite
    division = division + pc.division
    mod = mod + pc.mod
    and = and + pc.and
    or = or + pc.or
    neqComp = neqComp + pc.neqComp
    ltComp = ltComp + pc.ltComp
  }

  def costOfStmt(stmt : Statement) :Unit = {
    stmt match {
      case SGrouped(v) => costOfStmts(v)
      case SAssign(_,e) => costOfExpr(e)
      case SCondStore(_,inds,value,cond) => {
        inds.foreach(costOfExpr)
        costOfExpr(value)
        cond.foreach(costOfExpr)
      }
      case SForGrouped(v) => costOfStmts(v)
      case SIf(b,ts,es) => {
        costOfExpr(b)
        costOfStmts(ts)
        costOfStmts(es)
      }
      case x:SZebraOutsource => {
        val xx = CircuitCompilationPar.split(x.pwis)
        costOfPWSOOBC(xx._2)
      }
      case _ => ()
    }
  }
}
