import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin._

object Dependencies {
  val specs2 = "org.specs2" %% "specs2" % "2.2.2"
}

object Resolvers {
  lazy val releases = "github-releases" at "https://github.com/agustafson/mvn-repo/raw/master/releases"
  lazy val snapshots = "github-snapshots" at "https://github.com/agustafson/mvn-repo/raw/master/snapshots"
}

object ApplicationBuild extends Build {
  lazy val scalaxml = Project(
    id = "scala-xml",
    base = file("."),
    settings = Project.defaultSettings ++ releaseSettings ++ Seq(
      organization := "scala-xml",
      name := "xmlstream",
      scalaVersion := "2.10.2",
      publishTo <<= version { (v: String) =>
        Some(if (v.trim endsWith "SNAPSHOT") Resolvers.snapshots else Resolvers.releases)
      },
      credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
      libraryDependencies += Dependencies.specs2
    )
  )
}
