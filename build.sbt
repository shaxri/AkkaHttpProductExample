name := "AkkaHttpProductExample"

version := "1.0"

scalaVersion := "2.13.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.5.0",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.5.0",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.8.0",
  "com.typesafe.akka" %% "akka-stream" % "2.8.0",
  "com.typesafe.akka" %% "akka-slf4j" % "2.8.0",
  "ch.megard" %% "akka-http-cors" % "1.2.0",
  "com.typesafe.slick" %% "slick" % "3.4.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.1",
  "org.postgresql" % "postgresql" % "42.5.4",
  "com.typesafe.akka" %% "akka-testkit" % "2.8.0" % Test,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test
)