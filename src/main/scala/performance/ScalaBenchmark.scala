package com.github.shajra.learning.scala.performance


import com.google.caliper.SimpleBenchmark


/** A simple Scala wrapper for Google Caliper .*/
trait ScalaBenchmark extends SimpleBenchmark {

  def repeat[@specialized A](reps: Int)(snippet: => A) = {
    val zero = 0.asInstanceOf[A]
    var i = 0
    var result = zero
    while (i < reps) {
      val res = snippet
      if (res != zero) result = res
      i = i + 1
    }
    result
  }

}
