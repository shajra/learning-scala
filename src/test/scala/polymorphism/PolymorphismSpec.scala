package com.github.shajra.learning.scala


/** Exploring different mechanisms for polymorphism in Scala.  Each subpackage
  * under this package will try to obtain polymorphism in a slightly different
  * way, each with different benefits and liabilities.
  *
  * A simple "shapes" example will be used for comparison.
  */
package object polymorphism

package polymorphism {


  /** "Shapes" example implemented with simple subtype polymorphism.
    *
    * Dynamic dispatch to different different area methods is the popular and
    * traditional polymorphic solution.  Furthermore, because the shapes share
    * a common interface, a container can easily contain shapes of both types.
    *
    * Unfortunately, the area method is tightly coupled to the implementation
    * of each shape.  If these shapes had come from a third party, we may not
    * have the ability to easily augment these shapes with the necessary
    * imports.
    */
  package object inheritance

  package inheritance {

    trait HasArea { val area: Double }

    sealed trait Shape extends HasArea

    case class Rectangle(w: Double, h: Double) extends Shape {
      val area = w * h
    }

    case class Square(s: Double) extends Shape {
      val area = s * s
    }

  }


  /** "Shapes" example implemented with pattern matching.
    *
    * This is the algebraic variant of the subtype solution (co-algebraic).
    * The two aproaches are duals.  Adding new methods is tedious with
    * subtyping, because each shape implementation must be modified to include
    * the new method.  However, with pattern matching, adding new shapes is
    * tedious because each implemented method must be extended for the new
    * shape.  Both approaches suffer from problems of coupling, just in
    * different dimensions.
    */
  package object matching

  package matching {

    sealed trait Shape

    object Shape {

      def area(s: Shape) = s match {
        case Rectangle(w, h) => w * h
        case Square(s) => s * s
      }

    }

    case class Rectangle(w: Double, h: Double) extends Shape

    case class Square(s: Double) extends Shape

  }


  /** "Shapes" example implemented with an AreaProvider type class.
    *
    * The shape implementations are now decoupled from the implementations for
    * their respective area functions.  These area functions are implemented as
    * implicits, but because of Scala's resolution strategy for implicits, they
    * don't have to be imported explicitly to use since they're found in the
    * AreaProvider companion object.
    *
    * Note, this example does not illustrate strongly the motivation for type
    * classes.  Had the type class provided a set of functions that work
    * together as a set, the type class would make a lot more sense.
    * Furthermore, some of these functions may not even have the object type as
    * a parameter or return type.  In these cases, the type class makes a lot
    * of sense over subtype inheritance.  For example, consider the Monoid type
    * class offering both an operation and identity.
    *
    * The any2HasArea implicit conversion mostly just allows shapes to have the
    * extension "area" method.  But it also facilitates the conversion to a
    * common HasArea type, which enables these objects to easily be put into
    * the same collection (which wouldn't be possibly had this implicit
    * conversion return a structural type).
    */
  package object typeclass extends typeclass.Implicits

  package typeclass {

    case class Rectangle(w: Double, h: Double)

    case class Square(s: Double)

    trait AreaProvider[A] { def area(shape: A): Double }

    object AreaProvider {

      implicit object RectangleAreaProvider extends AreaProvider[Rectangle] {
        def area(rectangle: Rectangle) = rectangle.w * rectangle.h
      }

      implicit object SquareAreaProvider extends AreaProvider[Square] {
        def area(square: Square) = square.s * square.s
      }

    }

    trait HasArea { val area: Double }

    trait Implicits {

      implicit def any2HasArea[A]
          (shape: A)(implicit areaProvider: AreaProvider[A]) =
        new HasArea { val area = areaProvider.area(shape) }

    }

  }


  /** "Shapes" example implemented with only extension methods.
    *
    * This implementation appears to accomplish the same benefits as the type
    * class implementation, but with less boilerplate.  This is not exactly the
    * case.  The implicits ''have'' to be explicitly imported to use.  This is
    * a burden on the consumer of the API.  For this reason, many people
    * discourage the approach.
    */
  package object implicits extends implicits.Implicits

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


  import org.scalatest._

  class PolymorphismSpec extends FreeSpec with matchers.ShouldMatchers {

    "when using inheritance" - {

      import inheritance._

      "we can get areas for a heterogenious list of shapes" in {

        val shapes =
          List(new Square(s=4), new Rectangle(w=5, h=6))

        shapes.map { _.area } should be === List(16, 30)

      }

    }

    "when using a pattern matching" - {

      import matching._

      "we can get areas for a heterogenious list of shapes" in {

        val shapes =
          List(new Square(s=4), new Rectangle(w=5, h=6))

        shapes.map { Shape.area _  } should be === List(16, 30)
      }

    }


    "when using a typeclass" - {

      import typeclass._

      "we can get areas for a heterogenious list of shapes" in {

        val shapes: List[HasArea] =
          List(new Square(s=4), new Rectangle(w=5, h=6))

        shapes.map { _.area } should be === List(16, 30)
      }

    }

    "when using implicits" - {

      import implicits._

      "we can get areas for a heterogenious list of shapes" in {

        val shapes: List[HasArea] =
          List(new Square(s=4), new Rectangle(w=5, h=6))

        shapes.map { _.area } should be === List(16, 30)

      }

    }

  }


}
