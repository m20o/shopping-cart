import sbt._
import Keys._

object CartProject extends Build {

  lazy val commonSettings = Seq(
    organization := "net.badprogrammer",
    version := "1.0",
    scalaVersion := Versions.Scala,
    scalacOptions := Seq("-feature","-unchecked", "-deprecation", "-encoding", "utf8", "-Xfatal-warnings", "-Xlint")
  )


  override lazy val settings = super.settings ++ commonSettings

}
