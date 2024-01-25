ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"


libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.3.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.3.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.2.1"


lazy val root = (project in file("."))
  .settings(
    name := "spark-consumer"
  )
