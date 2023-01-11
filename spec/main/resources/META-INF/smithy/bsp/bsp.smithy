$version: "2"

namespace bsp

use jsonrpc#jsonRPC
use jsonrpc#enumKind
use jsonrpc#jsonRequest
use jsonrpc#jsonNotification

@jsonRPC
service BuildServer {
  operations: [
    InitializeBuild,
    OnBuildInitialized,
    ShutdownBuild,
    OnBuildShutdown
  ]
}

/// Like the language server protocol, the initialize request is sent as the first request from the client to the server.
/// If the server receives a request or notification before the initialize request it should act as follows:
///
/// * For a request the response should be an error with code: -32002. The message can be picked by the server.
/// * Notifications should be dropped, except for the exit notification. This will allow the exit of a server without an initialize request.
///
/// Until the server has responded to the initialize request with an InitializeBuildResult, the client must not send any additional
/// requests or notifications to the server.
@jsonRequest("build/initialize")
operation InitializeBuild {
  input: InitializeBuildParams
  output: InitializeBuildResults
}

@jsonNotification("build/initialized")
operation OnBuildInitialized {
  input: InitializeBuildParams
}

/// Like the language server protocol, the shutdown build request is sent from the client to the server. It asks the server to shut down,
/// but to not exit (otherwise the response might not be delivered correctly to the client). There is a separate exit notification that
/// asks the server to exit.
@jsonRequest("build/shutdown")
operation ShutdownBuild {
}

@jsonNotification("build/exit")
/// Like the language server protocol, a notification to ask the server to exit its process. The server should exit with success code 0
/// if the shutdown request has been received before; otherwise with error code 1.
operation OnBuildShutdown {
}

@jsonRPC
service BuildClient {
  operations: [
    ShowMessage,
    LogMessage,
    PublishDiagnostics
  ]
}

/// The show message notification is sent from a server to a client to ask the client to display a particular message in the user interface.
///
/// A build/showMessage notification is similar to LSP's window/showMessage, except for a few additions like id and originId.
@jsonNotification("build/showMessage")
operation ShowMessage {
  input: ShowMessageParams
}

/// The log message notification is sent from a server to a client to ask the client to log a particular message in its console.
///
/// A build/logMessage notification is similar to LSP's window/logMessage, except for a few additions like id and originId.
@jsonNotification("build/logMessage")
operation LogMessage {
  input: LogMessageParams
}

/// The Diagnostics notification are sent from the server to the client to signal results of validation runs.
///
/// Diagnostic is defined as it is in the LSP
///
/// When reset is true, the client must clean all previous diagnostics associated with the same textDocument and
/// buildTarget and set instead the diagnostics in the request. This is the same behaviour as PublishDiagnosticsParams
/// in the LSP. When reset is false, the diagnostics are added to the last active diagnostics, allowing build tools to
/// stream diagnostics to the client.
///
/// It is the server's responsibility to manage the lifetime of the diagnostics by using the appropriate value in the reset field.
///  Clients generate new diagnostics by calling any BSP endpoint that triggers a buildTarget/compile, such as buildTarget/compile, buildTarget/test and buildTarget/run.
@jsonNotification("build/publishDiagnostics")
operation PublishDiagnostics {
  input: PublishDiagnosticsParams
}


///  A resource identifier that is a valid URI according
/// to rfc3986: * https://tools.ietf.org/html/rfc3986
string URI

list URIs {
  member: URI
}

/// Represents an arbitrary piece of data, in Json format
document Json

/// Represents the identifier of a BSP request.
string RequestId

/// A unique identifier for a target, can use any URI-compatible encoding as long as it is unique within the workspace.
/// Clients should not infer metadata out of the URI structure such as the path or query parameters, use BuildTarget instead.
structure BuildTargetIdentifier {
  /// The target’s Uri
  uri: URI
}

list BuildTargetIdentifiers {
  member: BuildTargetIdentifier
}

/// Build target contains metadata about an artifact (for example library, test, or binary artifact). Using vocabulary of other build tools:
///
/// * sbt: a build target is a combined project + config. Example:
///   * a regular JVM project with main and test configurations will have 2 build targets, one for main and one for test.
///   * a single configuration in a single project that contains both Java and Scala sources maps to one BuildTarget.
///   * a project with crossScalaVersions 2.11 and 2.12 containing main and test configuration in each will have 4 build targets.
///   * a Scala 2.11 and 2.12 cross-built project for Scala.js and the JVM with main and test configurations will have 8 build targets.
/// * Pants: a pants target corresponds one-to-one with a BuildTarget
/// * Bazel: a bazel target corresponds one-to-one with a BuildTarget
///
/// The general idea is that the BuildTarget data structure should contain only information that is fast or cheap to compute.
structure BuildTarget {
  /// The target’s unique identifier
  @required
  id: BuildTargetIdentifier

  /// A human readable name for this target.
  /// May be presented in the user interface.
  /// Should be unique if possible.
  /// The id.uri is used if None.
  displayName: String

  /// The directory where this target belongs to. Multiple build targets are allowed to map
  /// to the same base directory, and a build target is not required to have a base directory.
  /// A base directory does not determine the sources of a target, see buildTarget/sources. */
  baseDirectory: URI

  /// Free-form string tags to categorize or label this build target.
  /// For example, can be used by the client to:
  /// - customize how the target should be translated into the client's project model.
  /// - group together different but related targets in the user interface.
  /// - display icons or colors in the user interface.
  /// Pre-defined tags are listed in `BuildTargetTag` but clients and servers
  /// are free to define new tags for custom purposes.
  tags: BuildTargetTags

  /// The direct upstream build target dependencies of this build target
  dependencies: BuildTargetIdentifiers

  /// Kind of data to expect in the `data` field. If this field is not set, the kind of data is not specified.
  dataKind: BuildTargetDataKind

  /// Language-specific metadata about this target.
  /// See ScalaBuildTarget as an example.
  data: Json
}

@enumKind("open")
enum BuildTargetDataKind {
  /// The `data` field contains a `ScalaBuildTarget` object
  SCALA = "scala"

  /// The `data` field contains a `SbtBuildTarget` object.
  SBT = "sbt"
}

list StringList {
  member: String
}

structure BuildTargetCapabilities {
  /// This target can be compiled by the BSP server.
  @required
  canCompile: Boolean
  /// This target can be tested by the BSP server.
  @required
  canTest: Boolean
  /// This target can be run by the BSP server.
  @required
  canRun: Boolean
  /// This target can be debugged by the BSP server.
  @required
  canDebug: Boolean
}


@enumKind("open")
enum BuildTargetTag {
  /// Target contains re-usable functionality for downstream targets. May have any
  /// combination of capabilities.
  LIBRARY = "library"

  /// Target contains source code for producing any kind of application, may have
  /// but does not require the `canRun` capability.
  APPLICATION = "application"

  /// Target contains source code for testing purposes, may have but does not
  /// require the `canTest` capability.
  TEST = "test"

  /// Target contains source code for integration testing purposes, may have
  /// but does not require the `canTest` capability.
  /// The difference between "test" and "integration-test" is that
  /// integration tests traditionally run slower compared to normal tests
  /// and require more computing resources to execute.
  INTEGRATION_TEST = "integration-test"

  /// Target contains source code to measure performance of a program, may have
  /// but does not require the `canRun` build target capability.
  BENCHMARK = "benchmark"

  /// Target should be ignored by IDEs.
  NO_IDE = "no-ide"

  /// Actions on the target such as build and test should only be invoked manually
  /// and explicitly. For example, triggering a build on all targets in the workspace
  /// should by default not include this target.
  ///
  /// The original motivation to add the "manual" tag comes from a similar functionality
  /// that exists in Bazel, where targets with this tag have to be specified explicitly
  /// on the command line.
  ///
  MANUAL = "manual"
}

list BuildTargetTags {
  member: BuildTargetTag
}

/// The Task Id allows clients to uniquely identify a BSP task and establish a client-parent relationship with another task id.
structure TaskId {
  /// A unique identifier
  id: Identifier

  // The parent task ids, if any. A non-empty parents field means
  // this task is a sub-task of every parent task id. The child-parent
  // relationship of tasks makes it possible to render tasks in
  // a tree-like user interface or inspect what caused a certain task
  // execution.
  parents: Identifiers
}

string Identifier
list Identifiers {
  member: Identifier
}

/// Included in notifications of tasks or requests to signal the completion state.
@enumKind("open")
intEnum StatusCode {
  /// Execution was successful.
  OK = 1
  /// Execution failed.
  ERROR = 2
  /// Execution was cancelled.
  CANCELLED = 3
}

structure JvmBuildTarget {
  @required
  javaHome: String
  @required
  javaVersion: String
}

structure ScalaBuildTarget {
  @required
  scalaOrganization: String

  @required
  scalaVersion: String

  @required
  scalaBinaryVersion: String

  @required
  platform: ScalaPlatform

  @required
  jars: URIs

  jvmBuildTarget: JvmBuildTarget
}

@enumKind("closed")
intEnum ScalaPlatform {
  JVM = 1
  JS = 2
  NATIVE = 3
}


structure InitializeBuildParams {
  /// Name of the client
  @required
  displayName: String

  /// The version of the client
  @required
  version: String

  /// The BSP version that the client speaks
  @required
  bspVersion: String

  /// The rootUri of the workspace
  @required
  rootUri: URI

  /// The capabilities of the client
  @required
  capabilities: BuildClientCapabilities

  /// Additional metadata about the client
  data: Json
}

structure BuildClientCapabilities {
  /// The languages that this client supports.
  /// The ID strings for each language is defined in the LSP.
  /// The server must never respond with build targets for other
  /// languages than those that appear in this list.
  @required
  languageIds: LanguageIds
}

@enumKind("open")
enum LanguageId {
  SCALA = "scala"
  JAVA = "java"
}

list LanguageIds {
  member: LanguageId
}

structure InitializeBuildResults {
  /// Name of the server
  @required
  displayName: String

  /// The version of the server
  @required
  version: String

  /// The BSP version that the server speaks
  @required
  bspVersion: String

  /// The capabilities of the build server
  @required
  capabilities: BuildServerCapabilities

  /// Additional metadata about the server
  data: Json
}

structure BuildServerCapabilities {
  /// The languages the server supports compilation via method buildTarget/compile.
  compileProvider: CompileProvider

  /// The languages the server supports test execution via method buildTarget/test.
  testProvider: TestProvider

  /// The languages the server supports run via method buildTarget/run.
  runProvider: RunProvider

  /// The languages the server supports debugging via method debugSession/start.
  debugProvider: DebugProvider

  /// The server can provide a list of targets that contain a
  /// * single text document via the method buildTarget/inverseSources
  inverseSourcesProvider: Boolean = false

  /// The server provides sources for library dependencies
  ///  via method buildTarget/dependencySources
  dependencySourcesProvider: Boolean = false

  /// The server can provide a list of dependency modules (libraries with meta information)
  /// via method buildTarget/dependencyModules
  dependencyModulesProvider: Boolean = false

  /// The server provides all the resource dependencies
  /// via method buildTarget/resources
  resourcesProvider: Boolean = false

  /// The server provides all output paths
  /// via method buildTarget/outputPaths
  outputPathsProvider: Boolean = false

  /// The server sends notifications to the client on build
  /// target change events via buildTarget/didChange
  buildTargetChangedProvider: Boolean = false

  /// The server can respond to `buildTarget/jvmRunEnvironment` requests with the
  /// necessary information required to launch a Java process to run a main class.
  jvmRunEnvironmentProvider: Boolean = false

  /// The server can respond to `buildTarget/jvmTestEnvironment` requests with the
  /// necessary information required to launch a Java process for testing or
  //  debugging.
  jvmTestEnvironmentProvider: Boolean = false

  /// Reloading the build state through workspace/reload is supported
  canReload: Boolean = false

}

@mixin
structure LanguageProvider {
  @required
  languageIds: LanguageIds
}

structure CompileProvider with [LanguageProvider] {}
structure RunProvider with [LanguageProvider] {}
structure DebugProvider with [LanguageProvider] {}
structure TestProvider with [LanguageProvider] {}

structure InitializedBuildParams {
}

@mixin
structure MessageParams {
  /// the message type.
  @required
  type: MessageType

  /// The task id if any.
  task: TaskId

  /// The request id that originated this notification.
  /// The originId field helps clients know which request originated a notification in case several requests are handled by the
  /// client at the same time. It will only be populated if the client defined it in the request that triggered this notification.
  originId: RequestId

  /// The actual message.
  @required
  message: String
}

structure ShowMessageParams with [MessageParams] {}
structure LogMessageParams with [MessageParams] {}

@enumKind("open")
intEnum MessageType {
  /// An error message.
  ERROR = 1
  /// A warning message.
  WARNING = 2
  /// An information message.
  INFO = 3
  /// A log message.
  LOG = 4
}

structure PublishDiagnosticsParams {
  /// The document where the diagnostics are published.
  @required
  textDocument: TextDocumentIdentifier

  /// The build target where the diagnostics origin.
  /// It is valid for one text document to belong to multiple
  /// build targets, for example sources that are compiled against multiple
  // platforms (JVM, JavaScript).
  @required
  buildTarget: BuildTargetIdentifier

  /// The request id that originated this notification.
  originId: RequestId

  /// The diagnostics to be published by the client.
  @required
  diagnostics: Diagnostics

  /// Whether the client should clear the previous diagnostics
  /// mapped to the same `textDocument` and `buildTarget`.
  @required
  reset: Boolean
}

structure Diagnostic {
	/// The range at which the message applies.
  @required
	range: Range

	/// The diagnostic's severity. Can be omitted. If omitted it is up to the
	/// client to interpret diagnostics as error, warning, info or hint.
	severity: DiagnosticSeverity

  /// The diagnostic's code, which might appear in the user interface.
	code: Code

	/// An optional property to describe the error code.
	codeDescription: CodeDescription

	/// A human-readable string describing the source of this
	///diagnostic, e.g. 'typescript' or 'super lint'.
	source: String

	/// The diagnostic's message.
  @required
	message: String

	// Additional metadata about the diagnostic.
	tags: DiagnosticTags

	/// An array of related diagnostic information, e.g. when symbol-names within
	/// a scope collide all definitions can be marked via this property.
	relatedInformation: DiagnosticRelatedInformation

	/// A data entry field that is preserved between a `textDocument/publishDiagnostics` notification
  // and a `textDocument/codeAction` request.
	data: Json
}

structure Position {
  @required
  line: Integer
  @required
  character: Integer
}

structure Range {
  @required
  start: Position
  @required
  end: Position
}

structure Location {
	uri: URI
	range: Range
}

structure TextDocumentIdentifier {
	///  The text document's URI.
	uri: URI
}

list Diagnostics {
  member: Diagnostic
}

@enumKind("open")
intEnum DiagnosticSeverity {
  ERROR = 1
  WARNING = 2
  INFORMATION = 3
  HINT = 4
}

union Code {
  integer: Integer,
  string: String
}

 /// Structure to capture a description for an error code.
structure CodeDescription {
	/// An URI to open with more information about the diagnostic error.
	href: URI
}

@enumKind("open")
intEnum DiagnosticTag {
	 /// Unused or unnecessary code.
	 ///
	 /// Clients are allowed to render diagnostics with this tag faded out instead of having an error squiggle.
  UNNECESSARY = 1

	/// Deprecated or obsolete code.
	///
	/// Clients are allowed to rendered diagnostics with this tag strike through.
	DEPRECATED = 2
}

list DiagnosticTags {
  member: DiagnosticTag
}

/// Represents a related message and source code location for a diagnostic.
/// This should be used to point to code locations that cause or are related to
/// a diagnostics, e.g when duplicating a symbol in a scope.
structure  DiagnosticRelatedInformation {
	 /// The location of this related diagnostic information.
	location: Location
	 /// The message of this related diagnostic information.
	message: String
}

list DiagnosticRelatedInformationList {
  member: DiagnosticRelatedInformation
}
