package ch.epfl.scala.bsp.testkit.client

import java.io.{File, InputStream, OutputStream}
import java.util.concurrent.{CompletableFuture, Executors}

import ch.epfl.scala.bsp.testkit.client.mock.{MockCommunications, MockSession}
import ch.epfl.scala.bsp4j._

import scala.collection.convert.ImplicitConversions._
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

  private def await[T](future: Future[T]): T = {
    Await.result(future, timeoutDuration)
  }

  def wrapTest(body: MockSession => Future[Unit]): Unit = {
    val (out, in, cleanup) = serverBuilder()
    val session: MockSession = new MockSession(in, out, initializeBuildParams, cleanup)

    await(testSessionInitialization(session))
    await(body(session))
    await(testShutdown(session, cleanup))
  }

  private def testIfSuccessful[T](value: CompletableFuture[T]): Future[T] = {
    value.toScala.recover {
      case _ =>
        throw new RuntimeException("Failed to compile targets that are compilable")
    }
  }

  private def testIfFailure[T](value: CompletableFuture[T]): Future[Unit] = {
    value.toScala
      .map(_ => {
        throw new RuntimeException("Compiled successfully supposedly uncompilable targets")
      })
      .recover {
        case _ =>
      }
  }

  def testInitializeAndShutdown(): Unit = wrapTest(_ => Future.unit)

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
  ): Future[Unit] = Future {
    targetsCompileSuccessfully(targets, session, List.empty)

    if (withTaskNotifications) {
      val taskStartNotification = session.client.poll(
        session.client.getTaskStart,
        alreadySentDiagnosticsTimeout,
        (_: TaskStartParams) => true
      )
      val taskEndNotification = session.client.poll(
        session.client.getTaskStart,
        alreadySentDiagnosticsTimeout,
        (_: TaskStartParams) => true
      )

      val taskStart = Await.ready(taskStartNotification, alreadySentDiagnosticsTimeout).value.get
      val taskEnd = Await.ready(taskEndNotification, alreadySentDiagnosticsTimeout).value.get
      assert(taskStart.isSuccess, "No task start notification sent")
      assert(taskEnd.isSuccess, "No task end notification sent")
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
    await(session.connection.server.workspaceBuildTargets().toScala).getTargets.asScala

  def testCompareWorkspaceTargetsResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult
  ): Unit =
    wrapTest(
      session => {
        compareWorkspaceTargetsResults(expectedWorkspaceBuildTargetsResult, session)
        Future.successful()
      }
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
      session.connection.server
        .buildTargetInverseSources(new InverseSourcesParams(textDocument))
        .toScala
        .map(inverseSourcesResult => {
          assert(
            inverseSourcesResult == expectedInverseSourcesResult,
            s"Expected $expectedInverseSourcesResult, got $inverseSourcesResult"
          )
        })
    })

  def testCleanCacheSuccessfully(): Unit =
    wrapTest(cleanCacheSuccessfully)
  def testCleanCacheUnsuccessfully(): Unit =
    wrapTest(cleanCacheUnsuccessfully)

  def testSessionInitialization(session: MockSession): Future[Unit] = {
    session.connection.server
      .buildInitialize(session.initializeBuildParams)
      .toScala
      .map(initializeBuildResult => {
        val bspVersion = Try(initializeBuildResult.getBspVersion)
        assert(
          bspVersion.isSuccess,
          s"Was not able to obtain BSP version"
        )
        session.connection.server.onBuildInitialized()
      })

  }

  private def testShutdown(session: MockSession, cleanup: () => Unit): Future[Unit] = {
    session.connection.server
      .buildShutdown()
      .toScala
      .map((_) => {
        val failedRequest =
          Await
            .ready(session.connection.server.workspaceBuildTargets().toScala, timeoutDuration)
            .value
            .get
        assert(failedRequest.isFailure, "Server is still accepting requests after shutdown")
        cleanup()
        session.cleanup()
      })
  }

  def resolveProject(session: MockSession): Future[Unit] = {
    session.connection.server
      .workspaceBuildTargets()
      .toScala
      .map(buildTargets => {
        val targets = buildTargets.getTargets.asScala
        val targetsId = targets.map(_.getId).asJava
        val buildTargetSources =
          session.connection.server.buildTargetSources(new SourcesParams(targetsId)).toScala
        val dependencySources =
          session.connection.server
            .buildTargetDependencySources(new DependencySourcesParams(targetsId))
            .toScala
        val resources =
          session.connection.server.buildTargetResources(new ResourcesParams(targetsId)).toScala
        Await.result(
          Future.sequence(List(buildTargetSources, dependencySources, resources)),
          timeoutDuration * 3
        )
      })
  }

  def targetCapabilities(session: MockSession): Future[Unit] = {
    session.connection.server
      .workspaceBuildTargets()
      .toScala
      .map(buildTargets => {
        val targets = buildTargets.getTargets.asScala
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
          target =>
            testIfFailure(session.connection.server.buildTargetRun(new RunParams(target.getId)))
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
      })
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
    compileDiagnostics.foreach(
      expectedDiagnostic => obtainExpectedDiagnostic(expectedDiagnostic, session)
    )
  }

  def targetsCompileSuccessfully(
      session: MockSession,
      compileDiagnostics: List[ExpectedDiagnostic] = List.empty
  ): Unit =
    targetsCompileSuccessfully(getAllBuildTargets(session).asJava, session, compileDiagnostics)

  final private def obtainExpectedDiagnostic(
      expectedDiagnostic: ExpectedDiagnostic,
      session: MockSession
  ): Unit = {
    val polledDiagnostic = session.client.poll(
      session.client.getPublishDiagnostics,
      alreadySentDiagnosticsTimeout,
      (diagnostics: PublishDiagnosticsParams) =>
        diagnostics.getDiagnostics.asScala.exists(expectedDiagnostic.isEqual)
    )

    val diagnostic = Await.ready(polledDiagnostic, alreadySentDiagnosticsTimeout).value.get
    assert(diagnostic.isSuccess, "Did not find expected diagnostic!")
  }

  final private def obtainExpectedNotification(
      expectedNotification: BuildTargetEventKind,
      session: MockSession
  ): Unit = {
    val polledNotification = session.client.poll(
      session.client.getDidChangeBuildTarget,
      alreadySentDiagnosticsTimeout,
      (didChange: DidChangeBuildTarget) =>
        didChange.getChanges.asScala.exists(expectedNotification == _.getKind)
    )

    val notification = Await.ready(polledNotification, alreadySentDiagnosticsTimeout).value.get
    assert(notification.isSuccess, "Did not find expected notification!")

  }

  private def targetsCompileUnsuccessfully(session: MockSession): Future[Unit] = {
    session.connection.server
      .workspaceBuildTargets()
      .toScala
      .map(targets => {
        compileTarget(targets.getTargets.asScala, session).map(compileResult => {

          assert(
            compileResult.getStatusCode != StatusCode.OK,
            "Targets compiled successfully when they should have failed!"
          )
        })
      })
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
  ): Future[Unit] = {
    testTargets(targets, session).map(testResult => {
      assert(testResult.getStatusCode == StatusCode.OK, "Tests to targets failed!")
    })
  }

  def targetsTestUnsuccessfully(session: MockSession): Future[Unit] = {
    session.connection.server
      .workspaceBuildTargets()
      .toScala
      .map(targets => {
        testTargets(targets.getTargets.asScala, session).map(testResult => {
          assert(
            testResult.getStatusCode != StatusCode.OK,
            "Tests pass when they should have failed!"
          )
        })
      })
  }

  private def targetsRunSuccessfully(
      targets: mutable.Buffer[BuildTarget],
      session: MockSession
  ): Future[Unit] = {
    val runResultsFuture = Future.sequence(runTargets(targets, session))

    runResultsFuture.map(
      runResults =>
        runResults.foreach(runResult => {
          assert(runResult.getStatusCode == StatusCode.OK, "Target did not run successfully!")
        })
    )
  }

  private def targetsRunUnsuccessfully(session: MockSession): Future[Unit] = {
    session.connection.server
      .workspaceBuildTargets()
      .toScala
      .map(targets => {
        val runResultsFuture = runTargets(
          targets.getTargets.asScala,
          session
        )
        runResultsFuture.map(
          runResults =>
            runResults.map(runResult => {
              assert(
                runResult.getStatusCode != StatusCode.OK,
                "Target ran successfully when it was supposed to fail!"
              )

            })
        )
      })

  }

  private def runTargets(targets: mutable.Buffer[BuildTarget], session: MockSession) =
    targets
      .filter(_.getCapabilities.getCanCompile)
      .map(
        target =>
          testIfSuccessful(
            session.connection.server.buildTargetRun(
              new RunParams(target.getId)
            )
          )
      )

  private def compareWorkspaceTargetsResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      session: MockSession
  ): Future[WorkspaceBuildTargetsResult] = {
    session.connection.server
      .workspaceBuildTargets()
      .toScala
      .map(workspaceBuildTargetsResult => {

        assert(
          workspaceBuildTargetsResult == expectedWorkspaceBuildTargetsResult,
          s"Workspace Build Targets did not match! Expected: $expectedWorkspaceBuildTargetsResult, got $workspaceBuildTargetsResult"
        )
        workspaceBuildTargetsResult
      })
  }

  private def compareResults[T](
      getResults: java.util.List[BuildTargetIdentifier] => CompletableFuture[T],
      expectedResults: T,
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      session: MockSession
  ): Future[Unit] = {
    compareWorkspaceTargetsResults(expectedWorkspaceBuildTargetsResult, session).flatMap(
      buildTargets => {
        val targets = buildTargets.getTargets.asScala
          .map(_.getId)
        getResults(targets.asJava).toScala.map(result => {
          assert(
            expectedResults == result,
            s"Expected $expectedResults, got $result"
          )
        })
      }
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

  def cleanCacheSuccessfully(session: MockSession): Future[Unit] = {
    cleanCache(session).map(cleanCacheResult => {
      assert(cleanCacheResult.getCleaned, "Did not clean cache successfully")
    })
  }

  def cleanCacheUnsuccessfully(session: MockSession): Future[Unit] = {
    cleanCache(session).map(cleanCacheResult => {
      assert(!cleanCacheResult.getCleaned, "Cleaned cache successfully, when it should have failed")
    })
  }

  private def cleanCache(session: MockSession) = {
    session.connection.server
      .workspaceBuildTargets()
      .toScala
      .map(buildTargets => {
        buildTargets.getTargets.asScala
          .map(_.getId)
          .asJava
      })
      .flatMap(targets => {
        session.connection.server
          .buildTargetCleanCache(new CleanCacheParams(targets))
          .toScala
          .map(cleanCacheResult => cleanCacheResult)
      })
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
      customProperties: java.util.Map[String, String]
  ): TestClient = {
    val workspace = new File(workspacePath)
    val (capabilities, connectionFiles) = MockCommunications.prepareSession(workspace)
    val failedConnections = connectionFiles.collect {
      case Failure(x) => x
    }
    assert(
      failedConnections.isEmpty,
      s"Found configuration files with errors: ${failedConnections.mkString("\n - ", "\n - ", "\n")}"
    )

    assert(
      connectionFiles.nonEmpty,
      "No configuration files found"
    )

    val client =
      MockCommunications.connect(
        workspace,
        capabilities,
        connectionFiles.head.get,
        customProperties.toMap
      )
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
