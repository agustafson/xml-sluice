import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin._

object ScalaxmlBuild extends Build {

  lazy val scalaxml = Project(
    id = "scala-xml",
    base = file("."),
    settings = Project.defaultSettings ++ releaseSettings ++ Seq(
      organization := "agustafson",
      name := "scala-xml",
      scalaVersion := "2.10.2"
    )
  )
}
