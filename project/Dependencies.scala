import sbt._


object Dependencies extends Build {

  private val Resolvers = Seq("jdgoldie at bintray" at "http://dl.bintray.com/jdgoldie/maven")

  def Spray: Seq[ModuleID] = Seq(
    "io.spray" %% "spray-can" % Version.Spray,
    "io.spray" %% "spray-routing" % Version.Spray,
    "io.spray" %% "spray-json" % Version.SprayJson,
    "io.spray" %% "spray-testkit" % Version.Spray % "test"
  )

  def ScalaTest: ModuleID = "org.scalatest" %% "scalatest" % Version.ScalaTest % "test"

  def Akka: Seq[ModuleID] = Seq(
    "com.typesafe.akka" %% "akka-actor" % Version.Akka,
    "com.typesafe.akka" %% "akka-persistence-experimental" % Version.Akka,
    "com.typesafe.akka" %% "akka-testkit" % Version.Akka % "test",
    "com.github.jdgoldie" %% "akka-persistence-shared-inmemory" % "1.0.16" % "test"
  )

  implicit def singleModuleToSequence(moduleId: ModuleID): Traversable[ModuleID] = Seq(moduleId)

}