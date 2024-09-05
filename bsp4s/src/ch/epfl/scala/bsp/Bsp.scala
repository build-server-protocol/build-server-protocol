package ch.epfl.scala.bsp

import ch.epfl.scala.util.CustomCodec

import java.net.{URI, URISyntaxException}

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.named
import com.github.plokhotnyuk.jsoniter_scala.core.JsonCodec
import com.github.plokhotnyuk.jsoniter_scala.core.JsonWriter
import com.github.plokhotnyuk.jsoniter_scala.core.JsonReader
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker
import jsonrpc4s.RawJson

object Bsp4s {
  val ProtocolVersion: String = "2.2.0"
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

/** Structure describing how to start a BSP server and the capabilities it supports.
  */
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
    languageIds: List[String],
    jvmCompileClasspathReceiver: Option[Boolean]
)

object BuildClientCapabilities {
  implicit val codec: JsonValueCodec[BuildClientCapabilities] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

/** The capabilities of the build server. Clients can use these capabilities to notify users what
  * BSP endpoints can and cannot be used and why.
  */
final case class BuildServerCapabilities(
    compileProvider: Option[CompileProvider],
    testProvider: Option[TestProvider],
    runProvider: Option[RunProvider],
    debugProvider: Option[DebugProvider],
    inverseSourcesProvider: Option[Boolean],
    wrappedSourcesProvider: Option[Boolean],
    dependencySourcesProvider: Option[Boolean],
    dependencyModulesProvider: Option[Boolean],
    resourcesProvider: Option[Boolean],
    outputPathsProvider: Option[Boolean],
    buildTargetChangedProvider: Option[Boolean],
    jvmRunEnvironmentProvider: Option[Boolean],
    jvmTestEnvironmentProvider: Option[Boolean],
    cargoFeaturesProvider: Option[Boolean],
    canReload: Option[Boolean],
    jvmCompileClasspathProvider: Option[Boolean]
)

object BuildServerCapabilities {
  implicit val codec: JsonValueCodec[BuildServerCapabilities] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

/** Build target contains metadata about an artifact (for example library, test, or binary
  * artifact). Using vocabulary of other build tools:
  *
  * * sbt: a build target is a combined project + config. Example: * a regular JVM project with main
  * and test configurations will have 2 build targets, one for main and one for test. * a single
  * configuration in a single project that contains both Java and Scala sources maps to one
  * BuildTarget. * a project with crossScalaVersions 2.11 and 2.12 containing main and test
  * configuration in each will have 4 build targets. * a Scala 2.11 and 2.12 cross-built project for
  * Scala.js and the JVM with main and test configurations will have 8 build targets. * Pants: a
  * pants target corresponds one-to-one with a BuildTarget * Bazel: a bazel target corresponds
  * one-to-one with a BuildTarget
  *
  * The general idea is that the BuildTarget data structure should contain only information that is
  * fast or cheap to compute.
  */
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

/** Clients can use these capabilities to notify users what BSP endpoints can and cannot be used and
  * why.
  */
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

/** The `BuildTargetEventKind` information can be used by clients to trigger reindexing or update
  * the user interface with the new information.
  */
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

/** A unique identifier for a target, can use any URI-compatible encoding as long as it is unique
  * within the workspace. Clients should not infer metadata out of the URI structure such as the
  * path or query parameters, use `BuildTarget` instead.
  */
final case class BuildTargetIdentifier(
    uri: Uri
)

object BuildTargetIdentifier {
  implicit val codec: JsonValueCodec[BuildTargetIdentifier] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

/** A list of predefined tags that can be used to categorize build targets.
  */
object BuildTargetTag {
  val Application = "application"
  val Benchmark = "benchmark"
  val IntegrationTest = "integration-test"
  val Library = "library"
  val Manual = "manual"
  val NoIde = "no-ide"
  val Test = "test"
}

final case class CancelRequestParams(
    id: Either[String, Int]
)

object CancelRequestParams {
  implicit val codec: JsonValueCodec[CancelRequestParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
  implicit val codecForEither: JsonValueCodec[Either[String, Int]] = CustomCodec.forEitherStringInt
}

/** `CargoBuildTarget` is a basic data structure that contains cargo-specific metadata.
  */
final case class CargoBuildTarget(
    edition: String,
    requiredFeatures: Set[String]
)

object CargoBuildTarget {
  implicit val codec: JsonValueCodec[CargoBuildTarget] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

/** **Unstable** (may change in future versions)
  */
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

/** Structure to capture a description for an error code.
  */
final case class CodeDescription(
    href: Uri
)

object CodeDescription {
  implicit val codec: JsonValueCodec[CodeDescription] =
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

/** The completion of a compilation task should be signalled with a `build/taskFinish` notification.
  * When the compilation unit is a build target, the notification's `dataKind` field must be
  * `compile-report` and the `data` field must include a `CompileReport` object:
  */
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

/** The beginning of a compilation unit may be signalled to the client with a `build/taskStart`
  * notification. When the compilation unit is a build target, the notification's `dataKind` field
  * must be "compile-task" and the `data` field must include a `CompileTask` object:
  */
final case class CompileTask(
    target: BuildTargetIdentifier
)

object CompileTask {
  implicit val codec: JsonValueCodec[CompileTask] = JsonCodecMaker.makeWithRequiredCollectionFields
}

/** `CppBuildTarget` is a basic data structure that contains c++-specific metadata, specifically
  * compiler reference.
  */
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

/** Diagnostic is defined as it is in the LSP.
  */
final case class Diagnostic(
    range: Range,
    severity: Option[DiagnosticSeverity],
    code: Option[Either[String, Int]],
    codeDescription: Option[CodeDescription],
    source: Option[String],
    message: String,
    tags: Option[List[Int]],
    relatedInformation: Option[List[DiagnosticRelatedInformation]],
    dataKind: Option[String],
    data: Option[RawJson]
)

object Diagnostic {
  implicit val codec: JsonValueCodec[Diagnostic] = JsonCodecMaker.makeWithRequiredCollectionFields
  implicit val codecForEither: JsonValueCodec[Either[String, Int]] = CustomCodec.forEitherStringInt
}

object DiagnosticDataKind {
  val Scala = "scala"
}

/** Represents a related message and source code location for a diagnostic. This should be used to
  * point to code locations that cause or are related to a diagnostics, e.g when duplicating a
  * symbol in a scope.
  */
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

final case class WrappedSourcesParams(
    targets: List[BuildTargetIdentifier]
)

object WrappedSourcesParams {
  implicit val codec: JsonValueCodec[WrappedSourcesParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class WrappedSourceItem(
    uri: String,
    generatedUri: String,
    topWrapper: String,
    bottomWrapper: String
)

object WrappedSourceItem {
  implicit val codec: JsonValueCodec[WrappedSourceItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class WrappedSourcesItem(
    target: BuildTargetIdentifier,
    sources: List[WrappedSourceItem]
)

object WrappedSourcesItem {
  implicit val codec: JsonValueCodec[WrappedSourcesItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class WrappedSourcesResult(
    items: List[WrappedSourcesItem]
)

object WrappedSourcesResult {
  implicit val codec: JsonValueCodec[WrappedSourcesResult] =
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

/** `JvmBuildTarget` is a basic data structure that contains jvm-specific metadata, specifically JDK
  * reference.
  */
final case class JvmBuildTarget(
    javaHome: Option[Uri],
    javaVersion: Option[String]
)

object JvmBuildTarget {
  implicit val codec: JsonValueCodec[JvmBuildTarget] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class JvmCompileClasspathItem(
    target: BuildTargetIdentifier,
    classpath: List[String]
)

object JvmCompileClasspathItem {
  implicit val codec: JsonValueCodec[JvmCompileClasspathItem] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class JvmCompileClasspathParams(
    targets: List[BuildTargetIdentifier]
)

object JvmCompileClasspathParams {
  implicit val codec: JsonValueCodec[JvmCompileClasspathParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class JvmCompileClasspathResult(
    items: List[JvmCompileClasspathItem]
)

object JvmCompileClasspathResult {
  implicit val codec: JsonValueCodec[JvmCompileClasspathResult] =
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

/** `JvmSourceItemData` contains JVM-specific metadata for a source item.
  */
final case class JvmSourceItemData(
    packageName: Option[String]
)

object JvmSourceItemData {
  implicit val codec: JsonValueCodec[JvmSourceItemData] =
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

/** `MavenDependencyModule` is a basic data structure that contains maven-like metadata. This
  * metadata is embedded in the `data: Option[Json]` field of the `DependencyModule` definition,
  * when the `dataKind` field contains "maven".
  */
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

/** **Unstable** (may change in future versions)
  */
final case class PrintParams(
    originId: String,
    task: Option[TaskId],
    message: String
)

object PrintParams {
  implicit val codec: JsonValueCodec[PrintParams] = JsonCodecMaker.makeWithRequiredCollectionFields
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

/** `PythonBuildTarget` is a basic data structure that contains Python-specific metadata,
  * specifically the interpreter reference and the Python version.
  */
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

/** **Unstable** (may change in future versions)
  */
final case class ReadParams(
    originId: String,
    task: Option[TaskId],
    message: String
)

object ReadParams {
  implicit val codec: JsonValueCodec[ReadParams] = JsonCodecMaker.makeWithRequiredCollectionFields
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
    environmentVariables: Option[Map[String, String]],
    workingDirectory: Option[Uri],
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

/** Crate types (`lib`, `rlib`, `dylib`, `cdylib`, `staticlib`) are listed for `lib` and `example`
  * target kinds. For other target kinds `bin` crate type is listed.
  */
sealed abstract class RustCrateType(val value: Int)
object RustCrateType {
  case object Bin extends RustCrateType(1)
  case object Lib extends RustCrateType(2)
  case object Rlib extends RustCrateType(3)
  case object Dylib extends RustCrateType(4)
  case object Cdylib extends RustCrateType(5)
  case object Staticlib extends RustCrateType(6)
  case object ProcMacro extends RustCrateType(7)
  case object Unknown extends RustCrateType(8)

  implicit val codec: JsonValueCodec[RustCrateType] = new JsonValueCodec[RustCrateType] {
    def nullValue: RustCrateType = null
    def encodeValue(msg: RustCrateType, out: JsonWriter): Unit = out.writeVal(msg.value)
    def decodeValue(in: JsonReader, default: RustCrateType): RustCrateType = {
      in.readInt() match {
        case 1 => Bin
        case 2 => Lib
        case 3 => Rlib
        case 4 => Dylib
        case 5 => Cdylib
        case 6 => Staticlib
        case 7 => ProcMacro
        case 8 => Unknown
        case n => in.decodeError(s"Unknown message type id for $n")
      }
    }
  }
}

object RustDepKind {
  val Build = "build"
  val Dev = "dev"
  val Normal = "normal"
  val Unclassified = "unclassified"
}

final case class RustDepKindInfo(
    kind: String,
    target: Option[String]
)

object RustDepKindInfo {
  implicit val codec: JsonValueCodec[RustDepKindInfo] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

final case class RustDependency(
    pkg: String,
    name: Option[String],
    depKinds: Option[List[RustDepKindInfo]]
)

object RustDependency {
  implicit val codec: JsonValueCodec[RustDependency] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

/** The Rust edition.
  */
object RustEdition {
  val E2015 = "2015"
  val E2018 = "2018"
  val E2021 = "2021"
}

/** A `crate` is the smallest amount of code that the Rust compiler considers at a time. It can come
  * in one of two forms: a binary crate or a library crate. `Binary crates` are programs you can
  * compile to an executable that you can run, such as a command-line program or a server. Each must
  * have a function called main that defines what happens when the executable runs. `Library crates`
  * don't have a main function, and they don't compile to an executable. Instead, they define
  * functionality intended to be shared with multiple projects.
  *
  * A `package` is a bundle of one or more crates that provides a set of functionality. It contains
  * a Cargo.toml file that describes how to build those crates. A package can contain many binary
  * crates, but at most only one library crate. However, it must contain at least one crate, whether
  * that's a library or binary crate.
  */
final case class RustPackage(
    id: String,
    rootUrl: Uri,
    name: String,
    version: String,
    origin: String,
    edition: String,
    source: Option[String],
    resolvedTargets: List[RustTarget],
    allTargets: List[RustTarget],
    features: Map[String, Set[String]],
    enabledFeatures: Set[String],
    cfgOptions: Option[Map[String, List[String]]],
    env: Option[Map[String, String]],
    outDirUrl: Option[Uri],
    procMacroArtifact: Option[Uri]
)

object RustPackage {
  implicit val codec: JsonValueCodec[RustPackage] = JsonCodecMaker.makeWithRequiredCollectionFields
}

object RustPackageOrigin {
  val Dependency = "dependency"
  val Stdlib = "stdlib"
  val StdlibDependency = "stdlib-dependency"
  val Workspace = "workspace"
}

final case class RustRawDependency(
    name: String,
    rename: Option[String],
    kind: Option[String],
    target: Option[String],
    optional: Boolean,
    usesDefaultFeatures: Boolean,
    features: Set[String]
)

object RustRawDependency {
  implicit val codec: JsonValueCodec[RustRawDependency] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

/** `RustTarget` contains data of the target as defined in Cargo metadata.
  */
final case class RustTarget(
    name: String,
    crateRootUrl: Uri,
    kind: RustTargetKind,
    crateTypes: Option[List[RustCrateType]],
    edition: String,
    doctest: Boolean,
    requiredFeatures: Option[Set[String]]
)

object RustTarget {
  implicit val codec: JsonValueCodec[RustTarget] = JsonCodecMaker.makeWithRequiredCollectionFields
}

sealed abstract class RustTargetKind(val value: Int)
object RustTargetKind {
  case object Lib extends RustTargetKind(1)
  case object Bin extends RustTargetKind(2)
  case object Test extends RustTargetKind(3)
  case object Example extends RustTargetKind(4)
  case object Bench extends RustTargetKind(5)
  case object CustomBuild extends RustTargetKind(6)
  case object Unknown extends RustTargetKind(7)

  implicit val codec: JsonValueCodec[RustTargetKind] = new JsonValueCodec[RustTargetKind] {
    def nullValue: RustTargetKind = null
    def encodeValue(msg: RustTargetKind, out: JsonWriter): Unit = out.writeVal(msg.value)
    def decodeValue(in: JsonReader, default: RustTargetKind): RustTargetKind = {
      in.readInt() match {
        case 1 => Lib
        case 2 => Bin
        case 3 => Test
        case 4 => Example
        case 5 => Bench
        case 6 => CustomBuild
        case 7 => Unknown
        case n => in.decodeError(s"Unknown message type id for $n")
      }
    }
  }
}

/** **Unstable** (may change in future versions)
  */
final case class RustWorkspaceParams(
    targets: List[BuildTargetIdentifier]
)

object RustWorkspaceParams {
  implicit val codec: JsonValueCodec[RustWorkspaceParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

/** **Unstable** (may change in future versions)
  */
final case class RustWorkspaceResult(
    packages: List[RustPackage],
    rawDependencies: Map[String, List[RustRawDependency]],
    dependencies: Map[String, List[RustDependency]],
    resolvedTargets: List[BuildTargetIdentifier]
)

object RustWorkspaceResult {
  implicit val codec: JsonValueCodec[RustWorkspaceResult] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

/** `SbtBuildTarget` is a basic data structure that contains sbt-specific metadata for providing
  * editor support for sbt build files.
  *
  * For example, say we have a project in `/foo/bar` defining projects `A` and `B` and two meta
  * builds `M1` (defined in `/foo/bar/project`) and `M2` (defined in `/foo/bar/project/project`).
  *
  * The sbt build target for `M1` will have `A` and `B` as the defined targets and `M2` as the
  * parent. Similarly, the sbt build target for `M2` will have `M1` as the defined target and no
  * parent.
  *
  * Clients can use this information to reconstruct the tree of sbt meta builds. The `parent`
  * information can be defined from `children` but it's provided by the server to simplify the data
  * processing on the client side.
  */
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

/** A Scala action represents a change that can be performed in code. See also [LSP: Code Action
  * Request](https://microsoft.github.io/language-server-protocol/specifications/lsp/3.17/specification/#textDocument_codeAction).
  *
  * **Note**: In LSP, `CodeAction` appears only as a response to a `textDocument/codeAction`
  * request, whereas ScalaAction is intended to be returned as `Diagnostics.data.actions`.
  */
final case class ScalaAction(
    title: String,
    description: Option[String],
    edit: Option[ScalaWorkspaceEdit]
)

object ScalaAction {
  implicit val codec: JsonValueCodec[ScalaAction] = JsonCodecMaker.makeWithRequiredCollectionFields
}

/** The debug session will connect to a running process. The DAP client will send the port of the
  * running process later.
  */
final case class ScalaAttachRemote(
)

object ScalaAttachRemote {
  implicit val codec: JsonValueCodec[ScalaAttachRemote] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

/** `ScalaBuildTarget` is a basic data structure that contains scala-specific metadata for compiling
  * a target containing Scala sources.
  */
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

/** `ScalaDiagnostic` is a data structure that contains Scala-specific metadata generated by Scala
  * compilation.
  */
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

/** `ScalaTestParams` contains scala-specific metadata for testing Scala targets.
  */
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

/** A textual edit applicable to a text document.
  */
final case class ScalaTextEdit(
    range: Range,
    newText: String
)

object ScalaTextEdit {
  implicit val codec: JsonValueCodec[ScalaTextEdit] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

/** A workspace edit represents changes to many resources managed in the workspace.
  */
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

/** **Unstable** (may change in future versions)
  */
final case class SetCargoFeaturesParams(
    packageId: String,
    features: Set[String]
)

object SetCargoFeaturesParams {
  implicit val codec: JsonValueCodec[SetCargoFeaturesParams] =
    JsonCodecMaker.makeWithRequiredCollectionFields
}

/** **Unstable** (may change in future versions)
  */
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
    generated: Boolean,
    dataKind: Option[String],
    data: Option[RawJson]
)

object SourceItem {
  implicit val codec: JsonValueCodec[SourceItem] = JsonCodecMaker.makeWithRequiredCollectionFields
}

object SourceItemDataKind {
  val Jvm = "jvm"
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

/** Included in notifications of tasks or requests to signal the completion state.
  */
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

/** Task finish notifications may contain an arbitrary interface in their `data` field. The kind of
  * interface that is contained in a notification must be specified in the `dataKind` field.
  *
  * There are predefined kinds of objects for compile and test tasks, as described in
  * [[bsp#BuildTargetCompile]] and [[bsp#BuildTargetTest]]
  */
object TaskFinishDataKind {
  val CompileReport = "compile-report"
  val TestFinish = "test-finish"
  val TestReport = "test-report"
}

final case class TaskFinishParams(
    taskId: TaskId,
    originId: Option[String],
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

/** The Task Id allows clients to _uniquely_ identify a BSP task and establish a client-parent
  * relationship with another task id.
  */
final case class TaskId(
    id: String,
    parents: Option[List[String]]
)

object TaskId {
  implicit val codec: JsonValueCodec[TaskId] = JsonCodecMaker.makeWithRequiredCollectionFields
}

/** Task progress notifications may contain an arbitrary interface in their `data` field. The kind
  * of interface that is contained in a notification must be specified in the `dataKind` field.
  */
object TaskProgressDataKind {}

final case class TaskProgressParams(
    taskId: TaskId,
    originId: Option[String],
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

/** Task start notifications may contain an arbitrary interface in their `data` field. The kind of
  * interface that is contained in a notification must be specified in the `dataKind` field.
  *
  * There are predefined kinds of objects for compile and test tasks, as described in
  * [[bsp#BuildTargetCompile]] and [[bsp#BuildTargetTest]]
  */
object TaskStartDataKind {
  val CompileTask = "compile-task"
  val TestStart = "test-start"
  val TestTask = "test-task"
}

final case class TaskStartParams(
    taskId: TaskId,
    originId: Option[String],
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
    environmentVariables: Option[Map[String, String]],
    workingDirectory: Option[Uri],
    dataKind: Option[String],
    data: Option[RawJson]
)

object TestParams {
  implicit val codec: JsonValueCodec[TestParams] = JsonCodecMaker.makeWithRequiredCollectionFields
}

object TestParamsDataKind {
  val ScalaTest = "scala-test"
  val ScalaTestSuites = "scala-test-suites"
  val ScalaTestSuitesSelection = "scala-test-suites-selection"
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

/** The beginning of a testing unit may be signalled to the client with a `build/taskStart`
  * notification. When the testing unit is a build target, the notification's `dataKind` field must
  * be `test-task` and the `data` field must include a `TestTask` object.
  */
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
