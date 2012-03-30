package com.github.shajra.learning.scala.performance


import akka.util.ByteString
import fj.data.{List => FJList}
import java.util.LinkedList


object Collections {

  def scalaArray[A](size: Int)(implicit manifest: Manifest[A]): Array[A] =
    new Array[A](size)

  def scalaList[A : Manifest](size: Int): List[A] =
    List(scalaArray(size):_*)

  def scalaVector[A : Manifest](size: Int): Vector[A] =
    Vector(scalaArray(size):_*)

  def akkaByteString(size: Int): ByteString =
    ByteString(scalaArray[Byte](size))

  def fjList[A : Manifest](size: Int): FJList[A] =
    scalaList(size).foldLeft(FJList.nil[A]) { _ cons _ }

  def javaList[A : Manifest](size:Int): LinkedList[A] = {
    val list = new LinkedList[A]()
    scalaList(size).foreach { i => list.add(i) }
    list
  }

  def filledScalaArray[A : Manifest](size: Int)(filler: => A): Array[A] = {
    val array = scalaArray(size)
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
