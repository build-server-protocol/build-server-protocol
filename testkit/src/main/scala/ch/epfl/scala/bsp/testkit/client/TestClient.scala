package ch.epfl.scala.bsp.testkit.client

import java.io.{File, InputStream, OutputStream}
import java.util.concurrent.{CompletableFuture, Executors}

import ch.epfl.scala.bsp.testkit.client.mock.{MockCommunications, MockSession}
import ch.epfl.scala.bsp4j._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.compat.java8.FutureConverters._
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Try}

class TestClient(
    serverBuilder: () => (OutputStream, InputStream, () => Unit),
    initializeBuildParams: InitializeBuildParams,
    timeoutDuration: Duration = 30.seconds
) {

  private val alreadySentDiagnosticsTimeout = 2.seconds

  private implicit val executionContext: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  private def await[T](future: CompletableFuture[T]): T = {
    Await.result(future.toScala, timeoutDuration)
  }

  def wrapTest(body: MockSession => Unit): Unit = {
    val (out, in, cleanup) = serverBuilder()
    val session: MockSession = new MockSession(in, out, initializeBuildParams, cleanup)
    testSessionInitialization(session)
    body(session)
    testShutdown(session, cleanup)
  }

  private def testIfSuccessful[T](value: CompletableFuture[T]): Future[T] = Future {
    val result = Await.ready(value.toScala, timeoutDuration).value.get
    assert(result.isSuccess, "Failed to compile targets that are compilable")
    result.get
  }

  private def testIfFailure[T](value: CompletableFuture[T]): Future[Unit] = Future {
    val compileResult = Await.ready(value.toScala, timeoutDuration).value.get
    assert(compileResult.isFailure, "Compiled successfully supposedly uncompilable targets")
  }

  def testInitializeAndShutdown(): Unit = wrapTest(_ => {})

  def testResolveProject(): Unit = wrapTest(resolveProject)

  def testTargetCapabilities(): Unit = wrapTest(targetCapabilities)

  def testTargetsCompileUnsuccessfully(): Unit =
    wrapTest(targetsCompileUnsuccessfully)

  def testTargetsCompileSuccessfully(
      targets: java.util.List[BuildTarget],
      withTaskNotifications: Boolean
  ): Unit =
    wrapTest(session => testTargetsCompileSuccessfully(session, withTaskNotifications, targets))

  def testTargetsCompileSuccessfully(
      withTaskNotifications: Boolean
  ): Unit =
    wrapTest(
      session =>
        testTargetsCompileSuccessfully(
          session,
          withTaskNotifications,
          getAllBuildTargets(session).asJava
        )
    )

  def testTargetsCompileSuccessfully(
      session: MockSession,
      withTaskNotifications: Boolean,
      targets: java.util.List[BuildTarget]
  ): Unit = {
    targetsCompileSuccessfully(targets, session, List.empty)

    if (withTaskNotifications) {
      val taskStartNotification = session.client.pollTaskStart(alreadySentDiagnosticsTimeout)
      val taskEndNotification = session.client.pollTaskFinish(alreadySentDiagnosticsTimeout)
      assert(taskStartNotification.isDefined, "No task start notification sent")
      assert(taskEndNotification.isDefined, "No task end notification sent")
    }
  }

  def testTargetsTestSuccessfully(targets: java.util.List[BuildTarget]): Unit = {
    wrapTest(
      session => targetsTestSuccessfully(targets.asScala, session)
    )
  }

  def testTargetsTestSuccessfully(): Unit = {
    wrapTest(
      session => targetsTestSuccessfully(getAllBuildTargets(session), session)
    )
  }

  def testTargetsTestUnsuccessfully(): Unit =
    wrapTest(targetsTestUnsuccessfully)

  def testTargetsRunUnsuccessfully(): Unit =
    wrapTest(targetsRunUnsuccessfully)

  def testTargetsRunSuccessfully(targets: java.util.List[BuildTarget]): Unit =
    wrapTest(
      session => targetsRunSuccessfully(targets.asScala, session)
    )

  def testTargetsRunSuccessfully(): Unit =
    wrapTest(
      session => targetsRunSuccessfully(getAllBuildTargets(session), session)
    )

  private def getAllBuildTargets(
      session: MockSession
  ) =
    await(session.connection.server.workspaceBuildTargets()).getTargets.asScala

  def testCompareWorkspaceTargetsResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult
  ): Unit =
    wrapTest(
      session => compareWorkspaceTargetsResults(expectedWorkspaceBuildTargetsResult, session)
    )

  def testDependencySourcesResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      expectedWorkspaceDependencySourcesResult: DependencySourcesResult
  ): Unit =
    wrapTest(
      session =>
        compareResults(
          targets =>
            session.connection.server
              .buildTargetDependencySources(new DependencySourcesParams(targets)),
          expectedWorkspaceDependencySourcesResult,
          expectedWorkspaceBuildTargetsResult,
          session
        )
    )

  def testResourcesResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      expectedResourcesResult: ResourcesResult
  ): Unit =
    wrapTest(
      session =>
        compareResults(
          targets => session.connection.server.buildTargetResources(new ResourcesParams(targets)),
          expectedResourcesResult,
          expectedWorkspaceBuildTargetsResult,
          session
        )
    )

  def testInverseSourcesResults(
      textDocument: TextDocumentIdentifier,
      expectedInverseSourcesResult: InverseSourcesResult
  ): Unit =
    wrapTest(session => {
      val inverseSourcesResult =
        await(
          session.connection.server
            .buildTargetInverseSources(new InverseSourcesParams(textDocument))
        )
      assert(
        inverseSourcesResult == expectedInverseSourcesResult,
        s"Expected $expectedInverseSourcesResult, got $inverseSourcesResult"
      )
    })

  def testCleanCacheSuccessfully(): Unit =
    wrapTest(cleanCacheSuccessfully)
  def testCleanCacheUnsuccessfully(): Unit =
    wrapTest(cleanCacheUnsuccessfully)

  def testSessionInitialization(session: MockSession): Unit = {
    val initializeBuildResult: InitializeBuildResult = await(
      session.connection.server.buildInitialize(session.initializeBuildParams)
    )

    val bspVersion = Try(initializeBuildResult.getBspVersion)
    assert(
      bspVersion.isSuccess,
      s"Bsp version must be a number, got ${initializeBuildResult.getBspVersion}"
    )
    session.connection.server.onBuildInitialized()
  }

  private def testShutdown(session: MockSession, cleanup: () => Unit): Unit = {
    await(session.connection.server.buildShutdown())
    val failedRequest =
      Await
        .ready(session.connection.server.workspaceBuildTargets().toScala, timeoutDuration)
        .value
        .get
    assert(failedRequest.isFailure, "Server is still accepting requests after shutdown")
    cleanup()
    session.cleanup()
  }

  def resolveProject(session: MockSession): Unit = {
    val targets = await(session.connection.server.workspaceBuildTargets()).getTargets.asScala

    val targetsId = targets.map(_.getId).asJava

    await(session.connection.server.buildTargetSources(new SourcesParams(targetsId)))
    await(
      session.connection.server.buildTargetDependencySources(new DependencySourcesParams(targetsId))
    )
    await(session.connection.server.buildTargetResources(new ResourcesParams(targetsId)))
  }

  def targetCapabilities(session: MockSession): Unit = {
    val targets = await(session.connection.server.workspaceBuildTargets()).getTargets.asScala

    val (compilableTargets, uncompilableTargets) =
      targets.partition(_.getCapabilities.getCanCompile)
    val (runnableTargets, unrunnableTargets) = targets.partition(_.getCapabilities.getCanRun)
    val (testableTargets, untestableTargets) = targets.partition(_.getCapabilities.getCanTest)

    val futures = ListBuffer[Future[Any]]()
    if (compilableTargets.nonEmpty)
      futures += testIfSuccessful(
        session.connection.server
          .buildTargetCompile(new CompileParams(compilableTargets.map(_.getId).asJava))
      )

    if (uncompilableTargets.nonEmpty)
      futures += testIfFailure(
        session.connection.server
          .buildTargetCompile(new CompileParams(uncompilableTargets.map(_.getId).asJava))
      )

    futures ++= runnableTargets.map(
      target =>
        testIfSuccessful(session.connection.server.buildTargetRun(new RunParams(target.getId)))
    )
    futures ++= unrunnableTargets.map(
      target => testIfFailure(session.connection.server.buildTargetRun(new RunParams(target.getId)))
    )

    if (testableTargets.nonEmpty)
      futures += testIfSuccessful(
        session.connection.server
          .buildTargetTest(new TestParams(testableTargets.map(_.getId).asJava))
      )
    if (untestableTargets.nonEmpty)
      futures += testIfFailure(
        session.connection.server
          .buildTargetTest(new TestParams(untestableTargets.map(_.getId).asJava))
      )

    Await.result(Future.sequence(futures), timeoutDuration.*(futures.size))
  }

  private def compileTarget(targets: mutable.Buffer[BuildTarget], session: MockSession) =
    testIfSuccessful(
      session.connection.server.buildTargetCompile(
        new CompileParams(targets.filter(_.getCapabilities.getCanCompile).map(_.getId).asJava)
      )
    )

  def targetsCompileSuccessfully(
      targets: java.util.List[BuildTarget],
      session: MockSession,
      compileDiagnostics: List[ExpectedDiagnostic]
  ): Unit = {
    val compileResult: CompileResult =
      Await.result(compileTarget(targets.asScala, session), timeoutDuration)
    assert(compileResult.getStatusCode == StatusCode.OK, "Targets failed to compile!")
    compileDiagnostics.foreach(expectedDiagnostic => {
      val diagnostics = obtainExpectedDiagnostic(expectedDiagnostic, session)
      diagnostics.foreach(session.client.onBuildPublishDiagnostics)
    })
  }

  def targetsCompileSuccessfully(
      session: MockSession,
      compileDiagnostics: List[ExpectedDiagnostic] = List.empty
  ): Unit =
    targetsCompileSuccessfully(getAllBuildTargets(session).asJava, session, compileDiagnostics)

  @tailrec
  final private def obtainExpectedDiagnostic(
      expectedDiagnostic: ExpectedDiagnostic,
      session: MockSession,
      list: List[PublishDiagnosticsParams] = List.empty
  ): List[PublishDiagnosticsParams] = {
    val polledDiagnostic = session.client.pollPublishDiagnostics(alreadySentDiagnosticsTimeout)
    assert(polledDiagnostic.isDefined, "Did not find expected diagnostic!")
    val diagnostics = polledDiagnostic.get
    val diagnostic = diagnostics.getDiagnostics.asScala.find(expectedDiagnostic.isEqual)
    diagnostic match {
      case None => obtainExpectedDiagnostic(expectedDiagnostic, session, diagnostics :: list)
      case Some(foundDiagnostic) =>
        diagnostics.getDiagnostics.remove(foundDiagnostic)
        diagnostics :: list
    }
  }

  @tailrec
  final private def obtainExpectedNotification(
      expectedNotification: BuildTargetEventKind,
      session: MockSession,
      list: List[DidChangeBuildTarget] = List.empty
  ): List[DidChangeBuildTarget] = {
    val polledNotification = session.client.pollBuildTargetDidChange(alreadySentDiagnosticsTimeout)
    assert(polledNotification.isDefined, "Did not find expected notification!")
    val notifications = polledNotification.get
    val diagnostic = notifications.getChanges.asScala.find(expectedNotification == _.getKind)
    diagnostic match {
      case None => obtainExpectedNotification(expectedNotification, session, notifications :: list)
      case Some(foundDiagnostic) =>
        notifications.getChanges.remove(foundDiagnostic)
        notifications :: list
    }
  }

  private def targetsCompileUnsuccessfully(session: MockSession): Unit = {
    val compileResult: CompileResult = Await.result(
      compileTarget(
        await(session.connection.server.workspaceBuildTargets()).getTargets.asScala,
        session
      ),
      timeoutDuration
    )
    assert(
      compileResult.getStatusCode != StatusCode.OK,
      "Targets compiled successfully when they should have failed!"
    )
  }

  private def testTargets(targets: mutable.Buffer[BuildTarget], session: MockSession) =
    testIfSuccessful(
      session.connection.server.buildTargetTest(
        new TestParams(targets.filter(_.getCapabilities.getCanCompile).map(_.getId).asJava)
      )
    )

  private def targetsTestSuccessfully(
      targets: mutable.Buffer[BuildTarget],
      session: MockSession
  ): Unit = {
    val testResult: TestResult =
      Await.result(testTargets(targets, session), timeoutDuration)
    assert(testResult.getStatusCode == StatusCode.OK, "Tests to targets failed!")
  }

  def targetsTestUnsuccessfully(session: MockSession): Unit = {
    val testResult: TestResult = Await.result(
      testTargets(
        await(session.connection.server.workspaceBuildTargets()).getTargets.asScala,
        session
      ),
      timeoutDuration
    )
    assert(testResult.getStatusCode != StatusCode.OK, "Tests pass when they should have failed!")
  }

  private def targetsRunSuccessfully(
      targets: mutable.Buffer[BuildTarget],
      session: MockSession
  ): Unit = {
    val runResults = runTargets(targets, session)

    runResults.foreach(
      runResult =>
        assert(runResult.getStatusCode == StatusCode.OK, "Target did not run successfully!")
    )
  }

  private def targetsRunUnsuccessfully(session: MockSession): Unit = {
    val runResults = runTargets(
      await(session.connection.server.workspaceBuildTargets()).getTargets.asScala,
      session
    )

    runResults.foreach(
      runResult =>
        assert(
          runResult.getStatusCode != StatusCode.OK,
          "Target ran successfully when it was supposed to fail!"
        )
    )
  }

  private def runTargets(targets: mutable.Buffer[BuildTarget], session: MockSession) =
    targets
      .filter(_.getCapabilities.getCanCompile)
      .map(
        target =>
          Await.result(
            testIfSuccessful(
              session.connection.server.buildTargetRun(
                new RunParams(target.getId)
              )
            ),
            timeoutDuration
          )
      )

  private def compareWorkspaceTargetsResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      session: MockSession
  ): WorkspaceBuildTargetsResult = {
    val workspaceBuildTargetsResult = await(session.connection.server.workspaceBuildTargets())
    assert(
      workspaceBuildTargetsResult == expectedWorkspaceBuildTargetsResult,
      s"Workspace Build Targets did not match! Expected: $expectedWorkspaceBuildTargetsResult, got $workspaceBuildTargetsResult"
    )
    workspaceBuildTargetsResult
  }

  private def compareResults[T](
      getResults: java.util.List[BuildTargetIdentifier] => CompletableFuture[T],
      expectedResults: T,
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      session: MockSession
  ): Unit = {
    val targets =
      compareWorkspaceTargetsResults(expectedWorkspaceBuildTargetsResult, session).getTargets.asScala
        .map(_.getId)
    val result = await(getResults(targets.asJava))
    assert(
      expectedResults == result,
      s"Expected $expectedResults, got $result"
    )
  }

  def testSourcesResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      expectedWorkspaceSourcesResult: SourcesResult,
      session: MockSession
  ): Unit = {
    compareResults(
      targets => session.connection.server.buildTargetSources(new SourcesParams(targets)),
      expectedWorkspaceSourcesResult,
      expectedWorkspaceBuildTargetsResult,
      session
    )
  }

  def cleanCacheSuccessfully(session: MockSession): Unit = {
    val cleanCacheResult: CleanCacheResult = cleanCache(session)
    assert(cleanCacheResult.getCleaned, "Did not clean cache successfully")
  }

  def cleanCacheUnsuccessfully(session: MockSession): Unit = {
    val cleanCacheResult: CleanCacheResult = cleanCache(session)
    assert(!cleanCacheResult.getCleaned, "Cleaned cache successfully, when it should have failed")
  }

  private def cleanCache(session: MockSession) = {
    val targets = await(session.connection.server.workspaceBuildTargets()).getTargets.asScala
      .map(_.getId)
      .asJava
    val cleanCacheResult = await(
      session.connection.server.buildTargetCleanCache(new CleanCacheParams(targets))
    )
    cleanCacheResult
  }

  def testDidChangeNotification(
      buildTargetEventKind: BuildTargetEventKind,
      session: MockSession
  ): Unit =
    obtainExpectedNotification(buildTargetEventKind, session)
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
