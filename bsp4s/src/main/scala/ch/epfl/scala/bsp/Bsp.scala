package ch.epfl.scala.bsp

import java.net.{URI, URISyntaxException}

import ch.epfl.scala.bsp.BuildTargetEventKind.{Changed, Created, Deleted}
import io.circe.Decoder.Result
import io.circe.derivation.JsonCodec
import io.circe._

final case class Uri private[Uri] (val value: String) {
  def toPath: java.nio.file.Path =
    java.nio.file.Paths.get(new java.net.URI(value))
}

object Uri {
  // This is the only valid way to create a URI
  def apply(u: URI): Uri = Uri(u.toString)

  implicit val uriEncoder: RootEncoder[Uri] = new RootEncoder[Uri] {
    override def apply(a: Uri): Json = Json.fromString(a.value)
  }

  implicit val uriDecoder: Decoder[Uri] = new Decoder[Uri] {
    // Should we add validation here
    override def apply(c: HCursor): Result[Uri] = {
      c.as[String].flatMap { str =>
        try Right(Uri(URI.create(str).toString))
        catch {
          case _: IllegalArgumentException | _: URISyntaxException =>
            Left(DecodingFailure(s"String $str is not a valid URI.", c.history))
        }
      }
    }
  }
}

@JsonCodec final case class TextDocumentIdentifier(
    uri: Uri
)

@JsonCodec final case class BuildTargetIdentifier(
    uri: Uri
)

@JsonCodec final case class BuildTargetCapabilities(
    canCompile: Boolean,
    canTest: Boolean,
    canRun: Boolean,
)

object BuildTargetTag {
  val Library = "library"
  val Test = "test"
  val Application = "application"
  val IntegrationTest = "integration-test"
  val Benchmark = "benchmark"
  val NoIDE = "no-ide"
}

@JsonCodec final case class BuildTarget(
    id: BuildTargetIdentifier,
    displayName: Option[String],
    baseDirectory: Option[Uri],
    tags: List[String],
    capabilities: BuildTargetCapabilities,
    languageIds: List[String],
    dependencies: List[BuildTargetIdentifier],
    dataKind: Option[String],
    data: Option[Json]
)

object BuildTargetDataKind {
  val Scala = "scala"
  val Sbt = "sbt"
}

@JsonCodec final case class BuildClientCapabilities(
    languageIds: List[String]
)

// Notification: 'build/initialized', C -> S
@JsonCodec final case class InitializedBuildParams()

// Request: 'build/initialize', C -> S
@JsonCodec final case class InitializeBuildParams(
    displayName: String,
    version: String,
    bspVersion: String,
    rootUri: Uri,
    capabilities: BuildClientCapabilities,
    data: Option[Json]
)

@JsonCodec final case class Shutdown()

@JsonCodec final case class Exit()

@JsonCodec final case class CompileProvider(
    languageIds: List[String]
)

@JsonCodec final case class TestProvider(
    languageIds: List[String]
)

@JsonCodec final case class RunProvider(
    languageIds: List[String]
)

@JsonCodec final case class BuildServerCapabilities(
    compileProvider: Option[CompileProvider],
    testProvider: Option[TestProvider],
    runProvider: Option[RunProvider],
    inverseSourcesProvider: Option[Boolean],
    dependencySourcesProvider: Option[Boolean],
    resourcesProvider: Option[Boolean],
    buildTargetChangedProvider: Option[Boolean],
    jvmTestEnvironmentProvider: Option[Boolean],
    jvmRunEnvironmentProvider: Option[Boolean]
)

@JsonCodec final case class InitializeBuildResult(
    displayName: String,
    version: String,
    bspVersion: String,
    capabilities: BuildServerCapabilities,
    data: Option[Json]
)

sealed abstract class MessageType(val id: Int)
object MessageType {
  case object Error extends MessageType(1)
  case object Warning extends MessageType(2)
  case object Info extends MessageType(3)
  case object Log extends MessageType(4)

  implicit val messageTypeEncoder: RootEncoder[MessageType] = new RootEncoder[MessageType] {
    override def apply(a: MessageType): Json = Json.fromInt(a.id)
  }

  implicit val messageTypeDecoder: Decoder[MessageType] = new Decoder[MessageType] {
    override def apply(c: HCursor): Result[MessageType] = {
      c.as[Int].flatMap {
        case 1 => Right(Error)
        case 2 => Right(Warning)
        case 3 => Right(Info)
        case 4 => Right(Log)
        case n => Left(DecodingFailure(s"Unknown message type id for $n", c.history))
      }
    }
  }
}

@JsonCodec final case class TaskId(
    id: String,
    parents: Option[List[String]]
)

@JsonCodec final case class ShowMessageParams(
    `type`: MessageType,
    task: Option[TaskId],
    originId: Option[String],
    message: String
)

@JsonCodec final case class LogMessageParams(
    `type`: MessageType,
    task: Option[TaskId],
    originId: Option[String],
    message: String
)

@JsonCodec final case class Position(
    line: Int,
    character: Int
)

@JsonCodec final case class Range(
    start: Position,
    end: Position
)

@JsonCodec case class Location(
    uri: Uri,
    range: Range
)

sealed abstract class DiagnosticSeverity(val id: Int)
object DiagnosticSeverity {
  case object Error extends DiagnosticSeverity(1)
  case object Warning extends DiagnosticSeverity(2)
  case object Information extends DiagnosticSeverity(3)
  case object Hint extends DiagnosticSeverity(4)

  implicit val diagnosticSeverityEncoder: RootEncoder[DiagnosticSeverity] = {
    new RootEncoder[DiagnosticSeverity] {
      override def apply(a: DiagnosticSeverity): Json = Json.fromInt(a.id)
    }
  }

  implicit val diagnosticSeverityDecoder: Decoder[DiagnosticSeverity] = {
    new Decoder[DiagnosticSeverity] {
      override def apply(c: HCursor): Result[DiagnosticSeverity] = {
        c.as[Int].flatMap {
          case 1 => Right(Error)
          case 2 => Right(Warning)
          case 3 => Right(Information)
          case 4 => Right(Hint)
          case n => Left(DecodingFailure(s"Unknown diagnostic severity id for $n", c.history))
        }
      }
    }
  }
}

@JsonCodec final case class DiagnosticRelatedInformation(
    location: Location,
    message: String
)

@JsonCodec final case class Diagnostic(
    range: Range,
    severity: Option[DiagnosticSeverity],
    code: Option[String],
    source: Option[String],
    message: String,
    relatedInformation: Option[DiagnosticRelatedInformation]
)

@JsonCodec final case class PublishDiagnosticsParams(
    textDocument: TextDocumentIdentifier,
    buildTarget: BuildTargetIdentifier,
    originId: Option[String],
    diagnostics: List[Diagnostic],
    reset: Boolean
)

@JsonCodec final case class WorkspaceBuildTargetsRequest()

// Request: 'workspace/buildTargets'
@JsonCodec final case class WorkspaceBuildTargetsResult(
    targets: List[BuildTarget]
)

sealed abstract class BuildTargetEventKind(val id: Int)
case object BuildTargetEventKind {
  case object Created extends BuildTargetEventKind(1)
  case object Changed extends BuildTargetEventKind(2)
  case object Deleted extends BuildTargetEventKind(3)

  implicit val buildTargetEventKindEncoder: RootEncoder[BuildTargetEventKind] =
    new RootEncoder[BuildTargetEventKind] {
      override def apply(a: BuildTargetEventKind): Json = Json.fromInt(a.id)
    }

  implicit val buildTargetEventKindDecoder: Decoder[BuildTargetEventKind] =
    new Decoder[BuildTargetEventKind] {
      override def apply(c: HCursor): Result[BuildTargetEventKind] = {
        c.as[Int].flatMap {
          case 1 => Right(Created)
          case 2 => Right(Changed)
          case 3 => Right(Deleted)
          case n => Left(DecodingFailure(s"Unknown build target event kind id for $n", c.history))
        }
      }
    }
}

@JsonCodec final case class BuildTargetEvent(
    target: BuildTargetIdentifier,
    kind: Option[BuildTargetEventKind],
    data: Option[Json]
)

// Notification: 'buildTarget/didChange', S -> C
@JsonCodec final case class DidChangeBuildTarget(
    changes: List[BuildTargetEvent]
)

// Request: 'buildTarget/sources', C -> S
@JsonCodec final case class SourcesParams(
    targets: List[BuildTargetIdentifier]
)

@JsonCodec final case class SourcesResult(
    items: List[SourcesItem]
)
@JsonCodec final case class SourcesItem(
    target: BuildTargetIdentifier,
    sources: List[SourceItem],
    roots: Option[List[Uri]]
)
@JsonCodec final case class SourceItem(
    uri: Uri,
    kind: SourceItemKind,
    generated: Boolean
)

sealed abstract class SourceItemKind(val id: Int)
object SourceItemKind {
  object File extends SourceItemKind(1)
  object Directory extends SourceItemKind(2)

  implicit val sourceItemKindEncoder: RootEncoder[SourceItemKind] =
    new RootEncoder[SourceItemKind] {
      override def apply(a: SourceItemKind): Json = Json.fromInt(a.id)
    }

  implicit val sourceItemKindDecoder: Decoder[SourceItemKind] =
    new Decoder[SourceItemKind] {
      override def apply(c: HCursor): Result[SourceItemKind] = {
        c.as[Int].flatMap {
          case 1 => Right(File)
          case 2 => Right(Directory)
          case n => Left(DecodingFailure(s"Unknown build target event kind id for $n", c.history))
        }
      }
    }
}

// Request: 'buildTarget/inverseSources', C -> S
@JsonCodec final case class InverseSourcesParams(
    textDocument: TextDocumentIdentifier
)

@JsonCodec final case class InverseSourcesResult(
    targets: List[BuildTargetIdentifier]
)

// Request: 'buildTarget/dependencySources', C -> S
@JsonCodec final case class DependencySourcesParams(
    targets: List[BuildTargetIdentifier]
)

@JsonCodec final case class DependencySourcesItem(
    target: BuildTargetIdentifier,
    sources: List[Uri]
)

@JsonCodec final case class DependencySourcesResult(
    items: List[DependencySourcesItem]
)

// Request: 'buildTarget/resources', C -> S
@JsonCodec final case class ResourcesParams(
    targets: List[BuildTargetIdentifier]
)

@JsonCodec final case class ResourcesResult(
    items: List[ResourcesItem]
)

@JsonCodec final case class ResourcesItem(
    target: BuildTargetIdentifier,
    resources: List[Uri]
)

// Request: 'buildTarget/compile', C -> S
@JsonCodec final case class CompileParams(
    targets: List[BuildTargetIdentifier],
    originId: Option[String],
    arguments: Option[List[String]]
)

@JsonCodec final case class CompileResult(
    originId: Option[String],
    statusCode: StatusCode,
    dataKind: Option[String],
    data: Option[Json],
)

@JsonCodec final case class CompileReport(
    target: BuildTargetIdentifier,
    originId: Option[String],
    errors: Int,
    warnings: Int,
    time: Option[Long]
)

@JsonCodec final case class CompileTask(
    target: BuildTargetIdentifier
)

@JsonCodec final case class TestParams(
    targets: List[BuildTargetIdentifier],
    originId: Option[String],
    arguments: Option[List[String]],
    dataKind: Option[String],
    data: Option[Json],
)

@JsonCodec final case class TestResult(
    originId: Option[String],
    statusCode: StatusCode,
    dataKind: Option[String],
    data: Option[Json],
)

@JsonCodec final case class TestReport(
    target: BuildTargetIdentifier,
    originId: Option[String],
    passed: Int,
    failed: Int,
    ignored: Int,
    cancelled: Int,
    skipped: Int,
    time: Option[Long]
)

@JsonCodec final case class TestTask(
    target: BuildTargetIdentifier
)

@JsonCodec final case class TestStart(
    displayName: String,
    location: Option[Location]
)

@JsonCodec final case class TestFinish(
    displayName: String,
    message: Option[String],
    status: TestStatus,
    location: Option[Location],
    dataKind: Option[String],
    data: Option[Json]
)

sealed abstract class TestStatus(val code: Int)
object TestStatus {
  case object Passed extends TestStatus(1)
  case object Failed extends TestStatus(2)
  case object Ignored extends TestStatus(3)
  case object Cancelled extends TestStatus(4)
  case object Skipped extends TestStatus(5)

  implicit val testStatusEncoder: RootEncoder[TestStatus] = new RootEncoder[TestStatus] {
    override def apply(a: TestStatus): Json = Json.fromInt(a.code)
  }

  implicit val statusCodeDecoder: Decoder[TestStatus] = new Decoder[TestStatus] {
    override def apply(c: HCursor): Result[TestStatus] = {
      c.as[Int].flatMap {
        case 1 => Right(Passed)
        case 2 => Right(Failed)
        case 3 => Right(Ignored)
        case 4 => Right(Cancelled)
        case 5 => Right(Skipped)
        case n => Left(DecodingFailure(s"Unknown test status $n", c.history))
      }
    }
  }
}

@JsonCodec final case class RunParams(
    target: BuildTargetIdentifier,
    originId: Option[String],
    arguments: Option[List[String]],
    dataKind: Option[String],
    data: Option[Json],
)

object RunParamsDataKind {
  val ScalaMainClass = "scala-main-class"
}

sealed abstract class StatusCode(val code: Int)
object StatusCode {
  case object Ok extends StatusCode(1)
  case object Error extends StatusCode(2)
  case object Cancelled extends StatusCode(3)

  implicit val statusCodeEncoder: RootEncoder[StatusCode] = new RootEncoder[StatusCode] {
    override def apply(a: StatusCode): Json = Json.fromInt(a.code)
  }

  implicit val statusCodeDecoder: Decoder[StatusCode] = new Decoder[StatusCode] {
    override def apply(c: HCursor): Result[StatusCode] = {
      c.as[Int].flatMap {
        case 1 => Right(Ok)
        case 2 => Right(Error)
        case 3 => Right(Cancelled)
        case n => Left(DecodingFailure(s"Unknown status code $n", c.history))
      }
    }
  }
}

@JsonCodec final case class RunResult(
    originId: Option[String],
    statusCode: StatusCode
)

@JsonCodec final case class CleanCacheParams(
    targets: List[BuildTargetIdentifier],
)

@JsonCodec final case class CleanCacheResult(
    message: Option[String],
    cleaned: Boolean
)

@JsonCodec final case class TaskStartParams(
    taskId: TaskId,
    eventTime: Option[Long],
    message: Option[String],
    dataKind: Option[String],
    data: Option[Json]
)

@JsonCodec final case class TaskProgressParams(
    taskId: TaskId,
    eventTime: Option[Long],
    message: Option[String],
    total: Option[Long],
    progress: Option[Long],
    unit: Option[String],
    dataKind: Option[String],
    data: Option[Json]
)

@JsonCodec final case class TaskFinishParams(
    taskId: TaskId,
    eventTime: Option[Long],
    message: Option[String],
    status: StatusCode,
    dataKind: Option[String],
    data: Option[Json]
)

object TaskDataKind {
  val CompileTask = "compile-task"
  val CompileReport = "compile-report"
  val TestTask = "test-task"
  val TestReport = "test-report"
  val TestStart = "test-start"
  val TestFinish = "test-finish"
}

sealed abstract class ScalaPlatform(val id: Int)
object ScalaPlatform {
  case object Jvm extends ScalaPlatform(1)
  case object Js extends ScalaPlatform(2)
  case object Native extends ScalaPlatform(3)

  implicit val scalaPlatformEncoder: RootEncoder[ScalaPlatform] = new RootEncoder[ScalaPlatform] {
    override def apply(a: ScalaPlatform): Json = Json.fromInt(a.id)
  }

  implicit val scalaPlatformDecoder: Decoder[ScalaPlatform] = new Decoder[ScalaPlatform] {
    override def apply(c: HCursor): Result[ScalaPlatform] = {
      c.as[Int].flatMap {
        case 1 => Right(Jvm)
        case 2 => Right(Js)
        case 3 => Right(Native)
        case n => Left(DecodingFailure(s"Unknown platform id for $n", c.history))
      }
    }
  }
}

@JsonCodec final case class JvmBuildTarget(
    javaHome: Option[Uri],
    javaVersion: Option[String]
)

@JsonCodec final case class ScalaBuildTarget(
    scalaOrganization: String,
    scalaVersion: String,
    scalaBinaryVersion: String,
    platform: ScalaPlatform,
    jars: List[Uri],
    jvmBuildTarget: Option[JvmBuildTarget]
)

@JsonCodec final case class ScalaTestParams(
    testClasses: Option[List[ScalaTestClassesItem]],
)

object TestParamsDataKind {
  val ScalaTest = "scala-test"
}

// Request: 'buildTarget/scalacOptions', C -> S
@JsonCodec final case class ScalacOptionsParams(
    targets: List[BuildTargetIdentifier]
)


@JsonCodec final case class JvmTestEnvironmentParams(
    targets: List[BuildTargetIdentifier],
    originId: Option[String]
)

@JsonCodec final case class JvmEnvironmentItem(
    target: BuildTargetIdentifier,
    classpath: List[String],
    jvmOptions: List[String],
    workingDirectory: String,
    environmentVariables: Map[String, String]
)

@JsonCodec final case class JvmTestEnvironmentResult(
    items: List[JvmEnvironmentItem]
)

@JsonCodec final case class JvmRunEnvironmentParams(
   targets: List[BuildTargetIdentifier],
   originId: Option[String]
)

@JsonCodec final case class JvmRunEnvironmentResult(
   items: List[JvmEnvironmentItem]
)

@JsonCodec final case class ScalacOptionsItem(
    target: BuildTargetIdentifier,
    options: List[String],
    classpath: List[Uri],
    classDirectory: Uri,
)

@JsonCodec final case class ScalacOptionsResult(
    items: List[ScalacOptionsItem]
)

// Request: 'buildTarget/scalaTestClasses', C -> S
@JsonCodec final case class ScalaTestClassesParams(
    targets: List[BuildTargetIdentifier],
    originId: Option[String],
)

@JsonCodec final case class ScalaTestClassesItem(
    target: BuildTargetIdentifier,
    // Fully qualified names of test classes
    classes: List[String]
)

@JsonCodec final case class ScalaTestClassesResult(
    items: List[ScalaTestClassesItem]
)

// Request: 'buildTarget/scalaMainClasses', C -> S
@JsonCodec final case class ScalaMainClassesParams(
    targets: List[BuildTargetIdentifier],
    originId: Option[String],
)

@JsonCodec final case class ScalaMainClass(
    `class`: String,
    arguments: List[String],
    jvmOptions: List[String],
    environmentVariables: List[String]
)

@JsonCodec final case class ScalaMainClassesItem(
    target: BuildTargetIdentifier,
    // Fully qualified names of test classes
    classes: List[ScalaMainClass]
)

@JsonCodec final case class ScalaMainClassesResult(
    items: List[ScalaMainClassesItem]
)

@JsonCodec final case class SbtBuildTarget(
    sbtVersion: String,
    autoImports: List[String],
    scalaBuildTarget: ScalaBuildTarget,
    parent: Option[BuildTargetIdentifier],
    children: List[BuildTargetIdentifier],
)
@JsonCodec final case class BspConnectionDetails(
    name: String,
    argv: List[String],
    version: String,
    bspVersion: String,
    languages: List[String]
)

@JsonCodec final case class DebugSessionParams(
    targets: List[BuildTargetIdentifier],
    dataKind: String,
    data: Json
)

object DebugSessionParamsDataKind {
  val ScalaMainClass = "scala-main-class"
  val ScalaTestSuites = "scala-test-suites"
}

@JsonCodec final case class DebugSessionAddress(uri: String)

@JsonCodec final case class JavacOptionsParams(
    targets: List[BuildTargetIdentifier]
)

@JsonCodec final case class JavacOptionsItem(
    target: BuildTargetIdentifier,
    options: List[String],
    classpath: List[Uri],
    classDirectory: Uri
)

@JsonCodec final case class JavacOptionsResult(
    items: List[JavacOptionsItem]
)
