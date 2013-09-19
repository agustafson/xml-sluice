import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin._

object ScalaXmlBuild extends Build {

  object Dependencies {
    val specs2 = "org.specs2" %% "specs2" % "2.2.2"
  }

  lazy val scalaxml = Project(
    id = "scala-xml",
    base = file("."),
    settings = Project.defaultSettings ++ releaseSettings ++ Seq(
      organization := "agustafson",
      name := "scala-xml",
      scalaVersion := "2.10.2",
      libraryDependencies += Dependencies.specs2
    )
  )
}
