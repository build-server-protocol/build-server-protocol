package ch.epfl.scala.bsp.testkit.client

import java.io.{File, InputStream, OutputStream}
import java.util.Optional
import java.util.concurrent.CompletableFuture

import ch.epfl.scala.bsp.testkit.client.mock.{MockCommunications, MockSession}
import ch.epfl.scala.bsp4j._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.compat.java8.FutureConverters._
import scala.compat.java8.OptionConverters._
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Try}

class TestClient(
    serverBuilder: () => (OutputStream, InputStream, () => Unit),
    initializeBuildParams: InitializeBuildParams,
    timeoutDuration: Duration = 30.seconds
) {

  private val alreadySentDiagnosticsTimeout = 2.seconds

  private def await[T](future: CompletableFuture[T]): T = {
    Await.result(future.toScala, timeoutDuration)
  }

  private def wrapTest(body: (TestClientConnection) => Unit): Unit = {
    val connection = new TestClientConnection(serverBuilder, initializeBuildParams)
    connection.testSessionInitialization()
    body(connection)
    connection.testShutdown()
  }

  def testMultipleUnitTests(tests: java.util.List[TestClient.ClientUnitTest.Value]): Unit = {
    val connection = new TestClientConnection(serverBuilder, initializeBuildParams)
    connection.testSessionInitialization()
    tests.asScala.map(connection.unitTests.get).foreach(test => test.foreach(_()))
    connection.testShutdown()
  }

  private def testIfSuccessful[T](value: CompletableFuture[T]): T = {
    val result = Await.ready(value.toScala, timeoutDuration).value.get
    assert(result.isSuccess, "Failed to compile targets that are compilable")
    result.get
  }

  private def testIfFailure[T](value: CompletableFuture[T]): Unit = {
    val compileResult = Await.ready(value.toScala, timeoutDuration).value.get
    assert(compileResult.isFailure, "Compiled successfully supposedly uncompilable targets")
  }

  def testResolveProject(): Unit = wrapTest(connection => connection.resolveProject())

  def testTargetCapabilities(): Unit = wrapTest(connection => connection.targetCapabilities())

  def testTargetsCompileUnsuccessfully(): Unit =
    wrapTest(connection => connection.targetsCompileUnsuccessfully())

  def testTargetsCompileSuccessfully(
      withTaskNotifications: Boolean = false,
      targets: Optional[java.util.List[BuildTarget]]
  ): Unit =
    wrapTest(connection => {
      targets.asScala match {
        case Some(targets) => connection.targetsCompileSuccessfully(targets.asScala)
        case None          => connection.targetsCompileSuccessfully()
      }
      if (withTaskNotifications) {
        val taskStartNotification = connection.client.pollTaskStart(alreadySentDiagnosticsTimeout)
        val taskEndNotification = connection.client.pollTaskFinish(alreadySentDiagnosticsTimeout)
        assert(taskStartNotification.isDefined, "No task start notification sent")
        assert(taskEndNotification.isDefined, "No task end notification sent")
      }
    })

  def testTargetsTestSuccessfully(targets: Optional[java.util.List[BuildTarget]]): Unit =
    wrapTest(
      connection =>
        targets.asScala match {
          case Some(targets) => connection.targetsTestSuccessfully(targets.asScala)
          case None          => connection.targetsTestSuccessfully()
        }
    )

  def testTargetsTestUnsuccessfully(): Unit =
    wrapTest(connection => connection.targetsTestUnsuccessfully())

  def testTargetsRunUnsuccessfully(): Unit =
    wrapTest(connection => connection.targetsRunUnsuccessfully())

  def testTargetsRunSuccessfully(targets: Optional[java.util.List[BuildTarget]]): Unit =
    wrapTest(
      connection =>
        targets.asScala match {
          case Some(targets) => connection.targetsRunSuccessfully(targets.asScala)
          case None          => connection.targetsRunSuccessfully()
        }
    )

  def testCompareWorkspaceTargetsResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult
  ): Unit =
    wrapTest(
      connection => connection.compareWorkspaceTargetsResults(expectedWorkspaceBuildTargetsResult)
    )

  def testDependencySourcesResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      expectedWorkspaceDependencySourcesResult: DependencySourcesResult
  ): Unit =
    wrapTest(
      connection =>
        connection.compareResults(
          targets =>
            connection.server.buildTargetDependencySources(new DependencySourcesParams(targets)),
          expectedWorkspaceDependencySourcesResult,
          expectedWorkspaceBuildTargetsResult
        )
    )

  def testResourcesResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      expectedResourcesResult: ResourcesResult
  ): Unit =
    wrapTest(
      connection =>
        connection.compareResults(
          targets => connection.server.buildTargetResources(new ResourcesParams(targets)),
          expectedResourcesResult,
          expectedWorkspaceBuildTargetsResult
        )
    )

  def testInverseSourcesResults(
      textDocument: TextDocumentIdentifier,
      expectedInverseSourcesResult: InverseSourcesResult
  ): Unit =
    wrapTest(connection => {
      val inverseSourcesResult =
        await(connection.server.buildTargetInverseSources(new InverseSourcesParams(textDocument)))
      assert(
        inverseSourcesResult == expectedInverseSourcesResult,
        s"Expected $expectedInverseSourcesResult, got $inverseSourcesResult"
      )
    })

  def testCleanCacheSuccessfully(): Unit =
    wrapTest(connection => connection.cleanCacheSuccessfully())
  def testCleanCacheUnsuccessfully(): Unit =
    wrapTest(connection => connection.cleanCacheUnsuccessfully())

  class TestClientConnection(
      serverBuilder: () => (OutputStream, InputStream, () => Unit),
      initializeBuildParams: InitializeBuildParams
  ) {
    private val (out, in, cleanup) = serverBuilder()
    private[client] val session = new MockSession(in, out, initializeBuildParams, cleanup)
    private[client] val server = session.connection.server
    private[client] val client = session.client

    private[client] val unitTests: Map[TestClient.ClientUnitTest.Value, () => Unit] = Map(
      TestClient.ClientUnitTest.ResolveProjectTest -> resolveProject,
      TestClient.ClientUnitTest.TargetCapabilities -> targetCapabilities,
      TestClient.ClientUnitTest.CompileSuccessfully -> compileSuccessfully,
      TestClient.ClientUnitTest.CompileUnsuccessfully -> targetsCompileUnsuccessfully,
      TestClient.ClientUnitTest.RunSuccessfully -> runSuccessfully,
      TestClient.ClientUnitTest.RunUnsuccessfully -> targetsRunUnsuccessfully,
      TestClient.ClientUnitTest.TestSuccessfully -> testSuccessfully,
      TestClient.ClientUnitTest.TestUnsuccessfully -> targetsTestUnsuccessfully,
      TestClient.ClientUnitTest.CleanCacheSuccessfully -> cleanCacheSuccessfully,
      TestClient.ClientUnitTest.CleanCacheUnsuccessfully -> cleanCacheUnsuccessfully
    )

    private[client] def testSessionInitialization(): Unit = {
      val initializeBuildResult: InitializeBuildResult = await(
        server.buildInitialize(session.initializeBuildParams)
      )

      val bspVersion = Try(initializeBuildResult.getBspVersion)
      assert(
        bspVersion.isSuccess,
        s"Bsp version must be a number, got ${initializeBuildResult.getBspVersion}"
      )
      server.onBuildInitialized()
    }

    private[client] def testShutdown(): Unit = {
      await(server.buildShutdown())
      val failedRequest =
        Await.ready(server.workspaceBuildTargets().toScala, timeoutDuration).value.get
      assert(failedRequest.isFailure, "Server is still accepting requests after shutdown")
      cleanup()
      session.cleanup()
    }

    private[client] def resolveProject(): Unit = {
      val targets = await(server.workspaceBuildTargets()).getTargets.asScala

      val targetsId = targets.map(_.getId).asJava

      await(server.buildTargetSources(new SourcesParams(targetsId)))
      await(server.buildTargetDependencySources(new DependencySourcesParams(targetsId)))
      await(server.buildTargetResources(new ResourcesParams(targetsId)))
    }

    private[client] def targetCapabilities(): Unit = {
      val targets = await(server.workspaceBuildTargets()).getTargets.asScala

      val (compilableTargets, uncompilableTargets) =
        targets.partition(_.getCapabilities.getCanCompile)
      val (runnableTargets, unrunnableTargets) = targets.partition(_.getCapabilities.getCanRun)
      val (testableTargets, untestableTargets) = targets.partition(_.getCapabilities.getCanTest)

      if (compilableTargets.nonEmpty)
        testIfSuccessful(
          server.buildTargetCompile(new CompileParams(compilableTargets.map(_.getId).asJava))
        )

      if (uncompilableTargets.nonEmpty)
        testIfFailure(
          server.buildTargetCompile(new CompileParams(uncompilableTargets.map(_.getId).asJava))
        )

      runnableTargets.foreach(
        target => testIfSuccessful(server.buildTargetRun(new RunParams(target.getId)))
      )
      unrunnableTargets.foreach(
        target => testIfFailure(server.buildTargetRun(new RunParams(target.getId)))
      )

      if (testableTargets.nonEmpty)
        testIfSuccessful(
          server.buildTargetTest(new TestParams(testableTargets.map(_.getId).asJava))
        )
      if (untestableTargets.nonEmpty)
        testIfFailure(server.buildTargetTest(new TestParams(untestableTargets.map(_.getId).asJava)))
    }

    private[client] def compileTarget(targets: mutable.Buffer[BuildTarget]) =
      testIfSuccessful(
        server.buildTargetCompile(
          new CompileParams(targets.filter(_.getCapabilities.getCanCompile).map(_.getId).asJava)
        )
      )

    private[client] def targetsCompileSuccessfully(
        targets: mutable.Buffer[BuildTarget] = await(server.workspaceBuildTargets()).getTargets.asScala,
        compileDiagnostics: List[ExpectedDiagnostic] = List.empty
    ): Unit = {
      val compileResult: CompileResult = compileTarget(targets)
      assert(compileResult.getStatusCode == StatusCode.OK, "Targets failed to compile!")
      compileDiagnostics.foreach(expectedDiagnostic => {
        val diagnostics = obtainExpectedDiagnostic(expectedDiagnostic)
        diagnostics.foreach(client.onBuildPublishDiagnostics)
      })
    }

    private[client] def compileSuccessfully(): Unit = targetsCompileSuccessfully()

    @tailrec
    final private[client] def obtainExpectedDiagnostic(
        expectedDiagnostic: ExpectedDiagnostic,
        list: List[PublishDiagnosticsParams] = List.empty
    ): List[PublishDiagnosticsParams] = {
      val polledDiagnostic = client.pollPublishDiagnostics(alreadySentDiagnosticsTimeout)
      assert(polledDiagnostic.isDefined, "Did not find expected diagnostic!")
      val diagnostics = polledDiagnostic.get
      val diagnostic = diagnostics.getDiagnostics.asScala.find(expectedDiagnostic.isEqual)
      diagnostic match {
        case None => obtainExpectedDiagnostic(expectedDiagnostic, diagnostics :: list)
        case Some(foundDiagnostic) =>
          diagnostics.getDiagnostics.remove(foundDiagnostic)
          diagnostics :: list
      }
    }

    @tailrec
    final private[client] def obtainExpectedNotification(
        expectedNotification: BuildTargetEventKind,
        list: List[DidChangeBuildTarget] = List.empty
    ): List[DidChangeBuildTarget] = {
      val polledNotification = client.pollBuildTargetDidChange(alreadySentDiagnosticsTimeout)
      assert(polledNotification.isDefined, "Did not find expected notification!")
      val notifications = polledNotification.get
      val diagnostic = notifications.getChanges.asScala.find(expectedNotification == _.getKind)
      diagnostic match {
        case None => obtainExpectedNotification(expectedNotification, notifications :: list)
        case Some(foundDiagnostic) =>
          notifications.getChanges.remove(foundDiagnostic)
          notifications :: list
      }
    }

    private[client] def targetsCompileUnsuccessfully(): Unit = {
      val compileResult: CompileResult = compileTarget(
        await(server.workspaceBuildTargets()).getTargets.asScala
      )
      assert(
        compileResult.getStatusCode != StatusCode.OK,
        "Targets compiled successfully when they should have failed!"
      )
    }

    private[client] def testTargets(targets: mutable.Buffer[BuildTarget]) =
      testIfSuccessful(
        server.buildTargetTest(
          new TestParams(targets.filter(_.getCapabilities.getCanCompile).map(_.getId).asJava)
        )
      )

    private[client] def targetsTestSuccessfully(
        targets: mutable.Buffer[BuildTarget] = await(server.workspaceBuildTargets()).getTargets.asScala
    ): Unit = {
      val testResult: TestResult = testTargets(targets)
      assert(testResult.getStatusCode == StatusCode.OK, "Tests to targets failed!")
    }

    private[client] def targetsTestUnsuccessfully(): Unit = {
      val testResult: TestResult = testTargets(
        await(server.workspaceBuildTargets()).getTargets.asScala
      )
      assert(testResult.getStatusCode != StatusCode.OK, "Tests pass when they should have failed!")
    }

    private[client] def testSuccessfully(): Unit =
      targetsTestSuccessfully()

    private[client] def targetsRunSuccessfully(
        targets: mutable.Buffer[BuildTarget] = await(server.workspaceBuildTargets()).getTargets.asScala
    ): Unit = {
      val runResults = runTargets(targets)

      runResults.foreach(
        runResult =>
          assert(runResult.getStatusCode == StatusCode.OK, "Target did not run successfully!")
      )
    }

    private[client] def targetsRunUnsuccessfully(): Unit = {
      val runResults = runTargets(await(server.workspaceBuildTargets()).getTargets.asScala)

      runResults.foreach(
        runResult =>
          assert(
            runResult.getStatusCode != StatusCode.OK,
            "Target ran successfully when it was supposed to fail!"
          )
      )
    }

    private[client] def runTargets(targets: mutable.Buffer[BuildTarget]) =
      targets
        .filter(_.getCapabilities.getCanCompile)
        .map(
          target =>
            testIfSuccessful(
              server.buildTargetRun(
                new RunParams(target.getId)
              )
            )
        )

    private[client] def runSuccessfully(): Unit = {
      targetsRunSuccessfully()
    }

    private[client] def compareWorkspaceTargetsResults(
        expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult
    ): WorkspaceBuildTargetsResult = {
      val workspaceBuildTargetsResult = await(server.workspaceBuildTargets())
      assert(
        workspaceBuildTargetsResult == expectedWorkspaceBuildTargetsResult,
        s"Workspace Build Targets did not match! Expected: $expectedWorkspaceBuildTargetsResult, got $workspaceBuildTargetsResult"
      )
      workspaceBuildTargetsResult
    }

    private[client] def compareResults[T](
        getResults: java.util.List[BuildTargetIdentifier] => CompletableFuture[T],
        expectedResults: T,
        expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult
    ): Unit = {
      val targets =
        compareWorkspaceTargetsResults(expectedWorkspaceBuildTargetsResult).getTargets.asScala
          .map(_.getId)
      val result = await(getResults(targets.asJava))
      assert(
        expectedResults == result,
        s"Expected $expectedResults, got $result"
      )
    }

    def testSourcesResults(
        expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
        expectedWorkspaceSourcesResult: SourcesResult
    ): Unit =
      compareResults(
        targets => server.buildTargetSources(new SourcesParams(targets)),
        expectedWorkspaceSourcesResult,
        expectedWorkspaceBuildTargetsResult
      )

    private[client] def cleanCacheSuccessfully(): Unit = {
      val cleanCacheResult: CleanCacheResult = cleanCache
      assert(cleanCacheResult.getCleaned, "Did not clean cache successfully")
    }

    private[client] def cleanCacheUnsuccessfully(): Unit = {
      val cleanCacheResult: CleanCacheResult = cleanCache
      assert(!cleanCacheResult.getCleaned, "Cleaned cache successfully, when it should have failed")
    }

    private def cleanCache = {
      val targets = await(server.workspaceBuildTargets()).getTargets.asScala.map(_.getId).asJava
      val cleanCacheResult = await(server.buildTargetCleanCache(new CleanCacheParams(targets)))
      cleanCacheResult
    }

    def testDidChangeNotification(buildTargetEventKind: BuildTargetEventKind): Unit =
      obtainExpectedNotification(buildTargetEventKind)
  }
}

object TestClient {
  object ClientUnitTest extends Enumeration {
    val ResolveProjectTest, TargetCapabilities, CompileSuccessfully, RunSuccessfully,
        TestSuccessfully, CompileUnsuccessfully, RunUnsuccessfully, TestUnsuccessfully,
        CleanCacheSuccessfully, CleanCacheUnsuccessfully = Value
  }

  def testInitialStructure(
      workspacePath: java.lang.String,
      compilerOutputDir: java.lang.String
  ): TestClient = {
    val workspace = new File(workspacePath)
    val compilerOutput = new File(workspace, compilerOutputDir)
    val (capabilities, connectionFiles) = MockCommunications.prepareSession(workspace)
    val failedConnections = connectionFiles.collect {
      case Failure(x) => x
    }
    assert(
      failedConnections.isEmpty,
      s"Found configuration files with errors: ${failedConnections.mkString("\n - ", "\n - ", "\n")}"
    )

    val client =
      MockCommunications.connect(workspace, compilerOutput, capabilities, connectionFiles.head.get)
    client
  }

  def apply(
      serverBuilder: () => (OutputStream, InputStream, () => Unit),
      initializeBuildParams: InitializeBuildParams
  ): TestClient = new TestClient(serverBuilder, initializeBuildParams)

  def apply(
      serverBuilder: () => (OutputStream, InputStream, () => Unit),
      initializeBuildParams: InitializeBuildParams,
      timeoutDuration: Duration
  ): TestClient = new TestClient(serverBuilder, initializeBuildParams, timeoutDuration)
}
