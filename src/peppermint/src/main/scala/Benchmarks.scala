import peppermint.Analysis.Plumbing
import peppermint.ILAST._

object BenchmarksUtil {
  def findZebraOutsource(stmt:Statement):Vector[SZebraOutsource] = stmt match {
    case x:SZebraOutsource => Vector(x)
    case SForGrouped(g) => findZebraOutsource(g)
    case SGrouped(g) => findZebraOutsource(g)
    case SIf(_,t,e) => findZebraOutsource(t) ++ findZebraOutsource(e)
    case _ => Vector()
  }

  def findZebraOutsource(vec:Vector[Statement]):Vector[SZebraOutsource] = {
    vec.foldLeft(Vector[SZebraOutsource]())((acc,s)=>acc++findZebraOutsource(s))
  }
}

object GiraffeSquashingBenchmark {
  // assuming input vector contains one and only one SZebraOutsource
  def findOutsourceCost(vec : Vector[Statement]) : Double = {
    val outs = BenchmarksUtil.findZebraOutsource(vec)
    outs.head.costForOutsource
  }

  def benchmark(vec:Vector[Statement], filename : String, rest : Vector[String] = Vector()) = {
    val outs = BenchmarksUtil.findZebraOutsource(vec)
    val c = outs.head.costForOutsource
    val copies = outs.head.numOfCopies
    val s = rest ++ Vector("%.0f".format(c), "%d".format(copies))
    val str = "%s\n".format(s.mkString(" "))
    Plumbing.writeStringToFile(str, filename, true)
  }

}
