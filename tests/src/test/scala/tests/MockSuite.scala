package tests

import ch.epfl.scala.bsp.testkit.mock.MockServer
import ch.epfl.scala.bsp.testkit.mock.MockServer.LocalMockServer
import ch.epfl.scala.bsp4j._
import com.google.gson.{Gson, JsonElement}
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import java.nio.file.{Files, Paths}
import java.util
import java.util.Collections
import scala.collection.JavaConverters._
import scala.reflect.ClassTag

trait MockBuildServer
    extends BuildServer
    with ScalaBuildServer
    with JvmBuildServer
    with JavaBuildServer
    with CppBuildServer
    with PythonBuildServer

class HappyMockSuite extends AnyFunSuite {

  def connectToBuildServer(
      localClient: BuildClient,
      baseDir: File
  ): (MockBuildServer, Cancelable) = {

    val LocalMockServer(runningMock, clientIn, clientOut) = MockServer.startMockServer(baseDir)

    val launcher = new Launcher.Builder[MockBuildServer]()
      // .traceMessages(new PrintWriter(System.out))
      .setRemoteInterface(classOf[MockBuildServer])
      .setInput(clientIn)
      .setOutput(clientOut)
      .setLocalService(localClient)
      .create()
    launcher.startListening()
    val bsp = launcher.getRemoteProxy
    val cancelable = Cancelable { () =>
      Cancelable.cancelAll(
        List(Cancelable(() => clientIn.close()), Cancelable(() => clientOut.close()))
      )
    }
    (bsp, cancelable)
  }

  private val gson = new Gson()

  implicit class XtensionBuildTarget(buildTarget: BuildTarget) {
    def asTarget[T: ClassTag]: T = {
      gson.fromJson[T](
        buildTarget.getData.asInstanceOf[JsonElement],
        implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
      )
    }
  }

  def assertWorkspaceBuildTargets(server: MockBuildServer): Unit = {
    // workspace/buildTargets
    val buildTargets = server.workspaceBuildTargets().get().getTargets.asScala
    assert(buildTargets.length == 5)
    val scalaBuildTarget = buildTargets.head.asTarget[ScalaBuildTarget]
    val jvmBuildTarget = buildTargets(1).asTarget[JvmBuildTarget]
    val sbtBuildTarget = buildTargets(2).asTarget[SbtBuildTarget]
    val cppBuildTarget = buildTargets(3).asTarget[CppBuildTarget]
    val pythonBuildTarget = buildTargets(4).asTarget[PythonBuildTarget]
    compareScalaBuildTarget(scalaBuildTarget)
    compareJvmBuildTarget(jvmBuildTarget)
    compareSbtBuildTarget(sbtBuildTarget)
    compareCppBuildTargets(cppBuildTarget)
    comparePythonBuildTarget(pythonBuildTarget)
  }

  def compareSbtBuildTarget(sbtBuildTarget: SbtBuildTarget): Unit = {
    assert(sbtBuildTarget.getSbtVersion == "1.0.0")
    assert(sbtBuildTarget.getAutoImports == List("task-key").asJava)
    compareScalaBuildTarget(sbtBuildTarget.getScalaBuildTarget)
  }

  def compareJvmBuildTarget(jvmBuildTarget: JvmBuildTarget): Unit = {
    val javaHome = sys.props.get("java.home").map(p => Paths.get(p).toUri.toString)
    val javaVersion = sys.props.get("java.vm.specification.version")

    assert(Option(jvmBuildTarget.getJavaHome) == javaHome)
    assert(Option(jvmBuildTarget.getJavaVersion) == javaVersion)
  }

  private def compareScalaBuildTarget(scalaBuildTarget: ScalaBuildTarget): Unit = {
    assert(scalaBuildTarget != null)
    assert(scalaBuildTarget.getScalaVersion == "2.12.7")
    val scalaJars = scalaBuildTarget.getJars.asScala
    List("scala-compiler", "scala-reflect", "scala-library").foreach { scalaJar =>
      assert(scalaJars.exists(_.contains(scalaJar)), (scalaJars, scalaJar))
    }
    assert(scalaBuildTarget.getScalaBinaryVersion == "2.12")
    compareJvmBuildTarget(scalaBuildTarget.getJvmBuildTarget)
  }

  private def compareCppBuildTargets(cppBuildTarget: CppBuildTarget): Unit = {
    assert(cppBuildTarget.getVersion == "C++11")
    assert(cppBuildTarget.getCCompiler == "/usr/bin/gcc")
    assert(cppBuildTarget.getCppCompiler == "/usr/bin/g++")
  }

  private def comparePythonBuildTarget(pythonBuildTarget: PythonBuildTarget): Unit = {
    assert(pythonBuildTarget.getVersion == "3.9")
    assert(pythonBuildTarget.getInterpreter == "/usr/bin/python")
  }

  def getBuildTargetIds(server: MockBuildServer): util.List[BuildTargetIdentifier] =
    server.workspaceBuildTargets().get().getTargets.asScala.map(_.getId).asJava

  def getBuildTargets(server: MockBuildServer): util.List[BuildTarget] =
    server.workspaceBuildTargets().get().getTargets

  def assertScalacOptions(server: MockBuildServer): Unit = {
    val scalacOptionsParams = new ScalacOptionsParams(getBuildTargetIds(server))
    val scalacOptionsResult = server.buildTargetScalacOptions(scalacOptionsParams).get
    val scalacOptionsItems = scalacOptionsResult.getItems.asScala
    scalacOptionsItems.foreach { item =>
      val options = item.getOptions.asScala
      assert(options.isEmpty)
      val uri = item.getTarget.getUri
      assert(uri.nonEmpty)
      val classpath = item.getClasspath.asScala
      assert(classpath.nonEmpty)
      assert(classpath.exists(_.contains("scala-library")))
    }
  }

  def assertJavacOptions(server: MockBuildServer): Unit = {
    val javacOptionsParams = new JavacOptionsParams(getBuildTargetIds(server))
    val javacOptionsResult = server.buildTargetJavacOptions(javacOptionsParams).get
    val javacOptionsItems = javacOptionsResult.getItems.asScala
    javacOptionsItems.foreach { item =>
      val options = item.getOptions.asScala
      assert(options.isEmpty)
      val uri = item.getTarget.getUri
      assert(uri.nonEmpty)
      val classpath = item.getClasspath.asScala
      assert(classpath.nonEmpty)
      assert(classpath.exists(_.contains("guava")))
    }
  }

  def assertCppOptions(server: MockBuildServer): Unit = {
    val cppOptionsParams = new CppOptionsParams(getBuildTargetIds(server))
    val cppOptionsResult = server.buildTargetCppOptions(cppOptionsParams).get
    val cppOptionsItems = cppOptionsResult.getItems.asScala
    cppOptionsItems.foreach { item =>
      val options = item.getCopts.asScala
      assert(options.nonEmpty)
      assert(options.exists(_.contains("-Iexternal/gtest/include")))
      val defines = item.getDefines.asScala
      assert(defines.nonEmpty)
      assert(defines.exists(_.contains("BOOST_FALLTHROUGH")))
      val linkopts = item.getLinkopts.asScala
      assert(linkopts.nonEmpty)
      assert(linkopts.exists(_.contains("-pthread")))
      assert(!item.getLinkshared)
    }
  }

  def assertPythonOptions(server: MockBuildServer): Unit = {
    val pythonOptionsParams = new PythonOptionsParams(getBuildTargetIds(server))
    val pythonOptionsResult = server.buildTargetPythonOptions(pythonOptionsParams).get
    val pythonOptionsItems = pythonOptionsResult.getItems.asScala
    pythonOptionsItems.foreach { item =>
      val options = item.getInterpreterOptions.asScala
      assert(options.nonEmpty)
      assert(options.exists(_.contains("-E")))
    }
  }

  def assertJvmTestEnvironment(server: MockBuildServer): Unit = {
    val jvmTestEnvironmentParams = new JvmTestEnvironmentParams(getBuildTargetIds(server))
    val scalacOptionsResult = server.buildTargetJvmTestEnvironment(jvmTestEnvironmentParams).get
    val scalacOptionsItems = scalacOptionsResult.getItems.asScala
    scalacOptionsItems.foreach { item =>
      val options = item.getJvmOptions.asScala
      assert(options.nonEmpty)
      val uri = item.getTarget.getUri
      assert(uri.nonEmpty)
      val classpath = item.getClasspath.asScala
      assert(classpath.nonEmpty)
      assert(classpath.exists(_.contains("scala-library")))
      val envVars = item.getEnvironmentVariables.asScala
      assert(envVars.get("TESTING").contains("true"))
    }
  }

  def assertJvmRunEnvironment(server: MockBuildServer): Unit = {
    val jvmRunEnvironmentParams = new JvmRunEnvironmentParams(getBuildTargetIds(server))
    val testEnvResult = server.buildTargetJvmRunEnvironment(jvmRunEnvironmentParams).get
    val testEnvItems = testEnvResult.getItems.asScala
    testEnvItems.foreach { item =>
      val options = item.getJvmOptions.asScala
      assert(options.nonEmpty)
      val uri = item.getTarget.getUri
      assert(uri.nonEmpty)
      val classpath = item.getClasspath.asScala
      assert(classpath.nonEmpty)
      assert(classpath.exists(_.contains("scala-library")))
      val envVars = item.getEnvironmentVariables.asScala
      assert(envVars.get("TESTING").contains("false"))
    }
  }

  def assertSources(server: MockBuildServer, client: TestBuildClient): Unit = {
    val params = new SourcesParams(getBuildTargetIds(server))
    val result = server.buildTargetSources(params).get()
    val items = result.getItems.asScala.toList
    assert(items.nonEmpty)
    items.foreach { item =>
      val sources = item.getSources.asScala.toList
      assert(sources.nonEmpty)
      assert(item.getTarget != null)
    }
  }

  def assertOutputPaths(server: MockBuildServer, client: TestBuildClient): Unit = {
    val params = new OutputPathsParams(getBuildTargetIds(server))
    val result = server.buildTargetOutputPaths(params).get()
    val items = result.getItems.asScala.toList
    assert(items.nonEmpty)
    items.foreach { item =>
      val sources = item.getOutputPaths.asScala.toList
      assert(sources.nonEmpty)
      assert(item.getTarget != null)
    }
  }

  def assertDependencySources(server: MockBuildServer, client: TestBuildClient): Unit = {
    val params = new DependencySourcesParams(getBuildTargetIds(server))
    val result = server.buildTargetDependencySources(params).get()
    val items = result.getItems.asScala.toList
    assert(items.nonEmpty)
    items.foreach { item =>
      val sources = item.getSources.asScala.toList
      assert(sources.nonEmpty)
      assert(sources.exists(_.endsWith(".jar")))
      assert(item.getTarget != null)
    }
  }

  def assertCompile(server: MockBuildServer, client: TestBuildClient): Unit = {
    client.reset()
    val params = new CompileParams(getBuildTargetIds(server))
    params.setOriginId("origin")
    val compileResult = server.buildTargetCompile(params).get()
    assert(compileResult.getOriginId == params.getOriginId)

    // TODO in HappyMockServer: send some notifications in compile
    assert(client.logMessages.nonEmpty)
    assert(client.showMessages.nonEmpty)
    assert(client.diagnostics.nonEmpty)
    assert(client.taskStarts.exists { p =>
      p.getTaskId.getId == "subtask1id" && p.getOriginId == params.getOriginId
    })
    assert(client.taskFinishes.exists { p =>
      p.getTaskId.getId == "subtask1id" && p.getStatus == StatusCode.OK && p.getOriginId == params.getOriginId
    })
    assert(client.compileReports.nonEmpty)
    assert(compileResult.getStatusCode == StatusCode.OK)
  }

  def assertTest(server: MockBuildServer, client: TestBuildClient): Unit = {
    client.reset()
    val testableTargets =
      getBuildTargets(server).asScala.filter(_.getCapabilities.getCanTest).map(_.getId).asJava
    val params = new TestParams(testableTargets)
    val testResult = server.buildTargetTest(params).get()
    assert(testResult.getOriginId == params.getOriginId)
    // TODO in HappyMockServer: send some notifications in compile
    // assert(client.logMessages.nonEmpty)
    // assert(client.testReports.nonEmpty)
    assert(testResult.getStatusCode == StatusCode.OK)
  }

  def assertRun(server: MockBuildServer, client: TestBuildClient): Unit = {
    client.reset()
    val targetToRun = getBuildTargets(server).asScala.find(_.getCapabilities.getCanRun).get
    val params = new RunParams(targetToRun.getId)
    val runResult = server.buildTargetRun(params).get()

    assert(runResult.getOriginId == params.getOriginId)
    assert(runResult.getStatusCode == StatusCode.OK)

    // TODO in HappyMockServer: some actual run
    // assert(client.logMessages.exists(_.getMessage == "Hello world!"))
    // Compilation was triggered by `assertCompile`, so no diagnostics are found this time
    // assert(client.logMessages.nonEmpty)
  }

  def assertServerCapabilities(serverCapabilities: BuildServerCapabilities): Unit = {
    val compiles = serverCapabilities.getCompileProvider.getLanguageIds.asScala
    val runs = serverCapabilities.getCompileProvider.getLanguageIds.asScala
    val tests = serverCapabilities.getCompileProvider.getLanguageIds.asScala

    val languages = List("scala", "java", "cpp", "python").sorted
    assert(compiles.sorted == languages)
    assert(runs.sorted == languages)
    assert(tests.sorted == languages)
  }

  def assertServerEndpoints(server: MockBuildServer, client: TestBuildClient): Unit = {
    assertWorkspaceBuildTargets(server)
    assertScalacOptions(server)
    assertJavacOptions(server)
    assertCppOptions(server)
    assertPythonOptions(server)
    assertJvmTestEnvironment(server)
    assertJvmRunEnvironment(server)
    assertSources(server, client)
    assertDependencySources(server, client)
    assertOutputPaths(server, client)
    assertCompile(server, client)
    assertTest(server, client)
    assertRun(server, client)
    // - buildTarget/mainClasses
    // - buildTarget/scalaMainClasses
  }

  test("end to end") {
    val testDirectory = Files.createTempDirectory("bsp.MockSuite")

    val client = new TestBuildClient
    val (server, cancel) = connectToBuildServer(client, testDirectory.toFile)
    try {
      val capabilities = new BuildClientCapabilities(Collections.singletonList("scala"))
      val initializeParams = new InitializeBuildParams(
        "test-client",
        "1.0.0",
        "2.0.0-M1",
        testDirectory.toUri.toString,
        capabilities
      )
      val serverCapabilities = server.buildInitialize(initializeParams).get().getCapabilities
      server.onBuildInitialized()
      try {
        assertServerCapabilities(serverCapabilities)
        assertServerEndpoints(server, client)
      } finally {
        try server.buildShutdown().get()
        finally server.onBuildExit()
      }
    } finally {
      Thread.sleep(1000) // complete server.exit()
      cancel.cancel()
    }
  }
}
