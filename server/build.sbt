name := "shopping-cart-server"

version := "1.0"

scalaVersion := Version.Scala

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

lazy val server = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)
