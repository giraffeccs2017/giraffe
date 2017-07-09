
package peppermint.CircuitCompilation

import peppermint.Analysis.{CPrinter, VLValue}
import peppermint.CircuitCompilation.CircuitCompilation.OOBTranscript
import peppermint.ILAST._

import scala.collection.mutable.ArrayBuffer


abstract class OutOfBandComputation

case class MemStore(lval : LValue, indices : Vector[VIdentSSA], value : VIdentSSA, cond : VIdentSSA) extends OutOfBandComputation {
  override def toString  : String = {
    "MemStore %s%s = %s, condition %s".format(lval.toString,indices.toString,value.toString,cond.toString)
  }
}
case class MemUncondStore(lval : LValue, indices : Vector[VIdentSSA], value : VIdentSSA) extends OutOfBandComputation
case class MemFetch(wire : VIdentSSA, lval : LValue, indices : Vector[VIdentSSA]) extends OutOfBandComputation {
  override def toString : String = {
    "MemFetch %s = %s%s".format(wire, lval, indices)
  }
}
case class WireConst(wire : VIdentSSA, value : BigInt) extends OutOfBandComputation {
  override def toString : String = {
    "WireConst %s = %d".format(wire, value)
  }
}
case class WireCopy(newWire : VIdentSSA, oldWire : VIdentSSA) extends OutOfBandComputation {
  var previousName : LValue = VIdentName("", None)
  // find the oldwire's current name and put it here
  var oldWiresCurrentName : VIdentSSA = VIdentSSA(VIdentName("", None), 0)
  override def toString : String = {
    // this is only for the benefit of Giraffe
    "WireCopy %s = previous %s, or %s".format(newWire, oldWiresCurrentName, previousName)
  }
}
case class BinopOOBC(op : BinOp, output : VIdentSSA, x : VIdentSSA, y : VIdentSSA) extends OutOfBandComputation {
  override def toString : String = {
    "BINOP %s = %s %s %s".format(output, x, CPrinter.binopToString(op), y)
  }
}

object Compile {

  def isOrig(v:VIdentSSA) : Boolean = {
    v.ident match {
      case VIdentName(n, _) => n.length() <= 2 || n.head != '_' || n.charAt(1) != 'c' || n.charAt(2) != 'c'
    }
  }

  def getIndexMapping(vec:Vector[VIdentSSA]):Map[VIdentSSA,Int] = {
    vec.zipWithIndex.toMap
  }

  def compileSquash(out:SOut, isets:Int=>Set[VIdentSSA], oSets:Int=>Set[VIdentSSA]) : SZebraOutsource = {
    val ret = new CircuitCompilationPar(out, isets, oSets).run()
    val inputLayer = ret._2.flatten
    val outputLayer = ret._3.flatten
    val origInput = inputLayer.filter(isOrig).toSet
    val origOutput = outputLayer.filter(isOrig).toSet
    SZebraOutsource(out.body,ret._1.flatten,Vector(), ret._1.size, origInput, origOutput,
      inputLayer,
      outputLayer, getIndexMapping(outputLayer) ++ getIndexMapping(inputLayer))
  }
  def compileSimple(vec:Vector[Statement],
                    inputSet:Set[VIdentSSA],
                    outputSet : Set[VIdentSSA]) : SZebraOutsource = {
    val ret = new CircuitCompilation(vec,inputSet,outputSet,Set()).run()
    SZebraOutsource(vec,ret._1,Vector(),1,inputSet,outputSet,ret._2,ret._3, getIndexMapping(ret._3)
      ++ getIndexMapping(ret._2)
    )
  }
}



object CircuitCompilation {

  type OOBTranscript = Vector[OutOfBandComputation]
  var c : Int = 0

  def nextC() : Int = {
    c = c + 1
    c
  }

  def nextSSA() : VIdentSSA = {
    val i = nextC()
    VIdentSSA(VIdentName("_cc_%d".format(i), None),i)
  }
}

abstract class ProverWorkItem
case class PWIOOBC(outOfBandComputation: OutOfBandComputation) extends ProverWorkItem
case class PWIAssn(assn : SAssign) extends ProverWorkItem

class CircuitCompilation(comp : Vector[Statement],
                         inputSet : Set[VIdentSSA],
                         outputSet : Set[VIdentSSA],
                         previousOutputSet : Set[VIdentSSA]) {
  import scala.collection.mutable.Set
  // replaceSet is used

  // eliminate EFetch, SCondStore, Binary comparisons

  val circuit : ArrayBuffer[SAssign] = ArrayBuffer()
  val pwis : ArrayBuffer[ProverWorkItem] = ArrayBuffer()

  val outOfBandInput : Set[VIdentSSA] = Set()
  val outOfBandOutput : Set[VIdentSSA] = Set()

  val inputFromOutside = inputSet diff previousOutputSet

  val toBeCopiedOutOfBand = inputSet diff inputFromOutside

  // the input into the circuit is OOBInput union inputFromOutside

  val oldToNewInputMap : Map[VIdentSSA, VIdentSSA] = {
    toBeCopiedOutOfBand.foldLeft[Map[VIdentSSA, VIdentSSA]](Map())(
      (acc, v) => acc.updated(v, genOOBInput())
    )
  }

  def addAssn(assn : SAssign) = {
      circuit append assn
      pwis append PWIAssn(assn)
  }

  def addOOBC(outOfBandComputation: OutOfBandComputation) = {
    pwis append PWIOOBC(outOfBandComputation)
  }

  def generateCopyTranscript() : Unit = {
    oldToNewInputMap.foreach(x => {
      val wc = WireCopy(x._2, x._1)

      wc.oldWiresCurrentName = outputSet.find(p => p.ident == x._1.ident).get
      wc.previousName = x._1.ident

      addOOBC(wc)
    })
  }

  def genOOBOutput(expr : Expression) : VIdentSSA = {
    val v = CircuitCompilation.nextSSA()
    val assn = dfsStatement(SAssign(v, expr))
    outOfBandOutput .update(v, true)
    v
  }

  def genOOBInput() : VIdentSSA = {
    val v = CircuitCompilation.nextSSA()
    outOfBandInput .update(v, true)
    v
  }

  def binopEmbeddable(op:BinOp) : Boolean = op match {
    case BArithOp(BMinus()) => true
    case BArithOp(BPlus()) => true
    case BArithOp(BMul()) => true
    case _ => false
  }

  def dfsExpr(expr : Expression) : Expression = expr match {
    case EConstant(_) => expr
    case ELValue(l:VIdentSSA) => {
      ELValue(oldToNewInputMap.getOrElse(l,l))
    }
    case EBinOp(op,x,y) if binopEmbeddable(op) => {
      EBinOp(op,dfsExpr(x),dfsExpr(y))
    }
    case EBinOp(op,x,y) => {
      // TODO: can optimize this when dfsExpr returns a constant or an Lvalue, might need a new OOB construct to handle the constant case
      val xx = genOOBOutput(dfsExpr(x))
      val yy = genOOBOutput(dfsExpr(y))
      val ret = genOOBInput()
      addOOBC(BinopOOBC(op,ret,xx,yy))
      ELValue(ret)
    }
    case EUniOp(op,x) => {
      val xx = dfsExpr(x)
      op match {
        case UANegate() => EConstant(0) - xx
        case ULNegate() => EConstant(1) - xx
      }
    }
    case ESelect(cond,x,y) => {
      val cc = dfsExpr(cond)
      val xx = dfsExpr(x)
      val yy = dfsExpr(y)
      // TODO: eliminate common subexpression: yy
      yy + cc * (xx - yy)
    }
    case EFetch(l, inds) => {
      val indSSAs : Vector[VIdentSSA] = inds.map(x => genOOBOutput(dfsExpr(x)))
      val wire = genOOBInput()
      val t = MemFetch(wire,l,indSSAs)
      addOOBC(t)
      ELValue(wire)
    }
  }

  def dfsStatement(stmt : Statement) : Unit = stmt match {
    case SAssign(l,r) => {
      val s = SAssign(l, dfsExpr(r))
      s.rval match {
        case _:EFetch => ()
        case _ => ()
      }
      addAssn(s)
    }
    case SGrouped(g) => g.foreach(dfsStatement)
    case SForGrouped(g) => g.foreach(dfsStatement)
    case SCondStore(l,inds,v,cond) => {
//      val cc = genOOBOutput(dfsExpr(cond.getOrElse(EConstant(1))))
      val indsSSA = inds.map(x => genOOBOutput(dfsExpr(x)))
      val vv = genOOBOutput(v)
      val store = cond match {
        case Some(x) => {
          val cc = genOOBOutput(dfsExpr(x))
          MemStore(l, indsSSA, vv, cc)
        }
        case None => {
          MemUncondStore(l, indsSSA, vv)
        }
      }
      addOOBC(store)
    }
    case SOut(_) => sys.error("nested out")
  }

  var constMap : Map[BigInt, VIdentSSA] = Map()

  def constVIdent(i : BigInt) : (Expression, Option[ProverWorkItem]) = {
    constMap.get(i) match {
      case Some(v) => (ELValue(v), None)
      case None => {
        val v = genOOBInput()
        constMap = constMap.updated(i,v)
        (ELValue(v), Some(PWIOOBC(WireConst(v, i))))
      }
    }
  }


  def elimConstants(stmts : Vector[ProverWorkItem]) : Vector[ProverWorkItem] = {
    def elimConst(expr : Expression) : (Expression, Option[ProverWorkItem]) = {
      expr match {
        case EConstant(i) => constVIdent(i)
        case (v:ELValue) => (expr, None)
      }
    }
    val ret = ArrayBuffer[ProverWorkItem]()
    stmts.foreach({
      case (v:PWIOOBC) => ret append v
      case PWIAssn(SAssign(l, EBinOp(op, x, y))) => {
        val (xx, vx) = elimConst(x)
        val (yy, vy) = elimConst(y)
//        vx ++ vy :+
        vx.foreach(ret append _)
        vy.foreach(ret append _)
        ret append PWIAssn(SAssign(l, EBinOp(op,xx,yy)))
      }
    })
    ret.toVector
  }

  def raiseIO(stmts : Vector[ProverWorkItem]) : (Vector[ProverWorkItem], Vector[VIdentSSA], Vector[VIdentSSA]) = {
//    val (ELValue(ztmp), zeroWorkItem) = constVIdent(0)
//    val zero = ztmp.asInstanceOf[VIdentSSA]
//    val ss = zeroWorkItem ++ stmts
    val is = inputFromOutside union outOfBandInput
    val isV = ArrayBuffer[VIdentSSA]()
    val osV = ArrayBuffer[VIdentSSA]()
    val vst = Set[VIdentSSA]()
    val os = outOfBandOutput union outputSet
    stmts.foreach({
      case PWIOOBC(_) => ()
      case (PWIAssn(SAssign(l:VIdentSSA,EBinOp(op,ELValue(x:VIdentSSA),ELValue(y:VIdentSSA))))) => {
        if (is.contains(x) && !vst.contains(x)) {
          isV append x
          vst .update( x, true)
        }
        if (is.contains(y) && !vst.contains(y)) {
          isV append y
          vst .update( y, true)
        }
        if (os.contains(l)) {
          osV append l
        }
      }
    })
    //val osV = os.to[Vector]
    (stmts, isV.toVector, osV.toVector)
  }

  def makeLayered(stmts : Vector[ProverWorkItem]) : Vector[ProverWorkItem] = {
    // TODO: make it another pass, instead of putting in this class
    val (ELValue(ztmp), _) = constVIdent(0)
    val zero = ztmp.asInstanceOf[VIdentSSA]
    val is = inputFromOutside union outOfBandInput
    val os = outOfBandOutput union outputSet
    val initMap = is.foldLeft[Map[VIdentSSA,Int]](Map())((acc,i) => acc.updated(i,0))
    val tmpMap : Map[VIdentSSA,Int] = stmts.foldLeft(initMap)(
      (m, pwi) => {
        pwi match {
          case (_:PWIOOBC) => m
          case (PWIAssn(SAssign(l:VIdentSSA,EBinOp(op,ELValue(x:VIdentSSA),ELValue(y:VIdentSSA))))) => {
            if (os.contains(l))
              m
            else {
              val vx = m(x)
              val vy = m(y)
              m.updated(l, math.max(vx, vy) + 1)
            }
          }
        }
      }
    )
    val outputLayerNum = tmpMap.foldLeft(0)({case (acc,(_,i))=>math.max(acc,i)}) + 1
    val layerMap = os.foldLeft(tmpMap)((m,i) => m.updated(i, outputLayerNum))
    var needToBeOnLayer : Map[VIdentSSA,Int] = layerMap
    def needVOnLayerX(v : VIdentSSA, x : Int) = {
      val begin = needToBeOnLayer(v)
      needToBeOnLayer = needToBeOnLayer.updated(v, math.max(x,begin))
      val zb = needToBeOnLayer(zero)
      needToBeOnLayer = needToBeOnLayer.updated(zero, math.max(x,zb))
    }
    stmts.foreach({
      case (_:PWIOOBC) => ()
      case (PWIAssn(SAssign(l:VIdentSSA,EBinOp(op,ELValue(x:VIdentSSA),ELValue(y:VIdentSSA))))) => {
        val ll = layerMap(l)
        needVOnLayerX(x, ll - 1)
        needVOnLayerX(y, ll - 1)
      }
    })

    ???
  }


  def run() : (Vector[ProverWorkItem], Vector[VIdentSSA], Vector[VIdentSSA]) = {
    generateCopyTranscript()
    comp.foreach(dfsStatement)
    val simp = ToSimpleForm.toSimpleForm(pwis.toVector)
    val elimed = elimConstants(simp)
    val ret = raiseIO(elimed)
    ret
  }
}

object ToSimpleForm {

  def toSimpleForm(expr : Expression) : (Expression, Vector[SAssign]) = expr match {
    case EConstant(_) => (expr, Vector())
    case ELValue(_) => (expr, Vector())
    case EBinOp(op, x, y) => {
      val (xx,vx) = toSimpleForm(x)
      val (yy,vy) = toSimpleForm(y)
      val vssa = CircuitCompilation.nextSSA()
      val assn = SAssign(vssa, EBinOp(op, xx, yy))
      (ELValue(vssa), (vx ++ vy) :+ assn)
    }
  }

  def toSimpleForm(stmt : SAssign) : Vector[SAssign] = stmt match {
    case SAssign(l, EBinOp(op, x, y)) => {
      if (x.isInstanceOf[EFetch]) {
        println(stmt)
      }
      val (xx,vx) = toSimpleForm(x)
      val (yy,vy) = toSimpleForm(y)
      val assn = SAssign(l, EBinOp(op, xx, yy))
      (vx ++ vy) :+ assn
    }
    case SAssign(l, expr@ELValue(_)) => Vector(SAssign(l, expr + EConstant(0)))
    case SAssign(l, expr@EConstant(_)) => Vector(SAssign(l,expr+EConstant(0)))
    case SAssign(_,_) => println("error", stmt); ???
  }

  def toSimpleForm(pwi : ProverWorkItem) : Vector[ProverWorkItem] = pwi match {
    case PWIAssn(assn) => toSimpleForm(assn).map(PWIAssn(_))
    case PWIOOBC(_) => Vector(pwi)
  }

  def toSimpleForm(circuit : Vector[ProverWorkItem]) : Vector[ProverWorkItem] = {
    circuit.flatMap(toSimpleForm)
  }
}

object CircuitCompilationPar {
  def split(pwsi : Vector[ProverWorkItem]) : (Vector[SAssign], OOBTranscript) = {
    pwsi.foldLeft[(Vector[SAssign], OOBTranscript)](Vector(),Vector())(
      {
        case ((vs,voob), PWIOOBC(oob)) => (vs, voob :+ oob)
        case ((vs,voob), PWIAssn(assn)) => (vs :+ assn, voob)
      }
    )
  }
}

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

  def compute(comp : Vector[Statement], importantSet : Set[LValue]) : (Set[LValue],Set[LValue]) = {
    multiple(comp, importantSet, Set())
  }
}

class CircuitCompilationPar(comp : SOut, inputSets : Int => Set[VIdentSSA], outputSets : Int => Set[VIdentSSA]) {
  def run():(Vector[Vector[ProverWorkItem]], Vector[Vector[VIdentSSA]], Vector[Vector[VIdentSSA]]) = {
//    var trans : OOBTranscript = ArrayBuffer()
//    var circuit : ArrayBuffer[Statement] = ArrayBuffer()
    val pwiVec : ArrayBuffer[Vector[ProverWorkItem]] = ArrayBuffer()
    val inputWires : ArrayBuffer[Vector[VIdentSSA]] = ArrayBuffer() //inputSets(comp.identity)
    val outputWires : ArrayBuffer[Vector[VIdentSSA]] = ArrayBuffer()//  outputSets(comp.identity)
    var previousOutputSet : Set[VIdentSSA] = Set()
//    val ooo = outputSets(comp.identity)
//    val imp = ComputeIgnoreSet.compute(comp.body,ooo.asInstanceOf[Set[LValue]])._1.asInstanceOf[Set[VIdentSSA]]

    var time = System.currentTimeMillis()
    var tick = 0
    comp.body.foreach(
      s => {
        tick = tick + 1
        val oS = outputSets(s.identity)
        val iS = inputSets(s.identity)
//        val (pwis, oobI, oobO) = new CircuitCompilation(Vector(s), inputSets(s.identity).intersect(imp), oS.intersect(imp),
//          previousOutputSet, imp.asInstanceOf[Set[VIdentSSA]]).run()
        val (pwis, oobI, oobO) = new CircuitCompilation(Vector(s), inputSets(s.identity), oS,
          previousOutputSet).run()
//        previousOutputSet = previousOutputSet ++ oS
        previousOutputSet = oS
        inputWires  append oobI
        outputWires append oobO
        pwiVec append pwis
        if (tick % 100 == 0) {
          val tmp = System.currentTimeMillis()
//          println(-(time - tmp) / 100)
          time = tmp
        }
      }
    )
    (pwiVec.map(ToSimpleForm.toSimpleForm).toVector, inputWires.toVector, outputWires.toVector)
  }
}

object PWSPrinter {

  def printExpr(expr : Expression) : String = expr match {
    case ELValue(lval:VIdent) => lval.name
    case ELValue(lval:VIdentName) => lval.name
    case EBinOp(op, x, y) => {
      val ops = op match {
        case BArithOp(_:BMinus) => "minus"
        case _ => peppermint.Analysis.CPrinter.binopToString(op)
      }
      "%s %s %s".format(
        printExpr(x),
        ops,
        printExpr(y)
      )
    }
    case EConstant(i) => i.toString
  }

  def printStmt(stmt:ProverWorkItem) : String = stmt match {
    case PWIAssn(SAssign(lval:VIdent,expr:Expression)) => "P %s = %s E".format(lval.name, printExpr(expr))
    case PWIAssn(SAssign(lval:VIdentName,expr:Expression)) => "P %s = %s E".format(lval.name, printExpr(expr))
    case PWIOOBC(oobc) => oobc.toString()
  }

  def printStmts(stmts : Vector[ProverWorkItem]): Vector[String] = {
    stmts.map(printStmt)
  }
}

class PWSGen(assns : Vector[ProverWorkItem], inputSet : Set[VIdentSSA], outputSet : Set[VIdentSSA]) {

  var c = 0
  def fresh(init : String) : VIdentName = {
    c = c + 1
    VIdentName("%s%d".format(init,c), None)
  }


  var videntToPWSName : Map[VIdentSSA, VIdentName] = {
    val ins = inputSet.to[Vector].map(i => (i,fresh("I")))
    val outs = outputSet.to[Vector].map(i=>(i,fresh("O")))
    (ins++outs).foldLeft[Map[VIdentSSA,VIdentName]](Map())({
      case (acc, (vi,vii)) => acc.updated(vi,vii)
    })
  }

  def printVIdentSSA(vIdentSSA: VIdentSSA) : VIdentName = {
    videntToPWSName.get(vIdentSSA) match {
      case Some(s) => s
      case None => {
        val t = fresh("V")
        videntToPWSName = videntToPWSName.updated(vIdentSSA, t)
        t
      }
    }
  }

  def pseudoVIdentSSA(vv : VIdentSSA) : VIdentSSA = {
    VIdentSSA(videntToPWSName(vv),0)
  }

  def genStmt(stmt : ProverWorkItem) : ProverWorkItem = stmt match {
    case PWIAssn(SAssign(lval : VIdentSSA, rval : Expression)) => PWIAssn(SAssign(printVIdentSSA(lval), genExpr(rval)))
    case PWIOOBC(oobc) => PWIOOBC(oobc match {
      case BinopOOBC(op,z,x,y) => BinopOOBC(op, pseudoVIdentSSA(z), pseudoVIdentSSA(x), pseudoVIdentSSA(y))
      case WireConst(name, i) => WireConst(pseudoVIdentSSA(name), i)
      case wctmp@WireCopy(a,b) => {
        val wc = WireCopy(pseudoVIdentSSA(a),pseudoVIdentSSA(b))
        wc.oldWiresCurrentName = pseudoVIdentSSA(wctmp.oldWiresCurrentName)
        wc.previousName = wctmp.previousName
        wc
      }
      case MemUncondStore(lval,Vector(inds),vv) =>
        MemUncondStore(lval, Vector(pseudoVIdentSSA(inds)), pseudoVIdentSSA(vv))
      case MemStore(lval, Vector(inds), vv, cond) => {
        MemStore(lval, Vector(pseudoVIdentSSA(inds)), pseudoVIdentSSA(vv), pseudoVIdentSSA(cond))
      }
      case MemFetch(wire, lval, Vector(inds)) =>
        MemFetch(pseudoVIdentSSA(wire), lval, Vector(pseudoVIdentSSA(inds)))
    })
  }

  def genExpr(expr : Expression) : Expression = expr match {
    case ELValue(lval:VIdentSSA) => ELValue(printVIdentSSA(lval))
    case EBinOp(op, x, y) => EBinOp(op, genExpr(x), genExpr(y))
    case EConstant(_) => expr
  }

  def run() : (Vector[ProverWorkItem], Map[VIdentSSA, VIdentName]) = {
    val r1 = assns.map(genStmt(_))
    (r1, videntToPWSName)
  }
}

