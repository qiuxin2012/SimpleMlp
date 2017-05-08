name := "samplemlp"

version := "0.1.0-SNAPSHOT"

organization := "com.intel.analytics.bigdl"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.1.0" % "compile",
  "org.apache.spark" % "spark-mllib_2.11" % "2.1.0" % "compile",
  "com.intel.analytics.bigdl" % "bigdl-SPARK_2.0" % "0.1.0",
  "com.intel.analytics.bigdl.native" % "mkl-java-mac" % "0.1.0" from "http://maven.aliyun.com/nexus/content/groups/public/com/intel/analytics/bigdl/native/mkl-java-mac/0.1.0/mkl-java-mac-0.1.0.jar"
)

resolvers ++= Seq(
  "Local Maven Repository" at Path.userHome.asFile.toURI.toURL + ".m2/repository"
)

lazy val commonSettings = Seq(
  version := "0.1.0",
  organization := "com.intel.analytics.bigdl",
  scalaVersion := "2.11.8"
)

lazy val app = (project in file(".")).
  settings(commonSettings: _*)

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
{
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
}

