name := "shopping-cart-server"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  Dependencies.ScalaTest,
  Dependencies.ScalaTestPlus
)
