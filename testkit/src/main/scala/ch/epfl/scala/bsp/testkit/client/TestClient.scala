package ch.epfl.scala.bsp.testkit.client

import java.io.File
import java.util.Optional
import java.util.concurrent.CompletableFuture

import ch.epfl.scala.bsp.testkit.client.mock.{MockCommunications, MockSession}
import ch.epfl.scala.bsp4j._

import scala.annotation.tailrec
import scala.collection.convert.ImplicitConversions.`collection asJava`
import scala.collection.mutable
import scala.compat.java8.FutureConverters._
import scala.compat.java8.OptionConverters._
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Try}

case class TestClient(
    workspacePath: String,
    compilerOutputDir: String,
    session: MockSession,
    timeoutDuration: Duration = 30.seconds
) {
  private val server = session.connection.server
  private val client = session.client
  private val alreadySentDiagnosticsTimeout = 2.seconds

  private val unitTests: Map[TestClient.ClientUnitTest.Value, () => Unit] = Map(
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

  private def await[T](future: CompletableFuture[T]): T = {
    Await.result(future.toScala, timeoutDuration)
  }

  private def testSessionInitialization(): Unit = {
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

  private def testShutdown(): Unit = {
    await(server.buildShutdown())
    val failedRequest =
      Await.ready(server.workspaceBuildTargets().toScala, timeoutDuration).value.get
    assert(failedRequest.isFailure, "Server is still accepting requests after shutdown")
  }

  private def wrapTest(body: () => Unit): Unit = {
    testSessionInitialization()
    body()
    testShutdown()
  }

  def testMultipleUnitTests(tests: java.util.List[TestClient.ClientUnitTest.Value]): Unit = {
    testSessionInitialization()
    tests.asScala.map(unitTests.get).foreach(test => test.foreach(_()))
    testShutdown()
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

  private def resolveProject(): Unit = {
    val languages = new mutable.HashSet[String]()
    val targets = await(server.workspaceBuildTargets()).getTargets.asScala

    targets
      .foreach(target => {
        languages.addAll(target.getLanguageIds)
      })

    val targetsId = targets.map(_.getId).asJava

    await(server.buildTargetSources(new SourcesParams(targetsId)))
    await(server.buildTargetDependencySources(new DependencySourcesParams(targetsId)))
    await(server.buildTargetResources(new ResourcesParams(targetsId)))
  }

  def testResolveProject(): Unit = wrapTest(resolveProject)

  private def targetCapabilities(): Unit = {
    val targets = await(server.workspaceBuildTargets()).getTargets.asScala

    val (compilableTargets, uncompilableTargets) =
      targets.partition(_.getCapabilities.getCanCompile)
    val (runnableTargets, unrunnableTargets) = targets.partition(_.getCapabilities.getCanRun)
    val (testableTargets, untestableTargets) = targets.partition(_.getCapabilities.getCanTest)

    testIfSuccessful(
      server.buildTargetCompile(new CompileParams(compilableTargets.map(_.getId).asJava))
    )
    testIfFailure(
      server.buildTargetCompile(new CompileParams(uncompilableTargets.map(_.getId).asJava))
    )

    runnableTargets.foreach(
      target => testIfSuccessful(server.buildTargetRun(new RunParams(target.getId)))
    )
    unrunnableTargets.foreach(
      target => testIfFailure(server.buildTargetRun(new RunParams(target.getId)))
    )

    testIfSuccessful(server.buildTargetTest(new TestParams(testableTargets.map(_.getId).asJava)))
    testIfFailure(server.buildTargetTest(new TestParams(untestableTargets.map(_.getId).asJava)))
  }

  def testTargetCapabilities(): Unit = wrapTest(targetCapabilities)

  private def compileTarget(targets: mutable.Buffer[BuildTarget]) =
    testIfSuccessful(
      server.buildTargetCompile(
        new CompileParams(targets.filter(_.getCapabilities.getCanCompile).map(_.getId).asJava)
      )
    )

  private def targetsCompileSuccessfully(
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

  private def compileSuccessfully(): Unit = targetsCompileSuccessfully()

  @tailrec
  private def obtainExpectedDiagnostic(
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
  private def obtainExpectedNotification(
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

  private def targetsCompileUnsuccessfully(): Unit = {
    val compileResult: CompileResult = compileTarget(
      await(server.workspaceBuildTargets()).getTargets.asScala
    )
    assert(
      compileResult.getStatusCode != StatusCode.OK,
      "Targets compiled successfully when they should have failed!"
    )
  }

  def testTargetsCompileUnsuccessfully(): Unit = wrapTest(() => targetsCompileUnsuccessfully())

  def testTargetsCompileSuccessfully(
      withTaskNotifications: Boolean = false,
      targets: Optional[java.util.List[BuildTarget]]
  ): Unit =
    wrapTest(() => {
      targets.asScala match {
        case Some(targets) => targetsCompileSuccessfully(targets.asScala)
        case None          => targetsCompileSuccessfully()
      }
      if (withTaskNotifications) {
        val taskStartNotification = client.pollTaskStart(alreadySentDiagnosticsTimeout)
        val taskEndNotification = client.pollTaskFinish(alreadySentDiagnosticsTimeout)
        assert(taskStartNotification.isDefined, "No task start notification sent")
        assert(taskEndNotification.isDefined, "No task end notification sent")
      }
    })

  private def testTargets(targets: mutable.Buffer[BuildTarget]) =
    testIfSuccessful(
      server.buildTargetTest(
        new TestParams(targets.filter(_.getCapabilities.getCanCompile).map(_.getId).asJava)
      )
    )

  private def targetsTestSuccessfully(
      targets: mutable.Buffer[BuildTarget] = await(server.workspaceBuildTargets()).getTargets.asScala
  ): Unit = {
    val testResult: TestResult = testTargets(targets)
    assert(testResult.getStatusCode == StatusCode.OK, "Tests to targets failed!")
  }

  private def targetsTestUnsuccessfully(): Unit = {
    val testResult: TestResult = testTargets(
      await(server.workspaceBuildTargets()).getTargets.asScala
    )
    assert(testResult.getStatusCode != StatusCode.OK, "Tests pass when they should have failed!")
  }

  def testTargetsTestUnsuccessfully(): Unit =
    wrapTest(() => targetsTestUnsuccessfully())

  private def testSuccessfully(): Unit =
    targetsTestSuccessfully()

  def testTargetsTestSuccessfully(targets: Optional[java.util.List[BuildTarget]]): Unit =
    wrapTest(
      () =>
        targets.asScala match {
          case Some(targets) => targetsTestSuccessfully(targets.asScala)
          case None          => targetsTestSuccessfully()
        }
    )

  private def targetsRunSuccessfully(
      targets: mutable.Buffer[BuildTarget] = await(server.workspaceBuildTargets()).getTargets.asScala
  ): Unit = {
    val runResults = runTargets(targets)

    runResults.foreach(
      runResult =>
        assert(runResult.getStatusCode == StatusCode.OK, "Target did not run successfully!")
    )
  }

  private def targetsRunUnsuccessfully(): Unit = {
    val runResults = runTargets(await(server.workspaceBuildTargets()).getTargets.asScala)

    runResults.foreach(
      runResult =>
        assert(
          runResult.getStatusCode != StatusCode.OK,
          "Target ran successfully when it was supposed to fail!"
        )
    )
  }

  def testTargetsRunUnsuccessfully(): Unit = wrapTest(() => targetsRunUnsuccessfully())

  private def runTargets(targets: mutable.Buffer[BuildTarget]) =
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

  private def runSuccessfully(): Unit = {
    targetsRunSuccessfully()
  }

  def testTargetsRunSuccessfully(targets: Optional[java.util.List[BuildTarget]]): Unit =
    wrapTest(
      () =>
        targets.asScala match {
          case Some(targets) => targetsRunSuccessfully(targets.asScala)
          case None          => targetsRunSuccessfully()
        }
    )

  private def compareWorkspaceTargetsResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult
  ): WorkspaceBuildTargetsResult = {
    val workspaceBuildTargetsResult = await(server.workspaceBuildTargets())
    assert(
      workspaceBuildTargetsResult == expectedWorkspaceBuildTargetsResult,
      s"Workspace Build Targets did not match! Expected: $expectedWorkspaceBuildTargetsResult, got $workspaceBuildTargetsResult"
    )
    workspaceBuildTargetsResult
  }

  private def compareResults[T](
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

  def testCompareWorkspaceTargetsResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult
  ): Unit = wrapTest(() => compareWorkspaceTargetsResults(expectedWorkspaceBuildTargetsResult))

  def testSourcesResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      expectedWorkspaceSourcesResult: SourcesResult
  ): Unit =
    compareResults(
      targets => server.buildTargetSources(new SourcesParams(targets)),
      expectedWorkspaceSourcesResult,
      expectedWorkspaceBuildTargetsResult
    )

  def testDependencySourcesResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      expectedWorkspaceDependencySourcesResult: DependencySourcesResult
  ): Unit =
    wrapTest(
      () =>
        compareResults(
          targets => server.buildTargetDependencySources(new DependencySourcesParams(targets)),
          expectedWorkspaceDependencySourcesResult,
          expectedWorkspaceBuildTargetsResult
        )
    )

  def testResourcesResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      expectedResourcesResult: ResourcesResult
  ): Unit =
    wrapTest(
      () =>
        compareResults(
          targets => server.buildTargetResources(new ResourcesParams(targets)),
          expectedResourcesResult,
          expectedWorkspaceBuildTargetsResult
        )
    )

  def testInverseSourcesResults(
      textDocument: TextDocumentIdentifier,
      expectedInverseSourcesResult: InverseSourcesResult
  ): Unit =
    wrapTest(() => {
      val inverseSourcesResult =
        await(server.buildTargetInverseSources(new InverseSourcesParams(textDocument)))
      assert(
        inverseSourcesResult == expectedInverseSourcesResult,
        s"Expected $expectedInverseSourcesResult, got $inverseSourcesResult"
      )
    })

  private def cleanCacheSuccessfully(): Unit = {
    val cleanCacheResult: CleanCacheResult = cleanCache
    assert(cleanCacheResult.getCleaned, "Did not clean cache successfully")
  }

  def testCleanCacheSuccessfully(): Unit = wrapTest(() => cleanCacheSuccessfully())

  private def cleanCacheUnsuccessfully(): Unit = {
    val cleanCacheResult: CleanCacheResult = cleanCache
    assert(!cleanCacheResult.getCleaned, "Cleaned cache successfully, when it should have failed")
  }

  def testCleanCacheUnsuccessfully(): Unit = wrapTest(() => cleanCacheUnsuccessfully())

  private def cleanCache = {
    val targets = await(server.workspaceBuildTargets()).getTargets.asScala.map(_.getId).asJava
    val cleanCacheResult = await(server.buildTargetCleanCache(new CleanCacheParams(targets)))
    cleanCacheResult
  }

  def testDidChangeNotification(buildTargetEventKind: BuildTargetEventKind): Unit =
    obtainExpectedNotification(buildTargetEventKind)
}

object TestClient {
  object ClientUnitTest extends Enumeration {
    val ResolveProjectTest, TargetCapabilities, CompileSuccessfully, RunSuccessfully,
        TestSuccessfully, CompileUnsuccessfully, RunUnsuccessfully, TestUnsuccessfully,
        CleanCacheSuccessfully, CleanCacheUnsuccessfully = Value
  }

  def testInitialStructure(workspacePath: java.lang.String, compilerOutputDir: java.lang.String): TestClient = {
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

    val session =
      MockCommunications.connect(workspace, compilerOutput, capabilities, connectionFiles.head.get)
    TestClient(workspacePath, compilerOutputDir, session)
  }

}
