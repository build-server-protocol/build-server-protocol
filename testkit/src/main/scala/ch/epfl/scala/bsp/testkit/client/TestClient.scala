package ch.epfl.scala.bsp.testkit.client

import java.io.File
import java.util.concurrent.CompletableFuture

import ch.epfl.scala.bsp.testkit.client.mock.{MockCommunications, MockSession}
import ch.epfl.scala.bsp4j._

import scala.collection.convert.ImplicitConversions.`collection asJava`
import scala.collection.mutable
import scala.compat.java8.FutureConverters._
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

  private val unitTests: Map[TestClient.ClientUnitTest.Value, () => Unit] = Map(
    TestClient.ClientUnitTest.ResolveProjectTest -> resolveProject,
    TestClient.ClientUnitTest.TargetCapabilities -> targetCapabilities
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

  def testMultipleUnitTests(tests: List[TestClient.ClientUnitTest.Value]): Unit = {
    testSessionInitialization()
    tests.map(unitTests.get).foreach(test => test.map(_()))
    testShutdown()
  }

  def testIfSuccessful[T](value: CompletableFuture[T]): T = {
    val result = Await.ready(value.toScala, timeoutDuration).value.get
    assert(result.isSuccess, "Failed to compile targets that are compilable")
    result.get
  }

  def testIfFailure[T](value: CompletableFuture[T]): Unit = {
    val compileResult = Await.ready(value.toScala, timeoutDuration).value.get
    assert(compileResult.isFailure, "Compiled successfully supposedly uncompilable targets")
  }

  def resolveProject(): Unit = {
    val languages = new mutable.HashSet[String]()
    val targets = await(server.workspaceBuildTargets()).getTargets.asScala

    targets
      .map(target => {
        languages.addAll(target.getLanguageIds)
      })
      .toList

    val targetsId = targets.map(_.getId).asJava

    await(server.buildTargetSources(new SourcesParams(targetsId)))
    await(server.buildTargetDependencySources(new DependencySourcesParams(targetsId)))
    await(server.buildTargetResources(new ResourcesParams(targetsId)))
  }

  def testResolveProject(): Unit = wrapTest(resolveProject)

  def targetCapabilities(): Unit = {
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

}

object TestClient {
  trait ClientTest extends Enumeration

  object ClientUnitTest extends ClientTest {
    val ResolveProjectTest, TargetCapabilities = Value
  }

  def testInitialStructure(workspacePath: String, compilerOutputDir: String): TestClient = {
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
