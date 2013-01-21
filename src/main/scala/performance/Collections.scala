package com.github.shajra.learning.scala.performance


import akka.util.ByteString
import fj.data.{List => FJList}
import java.util.LinkedList


object FilledCollections {

  def filledScalaArray[A : Manifest](size: Int)(filler: => A): Array[A] = {
    val array = new Array[A](size)
    var i = 0
    while (i < size) {
      array(i) = filler
      i = i + 1
    }
    array
  }

  def filledScalaList[A : Manifest](size: Int)(filler: => A): List[A] =
    List(filledScalaArray(size)(filler):_*)

  def filledScalaVector[A : Manifest](size: Int)(filler: => A): Vector[A] =
    Vector(filledScalaArray(size)(filler):_*)

  def filledAkkaByteString(size: Int)(filler: => Byte): ByteString =
    ByteString(filledScalaArray[Byte](size)(filler))

  def filledFjList[A : Manifest](size: Int)(filler: => A): FJList[A] =
    filledScalaList(size)(filler).foldLeft(FJList.nil[A]) { _ cons _ }

  def filledJavaList[A : Manifest](size:Int)(filler: => A): LinkedList[A] = {
    val list = new LinkedList[A]()
    filledScalaList(size)(filler).foreach { i => list.add(i) }
    list
  }

}
