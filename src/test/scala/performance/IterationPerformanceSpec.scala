package com.github.shajra.learning.scala.performance


import com.google.common.base.Stopwatch

import fj.data.{List => FJList}
import fj.function.Integers

import java.lang.{Integer => JInteger}
import java.util.concurrent.TimeUnit._
import java.util.LinkedList

import org.scalatest.FunSuite

import scala.collection.JavaConversions._


class IterationPerformanceSpec extends FunSuite {

  val SUM_TO = 1000000

  val NUM_JVM_WARMUP_TRIALS = 500

  test("timing collections") {

    time("scala.Array, while-loop, counter", scalaArray) { array =>
      var acc = 0
      var counter = 0
      while (counter < array.length) {
        acc += array(counter)
        counter += 1
      }
      acc
    }

    time("scala.Array, foreach-loop, counter", scalaArray) { array =>
      var acc = 0
      array.foreach { value => acc += value }
      acc
    }

    time("fj.data.List, foldLeft1", fjList) { _.foldLeft1(Integers.add) }

    time("java.util.LinkedList, while-loop, iterator", javaList) { list =>
      var acc = 0
      val iter = list.iterator
      while (iter.hasNext) { acc += iter.next }
      acc
    }

    time("scala.collection.immutable.List, while-loop, iterator",
         scalaList) { list =>
      var acc = 0
      val iter = list.iterator
      while (iter.hasNext) { acc += iter.next }
      acc
    }

    time("scala.collection.immutable.List, foldLeft", scalaList) { list =>
      list.foldLeft(0) { (_: Int) + (_: Int) }
    }

    time("scala.collection.immutable.Vector, while-loop, iterator",
         scalaVector) { vector =>
      var acc = 0
      val iter = vector.iterator
      while (iter.hasNext) { acc += iter.next }
      acc
    }

    time("scala.collection.immutable.Vector, foldLeft", scalaVector) { vector =>
      vector.foldLeft(0) { (_: Int) + (_: Int) }
    }

  }

  def fjList: FJList[JInteger] = {
    var list = FJList.nil[JInteger]
    (1 to SUM_TO).foreach {
      i => list = list.cons(i)
    }
    list
  }

  def javaList: LinkedList[Int] = {
    val list = new LinkedList[Int]()
    (1 to SUM_TO).foreach {
      i => list.add(i)
    }
    list
  }

  val scalaList: List[Int] = List.range(0, SUM_TO)

  val scalaArray: Array[Int] = Array.range(0, SUM_TO)

  val scalaVector: Vector[Int] = Vector.range(0, SUM_TO)

  def time[S, T[_]](msg: String, col: T[S])(calc: T[S] => S) {
    val sw = new Stopwatch()
    warmup(calc(col))
    sw.start()
    calc(col)
    sw.stop()
    val time = sw.elapsedTime(MICROSECONDS) / 1000.0
    println(msg + ": " + time + " ms")
  }

  def warmup[T](calc: => T) =
    for (_ <- 1 to NUM_JVM_WARMUP_TRIALS) yield calc

}
