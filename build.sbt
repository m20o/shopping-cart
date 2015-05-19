name := "shopping-cart-platform"

version := "1.0"

scalaVersion := Versions.Scala

lazy val root = (project in file(".")).aggregate(core, server, support)

lazy val core = project.dependsOn(support % Test)

lazy val server= project.dependsOn(core, support % Test)

lazy val support = project

