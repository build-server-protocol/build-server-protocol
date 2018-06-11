package ch.epfl.scala.bsp

import io.circe.{Decoder, Json, ObjectEncoder}
import io.circe.generic.JsonCodec

@JsonCodec final case class TextDocumentIdentifier(
    uri: String
)

@JsonCodec final case class BuildTargetIdentifier(
    uri: String
)

@JsonCodec final case class BuildTargetCapabilities(
    canCompile: Boolean,
    canTest: Boolean,
    canRun: Boolean,
)

@JsonCodec final case class BuildTarget(
    id: BuildTargetIdentifier,
    displayName: String,
    languageIds: List[String],
    dependencies: BuildTargetIdentifier,
    capabilities: BuildTargetCapabilities,
    data: Option[Json]
)

@JsonCodec final case class BuildClientCapabilities(
    languageIds: List[String]
)

// Notification: 'build/initialized', C -> S
@JsonCodec final case class InitializedBuildParams()

// Request: 'build/initialize', C -> S
@JsonCodec final case class InitializeBuildParams(
    rootUri: String,
    capabilities: BuildClientCapabilities
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
    compileProvider: CompileProvider,
    testProvider: TestProvider,
    runProvider: RunProvider,
    providesTextDocumentBuildTargets: Boolean,
    providesDependencySources: Boolean,
    providesResources: Boolean,
    providesBuildTargetChanged: Boolean
)

@JsonCodec final case class InitializeBuildResult(
    capabilities: BuildServerCapabilities
)

sealed trait MessageType
object MessageType {
  case object Created extends MessageType
  case object Changed extends MessageType
  case object Deleted extends MessageType

  implicit val messageTypeEncoder: ObjectEncoder[MessageType] = ???
  implicit val messageTypeDecoder: Decoder[MessageType] = ???
}

@JsonCodec final case class HierarchicalId(
    id: String,
    parentId: String
)

@JsonCodec final case class ShowMessageParams(
    `type`: MessageType,
    id: Option[HierarchicalId],
    requestId: Option[String],
    message: String
)

@JsonCodec final case class LogMessageParams(
    `type`: MessageType,
    id: Option[HierarchicalId],
    requestId: Option[String],
    message: String
)

@JsonCodec final case class PublishDiagnosticsParams(
    uri: String,
    requestId: Option[String],
    message: String
)

@JsonCodec final case class WorkspaceBuildTargetsRequest()

// Request: 'workspace/buildTargets'
@JsonCodec final case class WorkspaceBuildTargets(
    targets: List[BuildTarget]
)

sealed trait BuildTargetEventKind
case object BuildTargetEventKind {
  case object Created extends BuildTargetEventKind
  case object Changed extends BuildTargetEventKind
  case object Deleted extends BuildTargetEventKind

  implicit val buildTargetEventKindEncoder: ObjectEncoder[BuildTargetEventKind] = ???
  implicit val buildTargetEventKindDecoder: Decoder[BuildTargetEventKind] = ???
}

@JsonCodec final case class BuildTargetEvent(
    id: BuildTargetIdentifier,
    kind: Option[BuildTargetEventKind],
    data: Option[Json]
)

// Notification: 'buildTarget/didChange', S -> C
@JsonCodec final case class DidChangeBuildTarget(
    changes: List[BuildTargetEvent]
)

// Request: 'buildTarget/textDocument', C -> S
@JsonCodec final case class BuildTargetTextDocumentParams(
    targets: List[BuildTargetIdentifier]
)

@JsonCodec final case class BuildTargetTextDocumentsResult(
    textDocuments: List[TextDocumentIdentifier]
)

// Request: 'textDocument/buildTarget', C -> S
@JsonCodec final case class TextDocumentBuildTargetsParams(
    textDocument: TextDocumentIdentifier
)

@JsonCodec final case class TextDocumentBuildTargetsResult(
    targets: List[BuildTarget]
)

// Request: 'buildTarget/dependencySources', C -> S
@JsonCodec final case class DependencySourcesParams(
    targets: List[BuildTargetIdentifier]
)

@JsonCodec final case class DependencySourcesItem(
    target: BuildTargetIdentifier,
    uris: List[String]
)

@JsonCodec final case class DependencySourcesResult(
    items: List[DependencySourcesItem]
)

// Request: 'buildTarget/resources', C -> S
@JsonCodec final case class ResourcesParams(
    targets: List[BuildTargetIdentifier]
)

@JsonCodec final case class ResourcesItem(
    target: BuildTargetIdentifier,
    uris: List[String]
)

@JsonCodec final case class ResourcesResult(
    targets: List[ResourcesItem]
)

// Request: 'buildTarget/compile', C -> S
@JsonCodec final case class CompileParams(
    targets: List[BuildTargetIdentifier],
    requestId: Option[String],
    arguments: List[Json]
)

@JsonCodec final case class CompileResult(
    requestId: Option[String],
    data: Option[Json]
)

@JsonCodec final case class CompileReport(
    target: BuildTargetIdentifier,
    requestId: Option[String],
    errors: Int,
    warnings: Int,
    time: Option[Long]
)

@JsonCodec final case class TestParams(
    targets: List[BuildTargetIdentifier],
    requestId: Option[String],
    arguments: List[Json]
)

@JsonCodec final case class TestResult(
    requestId: Option[String],
    data: Option[Json]
)

@JsonCodec final case class TestReport(
    target: BuildTargetIdentifier,
    requestId: Option[String],
    passed: Int,
    failed: Int,
    ignored: Int,
    cancelled: Int,
    skipped: Int,
    pending: Int,
    time: Option[Long]
)

@JsonCodec final case class RunParams(
    target: BuildTargetIdentifier,
    requestId: Option[String],
    arguments: List[Json]
)

sealed abstract class ExitStatus(code: Int)
object ExitStatus {
  case object Ok extends ExitStatus(0)
  case object Error extends ExitStatus(1)
  case object Cancelled extends ExitStatus(-1)

  implicit val exitStatusEncoder: ObjectEncoder[ExitStatus] = ???
  implicit val exitStatusDecoder: Decoder[ExitStatus] = ???
}

@JsonCodec final case class RunResult(
    requestId: Option[String],
    exitStatus: ExitStatus
)

sealed abstract class ScalaPlatform(id: Int)
object ScalaPlatform {
  case object Jvm extends ScalaPlatform(1)
  case object Js extends ScalaPlatform(2)
  case object Native extends ScalaPlatform(3)

  implicit val scalaPlatformEncoder: ObjectEncoder[ScalaPlatform] = ???
  implicit val scalaPlatformDecoder: Decoder[ScalaPlatform] = ???
}

@JsonCodec final case class ScalaBuildTarget(
    scalaOrganization: String,
    scalaVersion: String,
    scalaBinaryVersion: String,
    platform: ScalaPlatform,
    jars: List[String]
)

// Request: 'buildTarget/scalacOptions', C -> S
@JsonCodec final case class ScalacOptionsParams(
    targets: List[BuildTargetIdentifier]
)

@JsonCodec final case class ScalacOptionsItem(
    target: BuildTargetIdentifier,
    options: List[String],
    classpath: List[String],
    classDirectory: String,
)

@JsonCodec final case class ScalacOptionsResult(
    items: List[ScalacOptionsItem]
)

// Request: 'buildTarget/scalaTestClasses', C -> S
@JsonCodec final case class ScalaTestClassesParams(
    targets: List[BuildTargetIdentifier],
    requestId: Option[String],
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
    requestId: Option[String],
)

@JsonCodec final case class ScalaMainClass(
    `class`: String,
    arguments: List[String],
    javaOptions: List[String]
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
    parent: Option[BuildTargetIdentifier],
    sbtVersion: String,
    scalaVersion: String,
    scalaJars: List[String],
    autoImports: List[String],
    classpath: List[String],
)
