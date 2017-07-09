
package peppermint.RWSetCompute

import peppermint.ILAST._
object Timer {
  def time[R](block: => R, comment : String = ""): R = {
    val t0 = System.currentTimeMillis()
    println("Enter block, ", comment)
    val result = block // call-by-name
    val t1 = System.currentTimeMillis()
    println("Elapsed time: " + (t1 - t0) / 1000 + "s", comment)
    result
  }

}
object RWSetCompute {
  def isOutput(lval: LValue): Boolean = lval match {
    //    case VIdent(n, _) => n == "output"
    case VIdentSSA(VIdent(n, _), _) => n == "output"
    case VIdent(n,_) => n == "output"
    case VIdentName(n,_) => n.splitAt(6)._1 == "output"
  }
  def findOutputSet(vs : Vector[Statement], isExternOutput : LValue => Boolean,
                    readSets : Map[Int,Set[LValue]], writeSets : Map[Int,Set[LValue]]
                   ) : Map[Int,Set[LValue]] = {
    // first is a mapping id -> the read set of statements immediately after id
//    val record = computeRWSetRecordWithRW(vs, readSets, writeSets, isExternOutput)
//    x => {
//      record.findOutputSetOfIdentity(x)
//    }

    import scala.collection.mutable.{Map => MMap, Set}
    import scala.collection.immutable.{Set => ISet}
    val mout = MMap[Int,ISet[LValue]]()
    val currentRead : Set[LValue] =  Set()
    val currentWrite : Set[LValue] = Set()
    var i = 0
    var tt : Long = 0

    vs.foldRight()(
      (stmt,_) => {
        val sr = readSets(stmt.identity)
        val sw = writeSets(stmt.identity)
        val t1 = System.currentTimeMillis()
        val out = sw.filter(x => !currentWrite(x) && isExternOutput(x)).union(sw.filter(currentRead))
        val t2 = System.currentTimeMillis()
        tt = tt + t2 - t1
        mout.update(stmt.identity, out)
        sr.foreach(currentRead.update(_,true))
        sw.foreach(currentWrite .update(_,true))
        i += 1
      }
    )
    println("Immutable time ", tt / 1000)
    mout.toMap
  }

  def computeRWSetRecordWithRW(comp : Vector[Statement],
                   r : Map[Int,Set[LValue]],
                   w : Map[Int,Set[LValue]],
                   isExternOutput : LValue => Boolean = isOutput) : RWSetRecord = {
    new RWSetRecord(comp,r,w, isExternOutput)
  }


  def computeRWSet(comp : Vector[Statement], isExternOutput : LValue => Boolean = isOutput) : RWSetRecord = {
    val (r,w) = Timer.time(new RWSetCompute(comp).run(), "RWSetCompute")
    Timer.time(new RWSetRecord(comp,r,w, isExternOutput), "RWSetRecord")
  }
}

class RWSetComputeMutable(comp : Vector[Statement]) {
  import scala.collection.mutable.{ Set => MSet, Map => MMap}

  def run() : (Map[Int, Set[LValue]], Map[Int, Set[LValue]]) = {
    val rmap : MMap[Int, Set[LValue]] = MMap()
    val wmap : MMap[Int, Set[LValue]] = MMap()

    comp.foreach(stmt => {
      val idx = stmt.identity
      val rs = MSet[LValue]()
      val ws = MSet[LValue]()
      dfsStmt(stmt, rs, ws)
      rmap.update(idx, rs.toSet)
      wmap.update(idx, ws.toSet)
    })
    (rmap.toMap, wmap.toMap)
  }

  def dfsExpr(expr : Expression, r : MSet[LValue]) : Unit = expr match {
    case EConstant(_) => ()
    case EUniOp(_, x) => dfsExpr(x,r)
    case EBinOp(_,x,y) => dfsExpr(x,r) ; dfsExpr(y,r)
    case ESelect(b,x,y) => dfsExpr(b,r);  dfsExpr(x,r);  dfsExpr(y,r)
    case EFetch(_, inds) => inds.foreach(e=>dfsExpr(e, r))
    case ELValue(l) => r.update(l, true)
  }

  def dfsStmts(stmts : Vector[Statement], rs : MSet[LValue], ws : MSet[LValue]) : Unit = {
    stmts.reverseIterator.foreach(dfsStmt(_, rs, ws))
  }

  def dfsStmt(stmt : Statement, rs : MSet[LValue], ws : MSet[LValue]) : Unit = stmt match {
    case SCondStore(_, inds, v, cond) => {
      (v+: (inds  ++ cond.to[Vector])).foreach(e=>dfsExpr(e,rs))
    }
    case SAssign(l,r) => {
      // remove l from rs
      rs.update(l, false)
      dfsExpr(r, rs)
      ws.update(l, true)
    }
    case SIf(b,es,ts) => {
      dfsExpr(b, rs)
      val rs_before = rs.clone
      val ws_before = ws.clone
      dfsStmts(es, rs, ws)
      dfsStmts(ts, rs_before, ws_before)
      rs_before.foreach(rs.update(_,true))
      ws_before.foreach(ws.update(_,true))
    }
    case SGrouped(v) => dfsStmts(v, rs, ws)
    case SForGrouped(v) => dfsStmts(v, rs, ws)
    case SOut(v) => dfsStmts(v, rs, ws)
    case _ => ()
  }
}



class RWSetCompute(comp:Vector[Statement]) {
  var readSets : Map[Int, Set[LValue]] = Map()
  var writeSets : Map[Int, Set[LValue]] = Map()


  def run() : (Map[Int,Set[LValue]],Map[Int,Set[LValue]]) = {
    dfsStmts(comp)
    val idSet = comp.map(_.identity).toSet
    (readSets.filter(x=>idSet(x._1)), writeSets.filter(x=>idSet(x._1)))
  }

  def dfsExpr(expr : Expression) : Set[LValue] = expr match {
    case EConstant(_) => Set()
    case EUniOp(_, x) => dfsExpr(x)
    case EBinOp(_,x,y) => dfsExpr(x) ++ dfsExpr(y)
    case ESelect(b,x,y) => dfsExpr(b) ++ dfsExpr(x) ++ dfsExpr(y)
    case EFetch(_, inds) => inds.foldLeft[Set[LValue]](Set())((acc,e)=>acc++dfsExpr(e))
    case ELValue(l) => Set(l)
  }

  def record(id : Int, rS : Set[LValue], wS : Set[LValue]) : Unit = {
    readSets = readSets.updated(id, rS)
    writeSets = writeSets.updated(id, wS)
  }

  def mergeVecss(vecss : Vector[(Set[LValue],Set[LValue])]) : (Set[LValue], Set[LValue]) = {
    if (vecss.isEmpty)
      (Set(),Set())
    else if (vecss.size == 1)
      vecss.head
    else {
      val mid = vecss.size / 2
      RWSetRecord.merge(mergeVecss(vecss.slice(0,mid)), mergeVecss(vecss.slice(mid,vecss.size)))
    }
  }

  def dfsStmts(stmts : Vector[Statement]) : (Set[LValue], Set[LValue]) = {
    val vecSS = stmts.map(dfsStmt)
    mergeVecss(vecSS)

    //    val record = new RWSetRecord(stmts,readSets,writeSets, _ => true)
    //    record.findReadWriteSet(0,stmts.length-1)

    //    val (x,y) =
    //    stmts.foldRight[(Set[LValue],Set[LValue])](Set(),Set())(
    //      {
    //        case (stmt,(cr,cw)) => {
    //          val (rs, wstmp) = dfsStmt(stmt)
    ////          val outputS = wstmp.filter(RWSetCompute.isOutput).union(wstmp.intersect(cr))
    ////          println(cr.size, cw.size)
    ////          (rs union (cr diff wstmp), wstmp union cw)
    //          (rs.union(cr diff wstmp),wstmp union cw)
    //        }
    //      }
    //    )
    //    (x,y)
  }

  def dfsStmt(stmt : Statement) : (Set[LValue], Set[LValue]) = stmt match {
    case SCondStore(_, inds, v, cond) => {
      val reads = (v+: (inds  ++ cond.to[Vector])).foldLeft[Set[LValue]](Set())((acc,e)=>acc++dfsExpr(e))
      record(stmt.identity, reads, Set())
      (reads, Set())
    }
    case SAssign(l,r) => {
      val rs = dfsExpr(r)
      val ws = Set(l)
      record(stmt.identity, rs, ws)
      (rs, ws)
    }
    case SIf(b,es,ts) => {
      val rsb = dfsExpr(b)
      val (esr, esw) = dfsStmts(es)
      val (tsr, tsw) = dfsStmts(ts)
      val (rs, ws) = (rsb ++ esr ++ tsr, esw ++ tsw)
      record(stmt.identity, rs, ws)
      (rs, ws)
    }
    case SGrouped(v) => {
      val (rs,ws) = dfsStmts(v)
      record(stmt.identity, rs, ws)
      (rs,ws)
    }
    case SForGrouped(v) => {
      val (rs,ws) = dfsStmts(v)
      record(stmt.identity, rs, ws)
      (rs,ws)
    }
    case SOut(v) => {
      val (rs,ws) = dfsStmts(v)
      record(stmt.identity, rs, ws)
      (rs,ws)
    }
    case _ => {
      record(stmt.identity, Set(), Set())
      (Set(),Set())
    }
  }
}

object RWSetRecord {
  type node = (Set[LValue], Set[LValue])
  def merge(x: node, y: node): node = {
    val ret = (x._1.union(y._1.diff(x._2)), x._2.union(y._2))
    ret
  }
}

class RWSetRecord(comp : Vector[Statement], readSets : Map[Int,Set[LValue]], writeSets : Map[Int,Set[LValue]],
                  isExternOutput : LValue => Boolean) {

  import segtree._
  import RWSetRecord._


  private def merge(x: node, y: node): node = {
    val ret = (x._1.union(y._1.diff(x._2)), x._2.union(y._2))
    ret
  }

  private val identityToIndexMap: Map[Int, Int] = {
    comp.zipWithIndex.foldLeft(Map[Int, Int]())({
      (m, si) => m.updated(si._1.identity, si._2)
    })
  }

  private val segTree: TreeNode[node] = {
    val emptyTree = SegTree.buildTree[node](0, comp.length - 1, (Set(), Set()))
    comp.foldLeft[(TreeNode[node], Int)]((emptyTree, 0)) {
      case ((t, idx), stmt) => (SegTree.insertPoint(t, idx, (readSets(stmt.identity), writeSets(stmt.identity)), merge)._1, idx + 1)
    }._1
  }

  def findReadWriteSetOfIdentity(idx: Int): (Set[LValue], Set[LValue]) = {
    val i = identityToIndexMap(idx)
    findReadWriteSet(i, i)
  }

  def findOutputSetOfIdentity(idx: Int): Set[LValue] = {
    val i = identityToIndexMap(idx)
    findOutputSet(i, i)
  }

  def findReadWriteSet(i: Int, j: Int): (Set[LValue], Set[LValue]) = {
    if (comp.isEmpty) (Set(),Set()) else SegTree.query(segTree, i, j, merge)
  }

  def findOutputSet(i: Int, j: Int): Set[LValue] = {
    if (comp.isEmpty)
      Set()
    else {
      val write = findReadWriteSet(i, j)._2
      val (readAfter, writeAfter): (Set[LValue], Set[LValue]) =
        if (j == comp.length - 1) (Set(), Set()) else findReadWriteSet(j + 1, comp.length - 1)
      write.intersect(readAfter).union(write.diff(writeAfter).filter(isExternOutput))
    }
  }
}
