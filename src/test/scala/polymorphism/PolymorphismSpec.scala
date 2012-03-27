package com.github.shajra.learning.scala.polymorphism


import org.scalatest._


package inheritance {

  trait HasArea { val area: Double }

  sealed abstract class Shape extends HasArea

  case class Rectangle(w: Double, h: Double) extends Shape { val area = w * h }

  case class Square(s: Double) extends Shape { val area = s * s }

}


package typeclass {

  trait HasArea { val area: Double }

  trait AreaProvider[A] { def area(shape: A): Double }

  case class Rectangle(w: Double, h: Double)

  case class Square(s: Double)

  object AreaProvider {

    implicit val forRectangle: AreaProvider[Rectangle] =
      new AreaProvider[Rectangle] {
        def area(rectangle: Rectangle) = rectangle.w * rectangle.h
      }

    implicit val forSquare: AreaProvider[Square] =
      new AreaProvider[Square] {
        def area(square: Square) = square.s * square.s
      }

  }

}


package object typeclass {

  implicit def any2HasArea[A: AreaProvider](shape: A) = new HasArea {
    val areaProvider = implicitly[AreaProvider[A]]
    val area = areaProvider.area(shape)
  }

}


package implicits {

  trait HasArea { val area: Double }

  case class Rectangle(w: Double, h: Double)

  case class Square(s: Double)

  trait Implicits {

    implicit def rectangle2HasArea(rectangle: Rectangle) =
      new HasArea {
        val area = rectangle.w * rectangle.h
      }

    implicit def square2HasArea(square: Square) =
      new HasArea {
        val area = square.s * square.s
      }

  }

}


package object implicits extends implicits.Implicits


class PolymorphismSpec extends FreeSpec
    with OneInstancePerTest
    with matchers.ShouldMatchers {

  "when using inheritance" - {

    import inheritance._

    "we can get areas for a heterogenious list of shapes" in {

      val shapes =
        List(new Square(s=4), new Rectangle(w=5, h=6))

      shapes.map( _.area ) should be === List(16, 30)

    }

  }

  "when using a typeclass" - {

    import typeclass._

    "we can get areas for a heterogenious list of shapes" in {

      val shapes: List[HasArea] =
        List(new Square(s=4), new Rectangle(w=5, h=6))

      shapes.map( _.area ) should be === List(16, 30)
    }

  }

  "when using implicits" - {

    import implicits._

    "we can get areas for a heterogenious list of shapes" in {

      val shapes: List[HasArea] =
        List(new Square(s=4), new Rectangle(w=5, h=6))

      shapes.map( _.area ) should be === List(16, 30)

    }

  }

}
