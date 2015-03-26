import sbt.Keys._
import sbt._


object Dependencies extends Build {

  private val Resolvers = Seq("dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven")

  def ScalaTest: Seq[ModuleID] = Seq("org.scalatest" %% "scalatest" % Versions.ScalaTest % "test")

  def Akka: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-actor" % Versions.Akka,
    "com.typesafe.akka" %% "akka-persistence-experimental" % Versions.Akka,
    "com.typesafe.akka" %% "akka-testkit" % Versions.Akka % "test",
    "com.github.dnvriend" %% "akka-persistence-inmemory" % "1.0.0"
  )

  def Spray: Seq[ModuleID] = Seq("io.spray" %% "spray-can" % Versions.Spray,
    "io.spray" %% "spray-routing" % Versions.Spray,
    "io.spray" %% "spray-json" % Versions.SprayJson,
    "io.spray" %% "spray-testkit" % Versions.Spray % "test")

  implicit def singleModuleToSequence(moduleId: ModuleID): Traversable[ModuleID] = Seq(moduleId)

}