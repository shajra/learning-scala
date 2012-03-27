package com.github.shajra.learning.scala.performance


import com.google.common.base.Stopwatch

import fj.data.{List => FJList}
import fj.F2

import java.util.concurrent.TimeUnit._
import java.util.LinkedList

import org.scalatest._

import scala.util.Random
import scala.collection.JavaConversions._


class IterationPerformanceSpec extends FreeSpec
    with OneInstancePerTest
    with matchers.ShouldMatchers {

  val SIZE = 1000000
  val NUM_JVM_WARMUP_TRIALS = 500
  val NUM_TIMED_TRIALS = 10

  "timing collections" in {

    time("scala.Array, while-loop, counter", scalaArray) { array =>
      var acc: Byte = 0
      var counter = 0
      while (counter < array.length) {
        acc = max(acc, array(counter))
        counter += 1
      }
      acc
    }

    time("scala.Array, foreach-loop, counter", scalaArray) { array =>
      var acc: Byte = 0
      array.foreach { value => acc = max(acc, value) }
      acc
    }

    time("fj.data.List, foldLeft1", fjList) {
      _.foldLeft1(
          new F2[Byte, Byte, Byte] {
            override def f(a: Byte, b: Byte): Byte = max(a, b)
          }.curry
        )
    }

    time("java.util.LinkedList, while-loop, iterator", javaList) { list =>
      var acc: Byte = 0
      val iter = list.iterator
      while (iter.hasNext) { acc = max(acc, iter.next) }
      acc
    }

    time("scala.collection.immutable.List, while-loop, iterator",
         scalaList) { list =>
      var acc: Byte = 0
      val iter = list.iterator
      while (iter.hasNext) { acc = max(acc, iter.next) }
      acc
    }

    time("scala.collection.immutable.List, foldLeft", scalaList) { list =>
      list.foldLeft(0: Byte) { max(_: Byte, _: Byte) }
    }

    time("scala.collection.immutable.Vector, while-loop, iterator",
         scalaVector) { vector =>
      var acc: Byte = 0
      val iter = vector.iterator
      while (iter.hasNext) { acc = max(acc, iter.next) }
      acc
    }

    time("scala.collection.immutable.Vector, foldLeft", scalaVector) { vector =>
      vector.foldLeft(0: Byte) { max(_: Byte, _: Byte) }
    }

  }

  val scalaArray: Array[Byte] = {
    val array = new Array[Byte](SIZE)
    Random.nextBytes(array)
    array
  }

  val scalaList: List[Byte] = List(scalaArray:_*)

  val scalaVector: Vector[Byte] = Vector(scalaArray:_*)

  def fjList: FJList[Byte] = {
    var list = FJList.nil[Byte]
    scalaVector.foreach { i => list = list.cons(i) }
    list
  }

  def javaList: LinkedList[Byte] = {
    val list = new LinkedList[Byte]()
    scalaVector.foreach { i => list.add(i) }
    list
  }

  def time[S, T[_]](msg: String, col: T[S])(calc: T[S] => S) {
    time(NUM_JVM_WARMUP_TRIALS, calc(col))
    val ms = time(NUM_TIMED_TRIALS, calc(col))
    println(msg + ": " + ms + " ms")
  }

  def time[S, T[_]](n: Int, calc: => S) = {
    Vector.fill(n) {
      val sw = new Stopwatch()
      sw.start()
      calc
      sw.stop()
      sw.elapsedTime(MICROSECONDS) / 1000.0
    }.sum / n
  }

  def max(a: Byte, b: Byte) = if (a > b) a else b

}
