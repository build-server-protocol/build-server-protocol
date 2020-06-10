package ch.epfl.scala.bsp.testkit.mock

import java.io.File
import java.net.URI
import java.nio.file.Paths
import java.util
import java.util.UUID
import java.util.concurrent.CompletableFuture

import ch.epfl.scala.bsp4j._

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.jdk.CollectionConverters._

import HappyMockServer.ProtocolError

object HappyMockServer {
  case object ProtocolError
}

/** Mock server that gives a happy successful result to any request. */
class HappyMockServer(base: File) extends AbstractMockServer {

  override var client: BuildClient = _

  val isInitialized: Promise[Either[ProtocolError.type, Unit]] = scala.concurrent.Promise[Either[ProtocolError.type, Unit]]()
  val isShutdown: Promise[Either[ProtocolError.type, Unit]] = scala.concurrent.Promise[Either[ProtocolError.type, Unit]]()

  // for easy override of individual parts of responses
  def name = "BSP Mock Server"
  def serverVersion = "1.0"
  def bspVersion = "2.0"

  def supportedLanguages: util.List[String] = List("java","scala").asJava

  def capabilities: BuildServerCapabilities = {
    val c = new BuildServerCapabilities()
    c.setCompileProvider(new CompileProvider(supportedLanguages))
    c.setTestProvider(new TestProvider(supportedLanguages))
    c.setRunProvider(new RunProvider(supportedLanguages))
    c.setInverseSourcesProvider(true)
    c.setDependencySourcesProvider(true)
    c.setResourcesProvider(true)
    c.setBuildTargetChangedProvider(true)
    c.setJvmRunEnvironmentProvider(true)
    c.setJvmTestEnvironmentProvider(true)
    c
  }

  val baseUri: URI = base.getCanonicalFile.toURI
  val target1 = new BuildTargetIdentifier(baseUri.resolve("target1").toString)
  val target2 = new BuildTargetIdentifier(baseUri.resolve("target2").toString)
  val target3 = new BuildTargetIdentifier(baseUri.resolve("target3").toString)

  def uriInTarget(target: BuildTargetIdentifier, filePath: String): URI =
    new URI(target1.getUri).resolve(filePath)

  private def asDirUri(path: URI): String =
    path.toString + "/"

  private def completeFuture[T](t: T): CompletableFuture[T] = {
    val ret = new CompletableFuture[T]()
    ret.complete(t)
    ret
  }

  private def environmentItem(testing: Boolean) = {
    val classpath = List("scala-library.jar").asJava
    val jvmOptions = List("-Xms256m").asJava
    val environmentVariables = Map("A" -> "a", "TESTING" -> testing.toString).asJava
    val workdir = "/tmp"
    val item1 = new JvmEnvironmentItem(
      target1, classpath, jvmOptions, workdir, environmentVariables
    )
    item1
  }
  override def jvmRunEnvironment(params: JvmRunEnvironmentParams): CompletableFuture[JvmRunEnvironmentResult] = {
    val item1: JvmEnvironmentItem = environmentItem(testing = false)
    val result = new JvmRunEnvironmentResult(List(item1).asJava)
    completeFuture(result)
  }

  override def jvmTestEnvironment(params: JvmTestEnvironmentParams): CompletableFuture[JvmTestEnvironmentResult] = {
    val item1: JvmEnvironmentItem = environmentItem(testing = true)
    val result = new JvmTestEnvironmentResult(List(item1).asJava)
    completeFuture(result)
  }

  override def buildTargetScalacOptions(params: ScalacOptionsParams): CompletableFuture[ScalacOptionsResult] = {
    val options = List.empty[String].asJava
    val classpath = List("scala-library.jar").asJava
    val item1 = new ScalacOptionsItem(target1, options, classpath, uriInTarget(target1, "out").toString)
    val item2 = new ScalacOptionsItem(target2, options, classpath, uriInTarget(target2, "out").toString)
    val item3 = new ScalacOptionsItem(target3, options, classpath, uriInTarget(target3, "out").toString)
    val result = new ScalacOptionsResult(List(item1, item2, item3).asJava)
    completeFuture(result)
  }

  override def buildTargetScalaTestClasses(params: ScalaTestClassesParams): CompletableFuture[ScalaTestClassesResult] = {
    // TODO return some test classes
    val result = new ScalaTestClassesResult(List.empty.asJava)
    completeFuture(result)
  }

  override def buildTargetScalaMainClasses(params: ScalaMainClassesParams): CompletableFuture[ScalaMainClassesResult] = {
    // TODO return a main class
    val result = new ScalaMainClassesResult(List.empty.asJava)
    completeFuture(result)
  }


  override def buildInitialize(params: InitializeBuildParams): CompletableFuture[InitializeBuildResult] = {
    val result = new InitializeBuildResult("BSP Mock Server", "1.0", "2.0", capabilities)
    completeFuture(result)
  }

  override def onBuildInitialized(): Unit = {
    isInitialized.success(Right(()))
  }

  override def buildShutdown(): CompletableFuture[AnyRef] = {
    isShutdown.success(Right())
    completeFuture("boo")
  }

  override def onBuildExit(): Unit = {
    Await.ready(isShutdown.future, 1.seconds)
  }

  override def workspaceBuildTargets(): CompletableFuture[WorkspaceBuildTargetsResult] = {

    val target1Capabilities = new BuildTargetCapabilities(true, false, false)
    val target2Capabilities = new BuildTargetCapabilities(true, true,  false)
    val target3Capabilities = new BuildTargetCapabilities(true, false, true)

    val languageIds = List("scala").asJava

    val javaHome = sys.props.get("java.home").map(p => Paths.get(p).toUri.toString)
    val javaVersion = sys.props.get("java.vm.specification.version")
    val jvmBuildTarget = new JvmBuildTarget(javaHome.get, javaVersion.get)
    val scalaJars = List("scala-compiler.jar", "scala-reflect.jar", "scala-library.jar").asJava
    val scalaBuildTarget = new ScalaBuildTarget("org.scala-lang", "2.12.7", "2.12", ScalaPlatform.JVM, scalaJars)
    scalaBuildTarget.setJvmBuildTarget(jvmBuildTarget)

    val t1 = new BuildTarget(target1, List(BuildTargetTag.LIBRARY).asJava, languageIds, List.empty.asJava, target1Capabilities)
    t1.setDisplayName("target 1")
    t1.setBaseDirectory(target1.getUri)
    t1.setDataKind(BuildTargetDataKind.SCALA)
    t1.setData(scalaBuildTarget)

    val t2 = new BuildTarget(target2, List(BuildTargetTag.TEST).asJava, languageIds, List(target1).asJava, target2Capabilities)
    t2.setDisplayName("target 2")
    t2.setBaseDirectory(target2.getUri)
    t2.setDataKind(BuildTargetDataKind.SCALA)
    t2.setData(scalaBuildTarget)

    val t3 = new BuildTarget(target3, List(BuildTargetTag.APPLICATION).asJava, languageIds, List(target1).asJava, target3Capabilities)
    t3.setDisplayName("target 3")
    t3.setBaseDirectory(target3.getUri)
    t3.setDataKind(BuildTargetDataKind.SCALA)
    t3.setData(scalaBuildTarget)

    val targets = List(t1,t2,t3).asJava

    val result = new WorkspaceBuildTargetsResult(targets)
    completeFuture(result)
  }

  override def buildTargetSources(params: SourcesParams): CompletableFuture[SourcesResult] = {
    val sourceDir1 = new URI(target1.getUri).resolve("src/")
    val item1 = new SourceItem(asDirUri(sourceDir1), SourceItemKind.DIRECTORY, true)
    val items1 = new SourcesItem(target1, List(item1).asJava)

    val sourceDir2 = new URI(target2.getUri).resolve("src-gen/")
    val item2 = new SourceItem(asDirUri(sourceDir2), SourceItemKind.DIRECTORY, true)
    val items2 = new SourcesItem(target2, List(item2).asJava)

    val sourceDir3 = new URI(target3.getUri).resolve("sauce/")
    val sourceFile1 = new URI(target3.getUri).resolve("somewhere/sourcefile1")
    val sourceFile2 = new URI(target3.getUri).resolve("somewhere/below/sourcefile2")
    val sourceFile3 = new URI(target3.getUri).resolve("somewhere/sourcefile3")
    val item3Dir = new SourceItem(asDirUri(sourceDir3), SourceItemKind.DIRECTORY, false)
    val item31 = new SourceItem(sourceFile1.toString, SourceItemKind.FILE, false)
    val item32 = new SourceItem(sourceFile2.toString, SourceItemKind.FILE, false)
    val item33 = new SourceItem(sourceFile3.toString, SourceItemKind.FILE, true)
    val items3 = new SourcesItem(target3, List(item3Dir, item31, item32, item33).asJava)

    val result = new SourcesResult(List(items1, items2, items3).asJava)
    completeFuture(result)
  }

  override def buildTargetInverseSources(params: InverseSourcesParams): CompletableFuture[InverseSourcesResult] = {
    val result = new InverseSourcesResult(List(target1, target2, target3).asJava)
    completeFuture(result)
  }

  override def buildTargetDependencySources(params: DependencySourcesParams): CompletableFuture[DependencySourcesResult] = {
    val target1Sources = List("lib/Library.scala","lib/Helper.scala", "lib/some-library.jar")
      .map(uriInTarget(target1,_).toString).asJava
    val target2Sources = List("lib/LibraryTest.scala","lib/HelperTest.scala", "lib/some-library.jar")
      .map(uriInTarget(target2,_).toString).asJava
    val target3Sources = List("lib/App.scala", "lib/some-library.jar")
      .map(uriInTarget(target3,_).toString).asJava

    val item1 = new DependencySourcesItem(target1, target1Sources)
    val item2 = new DependencySourcesItem(target2, target2Sources)
    val item3 = new DependencySourcesItem(target3, target3Sources)
    val result = new DependencySourcesResult(List(item1,item2,item3).asJava)

    completeFuture(result)
  }

  override def buildTargetResources(params: ResourcesParams): CompletableFuture[ResourcesResult] = {
    // TODO provide resources
    val result = new ResourcesResult(List.empty.asJava)
    completeFuture(result)
  }

  override def buildTargetCompile(params: CompileParams): CompletableFuture[CompileResult] = {
    val origin = List(params.getOriginId).asJava
    val compile1Id = new TaskId("compile1id")
    compile1Id.setParents(origin)

    compileStart(compile1Id, "compile started: " + target1.getUri, target1)

    val subtaskParents = List(compile1Id.getId).asJava
    logMessage("spawning subtasks", task = Some(compile1Id), origin = Some(params.getOriginId))
    val subtask1Id = new TaskId("subtask1id")
    subtask1Id.setParents(subtaskParents)
    val subtask2Id = new TaskId("subtask2id")
    subtask2Id.setParents(subtaskParents)
    val subtask3Id = new TaskId("subtask3id")
    subtask3Id.setParents(subtaskParents)
    taskStart(subtask1Id, "resolving widgets", None, None)
    taskStart(subtask2Id, "memoizing datapoints", None, None)
    taskStart(subtask3Id, "unionizing beams", None, None)

    val compileme = new URI(target1.getUri).resolve("compileme.scala")
    val doc = new TextDocumentIdentifier(compileme.toString)

    val errorMessage = new Diagnostic(new Range(new Position(1, 10), new Position(1,110)), "this is a compile error")
    errorMessage.setSeverity(DiagnosticSeverity.ERROR)

    val warningMessage = new Diagnostic(new Range(new Position(2, 10), new Position(2,20)), "this is a compile warning")
    warningMessage.setSeverity(DiagnosticSeverity.WARNING)

    val infoMessage = new Diagnostic(new Range(new Position(3, 1), new Position(3,33)), "this is a compile info")
    infoMessage.setSeverity(DiagnosticSeverity.INFORMATION)

    publishDiagnostics(doc, target1, List(errorMessage, warningMessage), Option(params.getOriginId))
    publishDiagnostics(doc, target1, List(infoMessage), Option(params.getOriginId))

    taskFinish(subtask1Id, "targets resolved", StatusCode.OK, None, None)
    taskFinish(subtask2Id, "datapoints forgotten", StatusCode.ERROR, None, None)
    taskFinish(subtask3Id, "beams are classless", StatusCode.CANCELLED, None, None)
    showMessage("subtasks done", task = Some(compile1Id), origin = Option(params.getOriginId))

    compileReport(compile1Id, "compile failed", target1, StatusCode.ERROR)

    params.getTargets.asScala.foreach { target =>
      val compileId = new TaskId(UUID.randomUUID().toString)
      compileId.setParents(origin)
      compileStart(compileId, "compile started: " + target.getUri, target)
      taskProgress(compileId, "compiling some files", 100, 23, None, None)
      compileReport(compileId, "compile complete", target, StatusCode.OK)
    }

    val result = new CompileResult(StatusCode.OK)
    result.setOriginId(params.getOriginId)
    completeFuture(result)
  }

  override def buildTargetTest(params: TestParams): CompletableFuture[TestResult] = {
    // TODO some test task/report notifications
    // TODO some individual test notifications
    val result = new TestResult(StatusCode.OK)
    result.setOriginId(params.getOriginId)
    completeFuture(result)
  }

  override def buildTargetRun(params: RunParams): CompletableFuture[RunResult] = {
    // TODO some task notifications
    val result = new RunResult(StatusCode.OK)
    result.setOriginId(params.getOriginId)
    completeFuture(result)
  }

  override def buildTargetCleanCache(params: CleanCacheParams): CompletableFuture[CleanCacheResult] = {
    val result = new CleanCacheResult("cleaned cache", true)
    completeFuture(result)
  }
}
