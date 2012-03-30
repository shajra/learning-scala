package com.github.shajra.learning.scala.timing


import com.google.common.base.Stopwatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit._


object Timing {

  def timeWithWarmup[A]
      (numTimedTrials: Int,
        numWarmupTrials: Int,
        unit: TimeUnit=MICROSECONDS)
      (calc: => A) = {
    time(numTrials=numWarmupTrials, unit=unit)(calc)
    time(numTrials=numTimedTrials)(calc)
  }

  def time[A]
      (numTrials: Int,
        unit: TimeUnit=MICROSECONDS)
      (calc: => A) =
    Vector.fill(numTrials) {
      val sw = new Stopwatch()
      sw.start()
      calc
      sw.stop()
      sw.elapsedTime(MICROSECONDS) / 1000.0
    }.sum / numTrials

}
