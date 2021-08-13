import ReleaseTransformations._
import sbtrelease.ReleasePlugin.autoImport.releaseStepCommand

lazy val commonSettings = Seq(
  organization := "com.kjetland",
  organizationName := "mbknor",
  scalaVersion := "2.13.6",
  crossScalaVersions := Seq("2.11.12", "2.12.14", "2.13.6"),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "http://artifactory.edgewise.devops/artifactory/gradle-dev-local"
    if (isSnapshot.value)
      Some("Artifactory Realm" at nexus + ";build.timestamp=" + new java.util.Date().getTime)
    else
      Some("Artifactory Realm" at nexus)
  },
  credentials += Credentials(Path.userHome / ".sbt" / ".credentials"),
  homepage := Some(url("https://github.com/EdgewiseNetworks/mbknor-jackson-jsonSchema")),
  licenses := Seq("MIT" -> url("https://github.com/EdgewiseNetworks/mbknor-jackson-jsonSchema/blob/master/LICENSE.txt")),
  startYear := Some(2021),
  pomExtra := (
      <scm>
        <url>git@github.com:EdgewiseNetworks/mbknor-jackson-jsonSchema.git</url>
        <connection>scm:git:git@github.com:EdgewiseNetworks/mbknor-jackson-jsonSchema.git</connection>
      </scm>
      <developers>
        <developer>
          <id>raymundane</id>
          <name>Raymond Liu</name>
          <email>rliu@zscaler.com</email>
          <url>https://github.com/raymundane</url>
        </developer>
      </developers>),
  compileOrder in Test := CompileOrder.Mixed,
  javacOptions ++= Seq("-source", "11", "-target", "11"),
  scalacOptions ++= Seq("-unchecked", "-deprecation"),
  releaseCrossBuild := true,
  scalacOptions in(Compile, doc) ++= Seq(scalaVersion.value).flatMap {
    case v if v.startsWith("2.12") =>
      Seq("-no-java-comments") //workaround for scala/scala-dev#249
    case _ =>
      Seq()
  },
  packageOptions in (Compile, packageBin) +=
    Package.ManifestAttributes( "Automatic-Module-Name" -> "mbknor.jackson.jsonschema" )
)


val jacksonVersion = "2.10.1"
val jacksonModuleScalaVersion = "2.10.1"
val slf4jVersion = "1.7.26"


lazy val deps  = Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "javax.validation" % "validation-api" % "2.0.1.Final",
  "org.slf4j" % "slf4j-api" % slf4jVersion,
  "io.github.classgraph" % "classgraph" % "4.8.21",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "test",
  "com.github.java-json-tools" % "json-schema-validator" % "2.2.11" % "test",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonModuleScalaVersion % "test",
  "com.fasterxml.jackson.module" % "jackson-module-kotlin" % jacksonVersion % "test",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % jacksonVersion % "test",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % jacksonVersion % "test",
  "joda-time" % "joda-time" % "2.10.1" % "test",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % jacksonVersion % "test"
)

lazy val root = (project in file("."))
  .settings(name := "mbknor-jackson-jsonSchema")
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= (deps))


releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  pushChanges
)
