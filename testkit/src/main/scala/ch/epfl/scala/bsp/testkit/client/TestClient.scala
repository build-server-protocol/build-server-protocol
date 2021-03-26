package ch.epfl.scala.bsp.testkit.client

import ch.epfl.scala.bsp.testkit.client.mock.{MockCommunications, MockSession}
import ch.epfl.scala.bsp4j._
import com.google.gson.{Gson, JsonElement}

import java.io.{File, InputStream, OutputStream}
import java.util.concurrent.{CompletableFuture, Executors}
import scala.collection.convert.ImplicitConversions._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.compat.java8.DurationConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent._
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

class TestClient(
    serverBuilder: () => (OutputStream, InputStream, () => Unit),
    initializeBuildParams: InitializeBuildParams,
    timeoutDuration: Duration = 30.seconds
) {

  private val alreadySentDiagnosticsTimeout = 2.seconds

  private implicit val executionContext: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  private implicit val gson: Gson = new Gson()

  def wrapTest(body: MockSession => Future[Any]): Unit = {
    val (out, in, cleanup) = serverBuilder()
    val session: MockSession = new MockSession(in, out, initializeBuildParams, cleanup)

    val test = testSessionInitialization(session)
      .flatMap(_ => body(session))
      .flatMap(_ => testShutdown(session, cleanup))
    try {
      Await.result(test, timeoutDuration * 3)
    } catch {
      case _: TimeoutException =>
        throw new OutOfTimeException()
      case e: ExecutionException =>
        throw new TestFailedException(e.getCause)
      case e: Throwable =>
        throw new TestFailedException(e)
    }
  }

  private def testIfSuccessful[T](value: CompletableFuture[T]): Future[T] = {
    value.toScala.recover {
      case _ =>
        throw new RuntimeException("Failed to compile targets that are compilable")
    }
  }

  private def testIfFailure[T](value: CompletableFuture[T]): Future[Unit] = {
    value.toScala
      .transformWith {
        case Failure(_) =>
          Future.unit
        case Success(_) =>
          throw new RuntimeException("Compiled successfully supposedly uncompilable targets")

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
        getAllBuildTargets(session)
          .flatMap(targets => {
            testTargetsCompileSuccessfully(
              session,
              withTaskNotifications,
              targets.asJava
            )
          })
    )

  def testTargetsCompileSuccessfully(
      session: MockSession,
      withTaskNotifications: Boolean,
      targets: java.util.List[BuildTarget]
  ): Future[Unit] =
    targetsCompileSuccessfully(targets, session, List.empty).map(_ => {
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

        Future.sequence(List(taskStartNotification, taskEndNotification))
      }
    })

  def testTargetsTestSuccessfully(targets: java.util.List[BuildTarget]): Unit = {
    wrapTest(
      session => targetsTestSuccessfully(targets.asScala, session)
    )
  }

  def testTargetsTestSuccessfully(): Unit = {
    wrapTest(
      session =>
        getAllBuildTargets(session).flatMap(targets => {
          targetsTestSuccessfully(targets, session)
        })
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
      session =>
        getAllBuildTargets(session).flatMap(targets => {
          targetsRunSuccessfully(targets, session)
        })
    )

  private def getAllBuildTargets(
      session: MockSession
  ): Future[mutable.Buffer[BuildTarget]] =
    session.connection.server
      .workspaceBuildTargets()
      .toScala
      .map(targetsResult => targetsResult.getTargets.asScala)

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
          (results: DependencySourcesResult) =>
            expectedWorkspaceDependencySourcesResult.getItems.forall { item =>
              results.getItems.exists(
                resultItem =>
                  resultItem.getTarget == item.getTarget && item.getSources.forall { source =>
                    resultItem.getSources.exists(_.contains(source))
                  }
              )
            },
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
          (results: ResourcesResult) =>
            expectedResourcesResult.getItems.forall { item =>
              results.getItems.exists(
                resultItem =>
                  resultItem.getTarget == item.getTarget && item.getResources.forall { source =>
                    resultItem.getResources.exists(_.contains(source))
                  }
              )
            },
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
      .flatMap(_ => {
        session.connection.server.workspaceBuildTargets().toScala
      })
      .transformWith {
        case Success(_) =>
          session.connection.server.onBuildExit()
          session.connection.cancelable()
          throw new RuntimeException("Server is still accepting requests after shutdown")
        case Failure(_) =>
          session.connection.server.onBuildExit()
          session.connection.cancelable()
          Future.unit
      }
  }

  def resolveProject(session: MockSession): Future[Any] = {
    session.connection.server
      .workspaceBuildTargets()
      .toScala
      .flatMap(buildTargets => {
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
        Future.sequence(List(buildTargetSources, dependencySources, resources))
      })
  }

  def targetCapabilities(session: MockSession): Future[Any] = {
    session.connection.server
      .workspaceBuildTargets()
      .toScala
      .flatMap(buildTargets => {
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

        Future.sequence(futures)
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
  ): Future[List[PublishDiagnosticsParams]] = {
    compileTarget(targets.asScala, session).flatMap(result => {
      assert(result.getStatusCode == StatusCode.OK, "Targets failed to compile!")
      Future
        .sequence(
          compileDiagnostics.map(
            expectedDiagnostic => obtainExpectedDiagnostic(expectedDiagnostic, session)
          )
        )
    })
  }

  def targetsCompileSuccessfully(
      session: MockSession,
      compileDiagnostics: List[ExpectedDiagnostic] = List.empty
  ): Future[Any] =
    getAllBuildTargets(session).flatMap(targets => {
      targetsCompileSuccessfully(targets.asJava, session, compileDiagnostics)
    })

  final private def obtainExpectedDiagnostic(
      expectedDiagnostic: ExpectedDiagnostic,
      session: MockSession
  ): Future[PublishDiagnosticsParams] =
    session.client.poll(
      session.client.getPublishDiagnostics,
      alreadySentDiagnosticsTimeout,
      (diagnostics: PublishDiagnosticsParams) =>
        diagnostics.getDiagnostics.asScala.exists(expectedDiagnostic.isEqual)
    )

  final private def obtainExpectedNotification(
      expectedNotification: BuildTargetEventKind,
      session: MockSession
  ): Future[DidChangeBuildTarget] = {
    session.client.poll(
      session.client.getDidChangeBuildTarget,
      alreadySentDiagnosticsTimeout,
      (didChange: DidChangeBuildTarget) =>
        didChange.getChanges.asScala.exists(expectedNotification == _.getKind)
    )
  }

  private def targetsCompileUnsuccessfully(session: MockSession): Future[Unit] = {
    session.connection.server
      .workspaceBuildTargets()
      .toScala
      .flatMap { targets =>
        compileTarget(targets.getTargets.asScala, session)
      }
      .map { compileResult =>
        assert(
          compileResult.getStatusCode != StatusCode.OK,
          "Targets compiled successfully when they should have failed!"
        )
      }
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

  def targetsTestUnsuccessfully(session: MockSession): Future[Unit] =
    session.connection.server
      .workspaceBuildTargets()
      .toScala
      .flatMap { targets =>
        testTargets(targets.getTargets.asScala, session)
      }
      .map { testResult =>
        assert(
          testResult.getStatusCode != StatusCode.OK,
          "Tests pass when they should have failed!"
        )
      }

  private def targetsRunSuccessfully(
      targets: mutable.Buffer[BuildTarget],
      session: MockSession
  ): Future[Unit] =
    runTargets(targets, session).map(targetResults => {
      assert(
        targetResults.forall(_.getStatusCode == StatusCode.OK),
        "Target did not run successfully!"
      )
    })

  private def targetsRunUnsuccessfully(session: MockSession): Future[Unit] =
    session.connection.server
      .workspaceBuildTargets()
      .toScala
      .flatMap(targets => {
        runTargets(
          targets.getTargets.asScala,
          session
        )
      }.map(targetResults => {
        assert(targetResults.forall(_.getStatusCode != StatusCode.OK), "Targets were able to run!")
      }))

  private def runTargets(targets: mutable.Buffer[BuildTarget], session: MockSession) =
    Future.sequence(
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
    )

  private def extractJdkData(data: JsonElement)(implicit gson: Gson): Option[JvmBuildTarget] =
    Option(gson.fromJson[JvmBuildTarget](data, classOf[JvmBuildTarget]))

  private def extractScalaSdkData(data: JsonElement)(implicit gson: Gson): Option[ScalaBuildTarget] =
    Option(gson.fromJson[ScalaBuildTarget](data, classOf[ScalaBuildTarget]))

  private def extractSbtData(data: JsonElement)(implicit gson: Gson): Option[SbtBuildTarget] =
    Option(gson.fromJson[SbtBuildTarget](data, classOf[SbtBuildTarget]))

  def convertJsonObjectToData(workspaceBuildTargetsResult: WorkspaceBuildTargetsResult): WorkspaceBuildTargetsResult = {
    val targets = workspaceBuildTargetsResult.getTargets
    targets.forEach {
      target =>
        Option(target.getData)
          .map(_.asInstanceOf[JsonElement])
          .flatMap(data => target.getDataKind match {
            case BuildTargetDataKind.JVM =>
              extractJdkData(data)
            case BuildTargetDataKind.SCALA =>
              extractScalaSdkData(data)
            case BuildTargetDataKind.SBT =>
              extractSbtData(data)
          })
          .map(target.setData(_))
    }
    new WorkspaceBuildTargetsResult(targets)
  }

  private def compareWorkspaceTargetsResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      session: MockSession
  ): Future[WorkspaceBuildTargetsResult] =
    session.connection.server
      .workspaceBuildTargets()
      .toScala
      .map(workspaceBuildTargetsResult => convertJsonObjectToData(workspaceBuildTargetsResult))
      .map(workspaceBuildTargetsResult => {
        val testTargets =
          compareBuildTargets(expectedWorkspaceBuildTargetsResult, workspaceBuildTargetsResult)
        assert(
          testTargets,
          s"Workspace Build Targets did not match! Expected: $expectedWorkspaceBuildTargetsResult, got $workspaceBuildTargetsResult"
        )
        workspaceBuildTargetsResult
      })

  private def compareBuildTargetData(foundTarget: BuildTarget, target: BuildTarget): Boolean = {
    List(Option(target.getData), Option(foundTarget.getData)) match {
      case List(Some(targetData), Some(foundTargetData)) => targetData.getClass.isAssignableFrom(foundTargetData.getClass) &&
        compareBuildTargetData(foundTargetData, targetData)
      case List(None, None) => true
      case _ => false
    }

  }

  private def compareBuildTargetData(foundTargetData: AnyRef, targetData: AnyRef): Boolean =
    List(foundTargetData, targetData) match {
      case List(foundScalaTarget: ScalaBuildTarget, targetScalaTarget: ScalaBuildTarget) =>
        compareScalaBuildTargets(foundScalaTarget, targetScalaTarget)
      case List(foundJvmTarget: JvmBuildTarget, targetJvmTarget: JvmBuildTarget) =>
        targetJvmTarget == foundJvmTarget
      case List(foundSbtTarget: SbtBuildTarget, targetSbtTarget: SbtBuildTarget) =>
        compareSbtBuildTargets(foundSbtTarget, targetSbtTarget)
    }

  private def compareSbtBuildTargets(foundSbtTarget: SbtBuildTarget, targetSbtTarget: SbtBuildTarget) = {
    targetSbtTarget.getAutoImports == foundSbtTarget.getAutoImports &&
      targetSbtTarget.getChildren == foundSbtTarget.getChildren &&
      targetSbtTarget.getParent == foundSbtTarget.getParent &&
      targetSbtTarget.getSbtVersion == foundSbtTarget.getSbtVersion &&
      targetSbtTarget.getClasspath.forall {
        classpath => foundSbtTarget.getClasspath.exists(_.contains(classpath))
      } &&
      compareBuildTargetData(foundSbtTarget.getScalaBuildTarget, targetSbtTarget.getScalaBuildTarget)
  }

  private def compareScalaBuildTargets(foundScalaTarget: ScalaBuildTarget, targetScalaTarget: ScalaBuildTarget) = {
    targetScalaTarget.getJars.forall {
      targetJar => foundScalaTarget.getJars.exists(_.contains(targetJar))
    } &&
      targetScalaTarget.getPlatform == foundScalaTarget.getPlatform &&
      targetScalaTarget.getScalaBinaryVersion == foundScalaTarget.getScalaBinaryVersion &&
      targetScalaTarget.getScalaOrganization == foundScalaTarget.getScalaOrganization &&
      targetScalaTarget.getScalaVersion == foundScalaTarget.getScalaVersion &&
      compareBuildTargetData(foundScalaTarget.getJvmBuildTarget, targetScalaTarget.getJvmBuildTarget)
  }

  private def compareBuildTargets(
                                   expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
                                   workspaceBuildTargetsResult: WorkspaceBuildTargetsResult
                                 ) =
    expectedWorkspaceBuildTargetsResult.getTargets.forall { target =>
      workspaceBuildTargetsResult.getTargets.exists(
        foundTarget =>
          foundTarget.getId == target.getId && foundTarget.getLanguageIds == target.getLanguageIds && foundTarget.getDependencies == target.getDependencies
            && foundTarget.getCapabilities == target.getCapabilities && foundTarget.getDataKind == target.getDataKind && compareBuildTargetData(foundTarget, target)
      )
    }

  private def compareResults[T](
      getResults: java.util.List[BuildTargetIdentifier] => CompletableFuture[T],
      condition: T => Boolean,
      expectedResults: T,
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      session: MockSession
  ): Future[Unit] = {
    compareWorkspaceTargetsResults(expectedWorkspaceBuildTargetsResult, session)
      .flatMap(buildTargets => {
        val targets = buildTargets.getTargets.asScala
          .map(_.getId)
        getResults(targets.asJava).toScala
      })
      .map(result => {
        assert(
          condition(result),
          s"Expected $expectedResults, got $result"
        )
      })
  }

  def testSourcesResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      expectedWorkspaceSourcesResult: SourcesResult
  ): Unit =
    wrapTest(
      session =>
        testSourcesResults(
          expectedWorkspaceBuildTargetsResult,
          expectedWorkspaceSourcesResult,
          session
        )
    )

  def testSourcesResults(
      expectedWorkspaceBuildTargetsResult: WorkspaceBuildTargetsResult,
      expectedWorkspaceSourcesResult: SourcesResult,
      session: MockSession
  ): Future[Unit] = {
    compareResults(
      targets => session.connection.server.buildTargetSources(new SourcesParams(targets)),
      (results: SourcesResult) =>
        expectedWorkspaceSourcesResult.getItems.forall { sourceItem =>
          {
            results.getItems.exists(
              resultItem =>
                resultItem.getTarget == sourceItem.getTarget && sourceItem.getSources.forall(
                  sourceFile =>
                    resultItem.getSources.exists(
                      resultSource =>
                        resultSource.getUri
                          .contains(sourceFile.getUri) && resultSource.getKind == sourceFile.getKind && resultSource.getGenerated == sourceFile.getGenerated
                    )
                )
            )
          }
        },
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

  private def cleanCache(session: MockSession): Future[CleanCacheResult] = {
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
      })
  }

  def testDidChangeNotification(
      buildTargetEventKind: BuildTargetEventKind,
      session: MockSession
  ): Future[DidChangeBuildTarget] =
    obtainExpectedNotification(buildTargetEventKind, session)

  def testJvmRunEnvironment(
      params: JvmRunEnvironmentParams,
      expectedResult: JvmRunEnvironmentResult,
      session: MockSession
  ): Future[Unit] = {
    session.connection.server
      .jvmRunEnvironment(params)
      .toScala
      .map(result => result.getItems)
      .map(jvmItems => {
        val testItems = testJvmItems(jvmItems, expectedResult.getItems)
        assert(
          testItems,
          s"JVM Run Environment Items did not match! Expected: $expectedResult, got $jvmItems"
        )
      })
  }

  def testJvmRunEnvironment(
      params: JvmRunEnvironmentParams,
      expectedResult: JvmRunEnvironmentResult
  ): Unit =
    wrapTest(session => testJvmRunEnvironment(params, expectedResult, session))

  def testJvmTestEnvironment(
      params: JvmTestEnvironmentParams,
      expectedResult: JvmTestEnvironmentResult,
      session: MockSession
  ): Future[Unit] = {
    session.connection.server
      .jvmTestEnvironment(params)
      .toScala
      .map(result => result.getItems)
      .map(jvmItems => {
        val testItems = testJvmItems(jvmItems, expectedResult.getItems)
        assert(
          testItems,
          s"JVM Test Environment Items did not match! Expected: $expectedResult, got $jvmItems"
        )
      })
  }

  def testJvmTestEnvironment(
      params: JvmTestEnvironmentParams,
      expectedResult: JvmTestEnvironmentResult
  ): Unit =
    wrapTest(session => testJvmTestEnvironment(params, expectedResult, session))

  private def testJvmItems(
      items: java.util.List[JvmEnvironmentItem],
      expectedItems: java.util.List[JvmEnvironmentItem]
  ) = {
    items.forall { item =>
      expectedItems.exists(
        expectedItem =>
          testExpectedEnvVars(expectedItem, item) &&
            item.getTarget == expectedItem.getTarget &&
            testExpectedClasspath(expectedItem.getClasspath, item.getClasspath) &&
            item.getJvmOptions == expectedItem.getJvmOptions
      )
    }
  }

  private def testExpectedEnvVars(expectedItem: JvmEnvironmentItem, item: JvmEnvironmentItem) = {
    expectedItem.getEnvironmentVariables
      .entrySet()
      .forall(
        entry =>
          item.getEnvironmentVariables.containsKey(entry.getKey) && item.getEnvironmentVariables
            .get(entry.getKey) == entry.getValue
      )
  }

  private def testExpectedClasspath(
      expectedClasspath: java.util.List[String],
      classpath: java.util.List[String]
  ) = {
    expectedClasspath.forall(expectedUrl => classpath.exists(url => url.contains(expectedUrl)))
  }

  def testJavacOptions(
      params: JavacOptionsParams,
      expectedResult: JavacOptionsResult,
      session: MockSession
  ): Future[Unit] = {
    session.connection.server
      .buildTargetJavacOptions(params)
      .toScala
      .map(result => result.getItems)
      .map(jvmItems => {
        val itemsTest = jvmItems.forall { item =>
          expectedResult.getItems.exists(
            expectedItem =>
              testExpectedClasspath(expectedItem.getClasspath, item.getClasspath) &&
                item.getClassDirectory.contains(expectedItem.getClassDirectory) &&
                item.getTarget == expectedItem.getTarget &&
                item.getOptions == expectedItem.getOptions

          )
        }
        assert(
          itemsTest,
          s"Javac Options Items did not match! Expected: $expectedResult, got $jvmItems"
        )
      })
  }

  def testJavacOptions(
      params: JavacOptionsParams,
      expectedResult: JavacOptionsResult
  ): Unit =
    wrapTest(session => testJavacOptions(params, expectedResult, session))

  def testScalacOptions(
      params: ScalacOptionsParams,
      expectedResult: ScalacOptionsResult,
      session: MockSession
  ): Future[Unit] = {
    session.connection.server
      .buildTargetScalacOptions(params)
      .toScala
      .map(result => result.getItems)
      .map(scalacItems => {
        val itemsTest = scalacItems.forall { item =>
          expectedResult.getItems.exists(
            expectedItem =>
              testExpectedClasspath(expectedItem.getClasspath, item.getClasspath) &&
                item.getClassDirectory.contains(expectedItem.getClassDirectory) &&
                item.getTarget == expectedItem.getTarget &&
                item.getOptions == expectedItem.getOptions
          )
        }
        assert(
          itemsTest,
          s"Scalac Environment Items did not match! Expected: $expectedResult, got $scalacItems"
        )
      })
  }

  def testScalacOptions(
      params: ScalacOptionsParams,
      expectedResult: ScalacOptionsResult
  ): Unit =
    wrapTest(session => testScalacOptions(params, expectedResult, session))

  def testScalaMainClasses(
      params: ScalaMainClassesParams,
      expectedResult: ScalaMainClassesResult,
      session: MockSession
  ): Future[Unit] = {
    session.connection.server
      .buildTargetScalaMainClasses(params)
      .toScala
      .map(result => result.getItems)
      .map(mainItems => {
        val itemsTest = mainItems.forall { item =>
          expectedResult.getItems.contains(item)
        } && expectedResult.getItems.size() == mainItems.size()
        assert(
          itemsTest,
          s"Scalac Main CLasses Items did not match! Expected: $expectedResult, got $mainItems"
        )
      })
  }

  def testScalaMainClasses(
      params: ScalaMainClassesParams,
      expectedResult: ScalaMainClassesResult
  ): Unit =
    wrapTest(session => testScalaMainClasses(params, expectedResult, session))

  def testScalaTestClasses(
      params: ScalaTestClassesParams,
      expectedResult: ScalaTestClassesResult,
      session: MockSession
  ): Future[Unit] = {
    session.connection.server
      .buildTargetScalaTestClasses(params)
      .toScala
      .map(result => result.getItems)
      .map(testItems => {
        val itemsTest = testItems.forall { item =>
          expectedResult.getItems.contains(item)
        } && expectedResult.getItems.size() == testItems.size()
        assert(
          itemsTest,
          s"Scalac Test CLasses Items did not match! Expected: $expectedResult, got $testItems"
        )
      })
  }

  def testScalaTestClasses(
      params: ScalaTestClassesParams,
      expectedResult: ScalaTestClassesResult
  ): Unit =
    wrapTest(session => testScalaTestClasses(params, expectedResult, session))

  def testWorkspaceReload(session: MockSession): Future[AnyRef] = session.connection.server
    .workspaceReload()
    .toScala

  def testWorkspaceReload(): Unit =
    wrapTest(testWorkspaceReload)
}

class TestFailedException(e: Throwable) extends Throwable(e) {
  override def printStackTrace(): Unit = {
    println("Test case failed!")
    e.printStackTrace()
  }
}

class OutOfTimeException extends Throwable {
  override def printStackTrace(): Unit = {
    println("Test failed to complete in time!")
    super.printStackTrace()
  }
}

object TestClient {

  def testInitialStructure(
      workspacePath: java.lang.String,
      customProperties: java.util.Map[String, String],
      timeoutDuration: java.time.Duration
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
        customProperties.toMap,
        timeoutDuration
      )
    client
  }

  def testInitialStructure(
      workspacePath: java.lang.String,
      customProperties: java.util.Map[String, String]
  ): TestClient = testInitialStructure(workspacePath, customProperties, 30.seconds.toJava)

  def apply(
      serverBuilder: () => (OutputStream, InputStream, () => Unit),
      initializeBuildParams: InitializeBuildParams
  ): TestClient = new TestClient(serverBuilder, initializeBuildParams)

  def apply(
      serverBuilder: () => (OutputStream, InputStream, () => Unit),
      initializeBuildParams: InitializeBuildParams,
      timeoutDuration: java.time.Duration
  ): TestClient = new TestClient(serverBuilder, initializeBuildParams, timeoutDuration.toScala)
}
