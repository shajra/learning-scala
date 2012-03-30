package com.github.shajra.learning.scala.performance


import akka.util.ByteString
import fj.data.{List => FJList}
import fj.F2
import java.util.concurrent.TimeUnit._
import java.util.LinkedList
import scala.collection.JavaConversions._
import scala.util.Random


object MaxOfBytes extends App {

  val SIZE = 1000000
  val NUM_JVM_WARMUP_TRIALS = 500
  val NUM_TIMED_TRIALS = 10

  val scalaArray = Collections.filledScalaArray(SIZE) { randomByte }
  val scalaVector = Collections.filledScalaVector(SIZE) { randomByte }
  val scalaList = Collections.filledScalaList(SIZE) { randomByte }
  val fjList = Collections.filledFjList(SIZE) { randomByte }
  val javaList = Collections.filledJavaList(SIZE) { randomByte }
  val akkaByteString = Collections.filledAkkaByteString(SIZE) { randomByte }

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

  def time[S, T[_]](msg: String, col: T[S])(calc: T[S] => S) {
    import com.github.shajra.learning.scala.timing.Timing._
    val ms = timeWithWarmup(
      numTimedTrials=NUM_TIMED_TRIALS,
      numWarmupTrials=NUM_JVM_WARMUP_TRIALS)(calc(col))
    println(msg + ": " + ms + " ms")
  }

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
    col.foldLeft(0: Byte) { max(_ , _) }

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

  def randomByte = Random.nextInt.toByte

}
