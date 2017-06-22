organization := "com.github.catalystcode"
name := "streaming-facebook"
description := "A library for reading social data from Facebook using Spark Streaming."

scalaVersion := "2.10.4"

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature"
)

libraryDependencies ++= Seq(
  // spark
  "org.apache.spark" % "spark-core_2.10" % "1.6.1" ,
  "org.apache.spark" % "spark-streaming_2.10" % "1.6.1",

  // testing
  "org.scalatest" % "scalatest_2.10" % "2.2.1" % "test",

  // dependencies
  "log4j" % "log4j" % "1.2.17",
  "org.facebook4j" % "facebook4j-core" % "2.4.9",

  "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.1" ,
  "org.apache.kafka" % "kafka_2.10" % "0.8.1"
)

assemblyMergeStrategy in assembly := {
  case PathList("javax", "inject", xs @ _*) => MergeStrategy.last
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.last
  case PathList("javax", "activation", xs @ _*) => MergeStrategy.last
  case PathList("org", "aopalliance", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.last
  case PathList("com", "codahale", xs @ _*) => MergeStrategy.last
  case PathList("com", "yammer", xs @ _*) => MergeStrategy.last
  case "about.html" => MergeStrategy.rename
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.last
  case "META-INF/mailcap" => MergeStrategy.last
  case "META-INF/mimetypes.default" => MergeStrategy.last
  case "plugin.properties" => MergeStrategy.last
  case "log4j.properties" => MergeStrategy.last
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
