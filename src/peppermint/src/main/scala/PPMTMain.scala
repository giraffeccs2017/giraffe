
import peppermint.CMTAnalysis
import peppermint.Analysis

object PPMTMain {
  def main(args : Array[String]) : Unit = {
    if (args(0) == "zaatar") {
      Analysis.Plumbing.main2(args.tail)

    } else if (args(0) == "zebra") {
      CMTAnalysis.CMTMain.main2(args.tail)
    }
  }
}
