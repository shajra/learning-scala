package com.github.shajra.learning.scala.performance


import akka.util.ByteString
import com.google.common.base.Stopwatch
import fj.data.{List => FJList}
import fj.F2
import java.util.concurrent.TimeUnit._
import java.util.LinkedList
import org.scalatest._
import scala.collection.JavaConversions._
import scala.util.Random


object Collections {

  val SIZE = 1000000

  val scalaArray: Array[Byte] = {
    val array = new Array[Byte](SIZE)
    Random.nextBytes(array)
    array
  }

  val scalaList: List[Byte] = List(scalaArray:_*)

  val scalaVector: Vector[Byte] = Vector(scalaArray:_*)

  val akkaByteString: ByteString = ByteString(scalaArray)

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

}


object Operations {

  def scalaArrayWhileMax(col: Array[Byte]) = {
    var acc: Byte = 0
    var counter = 0
    while (counter < col.length) {
      acc = max(acc, col(counter))
      counter += 1
    }
    acc
  }

  def scalaForeachMax(col: Traversable[Byte]) = {
    var acc: Byte = 0
    col.foreach { value => acc = max(acc, value) }
    acc
  }

  def scalaFoldLeftMax(col: Traversable[Byte]) =
    col.foldLeft(0: Byte) { max(_: Byte, _: Byte) }

  def scalaIterWhileMax(col: Iterable[Byte]) = {
    var acc: Byte = 0
    val iter = col.iterator
    while (iter.hasNext) { acc = max(acc, iter.next) }
    acc
  }

  def fjFoldLeftMax(col: FJList[Byte]) =
    col.foldLeft(
        new F2[Byte, Byte, Byte] {
          override def f(a: Byte, b: Byte): Byte = max(a, b)
        }.curry, 0: Byte)

  def max(a: Byte, b: Byte) = if (a > b) a else b

}


class IterationPerformanceSpec extends FreeSpec
    with OneInstancePerTest
    with matchers.ShouldMatchers {

  val NUM_JVM_WARMUP_TRIALS = 100
  val NUM_TIMED_TRIALS = 10

  import Collections._
  import Operations._

  "timing collections" in {

    time("scala.Array, while-array", scalaArray) { c =>
      scalaArrayWhileMax(c)
    }

    time("scala.Array, foreach", scalaArray) { c =>
      scalaForeachMax(c)
    }

    time("scala.Array, foldLeft", scalaArray) { c =>
      scalaFoldLeftMax(c)
    }

    time("fj.data.List, foldLeft", fjList) { c =>
      fjFoldLeftMax(c)
    }

    time("java.util.LinkedList, while-iter", javaList) { c =>
      scalaIterWhileMax(c)
    }

    time("scala.collection.immutable.Vector, while-iter", scalaVector) { c =>
      scalaIterWhileMax(c)
    }

    time("scala.collection.immutable.Vector, foreach", scalaVector) { c =>
      scalaForeachMax(c)
    }

    time("scala.collection.immutable.Vector, foldLeft", scalaVector) { c =>
      scalaFoldLeftMax(c)
    }

    time("scala.collection.immutable.List, while-iter", scalaList) { c =>
      scalaIterWhileMax(c)
    }

    time("scala.collection.immutable.List, foreach", scalaList) { c =>
      scalaForeachMax(c)
    }

    time("scala.collection.immutable.List, foldLeft", scalaList) { c =>
      scalaFoldLeftMax(c)
    }

    time("akka.util.ByteString, while-iter", akkaByteString) { c =>
      scalaIterWhileMax(c)
    }

    time("akka.util.ByteString, foreach", akkaByteString) { c =>
      scalaForeachMax(c)
    }

    time("akka.util.ByteString, foldLeft", akkaByteString) { c =>
      scalaFoldLeftMax(c)
    }

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

}
