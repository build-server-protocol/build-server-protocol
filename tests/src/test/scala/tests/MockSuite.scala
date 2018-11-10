package tests

import java.io.{File, PipedInputStream, PipedOutputStream}
import java.nio.file.Files
import java.util
import java.util.Collections

import ch.epfl.scala.bsp.mock.{HappyMockServer, MockServer}
import ch.epfl.scala.bsp4j._
import com.google.gson.{Gson, JsonElement}
import monix.execution.CancelableFuture
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.scalatest.FunSuite

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

trait MockBuildServer extends BuildServer with ScalaBuildServer

class MockTestingClient extends BuildClient {
  val gson: Gson = new Gson()

  val showMessages: ListBuffer[ShowMessageParams] = ListBuffer.empty[ShowMessageParams]
  val logMessages: ListBuffer[LogMessageParams] = ListBuffer.empty[LogMessageParams]
  val diagnostics: ListBuffer[PublishDiagnosticsParams] = ListBuffer.empty[PublishDiagnosticsParams]
  val compileReports: ListBuffer[CompileReport] = ListBuffer.empty[CompileReport]
  val testReports: ListBuffer[TestReport] = ListBuffer.empty[TestReport]
  def reset(): Unit = {
    showMessages.clear()
    logMessages.clear()
    diagnostics.clear()
    compileReports.clear()
  }
  override def onBuildShowMessage(params: ShowMessageParams): Unit = showMessages += params
  override def onBuildLogMessage(params: LogMessageParams): Unit = logMessages += params
  override def onBuildPublishDiagnostics(params: PublishDiagnosticsParams): Unit =
    diagnostics += params
  override def onBuildTargetDidChange(params: DidChangeBuildTarget): Unit = ()
  override def onBuildTaskStart(params: TaskStartParams): Unit = ()
  override def onBuildTaskProgress(params: TaskProgressParams): Unit = ()

  override def onBuildTaskFinish(params: TaskFinishParams): Unit = {
    params.getDataKind match {
      case TaskDataKind.COMPILE_REPORT =>
        val json = params.getData.asInstanceOf[JsonElement]
        val report = gson.fromJson[CompileReport](json, classOf[CompileReport])
        compileReports += report
      case TaskDataKind.TEST_REPORT =>
        val json = params.getData.asInstanceOf[JsonElement]
        val report = gson.fromJson[TestReport](json, classOf[TestReport])
        testReports += report
      case _ =>
    }
  }
}

class HappyMockSuite extends FunSuite {

  import MockServer.scheduler

  def startMockServer(testBaseDir: File): (CancelableFuture[_], PipedInputStream, PipedOutputStream) = {

    val clientIn = new PipedInputStream()
    val serverOut = new PipedOutputStream(clientIn)
    val serverIn = new PipedInputStream()
    val clientOut = new PipedOutputStream(serverIn)

    val mock = new HappyMockServer(testBaseDir)
    val running = MockServer.serverTask(mock, serverIn, serverOut).runAsync
    (running, clientIn, clientOut)
  }

  def connectToBuildServer(localClient: BuildClient, baseDir: File): (MockBuildServer, Cancelable) = {

    val (runningMock, clientIn, clientOut) = startMockServer(baseDir)

    val launcher = new Launcher.Builder[MockBuildServer]()
      //      .traceMessages(new PrintWriter(System.out))
      .setRemoteInterface(classOf[MockBuildServer])
      .setInput(clientIn)
      .setOutput(clientOut)
      .setLocalService(localClient)
      .create()
    launcher.startListening()
    val bsp = launcher.getRemoteProxy
    localClient.onConnectWithServer(bsp)
    val cancelable = Cancelable { () =>
      Cancelable.cancelAll(
        List(
          Cancelable(() => clientIn.close()),
          Cancelable(() => clientOut.close()),
          Cancelable(() => runningMock.cancel()),
        )
      )
    }
    (bsp, cancelable)
  }

  private val gson = new Gson()

  implicit class XtensionBuildTarget(buildTarget: BuildTarget) {
    def asScalaBuildTarget: ScalaBuildTarget = {
      gson.fromJson[ScalaBuildTarget](buildTarget.getData.asInstanceOf[JsonElement],
        classOf[ScalaBuildTarget])
    }
  }

  def assertWorkspaceBuildTargets(server: MockBuildServer): Unit = {
    // workspace/buildTargets
    val buildTargets = server.workspaceBuildTargets().get().getTargets.asScala
    assert(buildTargets.length == 3)
    val scalaBuildTargets = buildTargets.map(_.asScalaBuildTarget)
    scalaBuildTargets.foreach { scalaBuildTarget =>
      assert(scalaBuildTarget != null)
      assert(scalaBuildTarget.getScalaVersion == "2.12.7")
      val scalaJars = scalaBuildTarget.getJars.asScala
      List("scala-compiler", "scala-reflect", "scala-library").foreach { scalaJar =>
        assert(scalaJars.exists(_.contains(scalaJar)), (scalaJars, scalaJar))
      }
      assert(scalaBuildTarget.getScalaBinaryVersion == "2.12")
    }
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

  def assertDependencySources(server: MockBuildServer, client: BloopClient): Unit = {
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

  def assertCompile(server: MockBuildServer, client: BloopClient): Unit = {
    client.reset()
    val params = new CompileParams(getBuildTargetIds(server))
    val compileResult = server.buildTargetCompile(params).get()
    assert(compileResult.getOriginId == params.getOriginId)

    // TODO in HappyMockServer: send some notifications in compile
    // assert(client.logMessages.nonEmpty)
    // assert(client.diagnostics.nonEmpty)
    // assert(client.compileReports.nonEmpty)
    assert(compileResult.getStatusCode == StatusCode.OK)
  }

  def assertTest(server: MockBuildServer, client: BloopClient): Unit = {
    client.reset()
    val params = new TestParams(getBuildTargetIds(server))
    val testResult = server.buildTargetTest(params).get()
    assert(testResult.getOriginId == params.getOriginId)
    // TODO in HappyMockServer: send some notifications in compile
    // assert(client.logMessages.nonEmpty)
    // assert(client.testReports.nonEmpty)
    assert(testResult.getStatusCode == StatusCode.OK)
  }

  def assertRun(server: MockBuildServer, client: BloopClient): Unit = {
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

    val languages = List("scala","java").sorted
    assert(compiles.sorted == languages)
    assert(runs.sorted == languages)
    assert(tests.sorted == languages)
  }

  def assertServerEndpoints(server: MockBuildServer, client: BloopClient): Unit = {
    assertWorkspaceBuildTargets(server)
    assertScalacOptions(server)
    assertDependencySources(server, client)
    assertCompile(server, client)
    assertTest(server, client)
    assertRun(server, client)
    // - buildTarget/mainClasses
    // - buildTarget/scalaMainClasses
  }

  test("end to end") {
    val testDirectory = Files.createTempDirectory("bsp.MockSuite")

    val client = new BloopClient
    val (server, cancel) = connectToBuildServer(client, testDirectory.toFile)
    try {
      val capabilities = new BuildClientCapabilities(Collections.singletonList("scala"))
      val initializeParams = new InitializeBuildParams("test-client", "1.0.0", "2.0.0-M1", testDirectory.toUri.toString, capabilities)
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