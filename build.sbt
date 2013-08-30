name := "FreeLucky"
 
version := "1.0"
 
scalaVersion := "2.10.2"
 
seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)
 
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "spray nightlies repo" at "http://nightlies.spray.io"


resolvers += "rediscala" at "https://github.com/etaty/rediscala-mvn/raw/master/releases/"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.0"

libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.2.0"

libraryDependencies += "com.etaty.rediscala" %% "rediscala" % "1.0"

libraryDependencies += "io.spray" % "spray-can" % "1.2-20130801"

libraryDependencies += "io.spray" % "spray-http" % "1.2-20130801"

libraryDependencies += "io.spray" % "spray-httpx" % "1.2-20130801"

libraryDependencies += "io.spray" % "spray-routing" % "1.2-20130801"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.2.2"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.2.2"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.2.2"