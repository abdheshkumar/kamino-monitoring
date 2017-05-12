name := "kamino-practice"

version := "1.0"

scalaVersion := "2.11.8"

val kamonVersion = "0.6.6"

libraryDependencies ++= Seq(
  "io.kamon" %% "kamon-core" % kamonVersion,
  "io.kamon" %% "kamon-akka" % "0.6.3",
  "io.kamon" %% "kamon-statsd" % kamonVersion,
  "io.kamon" %% "kamon-log-reporter" % kamonVersion,
  "io.kamon" %% "kamon-system-metrics" % kamonVersion,
  "com.typesafe.akka" %% "akka-actor" % "2.4.16",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.16",
  "com.typesafe.akka" %% "akka-http" % "10.0.0",
  "org.json4s" %% "json4s-native" % "3.5.2",
  "org.json4s" %% "json4s-jackson" % "3.5.2",
  "org.slf4j" % "slf4j-log4j12" % "1.7.25"
)

aspectjSettings

javaOptions <++= AspectjKeys.weaverOptions in Aspectj

// when you call "sbt run" aspectj weaving kicks in
fork in run := true