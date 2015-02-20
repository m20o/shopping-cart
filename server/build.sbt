name := "shopping-cart-server"

version := "1.0"

scalaVersion := Version.Scala

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  Seq(
    "io.spray"            %%  "spray-can"     % Version.Spray,
    "io.spray"            %%  "spray-routing" % Version.Spray,
    "io.spray"            %%  "spray-json"    % Version.SprayJson,
    "io.spray"            %%  "spray-testkit" % Version.Spray  % "test",
     Dependencies.ScalaTest
  )
}

Revolver.settings