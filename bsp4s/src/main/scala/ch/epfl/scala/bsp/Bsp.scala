package ch.epfl.scala.bsp

import java.net.{URI, URISyntaxException}

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.named
import com.github.plokhotnyuk.jsoniter_scala.core.JsonCodec
import com.github.plokhotnyuk.jsoniter_scala.core.JsonWriter
import com.github.plokhotnyuk.jsoniter_scala.core.JsonReader
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import jsonrpc4s.RawJson

object Bsp4s {
  val ProtocolVersion: String = "2.1.0"
}

final case class Uri private[Uri] (val value: String) {
  def toPath: java.nio.file.Path =
    java.nio.file.Paths.get(new java.net.URI(value))
}

object Uri {
  // This is the only valid way to create a URI
  def apply(u: URI): Uri = Uri(u.toString)

  implicit val uriCodec: JsonValueCodec[Uri] = new JsonValueCodec[Uri] {
    def nullValue: Uri = null
    def encodeValue(id: Uri, out: JsonWriter): Unit = out.writeVal(id.value)
    def decodeValue(in: JsonReader, default: Uri): Uri = {
      val defaultStr = if (default == null) null else default.value
      val str = in.readString(defaultStr)
      try Uri(URI.create(str).toString)
      catch {
        case _: IllegalArgumentException | _: URISyntaxException =>
          in.decodeError(s"String $str is not a valid URI!")
      }
    }
  }
}

final case class BspConnectionDetails(
    name: String,
    argv: List[String],
    version: String,
    bspVersion: String,
    languages: List[String]
)

object BspConnectionDetails {
  implicit val codec: JsonValueCodec[BspConnectionDetails] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class BuildClientCapabilities(
    languageIds: List[String]
)

object BuildClientCapabilities {
  implicit val codec: JsonValueCodec[BuildClientCapabilities] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class BuildServerCapabilities(
    compileProvider: Option[CompileProvider],
    testProvider: Option[TestProvider],
    runProvider: Option[RunProvider],
    debugProvider: Option[DebugProvider],
    inverseSourcesProvider: Option[Boolean],
    dependencySourcesProvider: Option[Boolean],
    dependencyModulesProvider: Option[Boolean],
    resourcesProvider: Option[Boolean],
    outputPathsProvider: Option[Boolean],
    buildTargetChangedProvider: Option[Boolean],
    jvmRunEnvironmentProvider: Option[Boolean],
    jvmTestEnvironmentProvider: Option[Boolean],
    cargoFeaturesProvider: Option[Boolean],
    canReload: Option[Boolean]
)

object BuildServerCapabilities {
  implicit val codec: JsonValueCodec[BuildServerCapabilities] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class BuildTarget(
    id: BuildTargetIdentifier,
    displayName: Option[String],
    baseDirectory: Option[Uri],
    tags: List[String],
    languageIds: List[String],
    dependencies: List[BuildTargetIdentifier],
    capabilities: BuildTargetCapabilities,
    dataKind: Option[String],
    data: Option[RawJson]
)

object BuildTarget {
  implicit val codec: JsonValueCodec[BuildTarget] = JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class BuildTargetCapabilities(
    canCompile: Option[Boolean],
    canTest: Option[Boolean],
    canRun: Option[Boolean],
    canDebug: Option[Boolean]
)

object BuildTargetCapabilities {
  implicit val codec: JsonValueCodec[BuildTargetCapabilities] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

object BuildTargetDataKind {
  val Cargo = "cargo"
  val Cpp = "cpp"
  val Jvm = "jvm"
  val Python = "python"
  val Sbt = "sbt"
  val Scala = "scala"
}

final case class BuildTargetEvent(
    target: BuildTargetIdentifier,
    kind: Option[BuildTargetEventKind],
    dataKind: Option[String],
    data: Option[RawJson]
)

object BuildTargetEvent {
  implicit val codec: JsonValueCodec[BuildTargetEvent] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

object BuildTargetEventDataKind {}

sealed abstract class BuildTargetEventKind(val value: Int)
object BuildTargetEventKind {
  case object Created extends BuildTargetEventKind(1)
  case object Changed extends BuildTargetEventKind(2)
  case object Deleted extends BuildTargetEventKind(3)

  implicit val codec: JsonValueCodec[BuildTargetEventKind] =
    new JsonValueCodec[BuildTargetEventKind] {
      def nullValue: BuildTargetEventKind = null
      def encodeValue(msg: BuildTargetEventKind, out: JsonWriter): Unit = out.writeVal(msg.value)
      def decodeValue(in: JsonReader, default: BuildTargetEventKind): BuildTargetEventKind = {
        in.readInt() match {
          case 1 => Created
          case 2 => Changed
          case 3 => Deleted
          case n => in.decodeError(s"Unknown message type id for $n")
        }
      }
    }
}
final case class BuildTargetIdentifier(
    uri: Uri
)

object BuildTargetIdentifier {
  implicit val codec: JsonValueCodec[BuildTargetIdentifier] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

object BuildTargetTag {
  val Application = "application"
  val Benchmark = "benchmark"
  val IntegrationTest = "integration-test"
  val Library = "library"
  val Manual = "manual"
  val NoIde = "no-ide"
  val Test = "test"
}

final case class CargoBuildTarget(
    edition: String,
    required_features: Set[String]
)

object CargoBuildTarget {
  implicit val codec: JsonValueCodec[CargoBuildTarget] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class CargoFeaturesStateResult(
    packagesFeatures: List[PackageFeatures]
)

object CargoFeaturesStateResult {
  implicit val codec: JsonValueCodec[CargoFeaturesStateResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class CleanCacheParams(
    targets: List[BuildTargetIdentifier]
)

object CleanCacheParams {
  implicit val codec: JsonValueCodec[CleanCacheParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class CleanCacheResult(
    message: Option[String],
    cleaned: Boolean
)

object CleanCacheResult {
  implicit val codec: JsonValueCodec[CleanCacheResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class CompileParams(
    targets: List[BuildTargetIdentifier],
    originId: Option[String],
    arguments: Option[List[String]]
)

object CompileParams {
  implicit val codec: JsonValueCodec[CompileParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class CompileProvider(
    languageIds: List[String]
)

object CompileProvider {
  implicit val codec: JsonValueCodec[CompileProvider] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class CompileReport(
    target: BuildTargetIdentifier,
    originId: Option[String],
    errors: Int,
    warnings: Int,
    time: Option[Long],
    noOp: Option[Boolean]
)

object CompileReport {
  implicit val codec: JsonValueCodec[CompileReport] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class CompileResult(
    originId: Option[String],
    statusCode: StatusCode,
    dataKind: Option[String],
    data: Option[RawJson]
)

object CompileResult {
  implicit val codec: JsonValueCodec[CompileResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

object CompileResultDataKind {}

final case class CompileTask(
    target: BuildTargetIdentifier
)

object CompileTask {
  implicit val codec: JsonValueCodec[CompileTask] = JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class CppBuildTarget(
    version: Option[String],
    compiler: Option[String],
    cCompiler: Option[Uri],
    cppCompiler: Option[Uri]
)

object CppBuildTarget {
  implicit val codec: JsonValueCodec[CppBuildTarget] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class CppOptionsItem(
    target: BuildTargetIdentifier,
    copts: List[String],
    defines: List[String],
    linkopts: List[String],
    linkshared: Option[Boolean]
)

object CppOptionsItem {
  implicit val codec: JsonValueCodec[CppOptionsItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class CppOptionsParams(
    targets: List[BuildTargetIdentifier]
)

object CppOptionsParams {
  implicit val codec: JsonValueCodec[CppOptionsParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class CppOptionsResult(
    items: List[CppOptionsItem]
)

object CppOptionsResult {
  implicit val codec: JsonValueCodec[CppOptionsResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class DebugProvider(
    languageIds: List[String]
)

object DebugProvider {
  implicit val codec: JsonValueCodec[DebugProvider] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class DebugSessionAddress(
    uri: Uri
)

object DebugSessionAddress {
  implicit val codec: JsonValueCodec[DebugSessionAddress] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class DebugSessionParams(
    targets: List[BuildTargetIdentifier],
    dataKind: Option[String],
    data: Option[RawJson]
)

object DebugSessionParams {
  implicit val codec: JsonValueCodec[DebugSessionParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

object DebugSessionParamsDataKind {
  val ScalaAttachRemote = "scala-attach-remote"
  val ScalaMainClass = "scala-main-class"
  val ScalaTestSuites = "scala-test-suites"
  val ScalaTestSuitesSelection = "scala-test-suites-selection"
}

final case class DependencyModule(
    name: String,
    version: String,
    dataKind: Option[String],
    data: Option[RawJson]
)

object DependencyModule {
  implicit val codec: JsonValueCodec[DependencyModule] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

object DependencyModuleDataKind {
  val Maven = "maven"
}

final case class DependencyModulesItem(
    target: BuildTargetIdentifier,
    modules: List[DependencyModule]
)

object DependencyModulesItem {
  implicit val codec: JsonValueCodec[DependencyModulesItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class DependencyModulesParams(
    targets: List[BuildTargetIdentifier]
)

object DependencyModulesParams {
  implicit val codec: JsonValueCodec[DependencyModulesParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class DependencyModulesResult(
    items: List[DependencyModulesItem]
)

object DependencyModulesResult {
  implicit val codec: JsonValueCodec[DependencyModulesResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class DependencySourcesItem(
    target: BuildTargetIdentifier,
    sources: List[Uri]
)

object DependencySourcesItem {
  implicit val codec: JsonValueCodec[DependencySourcesItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class DependencySourcesParams(
    targets: List[BuildTargetIdentifier]
)

object DependencySourcesParams {
  implicit val codec: JsonValueCodec[DependencySourcesParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class DependencySourcesResult(
    items: List[DependencySourcesItem]
)

object DependencySourcesResult {
  implicit val codec: JsonValueCodec[DependencySourcesResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class Diagnostic(
    range: Range,
    severity: Option[DiagnosticSeverity],
    code: Option[String],
    source: Option[String],
    message: String,
    relatedInformation: Option[List[DiagnosticRelatedInformation]],
    dataKind: Option[String],
    data: Option[RawJson]
)

object Diagnostic {
  implicit val codec: JsonValueCodec[Diagnostic] = JsonCodecMaker.makeWithRequiredCollectionFields
}

object DiagnosticDataKind {
  val Scala = "scala"
}

final case class DiagnosticRelatedInformation(
    location: Location,
    message: String
)

object DiagnosticRelatedInformation {
  implicit val codec: JsonValueCodec[DiagnosticRelatedInformation] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

sealed abstract class DiagnosticSeverity(val value: Int)
object DiagnosticSeverity {
  case object Error extends DiagnosticSeverity(1)
  case object Warning extends DiagnosticSeverity(2)
  case object Information extends DiagnosticSeverity(3)
  case object Hint extends DiagnosticSeverity(4)

  implicit val codec: JsonValueCodec[DiagnosticSeverity] = new JsonValueCodec[DiagnosticSeverity] {
    def nullValue: DiagnosticSeverity = null
    def encodeValue(msg: DiagnosticSeverity, out: JsonWriter): Unit = out.writeVal(msg.value)
    def decodeValue(in: JsonReader, default: DiagnosticSeverity): DiagnosticSeverity = {
      in.readInt() match {
        case 1 => Error
        case 2 => Warning
        case 3 => Information
        case 4 => Hint
        case n => in.decodeError(s"Unknown message type id for $n")
      }
    }
  }
}
object DiagnosticTag {
  val Unnecessary = 1
  val Deprecated = 2
}

final case class DidChangeBuildTarget(
    changes: List[BuildTargetEvent]
)

object DidChangeBuildTarget {
  implicit val codec: JsonValueCodec[DidChangeBuildTarget] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class InitializeBuildParams(
    displayName: String,
    version: String,
    bspVersion: String,
    rootUri: Uri,
    capabilities: BuildClientCapabilities,
    dataKind: Option[String],
    data: Option[RawJson]
)

object InitializeBuildParams {
  implicit val codec: JsonValueCodec[InitializeBuildParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

object InitializeBuildParamsDataKind {}

final case class InitializeBuildResult(
    displayName: String,
    version: String,
    bspVersion: String,
    capabilities: BuildServerCapabilities,
    dataKind: Option[String],
    data: Option[RawJson]
)

object InitializeBuildResult {
  implicit val codec: JsonValueCodec[InitializeBuildResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

object InitializeBuildResultDataKind {}

final case class InverseSourcesParams(
    textDocument: TextDocumentIdentifier
)

object InverseSourcesParams {
  implicit val codec: JsonValueCodec[InverseSourcesParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class InverseSourcesResult(
    targets: List[BuildTargetIdentifier]
)

object InverseSourcesResult {
  implicit val codec: JsonValueCodec[InverseSourcesResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class JavacOptionsItem(
    target: BuildTargetIdentifier,
    options: List[String],
    classpath: List[String],
    classDirectory: String
)

object JavacOptionsItem {
  implicit val codec: JsonValueCodec[JavacOptionsItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class JavacOptionsParams(
    targets: List[BuildTargetIdentifier]
)

object JavacOptionsParams {
  implicit val codec: JsonValueCodec[JavacOptionsParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class JavacOptionsResult(
    items: List[JavacOptionsItem]
)

object JavacOptionsResult {
  implicit val codec: JsonValueCodec[JavacOptionsResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class JvmBuildTarget(
    javaHome: Option[Uri],
    javaVersion: Option[String]
)

object JvmBuildTarget {
  implicit val codec: JsonValueCodec[JvmBuildTarget] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class JvmEnvironmentItem(
    target: BuildTargetIdentifier,
    classpath: List[String],
    jvmOptions: List[String],
    workingDirectory: String,
    environmentVariables: Map[String, String],
    mainClasses: Option[List[JvmMainClass]]
)

object JvmEnvironmentItem {
  implicit val codec: JsonValueCodec[JvmEnvironmentItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class JvmMainClass(
    className: String,
    arguments: List[String]
)

object JvmMainClass {
  implicit val codec: JsonValueCodec[JvmMainClass] = JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class JvmRunEnvironmentParams(
    targets: List[BuildTargetIdentifier],
    originId: Option[String]
)

object JvmRunEnvironmentParams {
  implicit val codec: JsonValueCodec[JvmRunEnvironmentParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class JvmRunEnvironmentResult(
    items: List[JvmEnvironmentItem]
)

object JvmRunEnvironmentResult {
  implicit val codec: JsonValueCodec[JvmRunEnvironmentResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class JvmTestEnvironmentParams(
    targets: List[BuildTargetIdentifier],
    originId: Option[String]
)

object JvmTestEnvironmentParams {
  implicit val codec: JsonValueCodec[JvmTestEnvironmentParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class JvmTestEnvironmentResult(
    items: List[JvmEnvironmentItem]
)

object JvmTestEnvironmentResult {
  implicit val codec: JsonValueCodec[JvmTestEnvironmentResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class Location(
    uri: Uri,
    range: Range
)

object Location {
  implicit val codec: JsonValueCodec[Location] = JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class LogMessageParams(
    `type`: MessageType,
    task: Option[TaskId],
    originId: Option[String],
    message: String
)

object LogMessageParams {
  implicit val codec: JsonValueCodec[LogMessageParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class MavenDependencyModule(
    organization: String,
    name: String,
    version: String,
    artifacts: List[MavenDependencyModuleArtifact],
    scope: Option[String]
)

object MavenDependencyModule {
  implicit val codec: JsonValueCodec[MavenDependencyModule] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class MavenDependencyModuleArtifact(
    uri: Uri,
    classifier: Option[String]
)

object MavenDependencyModuleArtifact {
  implicit val codec: JsonValueCodec[MavenDependencyModuleArtifact] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

sealed abstract class MessageType(val value: Int)
object MessageType {
  case object Error extends MessageType(1)
  case object Warning extends MessageType(2)
  case object Info extends MessageType(3)
  case object Log extends MessageType(4)

  implicit val codec: JsonValueCodec[MessageType] = new JsonValueCodec[MessageType] {
    def nullValue: MessageType = null
    def encodeValue(msg: MessageType, out: JsonWriter): Unit = out.writeVal(msg.value)
    def decodeValue(in: JsonReader, default: MessageType): MessageType = {
      in.readInt() match {
        case 1 => Error
        case 2 => Warning
        case 3 => Info
        case 4 => Log
        case n => in.decodeError(s"Unknown message type id for $n")
      }
    }
  }
}
final case class OutputPathItem(
    uri: Uri,
    kind: OutputPathItemKind
)

object OutputPathItem {
  implicit val codec: JsonValueCodec[OutputPathItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

sealed abstract class OutputPathItemKind(val value: Int)
object OutputPathItemKind {
  case object File extends OutputPathItemKind(1)
  case object Directory extends OutputPathItemKind(2)

  implicit val codec: JsonValueCodec[OutputPathItemKind] = new JsonValueCodec[OutputPathItemKind] {
    def nullValue: OutputPathItemKind = null
    def encodeValue(msg: OutputPathItemKind, out: JsonWriter): Unit = out.writeVal(msg.value)
    def decodeValue(in: JsonReader, default: OutputPathItemKind): OutputPathItemKind = {
      in.readInt() match {
        case 1 => File
        case 2 => Directory
        case n => in.decodeError(s"Unknown message type id for $n")
      }
    }
  }
}
final case class OutputPathsItem(
    target: BuildTargetIdentifier,
    outputPaths: List[OutputPathItem]
)

object OutputPathsItem {
  implicit val codec: JsonValueCodec[OutputPathsItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class OutputPathsParams(
    targets: List[BuildTargetIdentifier]
)

object OutputPathsParams {
  implicit val codec: JsonValueCodec[OutputPathsParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class OutputPathsResult(
    items: List[OutputPathsItem]
)

object OutputPathsResult {
  implicit val codec: JsonValueCodec[OutputPathsResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class PackageFeatures(
    packageId: String,
    targets: List[BuildTargetIdentifier],
    availableFeatures: Map[String, Set[String]],
    enabledFeatures: Set[String]
)

object PackageFeatures {
  implicit val codec: JsonValueCodec[PackageFeatures] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class Position(
    line: Int,
    character: Int
)

object Position {
  implicit val codec: JsonValueCodec[Position] = JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class PublishDiagnosticsParams(
    textDocument: TextDocumentIdentifier,
    buildTarget: BuildTargetIdentifier,
    originId: Option[String],
    diagnostics: List[Diagnostic],
    reset: Boolean
)

object PublishDiagnosticsParams {
  implicit val codec: JsonValueCodec[PublishDiagnosticsParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class PythonBuildTarget(
    version: Option[String],
    interpreter: Option[Uri]
)

object PythonBuildTarget {
  implicit val codec: JsonValueCodec[PythonBuildTarget] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class PythonOptionsItem(
    target: BuildTargetIdentifier,
    interpreterOptions: List[String]
)

object PythonOptionsItem {
  implicit val codec: JsonValueCodec[PythonOptionsItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class PythonOptionsParams(
    targets: List[BuildTargetIdentifier]
)

object PythonOptionsParams {
  implicit val codec: JsonValueCodec[PythonOptionsParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class PythonOptionsResult(
    items: List[PythonOptionsItem]
)

object PythonOptionsResult {
  implicit val codec: JsonValueCodec[PythonOptionsResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class Range(
    start: Position,
    end: Position
)

object Range {
  implicit val codec: JsonValueCodec[Range] = JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ResourcesItem(
    target: BuildTargetIdentifier,
    resources: List[Uri]
)

object ResourcesItem {
  implicit val codec: JsonValueCodec[ResourcesItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ResourcesParams(
    targets: List[BuildTargetIdentifier]
)

object ResourcesParams {
  implicit val codec: JsonValueCodec[ResourcesParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ResourcesResult(
    items: List[ResourcesItem]
)

object ResourcesResult {
  implicit val codec: JsonValueCodec[ResourcesResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class RunParams(
    target: BuildTargetIdentifier,
    originId: Option[String],
    arguments: Option[List[String]],
    dataKind: Option[String],
    data: Option[RawJson]
)

object RunParams {
  implicit val codec: JsonValueCodec[RunParams] = JsonCodecMaker.makeWithRequiredCollectionFields
}

object RunParamsDataKind {
  val ScalaMainClass = "scala-main-class"
}

final case class RunProvider(
    languageIds: List[String]
)

object RunProvider {
  implicit val codec: JsonValueCodec[RunProvider] = JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class RunResult(
    originId: Option[String],
    statusCode: StatusCode
)

object RunResult {
  implicit val codec: JsonValueCodec[RunResult] = JsonCodecMaker.makeWithRequiredCollectionFields
}

object RustEdition {
  val E2015 = "2015"
  val E2018 = "2018"
  val E2021 = "2021"
}

final case class SbtBuildTarget(
    sbtVersion: String,
    autoImports: List[String],
    scalaBuildTarget: ScalaBuildTarget,
    parent: Option[BuildTargetIdentifier],
    children: List[BuildTargetIdentifier]
)

object SbtBuildTarget {
  implicit val codec: JsonValueCodec[SbtBuildTarget] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaAction(
    title: String,
    description: Option[String],
    edit: Option[ScalaWorkspaceEdit]
)

object ScalaAction {
  implicit val codec: JsonValueCodec[ScalaAction] = JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaAttachRemote(
)

object ScalaAttachRemote {
  implicit val codec: JsonValueCodec[ScalaAttachRemote] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaBuildTarget(
    scalaOrganization: String,
    scalaVersion: String,
    scalaBinaryVersion: String,
    platform: ScalaPlatform,
    jars: List[Uri],
    jvmBuildTarget: Option[JvmBuildTarget]
)

object ScalaBuildTarget {
  implicit val codec: JsonValueCodec[ScalaBuildTarget] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaDiagnostic(
    actions: Option[List[ScalaAction]]
)

object ScalaDiagnostic {
  implicit val codec: JsonValueCodec[ScalaDiagnostic] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaMainClass(
    @named("class")
    className: String,
    arguments: List[String],
    jvmOptions: List[String],
    environmentVariables: Option[List[String]]
)

object ScalaMainClass {
  implicit val codec: JsonValueCodec[ScalaMainClass] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaMainClassesItem(
    target: BuildTargetIdentifier,
    classes: List[ScalaMainClass]
)

object ScalaMainClassesItem {
  implicit val codec: JsonValueCodec[ScalaMainClassesItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaMainClassesParams(
    targets: List[BuildTargetIdentifier],
    originId: Option[String]
)

object ScalaMainClassesParams {
  implicit val codec: JsonValueCodec[ScalaMainClassesParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaMainClassesResult(
    items: List[ScalaMainClassesItem],
    originId: Option[String]
)

object ScalaMainClassesResult {
  implicit val codec: JsonValueCodec[ScalaMainClassesResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

sealed abstract class ScalaPlatform(val value: Int)
object ScalaPlatform {
  case object Jvm extends ScalaPlatform(1)
  case object Js extends ScalaPlatform(2)
  case object Native extends ScalaPlatform(3)

  implicit val codec: JsonValueCodec[ScalaPlatform] = new JsonValueCodec[ScalaPlatform] {
    def nullValue: ScalaPlatform = null
    def encodeValue(msg: ScalaPlatform, out: JsonWriter): Unit = out.writeVal(msg.value)
    def decodeValue(in: JsonReader, default: ScalaPlatform): ScalaPlatform = {
      in.readInt() match {
        case 1 => Jvm
        case 2 => Js
        case 3 => Native
        case n => in.decodeError(s"Unknown message type id for $n")
      }
    }
  }
}
final case class ScalaTestClassesItem(
    target: BuildTargetIdentifier,
    framework: Option[String],
    classes: List[String]
)

object ScalaTestClassesItem {
  implicit val codec: JsonValueCodec[ScalaTestClassesItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaTestClassesParams(
    targets: List[BuildTargetIdentifier],
    originId: Option[String]
)

object ScalaTestClassesParams {
  implicit val codec: JsonValueCodec[ScalaTestClassesParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaTestClassesResult(
    items: List[ScalaTestClassesItem]
)

object ScalaTestClassesResult {
  implicit val codec: JsonValueCodec[ScalaTestClassesResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaTestParams(
    testClasses: Option[List[ScalaTestClassesItem]],
    jvmOptions: Option[List[String]]
)

object ScalaTestParams {
  implicit val codec: JsonValueCodec[ScalaTestParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaTestSuiteSelection(
    className: String,
    tests: List[String]
)

object ScalaTestSuiteSelection {
  implicit val codec: JsonValueCodec[ScalaTestSuiteSelection] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaTestSuites(
    suites: List[ScalaTestSuiteSelection],
    jvmOptions: List[String],
    environmentVariables: List[String]
)

object ScalaTestSuites {
  implicit val codec: JsonValueCodec[ScalaTestSuites] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaTextEdit(
    range: Range,
    newText: String
)

object ScalaTextEdit {
  implicit val codec: JsonValueCodec[ScalaTextEdit] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalaWorkspaceEdit(
    changes: List[ScalaTextEdit]
)

object ScalaWorkspaceEdit {
  implicit val codec: JsonValueCodec[ScalaWorkspaceEdit] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalacOptionsItem(
    target: BuildTargetIdentifier,
    options: List[String],
    classpath: List[String],
    classDirectory: String
)

object ScalacOptionsItem {
  implicit val codec: JsonValueCodec[ScalacOptionsItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalacOptionsParams(
    targets: List[BuildTargetIdentifier]
)

object ScalacOptionsParams {
  implicit val codec: JsonValueCodec[ScalacOptionsParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ScalacOptionsResult(
    items: List[ScalacOptionsItem]
)

object ScalacOptionsResult {
  implicit val codec: JsonValueCodec[ScalacOptionsResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class SetCargoFeaturesParams(
    packageId: String,
    features: Set[String]
)

object SetCargoFeaturesParams {
  implicit val codec: JsonValueCodec[SetCargoFeaturesParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class SetCargoFeaturesResult(
    statusCode: StatusCode
)

object SetCargoFeaturesResult {
  implicit val codec: JsonValueCodec[SetCargoFeaturesResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class ShowMessageParams(
    `type`: MessageType,
    task: Option[TaskId],
    originId: Option[String],
    message: String
)

object ShowMessageParams {
  implicit val codec: JsonValueCodec[ShowMessageParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class SourceItem(
    uri: Uri,
    kind: SourceItemKind,
    generated: Boolean
)

object SourceItem {
  implicit val codec: JsonValueCodec[SourceItem] = JsonCodecMaker.makeWithRequiredCollectionFields
}

sealed abstract class SourceItemKind(val value: Int)
object SourceItemKind {
  case object File extends SourceItemKind(1)
  case object Directory extends SourceItemKind(2)

  implicit val codec: JsonValueCodec[SourceItemKind] = new JsonValueCodec[SourceItemKind] {
    def nullValue: SourceItemKind = null
    def encodeValue(msg: SourceItemKind, out: JsonWriter): Unit = out.writeVal(msg.value)
    def decodeValue(in: JsonReader, default: SourceItemKind): SourceItemKind = {
      in.readInt() match {
        case 1 => File
        case 2 => Directory
        case n => in.decodeError(s"Unknown message type id for $n")
      }
    }
  }
}
final case class SourcesItem(
    target: BuildTargetIdentifier,
    sources: List[SourceItem],
    roots: Option[List[Uri]]
)

object SourcesItem {
  implicit val codec: JsonValueCodec[SourcesItem] = JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class SourcesParams(
    targets: List[BuildTargetIdentifier]
)

object SourcesParams {
  implicit val codec: JsonValueCodec[SourcesParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class SourcesResult(
    items: List[SourcesItem]
)

object SourcesResult {
  implicit val codec: JsonValueCodec[SourcesResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

sealed abstract class StatusCode(val value: Int)
object StatusCode {
  case object Ok extends StatusCode(1)
  case object Error extends StatusCode(2)
  case object Cancelled extends StatusCode(3)

  implicit val codec: JsonValueCodec[StatusCode] = new JsonValueCodec[StatusCode] {
    def nullValue: StatusCode = null
    def encodeValue(msg: StatusCode, out: JsonWriter): Unit = out.writeVal(msg.value)
    def decodeValue(in: JsonReader, default: StatusCode): StatusCode = {
      in.readInt() match {
        case 1 => Ok
        case 2 => Error
        case 3 => Cancelled
        case n => in.decodeError(s"Unknown message type id for $n")
      }
    }
  }
}
object TaskFinishDataKind {
  val CompileReport = "compile-report"
  val TestFinish = "test-finish"
  val TestReport = "test-report"
}

final case class TaskFinishParams(
    taskId: TaskId,
    eventTime: Option[Long],
    message: Option[String],
    status: StatusCode,
    dataKind: Option[String],
    data: Option[RawJson]
)

object TaskFinishParams {
  implicit val codec: JsonValueCodec[TaskFinishParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class TaskId(
    id: String,
    parents: Option[List[String]]
)

object TaskId {
  implicit val codec: JsonValueCodec[TaskId] = JsonCodecMaker.makeWithRequiredCollectionFields
}

object TaskProgressDataKind {}

final case class TaskProgressParams(
    taskId: TaskId,
    eventTime: Option[Long],
    message: Option[String],
    total: Option[Long],
    progress: Option[Long],
    unit: Option[String],
    dataKind: Option[String],
    data: Option[RawJson]
)

object TaskProgressParams {
  implicit val codec: JsonValueCodec[TaskProgressParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

object TaskStartDataKind {
  val CompileTask = "compile-task"
  val TestStart = "test-start"
  val TestTask = "test-task"
}

final case class TaskStartParams(
    taskId: TaskId,
    eventTime: Option[Long],
    message: Option[String],
    dataKind: Option[String],
    data: Option[RawJson]
)

object TaskStartParams {
  implicit val codec: JsonValueCodec[TaskStartParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class TestFinish(
    displayName: String,
    message: Option[String],
    status: TestStatus,
    location: Option[Location],
    dataKind: Option[String],
    data: Option[RawJson]
)

object TestFinish {
  implicit val codec: JsonValueCodec[TestFinish] = JsonCodecMaker.makeWithRequiredCollectionFields
}

object TestFinishDataKind {}

final case class TestParams(
    targets: List[BuildTargetIdentifier],
    originId: Option[String],
    arguments: Option[List[String]],
    dataKind: Option[String],
    data: Option[RawJson]
)

object TestParams {
  implicit val codec: JsonValueCodec[TestParams] = JsonCodecMaker.makeWithRequiredCollectionFields
}

object TestParamsDataKind {
  val ScalaTest = "scala-test"
}

final case class TestProvider(
    languageIds: List[String]
)

object TestProvider {
  implicit val codec: JsonValueCodec[TestProvider] = JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class TestReport(
    originId: Option[String],
    target: BuildTargetIdentifier,
    passed: Int,
    failed: Int,
    ignored: Int,
    cancelled: Int,
    skipped: Int,
    time: Option[Long]
)

object TestReport {
  implicit val codec: JsonValueCodec[TestReport] = JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class TestResult(
    originId: Option[String],
    statusCode: StatusCode,
    dataKind: Option[String],
    data: Option[RawJson]
)

object TestResult {
  implicit val codec: JsonValueCodec[TestResult] = JsonCodecMaker.makeWithRequiredCollectionFields
}

object TestResultDataKind {}

final case class TestStart(
    displayName: String,
    location: Option[Location]
)

object TestStart {
  implicit val codec: JsonValueCodec[TestStart] = JsonCodecMaker.makeWithRequiredCollectionFields
}

sealed abstract class TestStatus(val value: Int)
object TestStatus {
  case object Passed extends TestStatus(1)
  case object Failed extends TestStatus(2)
  case object Ignored extends TestStatus(3)
  case object Cancelled extends TestStatus(4)
  case object Skipped extends TestStatus(5)

  implicit val codec: JsonValueCodec[TestStatus] = new JsonValueCodec[TestStatus] {
    def nullValue: TestStatus = null
    def encodeValue(msg: TestStatus, out: JsonWriter): Unit = out.writeVal(msg.value)
    def decodeValue(in: JsonReader, default: TestStatus): TestStatus = {
      in.readInt() match {
        case 1 => Passed
        case 2 => Failed
        case 3 => Ignored
        case 4 => Cancelled
        case 5 => Skipped
        case n => in.decodeError(s"Unknown message type id for $n")
      }
    }
  }
}
final case class TestTask(
    target: BuildTargetIdentifier
)

object TestTask {
  implicit val codec: JsonValueCodec[TestTask] = JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class TextDocumentIdentifier(
    uri: Uri
)

object TextDocumentIdentifier {
  implicit val codec: JsonValueCodec[TextDocumentIdentifier] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class WorkspaceBuildTargetsResult(
    targets: List[BuildTarget]
)

object WorkspaceBuildTargetsResult {
  implicit val codec: JsonValueCodec[WorkspaceBuildTargetsResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}
