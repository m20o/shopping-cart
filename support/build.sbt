name := "test-support"

resolvers += "dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % Versions.Akka,
  "com.typesafe.akka" %% "akka-persistence-experimental" % Versions.Akka,
  "com.typesafe.akka" %% "akka-testkit" % Versions.Akka,
  "com.github.dnvriend" %% "akka-persistence-inmemory" % "1.0.0",
  "com.github.scala-incubator.io" %% "scala-io-core" % Versions.ScalaIO,
  "com.github.scala-incubator.io" %% "scala-io-file" % Versions.ScalaIO,
  "com.github.nscala-time" %% "nscala-time" % Versions.ScalaTime,
  "org.scalatest" %% "scalatest" % Versions.ScalaTest
)

ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := "net\\.badprogrammer\\.platform\\.testsupport\\..*"
