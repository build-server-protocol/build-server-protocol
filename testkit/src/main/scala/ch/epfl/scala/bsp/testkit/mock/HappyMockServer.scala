package ch.epfl.scala.bsp.testkit.mock

import java.io.File
import java.net.URI
import java.nio.file.Paths
import java.util
import java.util.UUID
import java.util.concurrent.{CompletableFuture, Executors}
import ch.epfl.scala.bsp.testkit.mock.HappyMockServer.ProtocolError
import ch.epfl.scala.bsp4j._
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException
import org.eclipse.lsp4j.jsonrpc.messages.{ResponseError, ResponseErrorCode}

import scala.collection.immutable.ListMap
import scala.concurrent.duration._
import scala.concurrent._
import scala.jdk.CollectionConverters._
import scala.util.Try

object HappyMockServer {
  case object ProtocolError
}

/** Mock server that gives a happy successful result to any request. */
class HappyMockServer(base: File) extends AbstractMockServer {

  override var client: BuildClient = _

  val isInitialized: Promise[Either[ProtocolError.type, Unit]] =
    scala.concurrent.Promise[Either[ProtocolError.type, Unit]]()
  val isShutdown: Promise[Either[ProtocolError.type, Unit]] =
    scala.concurrent.Promise[Either[ProtocolError.type, Unit]]()

  private val executor = Executors.newCachedThreadPool()
  private implicit val executionContext: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(executor)

  // for easy override of individual parts of responses
  def name = "BSP Mock Server"
  def serverVersion = "1.0"
  def bspVersion = "2.0"

  def supportedLanguages: util.List[String] = List("java", "scala", "cpp", "python").asJava

  def capabilities: BuildServerCapabilities = {
    val c = new BuildServerCapabilities()
    c.setCompileProvider(new CompileProvider(supportedLanguages))
    c.setTestProvider(new TestProvider(supportedLanguages))
    c.setRunProvider(new RunProvider(supportedLanguages))
    c.setDebugProvider(new DebugProvider(supportedLanguages))
    c.setInverseSourcesProvider(true)
    c.setWrappedSourcesProvider(true)
    c.setDependencySourcesProvider(true)
    c.setResourcesProvider(true)
    c.setBuildTargetChangedProvider(true)
    c.setJvmRunEnvironmentProvider(true)
    c.setJvmTestEnvironmentProvider(true)
    c.setCanReload(true)
    c.setDependencyModulesProvider(true)
    c.setJvmCompileClasspathProvider(true)
    c
  }

  val baseUri: URI = base.getCanonicalFile.toURI
  private val languageIds = List("scala").asJava
  private val cppLanguageId = List("cpp").asJava
  private val pythonLanguageId = List("python").asJava

  val targetId1 = new BuildTargetIdentifier(baseUri.resolve("target1").toString)
  val targetId2 = new BuildTargetIdentifier(baseUri.resolve("target2").toString)
  val targetId3 = new BuildTargetIdentifier(baseUri.resolve("target3").toString)
  val targetId4 = new BuildTargetIdentifier(baseUri.resolve("target4").toString)
  val targetId5 = new BuildTargetIdentifier(baseUri.resolve("target5").toString)
  private val capabilities1 = new BuildTargetCapabilities()
  capabilities1.setCanCompile(true)
  val target1 = new BuildTarget(
    targetId1,
    List(BuildTargetTag.LIBRARY).asJava,
    languageIds,
    List.empty.asJava,
    capabilities1
  )

  private val capabilities2 = new BuildTargetCapabilities()
  capabilities2.setCanCompile(true)
  capabilities2.setCanTest(true)
  val target2 = new BuildTarget(
    targetId2,
    List(BuildTargetTag.TEST).asJava,
    languageIds,
    List(targetId1).asJava,
    capabilities2
  )

  val capabilities3 = new BuildTargetCapabilities()
  capabilities3.setCanCompile(true)
  capabilities3.setCanRun(true)
  val target3 = new BuildTarget(
    targetId3,
    List(BuildTargetTag.APPLICATION).asJava,
    languageIds,
    List(targetId1).asJava,
    capabilities3
  )

  val capabilities4 = new BuildTargetCapabilities()
  capabilities4.setCanCompile(true)
  capabilities4.setCanRun(true)
  val target4 = new BuildTarget(
    targetId4,
    List(BuildTargetTag.APPLICATION).asJava,
    cppLanguageId,
    List.empty.asJava,
    capabilities4
  )

  val capabilities5 = new BuildTargetCapabilities()
  capabilities5.setCanCompile(true)
  capabilities5.setCanRun(true)
  val target5 = new BuildTarget(
    targetId5,
    List(BuildTargetTag.APPLICATION).asJava,
    pythonLanguageId,
    List.empty.asJava,
    capabilities5
  )

  val compileTargets: Map[BuildTargetIdentifier, BuildTarget] = ListMap(
    targetId1 -> target1,
    targetId2 -> target2,
    targetId3 -> target3,
    targetId4 -> target4,
    targetId5 -> target5
  )

  def uriInTarget(target: BuildTargetIdentifier, filePath: String): URI =
    new URI(target.getUri).resolve(filePath)

  private def asDirUri(path: URI): String =
    path.toString + "/"

  private def environmentItem(testing: Boolean) = {
    val classpath = List("scala-library.jar").asJava
    val jvmOptions = List("-Xms256m").asJava
    val environmentVariables = Map("A" -> "a", "TESTING" -> testing.toString).asJava
    val workdir = "/tmp"
    val item1 = new JvmEnvironmentItem(
      targetId1,
      classpath,
      jvmOptions,
      workdir,
      environmentVariables
    )
    val mainClass = new JvmMainClass("MainClass.java", List.empty[String].asJava)
    item1.setMainClasses(List(mainClass).asJava)
    item1
  }
  override def buildTargetJvmRunEnvironment(
      params: JvmRunEnvironmentParams
  ): CompletableFuture[JvmRunEnvironmentResult] =
    handleRequest {
      val item1: JvmEnvironmentItem = environmentItem(testing = false)
      val result = new JvmRunEnvironmentResult(List(item1).asJava)
      Right(result)
    }

  override def buildTargetJvmTestEnvironment(
      params: JvmTestEnvironmentParams
  ): CompletableFuture[JvmTestEnvironmentResult] =
    handleRequest {
      val item1: JvmEnvironmentItem = environmentItem(testing = true)
      val result = new JvmTestEnvironmentResult(List(item1).asJava)
      Right(result)
    }

  override def buildTargetJvmCompileClasspath(
      params: JvmCompileClasspathParams
  ): CompletableFuture[JvmCompileClasspathResult] =
    handleRequest {
      val classpath = List("scala-library.jar").asJava
      val item1: JvmCompileClasspathItem = new JvmCompileClasspathItem(targetId1, classpath)
      val result = new JvmCompileClasspathResult(List(item1).asJava)
      Right(result)
    }

  override def buildTargetScalacOptions(
      params: ScalacOptionsParams
  ): CompletableFuture[ScalacOptionsResult] =
    handleRequest {
      val options = List.empty[String].asJava
      val classpath = List("scala-library.jar").asJava
      val item1 =
        new ScalacOptionsItem(targetId1, options, classpath, uriInTarget(targetId1, "out").toString)
      val item2 =
        new ScalacOptionsItem(targetId2, options, classpath, uriInTarget(targetId2, "out").toString)
      val item3 =
        new ScalacOptionsItem(targetId3, options, classpath, uriInTarget(targetId3, "out").toString)
      val result = new ScalacOptionsResult(List(item1, item2, item3).asJava)
      Right(result)
    }

  override def buildTargetJavacOptions(
      params: JavacOptionsParams
  ): CompletableFuture[JavacOptionsResult] = {
    handleRequest {
      val options = List.empty[String].asJava
      val classpath = List("guava.jar").asJava
      val item1 =
        new JavacOptionsItem(targetId1, options, classpath, uriInTarget(targetId1, "out").toString)
      val item2 =
        new JavacOptionsItem(targetId2, options, classpath, uriInTarget(targetId2, "out").toString)
      val item3 =
        new JavacOptionsItem(targetId3, options, classpath, uriInTarget(targetId3, "out").toString)
      val result = new JavacOptionsResult(List(item1, item2, item3).asJava)
      Right(result)
    }
  }

  override def buildTargetCppOptions(
      params: CppOptionsParams
  ): CompletableFuture[CppOptionsResult] = {
    handleRequest {
      val copts = List("-Iexternal/gtest/include").asJava
      val defines = List("BOOST_FALLTHROUGH").asJava
      val linkopts = List("-pthread").asJava
      val item = new CppOptionsItem(targetId4, copts, defines, linkopts)
      val result = new CppOptionsResult(List(item).asJava)
      Right(result)
    }
  }

  override def buildTargetPythonOptions(
      params: PythonOptionsParams
  ): CompletableFuture[PythonOptionsResult] = {
    handleRequest {
      val interpreterOpts = List("-E").asJava
      val item = new PythonOptionsItem(targetId5, interpreterOpts)
      val result = new PythonOptionsResult(List(item).asJava)
      Right(result)
    }
  }

  override def buildTargetScalaTestClasses(
      params: ScalaTestClassesParams
  ): CompletableFuture[ScalaTestClassesResult] =
    handleRequest {
      val classes1 = List("class1").asJava
      val classes2 = List("class2").asJava
      val testClassesItems = List(
        new ScalaTestClassesItem(targetId1, classes1),
        new ScalaTestClassesItem(targetId2, classes2)
      ).asJava
      val result = new ScalaTestClassesResult(testClassesItems)
      Right(result)
    }

  override def buildTargetScalaMainClasses(
      params: ScalaMainClassesParams
  ): CompletableFuture[ScalaMainClassesResult] =
    handleRequest {
      val classes1 = List(
        new ScalaMainClass("class1", List("arg1", "arg2").asJava, List("-deprecated").asJava)
      ).asJava
      val classes2 = List(
        new ScalaMainClass("class2", List("arg1", "arg2").asJava, List("-deprecated").asJava)
      ).asJava
      val mainClassesItems = List(
        new ScalaMainClassesItem(targetId1, classes1),
        new ScalaMainClassesItem(targetId1, classes2)
      ).asJava
      val result = new ScalaMainClassesResult(mainClassesItems)
      Right(result)
    }

  override def buildInitialize(
      params: InitializeBuildParams
  ): CompletableFuture[InitializeBuildResult] = {
    handleBuildInitializeRequest {
      val result = new InitializeBuildResult("BSP Mock Server", "1.0", "2.0", capabilities)
      Right(result)
    }
  }

  override def onBuildInitialized(): Unit =
    handleBuildInitializeRequest { Right(isInitialized.success(Right(()))) }

  override def buildShutdown(): CompletableFuture[AnyRef] = {
    handleBuildShutdownRequest {
      isShutdown.success(Right())
      Right("boo")
    }
  }

  override def onBuildExit(): Unit = {
    Await.ready(isShutdown.future, 1.seconds)
  }

  override def workspaceBuildTargets(): CompletableFuture[WorkspaceBuildTargetsResult] =
    handleRequest {
      val javaHome = sys.props.get("java.home").map(p => Paths.get(p).toUri.toString)
      val javaVersion = sys.props.get("java.vm.specification.version")
      val jvmBuildTarget = new JvmBuildTarget()
      jvmBuildTarget.setJavaHome(javaHome.get)
      jvmBuildTarget.setJavaVersion(javaVersion.get)
      val scalaJars = List("scala-compiler.jar", "scala-reflect.jar", "scala-library.jar").asJava
      val scalaBuildTarget =
        new ScalaBuildTarget("org.scala-lang", "2.12.7", "2.12", ScalaPlatform.JVM, scalaJars)
      scalaBuildTarget.setJvmBuildTarget(jvmBuildTarget)
      val autoImports = List("task-key").asJava
      val children = List(targetId3).asJava
      val sbtBuildTarget =
        new SbtBuildTarget("1.0.0", autoImports, scalaBuildTarget, children)
      val cppBuildTarget =
        new CppBuildTarget()
      cppBuildTarget.setVersion("C++11")
      cppBuildTarget.setCompiler("gcc")
      cppBuildTarget.setCCompiler("/usr/bin/gcc")
      cppBuildTarget.setCppCompiler("/usr/bin/g++")
      val pythonBuildTarget =
        new PythonBuildTarget()
      pythonBuildTarget.setInterpreter("/usr/bin/python")
      pythonBuildTarget.setVersion("3.9")
      target1.setDisplayName("target 1")
      target1.setBaseDirectory(targetId1.getUri)
      target1.setDataKind(BuildTargetDataKind.SCALA)
      target1.setData(scalaBuildTarget)

      target2.setDisplayName("target 2")
      target2.setBaseDirectory(targetId2.getUri)
      target2.setDataKind(BuildTargetDataKind.JVM)
      target2.setData(jvmBuildTarget)

      target3.setDisplayName("target 3")
      target3.setBaseDirectory(targetId3.getUri)
      target3.setDataKind(BuildTargetDataKind.SBT)
      target3.setData(sbtBuildTarget)

      target4.setDisplayName("target 4")
      target4.setBaseDirectory(targetId4.getUri)
      target4.setDataKind(BuildTargetDataKind.CPP)
      target4.setData(cppBuildTarget)

      target5.setDisplayName("target 5")
      target5.setBaseDirectory(targetId5.getUri)
      target5.setDataKind(BuildTargetDataKind.PYTHON)
      target5.setData(pythonBuildTarget)

      val result = new WorkspaceBuildTargetsResult(compileTargets.values.toList.asJava)
      Right(result)
    }

  override def workspaceReload(): CompletableFuture[AnyRef] =
    CompletableFuture.completedFuture(null)

  override def buildTargetSources(params: SourcesParams): CompletableFuture[SourcesResult] =
    handleRequest {

      val sourceDir1 = new URI(targetId1.getUri).resolve("src/")
      val item1 = new SourceItem(asDirUri(sourceDir1), SourceItemKind.DIRECTORY, true)
      val items1 = new SourcesItem(targetId1, List(item1).asJava)

      val sourceDir2 = new URI(targetId2.getUri).resolve("src-gen/")
      val item2 = new SourceItem(asDirUri(sourceDir2), SourceItemKind.DIRECTORY, true)
      val items2 = new SourcesItem(targetId2, List(item2).asJava)

      val sourceDir3 = new URI(targetId3.getUri).resolve("sauce/")
      val sourceFile1 = new URI(targetId3.getUri).resolve("somewhere/sourcefile1")
      val sourceFile2 = new URI(targetId3.getUri).resolve("somewhere/below/sourcefile2")
      val sourceFile3 = new URI(targetId3.getUri).resolve("somewhere/sourcefile3")
      val item3Dir = new SourceItem(asDirUri(sourceDir3), SourceItemKind.DIRECTORY, false)
      val item31 = new SourceItem(sourceFile1.toString, SourceItemKind.FILE, false)
      val item32 = new SourceItem(sourceFile2.toString, SourceItemKind.FILE, false)
      val item33 = new SourceItem(sourceFile3.toString, SourceItemKind.FILE, true)
      val items3 = new SourcesItem(targetId3, List(item3Dir, item31, item32, item33).asJava)

      val result = new SourcesResult(List(items1, items2, items3).asJava)
      Right(result)
    }

  override def buildTargetInverseSources(
      params: InverseSourcesParams
  ): CompletableFuture[InverseSourcesResult] =
    handleRequest {
      val result = new InverseSourcesResult(List(targetId1, targetId2, targetId3).asJava)
      Right(result)
    }

  override def buildTargetWrappedSources(
      params: WrappedSourcesParams
  ): CompletableFuture[WrappedSourcesResult] =
    handleRequest {
      params.targets.map(target =>
        new WrappedSourcesResult(List(WrappedSourcesItem(target, List.empty)))
      )
      Right(result)
    }

  override def buildTargetDependencySources(
      params: DependencySourcesParams
  ): CompletableFuture[DependencySourcesResult] =
    handleRequest {

      val target1Sources = List("lib/Library.scala", "lib/Helper.scala", "lib/some-library.jar")
        .map(uriInTarget(targetId1, _).toString)
        .asJava
      val target2Sources =
        List("lib/LibraryTest.scala", "lib/HelperTest.scala", "lib/some-library.jar")
          .map(uriInTarget(targetId2, _).toString)
          .asJava
      val target3Sources = List("lib/App.scala", "lib/some-library.jar")
        .map(uriInTarget(targetId3, _).toString)
        .asJava

      val item1 = new DependencySourcesItem(targetId1, target1Sources)
      val item2 = new DependencySourcesItem(targetId2, target2Sources)
      val item3 = new DependencySourcesItem(targetId3, target3Sources)
      val result = new DependencySourcesResult(List(item1, item2, item3).asJava)

      Right(result)
    }

  override def buildTargetResources(params: ResourcesParams): CompletableFuture[ResourcesResult] =
    handleRequest {
      // TODO provide resources
      val result = new ResourcesResult(List.empty.asJava)
      Right(result)
    }

  override def buildTargetOutputPaths(
      params: OutputPathsParams
  ): CompletableFuture[OutputPathsResult] =
    handleRequest {
      val outputDir1 = new URI(targetId1.getUri).resolve("log/")
      val item1 = new OutputPathItem(asDirUri(outputDir1), OutputPathItemKind.DIRECTORY)
      val items1 = new OutputPathsItem(targetId1, List(item1).asJava)

      val outputDir2 = new URI(targetId2.getUri).resolve("target/")
      val item2 = new OutputPathItem(asDirUri(outputDir2), OutputPathItemKind.DIRECTORY)
      val items2 = new OutputPathsItem(targetId2, List(item2).asJava)

      val outputDir3 = new URI(targetId3.getUri).resolve("work/")
      val outputFile1 = new URI(targetId3.getUri).resolve("tmp/file1")
      val outputFile2 = new URI(targetId3.getUri).resolve("tmp/below/file2")
      val outputFile3 = new URI(targetId3.getUri).resolve("tmp/file3")
      val item3Dir = new OutputPathItem(asDirUri(outputDir3), OutputPathItemKind.DIRECTORY)
      val item31 = new OutputPathItem(outputFile1.toString, OutputPathItemKind.FILE)
      val item32 = new OutputPathItem(outputFile2.toString, OutputPathItemKind.FILE)
      val item33 = new OutputPathItem(outputFile3.toString, OutputPathItemKind.FILE)
      val items3 = new OutputPathsItem(targetId3, List(item3Dir, item31, item32, item33).asJava)

      val result = new OutputPathsResult(List(items1, items2, items3).asJava)
      Right(result)
    }

  override def buildTargetCompile(params: CompileParams): CompletableFuture[CompileResult] =
    handleRequest {
      val uncompilableTargets = params.getTargets.asScala.filter(targetIdentifier => {
        compileTargets.get(targetIdentifier) match {
          case Some(target) =>
            !target.getCapabilities.getCanCompile
          case None => false
        }
      })

      if (uncompilableTargets.nonEmpty)
        Left(
          new ResponseError(
            ResponseErrorCode.InvalidParams,
            s"Targets ${uncompilableTargets.map(_.getUri)} are not compilable",
            null
          )
        )
      else {
        val origin = params.getOriginId
        val compile1Id = new TaskId("compile1id")

        compileStart(compile1Id, Some(origin), "compile started: " + targetId1.getUri, targetId1)

        val subtaskParents = List(compile1Id.getId).asJava
        logMessage("spawning subtasks", task = Some(compile1Id), origin = Some(params.getOriginId))
        val subtask1Id = new TaskId("subtask1id")
        subtask1Id.setParents(subtaskParents)
        val subtask2Id = new TaskId("subtask2id")
        subtask2Id.setParents(subtaskParents)
        val subtask3Id = new TaskId("subtask3id")
        subtask3Id.setParents(subtaskParents)
        taskStart(subtask1Id, Some(origin), "resolving widgets", None, None)
        taskStart(subtask2Id, Some(origin), "memoizing datapoints", None, None)
        taskStart(subtask3Id, Some(origin), "unionizing beams", None, None)

        val compileme = new URI(targetId1.getUri).resolve("compileme.scala")
        val doc = new TextDocumentIdentifier(compileme.toString)

        val errorMessage = new Diagnostic(
          new Range(new Position(1, 10), new Position(1, 110)),
          "this is a compile error"
        )
        errorMessage.setSeverity(DiagnosticSeverity.ERROR)

        val warningMessage = new Diagnostic(
          new Range(new Position(2, 10), new Position(2, 20)),
          "this is a compile warning"
        )
        warningMessage.setSeverity(DiagnosticSeverity.WARNING)

        val infoMessage =
          new Diagnostic(
            new Range(new Position(3, 1), new Position(3, 33)),
            "this is a compile info"
          )
        infoMessage.setSeverity(DiagnosticSeverity.INFORMATION)

        publishDiagnostics(
          doc,
          targetId1,
          List(errorMessage, warningMessage),
          Option(params.getOriginId)
        )
        publishDiagnostics(doc, targetId1, List(infoMessage), Option(params.getOriginId))

        taskFinish(subtask1Id, Some(origin), "targets resolved", StatusCode.OK, None, None)
        taskFinish(subtask2Id, Some(origin), "datapoints forgotten", StatusCode.ERROR, None, None)
        taskFinish(
          subtask3Id,
          Some(origin),
          "beams are classless",
          StatusCode.CANCELLED,
          None,
          None
        )
        showMessage("subtasks done", task = Some(compile1Id), origin = Option(params.getOriginId))

        compileReport(compile1Id, Some(origin), "compile failed", targetId1, StatusCode.ERROR)

        params.getTargets.asScala.foreach { target =>
          val compileId = new TaskId(UUID.randomUUID().toString)
          compileStart(compileId, Some(origin), "compile started: " + target.getUri, target)
          taskProgress(compileId, Some(origin), "compiling some files", 100, 23, None, None)
          compileReport(compileId, Some(origin), "compile complete", target, StatusCode.OK)
        }

        val result = new CompileResult(StatusCode.OK)
        result.setOriginId(params.getOriginId)
        Right(result)
      }

    }

  override def buildTargetTest(params: TestParams): CompletableFuture[TestResult] =
    handleRequest {
      val errorTargets = params.getTargets.asScala.filter(targetIdentifier => {
        compileTargets.get(targetIdentifier) match {
          case Some(target) =>
            !target.getCapabilities.getCanTest
          case None => false
        }
      })

      if (errorTargets.nonEmpty)
        Left(
          new ResponseError(
            ResponseErrorCode.InvalidParams,
            s"Target ${errorTargets.map(_.getUri)} is not compilable",
            null
          )
        )
      else {
        // TODO some test task/report notifications
        // TODO some individual test notifications
        val result = new TestResult(StatusCode.OK)
        result.setOriginId(params.getOriginId)
        Right(result)
      }
    }

  override def buildTargetRun(params: RunParams): CompletableFuture[RunResult] =
    handleRequest {
      compileTargets.get(params.getTarget) match {
        case Some(target) =>
          if (!target.getCapabilities.getCanRun)
            Left(
              new ResponseError(
                ResponseErrorCode.InvalidParams,
                s"Target ${target.getId.getUri} is not compilable",
                null
              )
            )
          else
            mockRunAnswer(params)
        case None =>
          mockRunAnswer(params)
      }
    }

  private def mockRunAnswer(params: RunParams) = {
    // TODO some task notifications
    val result = new RunResult(StatusCode.OK)
    result.setOriginId(params.getOriginId)
    Right(result)
  }

  override def debugSessionStart(
      params: DebugSessionParams
  ): CompletableFuture[DebugSessionAddress] =
    handleRequest {
      Right(new DebugSessionAddress("tcp://127.0.0.1:51379"))
    }

  override def buildTargetCleanCache(
      params: CleanCacheParams
  ): CompletableFuture[CleanCacheResult] =
    handleRequest {
      val result = new CleanCacheResult(true)
      Right(result)
    }

  override def buildTargetDependencyModules(
      params: DependencyModulesParams
  ): CompletableFuture[DependencyModulesResult] = {
    handleRequest {
      def jvmModule(
          targetId: BuildTargetIdentifier,
          org: String,
          name: String,
          version: String
      ): DependencyModule = {
        val fullName = s"$org-$name-$version"
        val module = new DependencyModule(
          s"$org-$name",
          version
        )
        val artifacts = List(None, Some("-sources")).map { classifier =>
          val path = s"lib/$fullName${classifier.getOrElse("")}.jar"
          val artifact = new MavenDependencyModuleArtifact(uriInTarget(targetId, path).toString)
          classifier.foreach(artifact.setClassifier)
          artifact
        }.asJava
        val data = new MavenDependencyModule(org, name, version, artifacts)
        module.setData(data)
        module.setDataKind("maven")
        module
      }

      val target1Modules = List(jvmModule(targetId1, "org.library1", "0.0.1", "jvm")).asJava
      val target2Modules = List(jvmModule(targetId2, "org.library2_2.13", "0.0.1", "jvm")).asJava
      val target3Modules = List(jvmModule(targetId3, "org.library3_2.13", "0.0.1", "jvm")).asJava

      val result = new DependencyModulesResult(
        List(
          new DependencyModulesItem(targetId1, target1Modules),
          new DependencyModulesItem(targetId2, target2Modules),
          new DependencyModulesItem(targetId3, target3Modules)
        ).asJava
      )
      Right(result)
    }
  }

  override def onRunReadStdin(params: ReadParams): Unit = {}

  private def handleRequest[T](
      f: => Either[ResponseError, T]
  ): CompletableFuture[T] = {
    val result = for {
      _ <- checkInitialize
      _ <- checkShutdown
      res <- getValue(f)
    } yield res

    toCompletableFuture(result)
  }

  private def toCompletableFuture[T](either: Either[ResponseError, T]): CompletableFuture[T] =
    either match {
      case Left(value)  => completeExceptionally(value)
      case Right(value) => CompletableFuture.completedFuture(value)
    }

  private def checkInitialize[T]: Either[ResponseError, Unit] = {
    try {
      Await.result(isInitialized.future, 1.seconds)
      Right(())
    } catch {
      case x: TimeoutException =>
        val err = new ResponseError(
          ResponseErrorCode.ServerNotInitialized,
          "Cannot handle requests before receiving the initialize request",
          null
        )
        Left(err)
    }
  }

  private def checkShutdown[T]: Either[ResponseError, Unit] =
    if (isShutdown.isCompleted) {
      val err = new ResponseError(
        ResponseErrorCode.jsonrpcReservedErrorRangeEnd, // FIXME: this has never been a valid code
        // previously it was named "serverErrorEnd", but it didn't mean "cannot handle requests after shutdown",
        // it was a marker. From the docs: "This is the start range of JSON RPC reserved error codes.
        // It doesn't denote a real error code."
        // I'm not changing it now, because it's a breaking change.
        "Cannot handle requests after receiving the shutdown request",
        null
      )
      Left(err)
    } else Right(())

  private def getValue[T](f: => Either[ResponseError, T]): Either[ResponseError, T] =
    Try(f).toEither.left
      .map(exception =>
        new ResponseError(ResponseErrorCode.InternalError, exception.getMessage, null)
      )
      .joinRight

  private def handleBuildInitializeRequest[T](
      f: => Either[ResponseError, T]
  ): CompletableFuture[T] =
    toCompletableFuture(checkShutdown.flatMap(_ => getValue(f)))

  private def handleBuildShutdownRequest[T](
      f: => Either[ResponseError, T]
  ): CompletableFuture[T] =
    toCompletableFuture(checkInitialize.flatMap(_ => getValue(f)))

  private def completeExceptionally[T](error: ResponseError): CompletableFuture[T] = {
    val future = new CompletableFuture[T]()
    future.completeExceptionally(
      new ResponseErrorException(
        error
      )
    )
    future
  }

}
