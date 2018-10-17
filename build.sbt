inThisBuild(List(
  scmInfo := Some(ScmInfo(
    browseUrl = url("https://github.com/scalacenter/bsp"),
    connection = "scm:git:git@github.com:scalacenter/bsp.git"
  ))
))
import java.io.File
import org.eclipse.xtend.core.XtendInjectorSingleton
import org.eclipse.xtend.core.compiler.batch.XtendBatchCompiler

// force javac to fork by setting javaHome to get error messages during compilation,
// see https://github.com/sbt/zinc/issues/520
def inferJavaHome() =
  Some(file(System.getProperty("java.home")).getParentFile)

cancelable.in(Global) := true

lazy val bsp = project
  .in(file("."))
  .settings(
    publishArtifact in Test := false,
    sources in (Compile, doc) := Nil,
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
    libraryDependencies ++= List(
      "io.circe" %% "circe-core" % "0.9.0",
      "io.circe" %% "circe-derivation" % "0.9.0-M4",
      "org.scalameta" %% "lsp4s" % "0.2.0"
    )
  )

lazy val bsp4j = project
  .in(file("bsp4j"))
  .settings(
    crossVersion := CrossVersion.disabled,
    autoScalaLibrary := false,
    javacOptions.in(Compile) ++= List(
      "-Xlint:all",
      "-Werror"
    ),
    javacOptions.in(Compile, doc) := List("-Xdoclint:none"),
    javaHome.in(Compile) := inferJavaHome(),
    javaHome.in(Compile, doc) := inferJavaHome(),
    TaskKey[Unit]("codegen") := {
      val compiler = XtendInjectorSingleton.INJECTOR.getInstance(classOf[XtendBatchCompiler])
      val classpath = dependencyClasspath.in(Compile).value.map(_.data).mkString(File.pathSeparator)
      compiler.setClassPath(classpath)
      val sourceDir = sourceDirectory.in(Compile).value / "java"
      compiler.setSourcePath(sourceDir.getCanonicalPath)
      val outDir = sourceDirectory.in(Compile).value / "xtend-gen"
      IO.delete(outDir)
      compiler.setOutputPath(outDir.getCanonicalPath)
      object XtendError
          extends Exception(s"Compilation of Xtend files in $sourceDir failed.")
          with sbt.internal.util.FeedbackProvidedException
      if (!compiler.compile())
        throw XtendError
    },
    unmanagedSourceDirectories.in(Compile) += sourceDirectory.in(Compile).value / "xtend-gen",
    resourceGenerators.in(Test) += Def.task {
      val out = managedResourceDirectories.in(Test).value.head / "bsp4j.properties"
      val props = new java.util.Properties()
      val bloopDirectory = sourceDirectory.in(Test).value / "bloop"
      props.put("bloopDirectory", bloopDirectory.getAbsolutePath)
      IO.write(props, "test data", out)
      List(out)
    },
    libraryDependencies ++= List(
      "org.eclipse.lsp4j" % "org.eclipse.lsp4j.generator" % "0.5.0",
      "org.eclipse.lsp4j" % "org.eclipse.lsp4j.jsonrpc" % "0.5.0",
      "org.scala-sbt.ipcsocket" % "ipcsocket" % "1.0.0" % Test,
      "ch.epfl.scala" % "bloop-frontend_2.12" % "1.0.0" % Test,
      "org.scalatest" % "scalatest_2.12" % "3.0.5" % Test
    )
  )
