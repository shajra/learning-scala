package com.github.shajra.learning.scala.performance


import akka.util.ByteString
import fj.data.{List => FJList}
import fj.F2
import java.util.LinkedList


/** Benchmark to explore datastructures and iterating strategies.
  *
  * Many people make some broad statements about the cost of programming in the
  * functional style.  This benchmark attempts to begin to ground this kind of
  * discussion with numbers.
  *
  * The timing measurements are methods with names prefixed with "time" (Google
  * Caliper uses reflection).
  *
  * All the measurements are for calculating the maximum byte in a collection
  * loaded with a constant number of random bytes.  We're using bytes so we can
  * see how Akka's ByteString fares comparatively.
  *
  * Upon running the benchmark you should see some numbers like:
  *
  * {{{
  *                        benchmark   ms linear runtime
  *       __scala_array__while_array 1.21 ===
  *    __akka_bytestring__while_iter 1.50 ====
  *       __akka_bytestring__foreach 1.66 =====
  *            __scala_list__foreach 3.97 ============
  *         __scala_list__while_iter 4.14 =============
  *       __scala_vector__while_iter 4.34 ==============
  *    __java_linkedlist__while_iter 4.35 ==============
  *           __scala_array__foreach 4.74 ===============
  *      __akka_bytestring__foldleft 5.29 =================
  *          __scala_array__foldleft 5.47 =================
  *          __scala_vector__foreach 6.19 ====================
  *           __scala_list__foldleft 6.35 ====================
  *              __fj_list__foldleft 7.27 =======================
  *         __scala_vector__foldleft 9.20 ==============================
  *}}}
  *
  * A while loop iterating over a mutable array is the control experiment, and
  * it's the fastest we can do this operation on the JVM.
  *
  * Note that the most functional "foldLeft" method is the most slow because of
  * it requires indirection through first-class functions.
  *
  * It's interesting to see how well Akka's bytestring performs, especially
  * when manipulated in an imperative style.
  */
class MaxOfBytesBenchMark extends ScalaBenchmark {

  val SIZE = 1000000

  import FilledCollections._

  val scalaArray = filledScalaArray(SIZE) { randomByte }
  val scalaVector = filledScalaVector(SIZE) { randomByte }
  val scalaList = filledScalaList(SIZE) { randomByte }
  val fjList = filledFjList(SIZE) { randomByte }
  val javaList = filledJavaList(SIZE) { randomByte }
  val akkaByteString = filledAkkaByteString(SIZE) { randomByte }

  def time__scala_array__while_array(reps: Int) =
    repeat(reps)(scalaArrayWhileMax(scalaArray))

  def time__scala_array__foreach(reps: Int) =
    repeat(reps)(scalaForeachMax(scalaArray))

  def time__scala_array__foldleft(reps: Int) =
    repeat(reps)(scalaFoldLeftMax(scalaArray))

  def time__fj_list__foldleft(reps: Int) =
    repeat(reps)(fjFoldLeftMax(fjList))

  def time__java_linkedlist__while_iter(reps: Int) = {
    import scala.collection.JavaConversions._
    repeat(reps)(scalaIterWhileMax(javaList))
  }

  def time__scala_vector__while_iter(reps: Int) =
    repeat(reps)(scalaIterWhileMax(scalaVector))

  def time__scala_vector__foreach(reps: Int) =
    repeat(reps)(scalaForeachMax(scalaVector))

  def time__scala_vector__foldleft(reps: Int) =
    repeat(reps)(scalaFoldLeftMax(scalaVector))

  def time__scala_list__while_iter(reps: Int) =
    repeat(reps)(scalaIterWhileMax(scalaList))

  def time__scala_list__foreach(reps: Int) =
    repeat(reps)(scalaForeachMax(scalaList))

  def time__scala_list__foldleft(reps: Int) =
    repeat(reps)(scalaFoldLeftMax(scalaList))

  def time__akka_bytestring__while_iter(reps: Int) =
    repeat(reps)(scalaIterWhileMax(akkaByteString))

  def time__akka_bytestring__foreach(reps: Int) =
    repeat(reps)(scalaForeachMax(akkaByteString))

  def time__akka_bytestring__foldleft(reps: Int) =
    repeat(reps)(scalaFoldLeftMax(akkaByteString))

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

  def randomByte = scala.util.Random.nextInt.toByte

}


/* A main method so you can run the benchmark in SBT with the 'run' command. */
object MaxOfBytesBenchMark extends App {

  import com.google.caliper.Runner
  Runner.main(classOf[MaxOfBytesBenchMark], args)

}
