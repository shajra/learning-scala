package com.github.shajra.learning.scala


/** Prime number streams to learn more about memoization
  *
  * The computation is a simple sieve of Eratosthenes.
  */
package object stream_memo

package stream_memo {

  /** Memory-leaking primes stream.
    *
    * Because the primes Stream is stored in a 'val', and because Stream
    * memoizes, the calculation is very efficient, but it consumes memory
    * that's never released.
    */
  object MemoryLeaking {

    val primes: Stream[Int] = 2 #:: 3 #:: Stream.iterate(5)(nextPrime)

    def nextPrime(x: Int): Int =
      (Stream.from(x + 1) find isPrime).get

    def isPrime(x: Int): Boolean = {
      val primeLimit: Int = math.ceil(math.sqrt(x)).toInt + 1
      primes takeWhile { _ < primeLimit } forall { x % _ != 0 }
    }

  }

  /** Naïve non-memory-leaking primes stream.
    *
    * A simple solution to the memory leak of [[MemoryLeaking]] is to turn the
    * 'val' into a 'def', so we get a new stream with each invocation.
    *
    * Notice how the call to "primes" in "isPrime" creates a new stream, which
    * makes the calculation of large primes slow.
    *
    * However the returned stream still memoizes, so as long as we use the same
    * stream, we benefit from saved values.
    *
    */
  object SlowButNotMemoryLeaking {

    def primes: Stream[Int] = 2 #:: 3 #:: Stream.iterate(5)(nextPrime)

    def nextPrime(x: Int): Int =
      (Stream.from(x + 1) find isPrime).get

    def isPrime(x: Int): Boolean = {
      val primeLimit: Int = math.ceil(math.sqrt(x)).toInt + 1
      primes takeWhile { _ < primeLimit } forall { x % _ != 0 }
    }

  }

  /** Improved non-memory-leaking primes stream.
    *
    * A better solution to the memory leak of [[MemoryLeaking]] is to keep
    * the stream cached in a 'val', so 'isPrime' benefits from all previous
    * calculations and is just as efficient as [[MemoryLeaking]]
    *
    * However, to allow garbage collection, we put this 'val' in a class
    * instead of an object.
    */
  object FastAndNotMemoryLeaking {

    class Environment {

      val primes: Stream[Int] = 2 #:: 3 #:: Stream.iterate(5)(nextPrime)

      def nextPrime(x: Int): Int =
        (Stream.from(x + 1) find isPrime).get

      def isPrime(x: Int): Boolean = {
        val primeLimit: Int = math.ceil(math.sqrt(x)).toInt + 1
        primes takeWhile { _ < primeLimit } forall { x % _ != 0 }
      }

    }

    def primes: Stream[Int] = (new Environment).primes

  }

}
