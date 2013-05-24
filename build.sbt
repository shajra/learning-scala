organization := "com.github.shajra"

name := "learning-scala"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.google.caliper" % "caliper" % "0.5-rc1",
  "com.google.guava" % "guava" % "14.0-rc1",
  "com.typesafe.akka" %% "akka-actor" % "2.1.0",
  "org.functionaljava" % "functionaljava" % "3.0",
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "test",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test",
  "org.scalaz" %% "scalaz-concurrent" % "7.0.0-M9",
  "org.scalaz" %% "scalaz-core" % "7.0.0-M9",
  "org.scalaz" %% "scalaz-effect" % "7.0.0-M9",
  "org.scalaz" %% "scalaz-typelevel" % "7.0.0-M9"
)

scalaVersion := "2.10.1"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-optimize"
  //"-Xlint"
)

fork in run := true

javaOptions in run <++= (fullClasspath in Runtime) map { cp =>
  Seq("-cp", sbt.Build.data(cp).mkString(":"))
}
