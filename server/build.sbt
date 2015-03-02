name := "shopping-cart-server"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  Dependencies.ScalaTest,
  "org.scalatestplus" %% "play" % "1.2.0" % "test"
)
