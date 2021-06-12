inThisBuild(List(
  scalaVersion := "2.12.11",
  organization := "ch.epfl.scala",
  homepage := Some(url("https://github.com/build-server-protocol/build-server-protocol")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
      Developer("olafurpg", "Ólafur Páll Geirsson", "olafurpg@gmail.com", url("https://github.com/olafurpg")),
      Developer("jvican", "Jorge Vicente Cantero", "jorge@vican.me", url("https://github.com/jvican")),
      Developer("jastice", "Justin Kaeser", "justin@justinkaeser.com", url("https://github.com/jastice")),
  )
))

import java.io.File
import org.eclipse.xtend.core.XtendInjectorSingleton
import org.eclipse.xtend.core.compiler.batch.XtendBatchCompiler

// force javac to fork by setting javaHome to get error messages during compilation,
// see https://github.com/sbt/zinc/issues/520
def inferJavaHome() = {
  val home = file(sys.props("java.home"))
  val actualHome =
    if (System.getProperty("java.version").startsWith("1.8")) home.getParentFile
    else home
  Some(actualHome)
}

Global / cancelable := true
publish / skip := true

lazy val bsp4s = project
  .in(file("bsp4s"))
  .settings(
    Test / publishArtifact := false,
    Compile / doc / sources := Nil,
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
    libraryDependencies ++= List(
      "io.circe" %% "circe-core" % "0.9.0",
      "io.circe" %% "circe-derivation" % "0.9.0-M4",
      "io.circe" %% "circe-generic-extras" % "0.9.0",
      "org.scalameta" %% "lsp4s" % "0.2.0"
    )
  )

lazy val bsp4j = project
  .in(file("bsp4j"))
  .settings(
    crossVersion := CrossVersion.disabled,
    autoScalaLibrary := false,
    Compile / javacOptions ++= List(
      "-Xlint:all",
      "-Werror"
    ),
    Compile / doc / javacOptions := List("-Xdoclint:none"),
    Compile / javaHome := inferJavaHome(),
    Compile / doc / javaHome := inferJavaHome(),
    TaskKey[Unit]("xtend") := {
      val compiler = XtendInjectorSingleton.INJECTOR.getInstance(classOf[XtendBatchCompiler])
      val classpath = (Compile / dependencyClasspath).value.map(_.data).mkString(File.pathSeparator)
      compiler.setClassPath(classpath)
      val sourceDir = (Compile / sourceDirectory).value / "java"
      compiler.setSourcePath(sourceDir.getCanonicalPath)
      val outDir = (Compile / sourceDirectory).value / "xtend-gen"
      IO.delete(outDir)
      compiler.setOutputPath(outDir.getCanonicalPath)
      object XtendError
          extends Exception(s"Compilation of Xtend files in $sourceDir failed.")
          with sbt.internal.util.FeedbackProvidedException
      if (!compiler.compile())
        throw XtendError
    },
    Compile / unmanagedSourceDirectories += (Compile / sourceDirectory).value / "xtend-gen",
    libraryDependencies ++= List(
      "org.eclipse.lsp4j" % "org.eclipse.lsp4j.generator" % "0.9.0",
      "org.eclipse.lsp4j" % "org.eclipse.lsp4j.jsonrpc" % "0.9.0"
    )
  )

lazy val tests = project
  .in(file("tests"))
  .settings(
    publish / skip := true,
    libraryDependencies ++= List(
      "com.googlecode.java-diff-utils" % "diffutils" % "1.3.0",
      "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.0",
      "org.scala-sbt.ipcsocket" % "ipcsocket" % "1.0.0",
      "org.scalatest" %% "scalatest" % "3.0.5"
    )
  )
  .dependsOn(bsp4s, bsp4j, `bsp-testkit`)

lazy val `bsp-testkit` = project
  .in(file("testkit"))
  .settings(
    Compile / mainClass := Some("ch.epfl.scala.bsp.mock.MockServer"),
    executableScriptName := "mockbsp",
    bashScriptExtraDefines += """addJava "-Dscript.path=${app_home}/"""" + executableScriptName.value,
    batScriptExtraDefines += """call :add_java "-Dscript.path=%APP_HOME%\\"""" + executableScriptName.value + ".bat",
    libraryDependencies ++= List(
      "org.scalacheck" %% "scalacheck" % "1.15.2",
      "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.1",
      "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.6"
    ),
    crossScalaVersions := List("2.12.11", "2.13.2")
  )
  .dependsOn(bsp4j)
  .enablePlugins(JavaAppPackaging)

lazy val docs = project
  .in(file("bsp-docs"))
  .dependsOn(bsp4j)
  .settings(
    publish / skip := true,
    mdocOut := (ThisBuild / baseDirectory).value / "website" / "target" / "docs",
    mdocVariables := Map(
      "VERSION" -> version.value
    )
  )
  .enablePlugins(DocusaurusPlugin)
