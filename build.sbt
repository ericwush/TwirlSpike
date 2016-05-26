name := "TwirlSpike"
version := "1.0"
scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)

libraryDependencies ++= Seq(
  "net.kaliber" %% "scala-pdf" % "0.11",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.4",
  "com.amazonaws" % "aws-lambda-java-core" % "1.1.0"
)

resolvers += "Kaliber Repository" at "https://jars.kaliber.io/artifactory/libs-release-local"
