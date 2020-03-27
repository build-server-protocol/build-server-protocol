package ch.epfl.scala.bsp.testkit.mock

import java.io.File
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import java.util.UUID
import java.util.concurrent.TimeUnit

import ch.epfl.scala.bsp
import ch.epfl.scala.bsp._
import mockServers._
import io.circe.syntax._
import monix.eval.Task
import scribe.Logger

import scala.concurrent.Promise
import scala.concurrent.duration.FiniteDuration
import scala.meta.jsonrpc.LanguageClient

/** Mock server that gives a happy successful result to any request.
  */
class HappyMockServer(base: File, val logger: Logger, implicit val client: LanguageClient) extends AbstractMockServer {

  val isInitialized: Promise[Either[ProtocolError, Unit]] = scala.concurrent.Promise[Either[ProtocolError, Unit]]()
  val isShutdown: Promise[Either[ProtocolError, Unit]] = scala.concurrent.Promise[Either[ProtocolError, Unit]]()
  val isShutdownTask: Task[Either[ProtocolError, Unit]] = Task.fromFuture(isShutdown.future).memoize

  override def initialize(params: InitializeBuildParams): BspResponse[InitializeBuildResult] =
    Task {
      val result = bsp.InitializeBuildResult("BSP Mock Server", "1.0", "2.0", capabilities, None)
      Right(result)
    }

  override def initialized(params: InitializedBuildParams): Unit = {
    isInitialized.success(Right(()))
  }

  override def shutdown(shutdown: bsp.Shutdown): Unit = {
    isShutdown.success(Right())
    ()
  }

  override def exit(exit: Exit): Task[Unit] = {
    isShutdownTask
      .map(_ => ())
      .timeoutTo(
        FiniteDuration(1, TimeUnit.SECONDS),
        Task.now(())
      )
  }

  override def buildTargets(request: WorkspaceBuildTargetsRequest): BspResponse[WorkspaceBuildTargetsResult] = {

    val target1Capabilities = BuildTargetCapabilities(canCompile = true, canTest = false, canRun = false)
    val target2Capabilities = BuildTargetCapabilities(canCompile = true, canTest = true, canRun = false)
    val target3Capabilities = BuildTargetCapabilities(canCompile = true, canTest = false, canRun = true)

    val languageIds = List("scala")

    val javaHome = sys.props.get("java.home").map(p => Uri(Paths.get(p).toUri))
    val javaVersion = sys.props.get("java.vm.specification.version")
    val jvmBuildTarget = JvmBuildTarget(javaHome, javaVersion)
    val scalaJars = List("scala-compiler.jar", "scala-reflect.jar", "scala-library.jar").map(Uri.apply)
    val scalaBuildTarget = ScalaBuildTarget("org.scala-lang", "2.12.7", "2.12", ScalaPlatform.Jvm, scalaJars, jvmBuildTarget)
    val scalaData = Some(scalaBuildTarget.asJson)

    val targets = List(
      BuildTarget(target1, Some("target 1"), Some(target1.uri), List(BuildTargetTag.Library), target1Capabilities,
        languageIds, List.empty, Some(BuildTargetDataKind.Scala), scalaData),
      BuildTarget(target2, Some("target 2"), Some(target2.uri), List(BuildTargetTag.Test), target2Capabilities,
        languageIds, List(target1), Some(BuildTargetDataKind.Scala), scalaData),
      BuildTarget(target3, Some("target 3"), Some(target3.uri), List(BuildTargetTag.Application), target3Capabilities,
        languageIds, List(target1), Some(BuildTargetDataKind.Scala), scalaData)
    )

    val result = WorkspaceBuildTargetsResult(targets)

    Task(Right(result))
  }

  def sources(params: SourcesParams): BspResponse[SourcesResult] = {
    val sourceDir1 = target1.uri.toPath.resolve("src/")
    val item1 = SourceItem(asDirUri(sourceDir1), SourceItemKind.Directory, true)
    val items1 = SourcesItem(target1, List(item1), roots = None)

    val sourceDir2 = target2.uri.toPath.resolve("src-gen/")
    val item2 = SourceItem(asDirUri(sourceDir2), SourceItemKind.Directory, true)
    val items2 = SourcesItem(target2, List(item2), roots = None)

    val sourceDir3 = target3.uri.toPath.resolve("sauce/")
    val sourceFile1 = target3.uri.toPath.resolve("somewhere/sourcefile1")
    val sourceFile2 = target3.uri.toPath.resolve("somewhere/below/sourcefile2")
    val sourceFile3 = target3.uri.toPath.resolve("somewhere/sourcefile3")
    val item3Dir = SourceItem(asDirUri(sourceDir3), SourceItemKind.Directory, false)
    val item31 = SourceItem(Uri(sourceFile1.toUri), SourceItemKind.File, false)
    val item32 = SourceItem(Uri(sourceFile2.toUri), SourceItemKind.File, false)
    val item33 = SourceItem(Uri(sourceFile3.toUri), SourceItemKind.File, true)
    val items3 = SourcesItem(target3, List(item3Dir, item31, item32, item33), roots = None)

    val result = SourcesResult(List(items1, items2, items3))

    Task(Right(result))
  }

  override def dependencySources(params: DependencySourcesParams): BspResponse[DependencySourcesResult] = {

    val target1Sources = List("lib/Library.scala","lib/Helper.scala", "lib/some-library.jar").map(uriInTarget(target1,_))
    val target2Sources = List("lib/LibraryTest.scala","lib/HelperTest.scala", "lib/some-library.jar").map(uriInTarget(target2,_))
    val target3Sources = List("lib/App.scala", "lib/some-library.jar").map(uriInTarget(target3,_))
    val item1 = DependencySourcesItem(target1, target1Sources)
    val item2 = DependencySourcesItem(target2, target2Sources)
    val item3 = DependencySourcesItem(target3, target3Sources)
    val result = DependencySourcesResult(List(item1,item2,item3))

    Task(Right(result))
  }

  override def inverseSources(params: InverseSourcesParams): BspResponse[InverseSourcesResult] = {
    val result = InverseSourcesResult(List(target1, target2, target3))
    Task(Right(result))
  }

  override def scalacOptions(params: ScalacOptionsParams): BspResponse[ScalacOptionsResult] = {
    val classpath = List(Uri("scala-library.jar"))
    val item1 = ScalacOptionsItem(target1, Nil, classpath, uriInTarget(target1, "out"))
    val item2 = ScalacOptionsItem(target2, Nil, classpath, uriInTarget(target2, "out"))
    val item3 = ScalacOptionsItem(target3, Nil, classpath, uriInTarget(target3, "out"))
    val result = ScalacOptionsResult(List(item1, item2, item3))
    Task(Right(result))
  }

  override def compile(params: CompileParams): BspResponse[CompileResult] = {
    val origin = params.originId.map(List(_))
    val compile1Id = TaskId("compile1id", origin)

    compileStart(compile1Id, "compile started: " + target1.uri, target1)

    val subtaskParents = Some(List(compile1Id.id))
    logMessage("spawning subtasks", task = Some(compile1Id), origin = params.originId)
    val subtask1Id = TaskId("subtask1id", subtaskParents)
    val subtask2Id = TaskId("subtask2id", subtaskParents)
    val subtask3Id = TaskId("subtask3id", subtaskParents)
    taskStart(subtask1Id, "resolving widgets", None, None)
    taskStart(subtask2Id, "memoizing datapoints", None, None)
    taskStart(subtask3Id, "unionizing beams", None, None)

    val compileme = target1.uri.toPath.resolve("compileme.scala").toUri
    val doc = TextDocumentIdentifier(Uri(compileme))
    val errorMessage = Diagnostic(Range(Position(1, 10), Position(1,110)), Some(DiagnosticSeverity.Error), None, None, "this is a compile error", None)
    val warningMessage = Diagnostic(Range(Position(2, 10), Position(2,20)), Some(DiagnosticSeverity.Warning), None, None, "this is a compile warning", None)
    val infoMessage = Diagnostic(Range(Position(3, 1), Position(3,33)), Some(DiagnosticSeverity.Information), None, None, "this is a compile info", None)
    publishDiagnostics(doc, target1, List(errorMessage, warningMessage), params.originId)
    publishDiagnostics(doc, target1, List(infoMessage), params.originId)

    taskFinish(subtask1Id, "targets resolved", StatusCode.Ok, None, None)
    taskFinish(subtask2Id, "datapoints forgotten", StatusCode.Error, None, None)
    taskFinish(subtask3Id, "beams are classless", StatusCode.Cancelled, None, None)
    showMessage("subtasks done", task = Some(compile1Id), origin = params.originId)

    compileReport(compile1Id, "compile failed", target1, StatusCode.Error)

    params.targets.map { target =>
      val compileId = TaskId(UUID.randomUUID().toString, origin)
      compileStart(compileId, "compile started: " + target.uri, target)
      taskProgress(compileId, "compiling some files", 100, 23, None, None)
      compileReport(compileId, "compile complete", target, StatusCode.Ok)
    }

    val result = CompileResult(params.originId, StatusCode.Ok, None, None)
    Task(Right(result))
  }
  override def test(params: TestParams): BspResponse[TestResult] = {
    // TODO some test task/report notifications
    // TODO some individual test notifications
    val result = TestResult(params.originId, StatusCode.Ok, None, None)
    Task(Right(result))
  }
  override def run(params: RunParams): BspResponse[RunResult] = {
    // TODO some task notifications
    val result = RunResult(params.originId, StatusCode.Ok)
    Task(Right(result))
  }


  // for easy override of individual parts of responses

  def name = "BSP Mock Server"
  def serverVersion = "1.0"
  def bspVersion = "2.0"

  def supportedLanguages = List("java","scala")

  def capabilities = bsp.BuildServerCapabilities(
    compileProvider = Some(bsp.CompileProvider(supportedLanguages)),
    testProvider = Some(bsp.TestProvider(supportedLanguages)),
    runProvider = Some(bsp.RunProvider(supportedLanguages)),
    inverseSourcesProvider = Some(true),
    dependencySourcesProvider = Some(true),
    resourcesProvider = Some(true),
    buildTargetChangedProvider = Some(true),
    jvmTestEnvironmentProvider = Some(true)
  )

  val baseUri: URI = base.getCanonicalFile.toURI
  val target1 = BuildTargetIdentifier(Uri(baseUri.resolve("target1")))
  val target2 = BuildTargetIdentifier(Uri(baseUri.resolve("target2")))
  val target3 = BuildTargetIdentifier(Uri(baseUri.resolve("target3")))

  def uriInTarget(target: BuildTargetIdentifier, filePath: String): Uri =
    Uri(target1.uri.toPath.toUri.resolve(filePath))

  private def asDirUri(path: Path): Uri =
    Uri(path.toUri.toString + "/")

  override def jvmTestEnvironment(params: JvmTestEnvironmentParams): BspResponse[JvmTestEnvironmentResult] = {
    val classpath = List("scala-library.jar")
    val jvmOptions = List("-Xms256m")
    val environmentVariables = Map("A"->"a")
    val workdir = "/tmp"
    val item1 = JvmEnvironmentItem(
      target = target1,
      classpath = classpath,
      jvmOptions = jvmOptions,
      workingDirectory = workdir,
      environmentVariables = environmentVariables
    )
    val result = JvmTestEnvironmentResult(List(item1))
    Task(Right(result))
  }
}
