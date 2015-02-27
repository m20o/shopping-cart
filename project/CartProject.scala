import sbt._
import Keys._

object CartProject extends Build {

  lazy val commonSettings = Seq(
    organization := "net.badprogrammer",
    version := "1.0",
    scalaVersion := Version.Scala,
    scalacOptions := Seq("-feature","-unchecked", "-deprecation", "-encoding", "utf8")
  )


  override lazy val settings = super.settings ++ commonSettings



}
