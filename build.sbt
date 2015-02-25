name := "shopping-cart-platform"

version := "1.0"

scalaVersion := Version.Scala

lazy val root = (project in file(".")).aggregate(support, core, server)

lazy val support = project

lazy val core = project.dependsOn(support)

lazy val server = project.dependsOn(core)