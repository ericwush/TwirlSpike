name := "TwirlSpike"
version := "1.0"
scalaVersion := "2.11.8"

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)

libraryDependencies ++= Seq(
  "net.kaliber" %% "scala-pdf" % "0.11"
)

resolvers += "Kaliber Repository" at "https://jars.kaliber.io/artifactory/libs-release-local"
