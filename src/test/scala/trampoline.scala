package com.github.shajra.learning.scala
package trampoline


import annotation.tailrec

import org.scalatest.FreeSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.prop.PropertyChecks


/** Illustrating ScalaZ's trampolining. */
class TrampolineSpec extends FreeSpec with ShouldMatchers {

  """implementing function to find "triangle" numbers""" - {

    "a niave implementation can overflow stack" in {

      def triangle(n: Int): Int =
        if (n < 2) n
        else triangle(n - 1) + n

      triangle(SmallSize) should be (safeTriangle(SmallSize))

      intercept[StackOverflowError] { triangle(LargeSize) }

    }

    "Scala's standard Future can overflow stack" in {

      import concurrent.Future
      import concurrent.ExecutionContext.Implicits.global

      import concurrent.Await.result
      import concurrent.duration._

      def triangle(n: Int): Future[Int] =
        if (n < 2) Future(n)
        else triangle(n - 1) map { _ + n }

      result(triangle(SmallSize), 1 millis) should be (safeTriangle(SmallSize))

      intercept[StackOverflowError] { triangle(LargeSize) }

    }

    "can implement with ScalaZ's Trampoline" in {

      import scalaz.std.function._
      import scalaz.Free.{Trampoline, return_, suspend}

      def triangle(n: Int): Trampoline[Int] =
        if (n < 2) return_(n)
        else for (a <- suspend(triangle(n - 1))) yield a + n

      triangle(LargeSize).run should be (safeTriangle(LargeSize))

    }

    "can implement with ScalaZ's Task, which trampolines internally" in {

      import scalaz.concurrent.Task
      import scalaz.concurrent.Task.{now, suspend}

      def triangle(n: Int): Task[Int] =
        if (n < 2) now(n)
        else for (a <- suspend(triangle(n - 1))) yield a + n

      triangle(LargeSize).run should be (safeTriangle(LargeSize))

    }

  }

  val SmallSize = 10
  val LargeSize = 30000

  def safeTriangle(n: Int): Int = {
    @tailrec def loop(acc: Int, m: Int): Int =
      if (m < 2) (m + acc)
      else loop(acc + m, m - 1)
    loop(0, n)
  }

}
