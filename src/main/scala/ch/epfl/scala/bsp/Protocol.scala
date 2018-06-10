package ch.epfl.scala.bsp

import io.circe.{Decoder, ObjectEncoder}
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
    data: Array[Byte]
)

@JsonCodec final case class BuildClientCapabilities(
    languageIds: List[String]
)

// Request: 'build/initialize', C -> S
@JsonCodec final case class InitializeBuildParams(
    rootUri: String,
    capabilities: BuildClientCapabilities
)

@JsonCodec final case class CompileProvider(
    languageIds: List[String]
)

@JsonCodec final case class TestProvider(
    languageIds: List[String]
)

@JsonCodec final case class BuildServerCapabilities(
    compileProvider: CompileProvider,
    testProvider: TestProvider,
    textDocumentBuildTargetsProvider: Boolean,
    dependencySourcesProvider: Boolean,
    buildTargetChangedProvider: Boolean
)

@JsonCodec final case class InitializeBuildResult(
    capabilities: BuildServerCapabilities
)

sealed trait MessageType
object MessageType {
  case object Unknown extends MessageType
  case object Created extends MessageType
  case object Changed extends MessageType
  case object Deleted extends MessageType

  implicit val messageTypeEncoder: ObjectEncoder[MessageType] = ???
  implicit val messageTypeDecoder: Decoder[MessageType] = ???
}

@JsonCodec final case class ShowMessageParams(
    `type`: MessageType,
    id: Option[String],
    parentId: Option[String],
    message: String
)

@JsonCodec final case class LogMessageParams(
    `type`: MessageType,
    id: Option[String],
    parentId: Option[String],
    message: String
)

// Notification: 'build/initialized', C -> S
@JsonCodec final case class InitializedBuildParams()

@JsonCodec final case class WorkspaceBuildTargetsRequest()

// Request: 'workspace/buildTargets'
@JsonCodec final case class WorkspaceBuildTargets(
    targets: List[BuildTarget]
)

sealed trait BuildTargetEventKind
case object BuildTargetEventKind {
  case object Unknown extends BuildTargetEventKind
  case object Created extends BuildTargetEventKind
  case object Changed extends BuildTargetEventKind
  case object Deleted extends BuildTargetEventKind

  implicit val buildTargetEventKindEncoder: ObjectEncoder[BuildTargetEventKind] = ???
  implicit val buildTargetEventKindDecoder: Decoder[BuildTargetEventKind] = ???
}

@JsonCodec final case class BuildTargetEvent(
    id: BuildTargetIdentifier,
    kind: BuildTargetEventKind
)

// Notification: 'buildTarget/didChange', S -> C
@JsonCodec final case class DidChangeBuildTargetParams(
    changes: List[BuildTargetEvent]
)

// Request: 'buildTarget/textDocument', C -> S
@JsonCodec final case class BuildTargetTextDocumentParams(
    targets: List[BuildTargetIdentifier]
)

@JsonCodec final case class BuildTargetTextDocuments(
    textDocuments: List[TextDocumentIdentifier]
)

// Request: 'textDocument/buildTarget', C -> S
@JsonCodec final case class TextDocumentBuildTargetsParams(
    textDocument: TextDocumentIdentifier
)

@JsonCodec final case class TextDocumentBuildTargets(
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

@JsonCodec final case class DependencySources(
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

@JsonCodec final case class Resources(
    targets: List[ResourcesItem]
)

// Request: 'buildTarget/compile', C -> S
@JsonCodec final case class CompileParams(
    targets: List[BuildTargetIdentifier]
)

@JsonCodec final case class CompileReportItem(
    target: BuildTargetIdentifier,
    errors: Int,
    warnings: Int,
    time: Long
)

@JsonCodec final case class CompileReport(
    items: List[CompileReportItem]
)

sealed trait ScalaPlatform
object ScalaPlatform {
  case object Jvm extends ScalaPlatform
  case object Js extends ScalaPlatform
  case object Native extends ScalaPlatform

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

@JsonCodec final case class ScalacOptions(
    items: List[ScalacOptionsItem]
)

// Request: 'buildTarget/scalaTestClasses', C -> S
@JsonCodec final case class ScalaTestClassesParams(
    target: BuildTargetIdentifier
)

@JsonCodec final case class ScalaTestClassesItem(
    target: BuildTargetIdentifier,
    // Fully qualified names of test classes
    classes: List[String]
)

@JsonCodec final case class ScalaTestClasses(
    items: List[ScalaTestClassesItem]
)
