name := "shopping-cart-server"

libraryDependencies ++= {
  Seq(
    "io.spray"            %%  "spray-can"     % Version.Spray,
    "io.spray"            %%  "spray-routing" % Version.Spray,
    "io.spray"            %%  "spray-json"    % Version.SprayJson,
    "io.spray"            %%  "spray-testkit" % Version.Spray  % "test",
    Dependencies.ScalaTest
  )
}