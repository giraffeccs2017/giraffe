import peppermint.Analysis.{CPrinter, Utility, VLValue}
import peppermint.CircuitCompilation._
import peppermint.ILAST._
import peppermint.SSATransform.SSATransform
import peppermint.RWSetCompute.RWSetCompute



case class VCheck(outOfBandComputation: OutOfBandComputation) extends Statement
case class VExtract(vssa:VIdentSSA) extends Statement
case class VInfuse(vssa : VIdentSSA) extends Statement

object SimplifyExpression {

  def isSingle(expr : Expression) : Boolean = expr match {
    case EConstant(_) => true
    case ELValue(_) => true
    case _ => false
  }

  def isSimple(expr : Expression) : Boolean =
    isSingle(expr) || (expr match {
      case EUniOp(_, x) => isSingle(x)
      case EBinOp(_,x,y) => isSingle(x) && isSingle(y)
      case EFetch(l, Vector(e)) => isSingle(e)
    })

  def toSingle(expr: Expression, rename: (Int) => LValue, begin: Int = 0): (Int,Vector[SAssign],Expression) = {
    // returns some expr that is *single*
    if (isSingle(expr)) {
      (begin, Vector(), expr)
    } else {
      val (tmp, vc, ee) = toSimple(expr, rename, begin)
      val nextBegin = tmp + 1
      val vi = rename(tmp)
      val as = SAssign(vi, ee)
      (nextBegin, vc :+ as, ELValue(vi))
    }
  }

  def toSimple(expr: Expression, rename: (Int) => LValue, begin: Int = 0): (Int, Vector[SAssign], Expression) = {
    // returns some expr that is *simple*
    if (isSimple(expr)) {
      (begin, Vector(), expr)
    } else {
      expr match {
        case EBinOp(op, x, y) => {
          val (by, vx, xx) = toSingle(x, rename, begin)
          val (bz, vy, yy) = toSingle(y, rename, by)
          (bz, vx ++ vy, EBinOp(op, xx, yy))
        }
        case EUniOp(op, x) => {
          val (by, vx, xx) = toSingle(x, rename, begin)
          (by, vx, EUniOp(op,xx))
        }
        case EFetch(l, Vector(e)) => {
          val (by, vx, xx) = toSingle(e, rename, begin)
          (by, vx, EFetch(l,Vector(xx)))
        }
      }
    }
  }
}

object SimplifyStatement {
  def simplifyStatements(stmts : Vector[Statement], rename : (Int)=>VIdent) : (Int,Vector[Statement]) = {
    stmts.foldLeft[(Int,Vector[Statement])]((0,Vector()))({
      case ((ab,av),stmt) => {
        val (ii, vv) = simplifyStatement(stmt, rename)
        (math.max(ab,ii), av ++ vv)
      }
    })
  }

  def simplifyStatement(stmt : Statement, rename : (Int)=>VIdent) : (Int, Vector[Statement]) = stmt match {
    case SAssign(l,r) => {
      val (i,v,rr) = SimplifyExpression.toSimple(r, rename, 0)
      (i,v :+ SAssign(l, rr))
    }
    case SCondStore(l, Vector(e), value, cond) => {
      val (ie, vse, ee) = SimplifyExpression.toSingle(e, rename, 0)
      val (iv, vsv, ev) = SimplifyExpression.toSingle(value, rename, ie)
      val mb = cond.map(SimplifyExpression.toSingle(_, rename, iv))
      mb match {
        case Some((ii,vv,cc)) => (ii, (vse ++ vsv ++ vv) :+ SCondStore(l, Vector(ee), ev, Some(cc)))
        case None => (iv, vse ++ vsv :+ SCondStore(l, Vector(ee), ev, None))
      }
    }
    case SGrouped(v) => simplifyStatements(v, rename)
    case SIf(b,ts,es) => {
      val (ib, vsb, bb) = SimplifyExpression.toSingle(b, rename, 0)
      val (ti,tts) = simplifyStatements(ts, rename)
      val (ei, ees) = simplifyStatements(es, rename)
      (math.max(ib,math.max(ti,ei)), vsb :+ SIf(bb, tts, ees))
    }
    case SWhile(c, body) => {
      val (ic, vsc, cc) = SimplifyExpression.toSingle(c, rename)
      val (bi, bodyV) = simplifyStatements(body, rename)
      (math.max(ic,bi), vsc :+ SWhile(cc, bodyV ++ vsc))
    }
    case SBlank() => (0,Vector())
  }
}

object ArrayAcc {
  def arrAcc(arrName : String, idx:Int) : String = "%s[%d]".format(arrName,idx)
  def arrAcc(arrName : String, idx : String) : String = "%s[%s]".format(arrName,idx)
}

object GenChecking {
  import ArrayAcc._
  def run(vcs : Vector[VCheck],
          indexMapping : Map[VIdentSSA, Int], // mapping for the circuit I/O
          prevIName : String,
          prevOName : String,
          globalMapping : Map[VIdent,Int],
          localStateName : String) : String = {
//    var acc : Vector[String] = Vector()
    val acc = vcs.map(x => x.outOfBandComputation match {
      case MemStore(lval:VIdent,inds,value,cond) => {
        val cc = ArrayAcc.arrAcc(prevOName,indexMapping(cond))
        val ind = ArrayAcc.arrAcc(prevOName,indexMapping(inds.head))
        val lvalG = ArrayAcc.arrAcc(localStateName + "->state",
          "%d + (int)mpz_get_si(%s)".format(globalMapping(lval), ind))
        val vall = ArrayAcc.arrAcc(prevOName,indexMapping(value))
          """
            | if (mpz_cmp_si(%s, 0) != 0) {
            |    mpz_set(%s, %s);
            | }
          """.stripMargin.format(cc, lvalG, vall)
      }
      case MemUncondStore(lval : VIdent, inds, value) => {
        val ind = ArrayAcc.arrAcc(prevOName,indexMapping(inds.head))
        val lvalG = ArrayAcc.arrAcc(localStateName + "->state",
          "%d + (int)mpz_get_si(%s)".format(globalMapping(lval), ind))
        val vall = ArrayAcc.arrAcc(prevOName,indexMapping(value))
          """
            |    mpz_set(%s, %s);
          """.stripMargin.format(lvalG, vall)
      }
      case MemFetch(wire, lval : VIdent, inds) => {
        val ww = arrAcc(prevIName, indexMapping(wire))
        val ii = arrAcc(prevOName, indexMapping(inds.head))
        val lvalG = ArrayAcc.arrAcc(localStateName + "->state",
          "%d + (int)mpz_get_si(%s)".format(globalMapping(lval), ii))
        """
          | assert(mpz_cmp(%s, %s) == 0);
        """.stripMargin.format(ww, lvalG)
      }
      case WireCopy(newWire, oldWire) => {
        val nn = arrAcc(prevIName, indexMapping(newWire))
        val oo = arrAcc(prevOName, indexMapping(oldWire))
        """
          | assert(mpz_cmp(%s, %s) == 0);
        """.stripMargin.format(nn, oo)
      }
      case BinopOOBC(op, output, x, y) => {
        val oo = arrAcc(prevIName, indexMapping(output))
        val xx = arrAcc(prevIName, indexMapping(x))
        val yy = arrAcc(prevIName, indexMapping(y))
        op match {
          case BArithOp(x) => ???
          case _ => {
            val ops =
              op match {
                case _:BLE => "<="
                case _:BLT => "<"
                case _:BGE => ">="
                case _:BGT => ">"
              }
            """
              | {
              |   int tmp = mpz_cmp(%s, %s) %s 0;
              |   assert(tmp == ((int)mpz_get_si(%s)));
              | }
            """.stripMargin.format(xx, yy, ops, oo)
          }
        }
      }
    })
    acc.foldLeft("")((acc,s) => acc + "\n" + s)
  }
}

class GenLocal(stmts : Vector[Statement],
               globalMapping : Map[VIdent,Int],
               localStateName : String
              ) {
  // precondition: stmts have to be in "simple form", converted by SimplifyStatements

  def genExpr(expr : Expression, tmpStart : Int) : (String, String) = expr match {
    case _ => ???
  }


  def genVIdent(vident : VIdent, idx : Int = 0) : String = {
     globalMapping.get(vident) match {
       case None => CPrinter.printLVal(vident)
       case Some(i) => "%s->state[%d]".format(localStateName, i + idx)
     }
  }

  def genLVal(lval : LValue) : String = lval match {
    case VIdentSSA(v,_) => {
      genLVal(v)
    }
    case v:VIdent => {
      val (vv:VIdent, p) = Utility.spanArrIndex(v)
      val offset = p match {
          // can only be a constant, if p is present.
          // Non-constant case is handled by SCondStore or EFetch
        case Vector(LVPArray(EConstant(i))) => i.toInt
        case _ => 0
      }
      genVIdent(vv, offset)
    }
  }

  def genStmts(v : Vector[Statement]) : String = v.foldLeft("")((acc,s) => acc + "\n" + genStmt(s))


  def genArrAccess(aname : VIdent, expr : Expression) : String = {
    // assume the expr is single
    expr match {
      case EConstant(i) => genVIdent(aname, i.toInt)
      case ELValue(j) => "%s->state[(int) mpz_get_si(%s)];".format(localStateName, genLVal(j))
    }
  }

  def singleToInt(expr : Expression) : String = expr match {
    case EConstant(i) => i.toString()
    case ELValue(l) => "((int) mpz_get_si(%s))".format(genLVal(l))
  }

  def genStmt(stmt : Statement) : String = {
    stmt match {
      case SCondStore(l,Vector(idx),vv,None)=> {
        "mpz_set(%s, %s);".format(genArrAccess(l,idx), singleToInt(vv))
      }
      case SCondStore(l,Vector(idx),vv,Some(cc)) => {
        """
          | if (%s) {
          |    mpz_set(%s,%s);
          | }
        """.stripMargin.format(singleToInt(cc), genArrAccess(l,idx), singleToInt(vv))
      }
      case SAssign(l, EConstant(i)) => "mpz_set_si(%s, %d);".format(genLVal(l), i.toInt)
      case SAssign(l, EFetch(aname:VIdent, Vector(e))) =>
        "mpz_set(%s, %s);".format(genLVal(l), genArrAccess(aname, e))
      case SAssign(l, r) => {
        val ll = genLVal(l)
        r match {
          case EUniOp(UANegate(), EConstant(i)) => "mpz_set_si(%s, %d);".format(ll, i.toInt * -1)
          case EUniOp(UANegate(), ELValue(x)) => "mpz_neg(%s, %s);".format(ll, genLVal(x))
          case EUniOp(ULNegate(), EConstant(i)) => {assert(i == 0 || i == 1); "mpz_set_si(%s, %d);".format(ll, 1 - i.toInt)}
          case EUniOp(ULNegate(), ELValue(x)) => "mpz_ui_sub(%s, 1, %s);".format(ll, genLVal(x))
          case EBinOp(BArithOp(op),x,y) => {
            op match {
              case BPlus() => {
                (x,y) match {
                  case (EConstant(i), EConstant(j)) => "mpz_set_si(%s,%d);".format(ll, (i+j).toInt)
                  case (ELValue(i), EConstant(j)) => "mpz_add_ui(%s, %s, %d);".format(ll, genLVal(i), j.toInt)
                  case (EConstant(i), ELValue(j)) => "mpz_add_ui(%s, %s, %d);".format(ll, genLVal(j), i.toInt)
                  case (ELValue(i), ELValue(j)) => "mpz_add(%s, %s, %s);".format(ll, genLVal(i), genLVal(j))
                }
              }
              case BMinus() => {
                (x,y) match {
                  case (EConstant(i), EConstant(j)) => "mpz_set_si(%s,%d);".format(ll, (i-j).toInt)
                  case (ELValue(i), EConstant(j)) => "mpz_sub_ui(%s, %s, %d);".format(ll, genLVal(i), j.toInt)
                  case (EConstant(i), ELValue(j)) => "mpz_ui_sub(%s, %d, %s);".format(ll, i.toInt, genLVal(j))
                  case (ELValue(i), ELValue(j)) => "mpz_sub(%s, %s, %s);".format(ll, genLVal(i), genLVal(j))
                }
              }
              case BMul() => {
                (x,y) match {
                  case (EConstant(i), EConstant(j)) => "mpz_set_si(%s,%d);".format(ll, (i*j).toInt)
                  case (ELValue(i), EConstant(j)) => "mpz_mul_si(%s, %s, %d);".format(ll, genLVal(i), j.toInt)
                  case (EConstant(i), ELValue(j)) => "mpz_mul_si(%s, %s, %d);".format(ll, genLVal(j), i.toInt)
                  case (ELValue(i), ELValue(j)) => "mpz_mul(%s, %s, %s);".format(ll, genLVal(i), genLVal(j))
                }
              }
              case BDiv() => {
                (x,y) match {
                  case (EConstant(i), EConstant(j)) => "mpz_set_si(%s,%d);".format(ll, (i/j).toInt)
                  case (ELValue(i), EConstant(j)) => "mpz_fdiv_q_ui(%s, %s, %d);".format(ll, genLVal(i), j.toInt)
                  case (EConstant(i), ELValue(j)) => "mpz_set_si(%s, %d / mpz_get_si(%s));".format(ll, i.toInt, genLVal(j))
                  case (ELValue(i), ELValue(j)) => "mpz_fdiv_q(%s, %s, %s);".format(ll, genLVal(i), genLVal(j))
                }
              }
              case BMod() => ???
            }
          }
          case EBinOp(op, x, y) => {
            op match {
              case BAnd() => genStmt(SAssign(l, x * y))
              case BOr() => ???
            }
          }
        }
      }
      case SIf(ELValue(b), ts, es) => {
        """
          | if (mpz_get_si(%s)) {
          |    %s
          | } else {
          |    %s
          | }
        """.stripMargin.format(genLVal(b), genStmts(ts), genStmts(es))
      }
      case SGrouped(v) => "{\n" + genStmts(v) + "\n}"
      case SOut(_) => sys.error("")
      case _ => ""
    }
  }
}

object GenExtraction {
  def run(extracts : Vector[VExtract],
          indexMapping : Map[VIdentSSA, Int],
          prevIName : String,
          prevOName : String,
          globalMapping : Map[LValue,Int],
          localStateName : String
         ) : String = {
    import ArrayAcc._
    val acc = extracts.map(v => {
      val ssa = v.vssa
      val ident = ssa.ident
      val l = arrAcc(localStateName + "->state", globalMapping(ident))
      val r = arrAcc(prevOName, indexMapping(ssa))
      """
        | mpz_set(%s, %s);
      """.stripMargin.format(l,r)
    })
    acc.foldLeft("")((acc,s) => acc + "\n" + s)
  }
}

object GenInfusion {
  def run(infusions : Vector[VInfuse],
          indexMapping : Map[VIdentSSA, Int],
          nextIName : String,
          globalMapping : Map[LValue,Int],
          localStateName : String
         ) : String = {
    import ArrayAcc._
    val acc = infusions.map(v => {
      val ssa = v.vssa
      val ident = ssa.ident
      val r = arrAcc(localStateName + "->state", globalMapping(ident))
      val l = arrAcc(nextIName, indexMapping(ssa))
      """
        | mpz_set(%s, %s);
      """.stripMargin.format(l,r)
    })
    acc.foldLeft("")((acc,s) => acc + "\n" + s)
  }
}

class ZebraCompilerForMarked(ssa : Vector[Statement], isExtern : VIdentSSA => Boolean = AutomaticCutting.isOutput ) {

//  val (rs,ws) = new RWSetCompute(ssa).run().asInstanceOf[(Map[Int,Set[VIdentSSA]],Map[Int,Set[VIdentSSA]])]
//  val (_, mout) = RWSetCompute.findOutputSet[VIdentSSA](ssa, isExtern, rs, ws)
//  val zebraOutSource = ssa.map(s => s match {
//    case SOut(v) => {
//      val outputSet = mout(s.identity)
//      val (_, innerOutMap) = RWSetCompute.findOutputSet[VIdentSSA](v, outputSet.contains, rs, ws)
//      val x = new CircuitCompilationPar(s.asInstanceOf[SOut], rs, innerOutMap.updated(s.identity,outputSet)).run()
//      val circ = CircuitCompilationPar.split(x._1.flatten)
//      val iset = rs(s.identity).union(x._2.map(_.toSet).reduce((x,y)=>x.union(y)))
//      val oset = outputSet.union(x._3.map(_.toSet).reduce((x,y)=>x.union(y)))
//      val (vsassn, videntm) = new PWSGen(circ._1, iset, oset).run()
//      val iinds = iset.toVector.sortBy(v=>videntm(v).name.tail.toInt).zipWithIndex.foldLeft[Map[VIdentSSA,Int]](Map()){
//        case (acc,(videntssa,i)) => acc.updated(videntssa,i)
//      }
//      val ioinds = oset.toVector.sortBy(v=>videntm(v).name.tail.toInt).zipWithIndex.foldLeft[Map[VIdentSSA,Int]](iinds){
//        case (acc,(videntssa,i)) => acc.updated(videntssa,i)
//      }
//      val single = CircuitCompilationPar.split(x._1.head)._1
//      val singlePWS = new PWSGen(single,iset,oset).run()
//      val size = x._1.size
//      PWSPrinter.printStmts(singlePWS._1).foreach(println)
//      println(x._1.size)
//      SZebraOutsource(v, x._1.flatten,
//        single, size, rs(s.identity), outputSet, iset, oset, ioinds)
//    }
//    case _ => s
//  })

}

class ZebraCompiler(prog : Program) {
//  val preprocessedProgram = new FlattenArrayAccesses(prog)
//  val originalProg : Vector[Statement] = preprocessedProgram.newMainBody
//
//  val constprop = ConstProp.rewriteStmts(originalProg)
//  val ssa = new SSATransform(constprop).run()
//  // we know that originalProg, constprop, ssa, must be of the form: local, (outsource, local)*
//
//  val (rs,ws) = new RWSetCompute(ssa).run().asInstanceOf[(Map[Int,Set[VIdentSSA]],Map[Int,Set[VIdentSSA]])]
//  val (_, mout) = RWSetCompute.findOutputSet[VIdentSSA](ssa, AutomaticCutting.isOutput, rs, ws)
//
//  val zebraOutSource = ssa.map(s => s match {
//    case SOut(v) => {
//      val outputSet = mout(s.identity)
//      val (_, innerOutMap) = RWSetCompute.findOutputSet[VIdentSSA](v, outputSet.contains, rs, ws)
//      val x = new CircuitCompilationPar(s.asInstanceOf[SOut], rs, innerOutMap.updated(s.identity,outputSet)).run()
//      val circ = CircuitCompilationPar.split(x._1.flatten)
//      val iset = rs(s.identity).union(x._2.map(_.toSet).reduce((x,y)=>x.union(y)))
//      val oset = outputSet.union(x._3.map(_.toSet).reduce((x,y)=>x.union(y)))
//      val (vsassn, videntm) = new PWSGen(circ._1, iset, oset).run()
//      val iinds = iset.toVector.sortBy(v=>videntm(v).name.tail.toInt).zipWithIndex.foldLeft[Map[VIdentSSA,Int]](Map()){
//        case (acc,(videntssa,i)) => acc.updated(videntssa,i)
//      }
//      val ioinds = oset.toVector.sortBy(v=>videntm(v).name.tail.toInt).zipWithIndex.foldLeft[Map[VIdentSSA,Int]](iinds){
//        case (acc,(videntssa,i)) => acc.updated(videntssa,i)
//      }
//      SZebraOutsource(v, x._1.flatten,
//        CircuitCompilationPar.split(x._1.head)._1, x._1.size, rs(s.identity), outputSet, iset, oset, ioinds)
//    }
//    case _ => s
//  })
//
//  val (localStateSize, globalMapping) : (Int, Map[VIdent, Int]) = {
//    preprocessedProgram.lvalToSizesVec.foldLeft[(Int,Map[VIdent,Int])]((0,Map())){
//      case ((n,acc),(lval,ints)) => {
//        val size = ints.product
//        (n+size, acc.updated(lval, n))
//      }
//    }
//  }
}
