name := "samplemlp"

version := "0.1.0-SNAPSHOT"

organization := "com.intel.analytics.bigdl"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.10" % "1.5.1",
  "org.apache.spark" % "spark-mllib_2.10" % "1.5.1",
  "com.intel.analytics.bigdl" % "bigdl" % "0.1.0-SNAPSHOT"
)

resolvers ++= Seq(
  "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Aliyun Central" at "http://maven.aliyun.com/nexus/content/groups/public"
)

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")

lazy val commonSettings = Seq(
  version := "0.1.0-SNAPSHOT",
  organization := "com.intel.analytics.bigdl",
  scalaVersion := "2.10.5"
)

lazy val app = (project in file(".")).
  settings(commonSettings: _*)

