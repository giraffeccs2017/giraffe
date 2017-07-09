import scala.math._


object LogCeil {
  def logceil(x:Int) : Int = ceil(log(x)/log(2)).toInt
}

object ZebraVCost {
  import LogCeil._
  def costWithWidths(depth:Int,widths:Vector[Int],inputCount:Int,outputCount:Int,copies:Int,gateTypes:Int=3):(Int,Int)
  = {
    val k = inputCount
    val l = outputCount
    val g = gateTypes
    val t = copies
    val d = depth
    val y = widths.map(x => logceil(x*t)).sum
    val mulCount = -8+d+d*g+3*k*t+3*l*t+logceil(l*t)+5*y
    val addCount = 2+3*d+d*g+k*t+l*t+2*logceil(k*t)+5*logceil(l*t)+15*y
    (mulCount,addCount)
  }
  //      B3         B4          B6             B7               B9             B2
  def cost(depth:Int, width:Int, inputCount:Int, outputCount:Int, copies:Int, gateTypes:Int = 3) : (Int,Int) = {
    // B4
    val gp = logceil(width)
    // B11
    val G = copies * width
    // B12
    val g = logceil(G)
    // B10
    val b = logceil(copies)
    // B13
    val S = G*depth
    // B14
    val s = logceil(S)
    // B15
    val totInputs = inputCount*copies
    // B16
    val logTotInputs = logceil(totInputs)
    // B17
    val totOutputs = outputCount*copies
    // B18
    val logTotOutputs = logceil(totOutputs)
    // B23
    val inputMuls = 3 * totInputs - 4
    // B24
    val inputAdds = totInputs - 1
    // B25
    val inputSubs = logTotInputs + 1
    // B28
    val outputMuls = 3 * totOutputs - 4 + logTotOutputs
    // B29
    val outputAdds = totOutputs - 1 + logTotOutputs
    // B30
    val outputSubs = 2 * logTotOutputs + 1
    // F34
    val numInvocations = depth
    // F35
    val roundsPerInvok = 2 * g
    // F38
    val totRounds = numInvocations * roundsPerInvok
    //   F40          F41         F42
    val (mulPerRound,addPerRound,subPerRound) = (2,5,1)
    //  F47                F48
    val (betaMulsPerRound,betaAddsPerRound) = (0,0)
    //  F49        F50     F51
    val (tvMuls, tvAdds, tvSubs) = (gateTypes+1,gateTypes,2)
    //    F52      F53      F54
    val (rtpMuls, rtpAdds, rtpSubs) = (g,g-1,0)
    val totSubs = totRounds * subPerRound + numInvocations * (tvSubs + rtpSubs) + inputSubs + outputSubs

    val (totMuls,totAdds) : (Int,Int) = (
      totRounds * mulPerRound + numInvocations * (betaAddsPerRound+tvMuls+rtpMuls) + inputMuls + outputMuls,
      totRounds * addPerRound + numInvocations * (betaAddsPerRound+tvAdds+rtpAdds) + 2 * totSubs + inputAdds + outputAdds
      )
    (totMuls,totAdds)
  }

  def main(args:Array[String]): Unit = {
    println(cost(15,8,8,8,2048))
    println(costWithWidths(15,Vector.range(0,15).map(_=>8),8,8,2048))
    println(cost(15,16,16,16,4096))
    println(costWithWidths(15,Vector.range(0,15).map(_=>16),16,16,4096))
  }
}

object GiraffeVCost {
  import LogCeil._

  def costWithWidths2( depth:Int,
                       widths:Vector[Int],
                       inputCount:Int,
                       outputCount:Int,
                       copies:Int,
                       gateTypes:Int = 3) : (Int,Int) = {
    // taken from sweval-plan3.md, commit 0bdd79555a12dfc092be0846e35e0d308c0a1bc4
    val I = inputCount * copies
    val b_i = logceil(I)
    val O = outputCount * copies
    val b_o = logceil(O)
    val C = copies
    val b_c = logceil(C)
    val typ = gateTypes

    val muls = 2 * (I - 1) + 2 * (O - 1) +
      widths.map(G => {
          val b_g = logceil(G)
          6 * G + 6 * b_g + 5 * b_c + typ
      }).sum

    val adds = I + b_i - 1 + O + b_o - 1 +
      widths.map(G => {
        val b_g = logceil(G)
        G + 15 * b_g + 11 * b_c + typ
      }).sum

    (muls, adds)
  }

  def costWithWidths(
                      depth:Int,
                      widths:Vector[Int],
                      inputCount:Int,
                      outputCount:Int,
                      copies:Int,
                      gateTypes:Int = 3) : (Int,Int) = {
    // this is the same thing as in cost which is taken from the spreasheet, now outdated
    val k = inputCount
    val l = outputCount
    val g = gateTypes
    val t = copies
    val d = depth
    val x = widths.map(logceil).sum
    val y = widths.map(x => logceil(x*t)).sum
    val z = widths.sum
    val mulCount = -8 - d + d * g + 3 * k * t + 3 * l * t + 6 * z + 3 * d * logceil(t) +
      logceil(l*t) + 6 * x + 2 * y
    val addCount = 2 + 6 * d + d * g + k * t + l * t + z +
      9 * d * logceil(t) + 2 * logceil(k * t) + 5 * logceil(l * t) + 17 * x + 4 * y
    (mulCount, addCount)
  }
  //      B3         B4          B6             B7               B9             B2
  def cost(depth:Int, width:Int, inputCount:Int, outputCount:Int, copies:Int, gateTypes:Int = 3) : (Int,Int) = {
    // B5
    val gp = logceil(width)
    // B11
    val G = copies * width
    // B12
    val g = logceil(G)
    // B10
    val b = logceil(copies)
    // B13
    val S = G*depth
    // B14
    val s = logceil(S)
    // B15
    val totInputs = inputCount*copies
    // B16
    val logTotInputs = logceil(totInputs)
    // B17
    val totOutputs = outputCount*copies
    // B18
    val logTotOutputs = logceil(totOutputs)
    // B23
    val inputMuls = 3 * totInputs - 4
    // B24
    val inputAdds = totInputs - 1
    // B25
    val inputSubs = logTotInputs + 1
    // B28
    val outputMuls = 3 * totOutputs - 4 + logTotOutputs
    // B29
    val outputAdds = totOutputs - 1 + logTotOutputs
    // B30
    val outputSubs = 2 * logTotOutputs + 1
    // K34
    val numInvocations = depth
    // K36
    val qrdsPerInvok = 2 * gp
    // K37
    val crdsPerInvok = b
    // K35
    val roundsPerInvok = crdsPerInvok + qrdsPerInvok
    // K38
    val totRounds = numInvocations * roundsPerInvok
    //   K40          K41         K42
    val (mulPerRound,addPerRound,subPerRound) = (2,5,1)
    //  K43      K44     K45
    val (cMulPR, cAddPR, cSubPR) = (3,7,1)
    //  K47                K48
    val (betaMulsPerRound,betaAddsPerRound) = (2*g-1,4*g)
    //  K49        K50     K51
    val (tvMuls, tvAdds, tvSubs) = (2*3*width+gateTypes+1,width+gateTypes,2)
    //    K52      K53      K54
    val (rtpMuls, rtpAdds, rtpSubs) = (2*gp-1,gp*3,1)
    val totSubs = numInvocations * (qrdsPerInvok * subPerRound + crdsPerInvok*cSubPR+tvSubs+rtpSubs) + inputSubs + outputSubs

    val (totMuls,totAdds) : (Int,Int) = (
      numInvocations*(qrdsPerInvok*mulPerRound+crdsPerInvok*cMulPR+betaMulsPerRound+tvMuls+rtpMuls) + inputMuls + outputMuls,
      numInvocations*(qrdsPerInvok*addPerRound+crdsPerInvok*cAddPR+betaAddsPerRound+tvAdds+rtpAdds) + 2 * totSubs + inputAdds + outputAdds
      )
    (totMuls,totAdds)
  }

  def main(args:Array[String]): Unit = {
    println(cost(15,8,8,8,2048))
    println(costWithWidths(15,Vector.range(0,15).map(_=>8),8,8,2048))
    println(costWithWidths2(15,Vector.range(0,15).map(_=>8),8,8,2048))
    println(cost(15,16,16,16,1024))
    println(costWithWidths(15,Vector.range(0,15).map(_=>16),16,16,1024))
  }
}
