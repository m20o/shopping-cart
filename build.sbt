name := "shopping-cart-platform"

version := "1.0"

scalaVersion := Versions.Scala

lazy val root = (project in file(".")).aggregate(core)

lazy val core = project

