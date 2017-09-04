organization := "com.sksamuel.elastic4s"

name := "elastic4s_h21"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.elasticsearch" % "elasticsearch" % "2.1.2",
  "org.scalactic" % "scalactic_2.11" % "2.2.5",
  "org.slf4j" % "slf4j-api" % "1.7.12"
)
