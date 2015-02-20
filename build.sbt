name := "shopping-cart-platform"

version := "1.0"

scalaVersion := Version.Scala

lazy val root = (project in file(".")). aggregate(core, server)

lazy val core = project

lazy val server = project.dependsOn(core)