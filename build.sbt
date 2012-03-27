name := "learning-scala"

organization := "com.github.shajra"

version := "0.1.0-SNAPSHOT"

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "11.0.1",
  "com.typesafe.akka" % "akka-actor" % "2.0",
  "org.functionaljava" % "functionaljava" % "3.0",
  "org.scalatest" %% "scalatest" % "1.7.1" % "test"
)
