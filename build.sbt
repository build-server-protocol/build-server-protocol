inThisBuild(
  List(
    scalaVersion := V.scala213,
    organization := "ch.epfl.scala",
    homepage := Some(url("https://github.com/build-server-protocol/build-server-protocol")),
    licenses := List("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "ckipp01",
        "Chris Kipp",
        "open-source@chris-kipp.io",
        url("https://github.com/ckipp01")
      ),
      Developer(
        "jastice",
        "Justin Kaeser",
        "justin@justinkaeser.com",
        url("https://github.com/jastice")
      ),
      Developer(
        "agluszak",
        "Andrzej Głuszak",
        "andrzej.gluszak@jetbrains.com",
        url("https://github.com/agluszak")
      )
    )
  )
)

lazy val V = new {
  val scala212 = "2.12.18"
  val scala213 = "2.13.12"
  val supportedScalaVersions = List(scala212, scala213)
  val cats = "2.9.0"
  val jsoniter = "2.23.5"
  val java8Compat = "1.0.2"
  val lsp4j = "0.20.1"
  val scalacheck = "1.17.0"
  val scalaCollectionCompat = "2.11.0"
  val osLib = "0.9.1"
  val decline = "2.4.1"
  val smithy = "1.35.0"
  val diffutils = "1.3.0"
  val scalatest = "3.2.17"
  val ipcsocket = "1.0.1"
  val scalatestScalacheck = "3.2.14.0"
  val jsonrpc4s = "0.1.0"
}

import scala.sys.process._
import java.io.File
import org.eclipse.xtend.core.compiler.batch.XtendBatchCompiler
import org.eclipse.xtend.core.XtendStandaloneSetup

Global / cancelable := true
publish / skip := true

addCommandAlias(
  "generate",
  "generateCode ; generateWebsite"
)

addCommandAlias(
  "generateCode",
  "codegen ; xtend ; scalafmtAll ; scalafmtSbt"
)

addCommandAlias(
  "generateWebsite",
  "mdoc ; format"
)

// Bsp4s is now generated from the smithy model
lazy val bsp4s = project
  .in(file("bsp4s"))
  .settings(
    crossScalaVersions := V.supportedScalaVersions,
    Test / publishArtifact := false,
    Compile / doc / sources := Nil,
    libraryDependencies ++= List(
      "me.vican.jorge" %% "jsonrpc4s" % V.jsonrpc4s,
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % V.jsoniter
    ),
    TaskKey[Unit]("codegen") := {
      val _ = runCodegen(Compile, "bsp.codegen.bsp4s.Main", "scala").value

    }
  )

// Bsp4j is now generated from the smithy model
lazy val bsp4j = project
  .in(file("bsp4j"))
  .settings(
    autoScalaLibrary := false,
    crossPaths := false,
    Compile / javacOptions ++= {
      List(
        "-Xlint:all",
        "-Werror"
      )
    },
    Compile / doc / javacOptions := List("-Xdoclint:none"),
    TaskKey[Unit]("codegen") := {
      val _ = runCodegen(Compile, "bsp.codegen.bsp4j.Main", "java").value
    },
    TaskKey[Unit]("xtend") := {
      val _ = invokeXtendGeneration(Compile).value
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
      "com.googlecode.java-diff-utils" % "diffutils" % V.diffutils,
      "org.scala-lang.modules" %% "scala-java8-compat" % V.java8Compat,
      "org.scala-sbt.ipcsocket" % "ipcsocket" % V.ipcsocket,
      "org.scalatest" %% "scalatest" % V.scalatest,
      "org.scalatestplus" %% "scalacheck-1-16" % V.scalatestScalacheck,
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
      "org.scala-lang.modules" %% "scala-collection-compat" % V.scalaCollectionCompat
    ),
    crossScalaVersions := V.supportedScalaVersions
  )
  .dependsOn(bsp4j)
  .enablePlugins(JavaAppPackaging)

// Defines the BSP specification in terms of a smithy files.
// In theory, the spec files can live elsewhere and a build task
// could ensure that this artifact packages them correctly
// for downstream users
lazy val spec = project
  .in(file("spec"))
  .settings(
    crossVersion := CrossVersion.disabled,
    autoScalaLibrary := false
  )

// Sidecar artifact for the spec artifact, defining a bunch
// of POJOs reflecting the annotations the spec uses. This
// allows downstream tools (code-generators instead) to query
// shapes by classes and get the annotations automatically
// decoded to the POJOS. This is entirely optional but helps.
//
// Also will contain bespoke linting rules
lazy val `spec-traits` = project
  .in(file("spec-traits"))
  .dependsOn(spec)
  .settings(
    crossVersion := CrossVersion.disabled,
    autoScalaLibrary := false,
    libraryDependencies ++= Seq(
      "software.amazon.smithy" % "smithy-model" % V.smithy
    )
  )

// A codegen module that contains the logic for generating bsp4j
// This will be invoked via shell-out using a bespoke sbt task
lazy val codegen = project
  .in(file("codegen"))
  .dependsOn(`spec-traits`)
  .settings(
    publish := {},
    publishLocal := {},
    crossScalaVersions := V.supportedScalaVersions,
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-collection-compat" % V.scalaCollectionCompat,
      "com.lihaoyi" %% "os-lib" % V.osLib,
      "com.monovore" %% "decline" % V.decline,
      "org.typelevel" %% "cats-core" % V.cats
    )
  )

// Remove whatever comes after the + sign in the version
def cleanLibraryVersion(version: String): String = {
  println(s"Cleaning version $version")
  val idx = version.indexOf('+')
  if (idx < 0) version
  else version.substring(0, idx)
}

lazy val docs = project
  .in(file("bsp-docs"))
  .dependsOn(bsp4j, codegen)
  .settings(
    scalaVersion := V.scala213,
    publish / skip := true,
    mdocOut := (ThisBuild / baseDirectory).value / "website" / "generated" / "docs",
    mdocVariables := Map(
      "LIBRARY_VERSION" -> cleanLibraryVersion(version.value)
    ),
    TaskKey[Unit]("format") := {
      "yarn --cwd website install" #&&
        "yarn --cwd website format" !
    }
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

def invokeXtendGeneration(configuration: Configuration) = Def.task {
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
  IO.copyDirectory(outDir, sourceDir, overwrite = true)
  IO.delete(outDir)
}
def runCodegen(
    config: Configuration,
    mainClassName: String,
    outputPath: String
): Def.Initialize[Task[Seq[File]]] = Def.task {

  import sjsonnew._
  import BasicJsonProtocol._
  import sbt.FileInfo
  import sbt.HashFileInfo
  import sbt.io.Hash
  import scala.jdk.CollectionConverters._
  import java.nio.file.Files
  import java.util.stream.Collectors

  // Json codecs used by SBT's caching constructs
  // This serialises a path by providing a hash of the content it points to.
  // Because the hash is part of the Json, this allows SBT to detect when a file
  // changes and invalidate its relevant caches.
  implicit val pathFormat: JsonFormat[File] =
    BasicJsonProtocol.projectFormat[File, HashFileInfo](
      p => {
        if (p.isFile()) FileInfo.hash(p)
        else
          // If the path is a directory, we get the hashes of all files
          // then hash the concatenation of the hash's bytes.
          FileInfo.hash(
            p,
            Hash(
              Files
                .walk(p.toPath(), 2)
                .collect(Collectors.toList())
                .asScala
                .map(_.toFile())
                .map(Hash(_))
                .foldLeft(Array.emptyByteArray)(_ ++ _)
            )
          )
      },
      hash => hash.file
    )

  case class CodegenInput(files: Seq[File])
  object CodegenInput {
    implicit val seqFormat: JsonFormat[CodegenInput] =
      BasicJsonProtocol.projectFormat[CodegenInput, Seq[File]](
        input => input.files,
        files => CodegenInput(files)
      )(BasicJsonProtocol.seqFormat(pathFormat))
  }

  val codegenClasspath = (codegen / Compile / fullClasspath).value.map(_.data)
  val outputDir = (config / sourceDirectory).value / outputPath

  val s = (config / streams).value

  val cached =
    Tracked.inputChanged[CodegenInput, Seq[File]](
      s.cacheStoreFactory.make("input")
    ) {
      Function.untupled {
        Tracked
          .lastOutput[(Boolean, CodegenInput), Seq[File]](
            s.cacheStoreFactory.make("output")
          ) { case ((changed, files), outputs) =>
            if (changed || outputs.isEmpty) {
              val args = List("--output", outputDir.getAbsolutePath)

              val outputStream = new java.io.ByteArrayOutputStream()

              val classpath = codegenClasspath
                .map(_.getAbsolutePath())
                .mkString(":")

              val options = ForkOptions()
                .withRunJVMOptions(Vector("-cp", classpath))
                .withOutputStrategy(
                  CustomOutput(outputStream)
                )

              val exitCode = Fork.java(options, mainClassName +: args)

              if (exitCode != 0) {
                s.log.error(outputStream.toString())
                throw new RuntimeException(s"Codegen failed with exit code $exitCode")
              }

              val res = outputStream.toString().split("\n").toSeq
              res.foreach(file => s.log.info(s"Generated $file"))
              res.map(new File(_))
            } else outputs.getOrElse(Seq.empty)
          }
      }
    }

  // We're re-generating everything the classpath of the codegen module changes,
  // which indicates a change in the spec or a change in the codegen logic
  val trackedFiles = codegenClasspath.allPaths.get()
  cached(CodegenInput(trackedFiles))
}
