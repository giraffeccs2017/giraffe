
import peppermint.ILAST._
import ConstProp._
import peppermint.SSATransform._
import peppermint.CircuitCompilation._
import peppermint.CMTAnalysis._
import peppermint.RWSetCompute._
import peppermint.Analysis._
import peppermint.Parser._

object AutomaticCutting {
  def isOutput(lval: VIdentSSA): Boolean = lval match {
    //    case VIdent(n, _) => n == "output"
    case VIdentSSA(VIdent(n, _), _) => n == "output"
  }
  def runOne(prog:Program) : Unit = {
    println(new AutomaticCutting(prog).runOne())
  }

  def run(prog : Program) : Unit = {
    new AutomaticCutting(prog).run()
  }

  def main(args : Array[String]) = {
    //runOne(CParserPlus.parseFile(args(0)))
    run(new Cleaner(CParserPlus.parseFile("apps/hello_world.c")).cleanFunctions)
  }
}

class AutomaticCutting(prog : Program) {

//  var ws : Map[Int, Set[VIdentSSA]] = Map()
//  var rs : Map[Int, Set[VIdentSSA]] = Map()

  def run() : Unit = {
//    val main = peppermint.Analysis.Utility.findMain(prog)
//    val constprop = ConstProp.rewriteStmts(main.body)
//    val ssa = new SSATransform(constprop).run()
//    val (rs,ws) = new RWSetCompute(ssa).run().asInstanceOf[(Map[Int,Set[VIdentSSA]],Map[Int,Set[VIdentSSA]])]
//    val (_, mout) = RWSetCompute.findOutputSet[VIdentSSA](ssa, AutomaticCutting.isOutput, rs, ws)
////    println(CPrinter.printStatements(constprop))
//    ssa.map(s => s match
//      {
//        case SOut(v) => {
//          val outputSet = mout(s.identity)
//          val (_, innerOutMap) = RWSetCompute.findOutputSet[VIdentSSA](v, outputSet.contains, rs, ws)
//          val x = new CircuitCompilationPar(s.asInstanceOf[SOut], rs, innerOutMap.updated(s.identity,outputSet)).run()
//          val circ = CircuitCompilationPar.split(x._1.flatten)
//          circ._1.foreach(x => println(CPrinter.printStatement(x)))
//          circ._2.foreach(println)
//          s
//        }
//        case _ => s
//      }
//    )
  }


  def runOne() : String = {
    val main = peppermint.Analysis.Utility.findMain(prog)
    val constprop = ConstProp.rewriteStmts(main.body)
    val ssa = SOut(Vector(SGrouped(new SSATransform(constprop).run())))
    val (rm,wm) = new RWSetCompute(Vector(ssa)).run().asInstanceOf[(Map[Int,Set[VIdentSSA]],Map[Int,Set[VIdentSSA]])]
    val ss = wm.mapValues(x => x.filter(AutomaticCutting.isOutput(_)))
    println(wm(ssa.identity))
    println(rm(ssa.identity))
    println(ss(ssa.identity))
    val x = new CircuitCompilationPar(ssa,rm,ss).run()
    val circ = CircuitCompilationPar.split(x._1.flatten)
    circ._2.foreach(println)
//    val circStrs = new PWSGen(circ._1, rm(ssa.identity).union(x._2), ss(ssa.identity).union(x._3)).run()
//    PWSPrinter.printStmts(circStrs._1).foldLeft("")((acc,str)=>acc+"\n"+str)
    ???
  }


  def runMultiple() : Unit = {

  }
}
