name := "kamino-practice"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "io.kamon" %% "kamon-core" % "0.6.0",
  "io.kamon" %% "kamon-statsd" % "0.6.0",
  "com.typesafe.akka" %% "akka-http" % "10.0.0",
  "org.json4s" %% "json4s-native" % "3.5.2",
  "org.json4s" %% "json4s-jackson" % "3.5.2"
)

// Bring the sbt-aspectj settings into this build
aspectjSettings

// Here we are effectively adding the `-javaagent` JVM startup
// option with the location of the AspectJ Weaver provided by
// the sbt-aspectj plugin.
javaOptions in run <++= AspectjKeys.weaverOptions in Aspectj

// We need to ensure that the JVM is forked for the
// AspectJ Weaver to kick in properly and do it's magic.
fork in run := true