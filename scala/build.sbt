name := "KanjiDamerauLevenshteinDistance"

version := "0.1"

scalaVersion := "2.13.0"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.7.4",
  "org.scalatest" % "scalatest_2.13" % "3.0.8" % "test"
)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8", // yes, this is 2 args
  "-feature",
  //"-language:implicitConversions",
  "-unchecked",
  "-Xlint")

coverageEnabled := true