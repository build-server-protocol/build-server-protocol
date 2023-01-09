inThisBuild(
  List(
    scalaVersion := V.scala213,
    organization := "ch.epfl.scala",
    homepage := Some(url("https://github.com/build-server-protocol/build-server-protocol")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "olafurpg",
        "Ólafur Páll Geirsson",
        "olafurpg@gmail.com",
        url("https://github.com/olafurpg")
      ),
      Developer(
        "jvican",
        "Jorge Vicente Cantero",
        "jorge@vican.me",
        url("https://github.com/jvican")
      ),
      Developer(
        "jastice",
        "Justin Kaeser",
        "justin@justinkaeser.com",
        url("https://github.com/jastice")
      )
    )
  )
)

lazy val V = new {
  val scala212 = "2.12.17"
  val scala213 = "2.13.10"
  val supportedScalaVersions = List(scala212, scala213)
  val jsoniter = "2.20.2"
  val java8Compat = "1.0.2"
  val lsp4j = "0.12.0"
  val scalacheck = "1.17.0"
}

import java.io.File
import org.eclipse.xtend.core.compiler.batch.XtendBatchCompiler
import org.eclipse.xtend.core.XtendStandaloneSetup

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
    crossScalaVersions := V.supportedScalaVersions,
    Test / publishArtifact := false,
    Compile / doc / sources := Nil,
    libraryDependencies ++= List(
      "me.vican.jorge" %% "jsonrpc4s" % "0.1.0",
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % V.jsoniter
    )
  )

lazy val bsp4j = project
  .in(file("bsp4j"))
  .settings(
    crossVersion := CrossVersion.disabled,
    autoScalaLibrary := false,
    Compile / javacOptions ++= {
      val specifyRelease =
        if (sys.props("java.version").startsWith("1.8"))
          List.empty
        else
          List("--release", "8")

      List(
        "-Xlint:all",
        "-Werror"
      ) ++ specifyRelease
    },
    Compile / doc / javacOptions := List("-Xdoclint:none"),
    Compile / javaHome := inferJavaHome(),
    Compile / doc / javaHome := inferJavaHome(),
    TaskKey[Unit]("xtend") := {
      val injector = new XtendStandaloneSetup().createInjectorAndDoEMFRegistration
      val compiler = injector.getInstance(classOf[XtendBatchCompiler])
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
      "org.eclipse.lsp4j" % "org.eclipse.lsp4j.generator" % V.lsp4j,
      "org.eclipse.lsp4j" % "org.eclipse.lsp4j.jsonrpc" % V.lsp4j
    )
  )

lazy val tests = project
  .in(file("tests"))
  .settings(
    publish / skip := true,
    libraryDependencies ++= List(
      "com.googlecode.java-diff-utils" % "diffutils" % "1.3.0",
      "org.scala-lang.modules" %% "scala-java8-compat" % V.java8Compat,
      "org.scala-sbt.ipcsocket" % "ipcsocket" % "1.6.2",
      "org.scalatest" %% "scalatest" % "3.2.15",
      "org.scalatestplus" %% "scalacheck-1-15" % "3.2.11.0",
      "org.scalacheck" %% "scalacheck" % V.scalacheck,
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % V.jsoniter
    )
  )
  .dependsOn(bsp4s, bsp4j, `bsp-testkit`)

lazy val `bsp-testkit` = project
  .in(file("testkit"))
  .settings(
    Compile / mainClass := Some("ch.epfl.scala.bsp.testkit.mock.MockServer"),
    executableScriptName := "mockbsp",
    bashScriptExtraDefines += """addJava "-Dscript.path=${app_home}/"""" + executableScriptName.value,
    batScriptExtraDefines += """call :add_java "-Dscript.path=%APP_HOME%\\"""" + executableScriptName.value + ".bat",
    libraryDependencies ++= List(
      "org.scalacheck" %% "scalacheck" % V.scalacheck,
      "de.danielbechler" % "java-object-diff" % "0.95",
      "org.scala-lang.modules" %% "scala-java8-compat" % V.java8Compat,
      "org.scala-lang.modules" %% "scala-collection-compat" % "2.9.0"
    ),
    crossScalaVersions := V.supportedScalaVersions
  )
  .dependsOn(bsp4j)
  .enablePlugins(JavaAppPackaging)

lazy val docs = project
  .in(file("bsp-docs"))
  .dependsOn(bsp4j)
  .settings(
    scalaVersion := V.scala212,
    publish / skip := true,
    mdocOut := (ThisBuild / baseDirectory).value / "website" / "target" / "docs",
    mdocVariables := Map(
      "VERSION" -> version.value
    )
  )
  .enablePlugins(DocusaurusPlugin)

addCommandAlias(
  "scalaFormat",
  "scalafmtAll ; scalafmtSbt"
)

addCommandAlias(
  "checkScalaFormat",
  "scalafmtCheckAll ; scalafmtSbtCheck"
)
