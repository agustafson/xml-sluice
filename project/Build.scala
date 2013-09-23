import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin._

object Build extends Build {

  object Dependencies {
    val specs2 = "org.specs2" %% "specs2" % "2.2.2"
  }

  lazy val scalaxml = Project(
    id = "scala-xml",
    base = file("."),
    settings = Project.defaultSettings ++ releaseSettings ++ Seq(
      organization := "scala-xml",
      name := "xmlstream",
      scalaVersion := "2.10.2",
      libraryDependencies += Dependencies.specs2
    )
  )
}
