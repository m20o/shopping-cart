name := "shopping-cart-platform"

lazy val root = (project in file(".")).aggregate(support, core, server)

lazy val support = project

lazy val core = project.dependsOn(support)

lazy val server = (project in file("server")).enablePlugins(PlayScala).dependsOn(core).aggregate(core)