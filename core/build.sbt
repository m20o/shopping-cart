name := "shopping-cart-core"

version := "1.0"

scalaVersion := Version.Scala

fork := true

scalacOptions := Seq("-unchecked", "-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % Version.Akka,
  "com.typesafe.akka" %% "akka-persistence-experimental" % Version.Akka,
  "com.typesafe.akka" %% "akka-testkit" %  Version.Akka,
  "com.github.dnvriend" %% "akka-persistence-inmemory" % "1.0.0",
  "com.github.scala-incubator.io" %% "scala-io-core" % Version.ScalaIO,
  "com.github.scala-incubator.io" %% "scala-io-file" % Version.ScalaIO,
  "com.github.nscala-time" %% "nscala-time" % Version.ScalaTime,
  "org.scalatest" %% "scalatest" % Version.ScalaTest % "test"
)
  