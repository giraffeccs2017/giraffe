name := "peppermint"

version := "1.0"

scalaVersion := "2.11.8"


libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.4"
//libraryDependencies += "org.scala-lang.modules" %% "scala-pickling" % "0.8.0-SNAPSHOT"
libraryDependencies += "org.scala-lang.modules" %% "scala-pickling" % "0.10.0"
libraryDependencies += "org.scalatra.scalate" %% "scalate-core" % "1.7.0"

//addSbtPlugin("com.simplytyped" % "sbt-antlr4" % "0.7.11")
antlr4Settings

fork := true

javaOptions ++= Seq( "-Xms128m", 
        "-Xmx30G", "-server", 
        "-XX:ReservedCodeCacheSize=64m",
        "-XX:+UseCodeCacheFlushing",
        "-XX:+UseCompressedOops")

