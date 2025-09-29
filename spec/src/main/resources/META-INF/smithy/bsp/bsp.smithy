$version: "2"

namespace bsp

use traits#data
use traits#dataKind
use traits#enumKind
use traits#jsonNotification
use traits#jsonRPC
use traits#jsonRequest
use traits#untaggedUnion

/// An integer is a 32-bit signed integer ranging from -2^31 to (2^31)-1 (inclusive).
integer Integer

/// A long is a 64-bit signed integer ranging from -2^63 to (2^63)-1 (inclusive).
long Long

@jsonRPC
service BuildClient {
    operations: [
        OnBuildShowMessage
        OnBuildLogMessage
        OnBuildPublishDiagnostics
        OnBuildTargetDidChange
        OnBuildTaskStart
        OnBuildTaskProgress
        OnBuildTaskFinish
        OnRunPrintStdout
        OnRunPrintStderr
    ]
}

@jsonRPC
service BuildServer {
    operations: [
        BuildInitialize
        OnBuildInitialized
        BuildShutdown
        OnBuildExit
        WorkspaceBuildTargets
        WorkspaceReload
        BuildTargetSources
        BuildTargetInverseSources
        BuildTargetWrappedSources
        BuildTargetDependencySources
        BuildTargetDependencyModules
        BuildTargetResources
        BuildTargetOutputPaths
        BuildTargetCompile
        BuildTargetRun
        BuildTargetTest
        DebugSessionStart
        BuildTargetCleanCache
        OnRunReadStdin
    ]
}

/// Build target contains metadata about an artifact (for example library, test, or binary artifact). Using vocabulary of other build tools:
///
/// * sbt: a build target is a combined project + config. Example:
/// * a regular JVM project with main and test configurations will have 2 build targets, one for main and one for test.
/// * a single configuration in a single project that contains both Java and Scala sources maps to one BuildTarget.
/// * a project with crossScalaVersions 2.11 and 2.12 containing main and test configuration in each will have 4 build targets.
/// * a Scala 2.11 and 2.12 cross-built project for Scala.js and the JVM with main and test configurations will have 8 build targets.
/// * Pants: a pants target corresponds one-to-one with a BuildTarget
/// * Bazel: a bazel target corresponds one-to-one with a BuildTarget
///
/// The general idea is that the BuildTarget data structure should contain only information that is fast or cheap to compute.
structure BuildTarget {
    /// The target's unique identifier
    @required
    id: BuildTargetIdentifier
    /// A human readable name for this target.
    /// May be presented in the user interface.
    /// Should be unique if possible.
    /// The id.uri is used if None.
    displayName: String
    /// The directory where this target belongs to. Multiple build targets are allowed to map
    /// to the same base directory, and a build target is not required to have a base directory.
    /// A base directory does not determine the sources of a target, see buildTarget/sources.
    baseDirectory: URI
    /// Free-form string tags to categorize or label this build target.
    /// For example, can be used by the client to:
    /// - customize how the target should be translated into the client's project model.
    /// - group together different but related targets in the user interface.
    /// - display icons or colors in the user interface.
    /// Pre-defined tags are listed in `BuildTargetTag` but clients and servers
    /// are free to define new tags for custom purposes.
    @required
    tags: BuildTargetTags
    /// The set of languages that this target contains.
    /// The ID string for each language is defined in the LSP.
    @required
    languageIds: LanguageIds
    /// The direct upstream build target dependencies of this build target
    @required
    dependencies: BuildTargetIdentifiers
    /// The capabilities of this build target.
    @required
    capabilities: BuildTargetCapabilities
    /// Language-specific metadata about this target.
    /// See ScalaBuildTarget as an example.
    data: BuildTargetData
}

/// A list of predefined tags that can be used to categorize build targets.
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
    /// The original motivation to add the "manual" tag comes from a similar functionality
    /// that exists in Bazel, where targets with this tag have to be specified explicitly
    /// on the command line.
    MANUAL = "manual"
}

list BuildTargetTags {
    member: BuildTargetTag
}

/// Clients can use these capabilities to notify users what BSP endpoints can and
/// cannot be used and why.
structure BuildTargetCapabilities {
    /// This target can be compiled by the BSP server.
    canCompile: Boolean
    /// This target can be tested by the BSP server.
    canTest: Boolean
    /// This target can be run by the BSP server.
    canRun: Boolean
    /// This target can be debugged by the BSP server.
    canDebug: Boolean
}

/// A unique identifier for a target, can use any URI-compatible encoding as long as it is unique within the workspace.
/// Clients should not infer metadata out of the URI structure such as the path or query parameters, use `BuildTarget` instead.
structure BuildTargetIdentifier {
    /// The target's Uri
    @required
    uri: URI
}

/// The Task Id allows clients to _uniquely_ identify a BSP task and establish a client-parent relationship with another task id.
structure TaskId {
    /// A unique identifier
    @required
    id: Identifier
    /// The parent task ids, if any. A non-empty parents field means
    /// this task is a sub-task of every parent task id. The child-parent
    /// relationship of tasks makes it possible to render tasks in
    /// a tree-like user interface or inspect what caused a certain task
    /// execution.
    /// OriginId should not be included in the parents field, there is a separate
    /// field for that.
    parents: Identifiers
}

string Identifier

list Identifiers {
    member: Identifier
}

/// Included in notifications of tasks or requests to signal the completion state.
@enumKind("closed")
intEnum StatusCode {
    /// Execution was successful.
    OK = 1
    /// Execution failed.
    ERROR = 2
    /// Execution was cancelled.
    CANCELLED = 3
}

/// A resource identifier that is a valid URI according to rfc3986:
/// https://tools.ietf.org/html/rfc3986
string URI

list URIs {
    member: URI
}

list Languages {
    member: String
}

/// Structure describing how to start a BSP server and the capabilities it supports.
structure BspConnectionDetails {
    /// The name of the BSP server.
    @required
    name: String

    /// Arguments to pass to the BSP server.
    @required
    argv: Arguments

    /// The version of the BSP server.
    @required
    version: String

    /// Supported BSP version.
    @required
    bspVersion: String

    /// The languages supported by the BSP server.
    @required
    languages: Languages
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
operation BuildInitialize {
    input: InitializeBuildParams
    output: InitializeBuildResult
}

/// Like the language server protocol, the initialized notification is sent from the
/// client to the server after the client received the result of the initialize
/// request but before the client is sending any other request or notification to
/// the server. The server can use the initialized notification for example to
/// initialize intensive computation such as dependency resolution or compilation.
/// The initialized notification may only be sent once.
@jsonNotification("build/initialized")
operation OnBuildInitialized {
}

/// Like the language server protocol, the shutdown build request is sent from the
/// client to the server. It asks the server to shut down, but to not exit
/// (otherwise the response might not be delivered correctly to the client). There
/// is a separate exit notification that asks the server to exit.
@jsonRequest("build/shutdown")
operation BuildShutdown {
}

/// Like the language server protocol, a notification to ask the server to exit its process. The server should exit with success code 0
/// if the shutdown request has been received before; otherwise with error code 1.
@jsonNotification("build/exit")
operation OnBuildExit {
}

/// The show message notification is sent from a server to a client to ask the client to display a particular message in the user interface.
///
/// A build/showMessage notification is similar to LSP's window/showMessage, except for a few additions like id and originId.
@jsonNotification("build/showMessage")
operation OnBuildShowMessage {
    input: ShowMessageParams
}

/// The log message notification is sent from a server to a client to ask the client to log a particular message in its console.
///
/// A build/logMessage notification is similar to LSP's window/logMessage, except for a few additions like id and originId.
@jsonNotification("build/logMessage")
operation OnBuildLogMessage {
    input: LogMessageParams
}

/// The Diagnostics notification are sent from the server to the client to signal results of validation runs.
///
/// When reset is true, the client must clean all previous diagnostics associated with the same textDocument and
/// buildTarget and set instead the diagnostics in the request. This is the same behaviour as PublishDiagnosticsParams
/// in the LSP. When reset is false, the diagnostics are added to the last active diagnostics, allowing build tools to
/// stream diagnostics to the client.
///
/// It is the server's responsibility to manage the lifetime of the diagnostics by using the appropriate value in the reset field.
/// Clients generate new diagnostics by calling any BSP endpoint that triggers a buildTarget/compile, such as buildTarget/compile, buildTarget/test and buildTarget/run.
///
/// If the computed set of diagnostic is empty, the server must push an empty array with reset set to true, in order to clear previous diagnostics.
///
/// The optional originId field in the definition of PublishDiagnosticsParams can be used by clients to know which request originated the notification.
/// This field will be defined if the client defined it in the original request that triggered this notification.
@jsonNotification("build/publishDiagnostics")
operation OnBuildPublishDiagnostics {
    input: PublishDiagnosticsParams
}

/// The workspace build targets request is sent from the client to the server to ask
/// for the list of all available build targets in the workspace.
@jsonRequest("workspace/buildTargets")
operation WorkspaceBuildTargets {
    output: WorkspaceBuildTargetsResult
}

/// The `reload` request is sent from the client to instruct the build server to reload
/// the build configuration. This request should be supported by build tools that keep
/// their state in memory. If the `reload` request returns with an error, it's expected
/// that other requests respond with the previously known "good" state.
@jsonRequest("workspace/reload")
operation WorkspaceReload {

}

/// The build target changed notification is sent from the server to the client to
/// signal a change in a build target. The server communicates during the initialize
/// handshake whether this method is supported or not.
@jsonNotification("buildTarget/didChange")
operation OnBuildTargetDidChange {
    input: DidChangeBuildTarget
}

/// The build target sources request is sent from the client to the server to query
/// for the list of text documents and directories that are belong to a build
/// target. The sources response must not include sources that are external to the
/// workspace, see `buildTarget/dependencySources`.
@jsonRequest("buildTarget/sources")
operation BuildTargetSources {
    input: SourcesParams
    output: SourcesResult
}

/// The inverse sources request is sent from the client to the server to query for
/// the list of build targets containing a text document. The server communicates
/// during the initialize handshake whether this method is supported or not. This
/// request can be viewed as the inverse of `buildTarget/sources`, except it only
/// works for text documents and not directories.
@jsonRequest("buildTarget/inverseSources")
operation BuildTargetInverseSources {
    input: InverseSourcesParams
    output: InverseSourcesResult
}


/// The wrapped sources request is sent from the client to the server to query for
/// the list of build targets containing wrapped sources. Wrapped sources are script
/// sources that are wrapped by the build tool with some top and bottom wrappers.
/// The server communicates during the initialize handshake whether this method is
/// supported or not.
@jsonRequest("buildTarget/wrappedSources")
operation BuildTargetWrappedSources {
    input: WrappedSourcesParams
    output: WrappedSourcesResult
}

/// The build target dependency sources request is sent from the client to the
/// server to query for the sources of build target dependencies that are external
/// to the workspace. The dependency sources response must not include source files
/// that belong to a build target within the workspace, see `buildTarget/sources`.
///
/// The server communicates during the initialize handshake whether this method is
/// supported or not. This method can for example be used by a language server on
/// `textDocument/definition` to "Go to definition" from project sources to
/// dependency sources.
@jsonRequest("buildTarget/dependencySources")
operation BuildTargetDependencySources {
    input: DependencySourcesParams
    output: DependencySourcesResult
}

/// The build target dependency modules request is sent from the client to the
/// server to query for the libraries of build target dependencies that are external
/// to the workspace including meta information about library and their sources.
/// It's an extended version of `buildTarget/sources`.
@jsonRequest("buildTarget/dependencyModules")
operation BuildTargetDependencyModules {
    input: DependencyModulesParams
    output: DependencyModulesResult
}

/// The build target resources request is sent from the client to the server to
/// query for the list of resources of a given list of build targets.
///
/// A resource is a data dependency required to be present in the runtime classpath
/// when a build target is run or executed. The server communicates during the
/// initialize handshake whether this method is supported or not.
///
/// This request can be used by a client to highlight the resources in a project
/// view, for example.
@jsonRequest("buildTarget/resources")
operation BuildTargetResources {
    input: ResourcesParams
    output: ResourcesResult
}

/// The build target output paths request is sent from the client to the server to
/// query for the list of output paths of a given list of build targets.
///
/// An output path is a file or directory that contains output files such as build
/// artifacts which IDEs may decide to exclude from indexing. The server communicates
/// during the initialize handshake whether this method is supported or not.
@jsonRequest("buildTarget/outputPaths")
operation BuildTargetOutputPaths {
    input: OutputPathsParams
    output: OutputPathsResult
}

/// The BSP server can inform the client on the execution state of any task in the
/// build tool. The execution of some tasks, such as compilation or tests, must
/// always be reported by the server.
///
/// The server may also send additional task notifications for actions not covered
/// by the protocol, such as resolution or packaging. BSP clients can then display
/// this information to their users at their discretion.
///
/// When beginning a task, the server may send `build/taskStart`, intermediate
/// updates may be sent in `build/taskProgress`.
///
/// If a `build/taskStart` notification has been sent, the server must send
/// `build/taskFinish` on completion of the same task.
///
/// `build/taskStart`, `build/taskProgress` and `build/taskFinish` notifications for
/// the same task must use the same `taskId`.
///
/// Tasks that are spawned by another task should reference the originating task's
/// `taskId` in their own `taskId`'s `parent` field. Tasks spawned directly by a
/// request should reference the request's `originId` parent.
@jsonNotification("build/taskStart")
operation OnBuildTaskStart {
    input: TaskStartParams
}

/// After a `taskStart` and before `taskFinish` for a `taskId`, the server may send
/// any number of progress notifications.
@jsonNotification("build/taskProgress")
operation OnBuildTaskProgress {
    input: TaskProgressParams
}

/// A `build/taskFinish` notification must always be sent after a `build/taskStart`
/// with the same `taskId` was sent.
@jsonNotification("build/taskFinish")
operation OnBuildTaskFinish {
    input: TaskFinishParams
}

/// The compile build target request is sent from the client to the server to
/// compile the given list of build targets. The server communicates during the
/// initialize handshake whether this method is supported or not. This method can
/// for example be used by a language server before `textDocument/rename` to ensure
/// that all workspace sources typecheck correctly and are up-to-date.
@jsonRequest("buildTarget/compile")
operation BuildTargetCompile {
    input: CompileParams
    output: CompileResult
}

/// The test build target request is sent from the client to the server to test the
/// given list of build targets. The server communicates during the initialize
/// handshake whether this method is supported or not.
///
/// The "Implementation notes" section of the `buildTarget/run` request applies to
/// this request as well.
@jsonRequest("buildTarget/test")
operation BuildTargetTest {
    input: TestParams
    output: TestResult
}

/// The run request is sent from the client to the server to run a build target. The
/// server communicates during the initialize handshake whether this method is
/// supported or not.
///
/// Note that a run request containing only the target id is valid.
/// If no further parameters are provided, the server should use the default ones.
///
/// Implementation notes:
///
/// This request may trigger a compilation on the selected build targets. The server
/// is free to send any number of `build/task*`, `build/publishDiagnostics` and
/// `build/logMessage` notifications during compilation before completing the
/// response.
///
/// The client will get a `originId` field in `RunResult` if and only if
/// the `originId` field in the `RunParams` is defined.
///
/// Cancelling this request must kill the running process.
///
/// If the BSP server wishes to forward the stdout and stderr streams of the running process
/// to the client, it can do so by sending `run/printStdout` and `run/printStderr` notifications.
///
/// If the client wishes to send input to the running process, it can do so by sending
/// `run/readStdin` notifications to the server.

@jsonRequest("buildTarget/run")
operation BuildTargetRun {
    input: RunParams
    output: RunResult
}

/// The debug request is sent from the client to the server to debug build target(s). The
/// server launches a [Microsoft DAP](https://microsoft.github.io/debug-adapter-protocol/) server
/// and returns a connection URI for the client to interact with.
@jsonRequest("debugSession/start")
operation DebugSessionStart {
    input: DebugSessionParams
    output: DebugSessionAddress
}

/// The clean cache request is sent from the client to the server to reset any state
/// associated with a given build target. The state can live either in the build
/// tool or in the file system.
///
/// The build tool defines the exact semantics of the clean cache request:
///
/// 1. Stateless build tools are free to ignore the request and respond with a
///    successful response.
/// 2. Stateful build tools must ensure that invoking compilation on a target that
///    has been cleaned results in a full compilation.
@jsonRequest("buildTarget/cleanCache")
operation BuildTargetCleanCache {
    input: CleanCacheParams
    output: CleanCacheResult
}

/// Represents the identifier of a BSP request.
string OriginId

list BuildTargetIdentifiers {
    member: BuildTargetIdentifier
}

@data
document BuildTargetData

@data
document InitializeBuildParamsData

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
    data: InitializeBuildParamsData
}

structure BuildClientCapabilities {
    /// The languages that this client supports.
    /// The ID strings for each language is defined in the LSP.
    /// The server must never respond with build targets for other
    /// languages than those that appear in this list.
    @required
    languageIds: LanguageIds    
    /// Mirror capability to BuildServerCapabilities.jvmCompileClasspathProvider
    /// The client will request classpath via `buildTarget/jvmCompileClasspath` so
    /// it's safe to return classpath in ScalacOptionsItem empty.
    jvmCompileClasspathReceiver: Boolean = false
}

/// Language IDs are defined here
/// https://microsoft.github.io/language-server-protocol/specifications/lsp/3.17/specification/#textDocumentItem
string LanguageId

list LanguageIds {
    member: LanguageId
}

@data
document InitializeBuildResultData

structure InitializeBuildResult {
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
    data: InitializeBuildResultData
}

/// The capabilities of the build server.
/// Clients can use these capabilities to notify users what BSP endpoints can and
/// cannot be used and why.
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
    /// single text document via the method buildTarget/inverseSources
    inverseSourcesProvider: Boolean = false
    /// The server provides sources for library dependencies
    /// via method buildTarget/dependencySources
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
    /// debugging.
    jvmTestEnvironmentProvider: Boolean = false
    /// The server can respond to `workspace/cargoFeaturesState` and
    /// `setCargoFeatures` requests. In other words, supports Cargo Features extension.
    cargoFeaturesProvider: Boolean = false
    /// Reloading the build state through workspace/reload is supported
    canReload: Boolean = false
    /// The server can respond to `buildTarget/jvmCompileClasspath` requests with the
    /// necessary information about the target's classpath.
    jvmCompileClasspathProvider: Boolean = false
}

@mixin
structure LanguageProvider {
    @required
    languageIds: LanguageIds
}

structure CompileProvider with [LanguageProvider] {

}

structure RunProvider with [LanguageProvider] {

}

structure DebugProvider with [LanguageProvider] {

}

structure TestProvider with [LanguageProvider] {

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
    originId: OriginId
    /// The actual message.
    @required
    message: String
}

structure ShowMessageParams with [MessageParams] {

}

structure LogMessageParams with [MessageParams] {

}

@enumKind("closed")
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
    /// platforms (JVM, JavaScript).
    @required
    buildTarget: BuildTargetIdentifier
    /// The request id that originated this notification.
    originId: OriginId
    /// The diagnostics to be published by the client.
    @required
    diagnostics: Diagnostics
    /// Whether the client should clear the previous diagnostics
    /// mapped to the same `textDocument` and `buildTarget`.
    @required
    reset: Boolean
}

@data
document DiagnosticData

/// Diagnostic is defined as it is in the LSP.
structure Diagnostic {
    /// The range at which the message applies.
    @required
    range: Range
    /// The diagnostic's severity. Can be omitted. If omitted it is up to the
    /// client to interpret diagnostics as error, warning, info or hint.
    severity: DiagnosticSeverity
    /// The diagnostic's code, which might appear in the user interface.
    code: DiagnosticCode
    /// An optional property to describe the error code.
    codeDescription: CodeDescription
    /// A human-readable string describing the source of this
    /// diagnostic, e.g. 'typescript' or 'super lint'.
    source: String
    /// The diagnostic's message.
    @required
    message: String
    /// Additional metadata about the diagnostic.
    tags: DiagnosticTags
    /// An array of related diagnostic information, e.g. when symbol-names within
    /// a scope collide all definitions can be marked via this property.
    relatedInformation: DiagnosticRelatedInformations
    /// A data entry field that is preserved between a
    /// `textDocument/publishDiagnostics` notification and
    /// `textDocument/codeAction` request.
    data: DiagnosticData
}

structure Position {
    /// Line position in a document (zero-based).
    @required
    line: Integer
    /// Character offset on a line in a document (zero-based)
    ///
    /// If the character value is greater than the line length it defaults back
    /// to the line length.
    @required
    character: Integer
}

structure Range {
    /// The range's start position.
    @required
    start: Position
    /// The range's end position.
    @required
    end: Position
}

structure Location {
    @required
    uri: URI
    @required
    range: Range
}

structure TextDocumentIdentifier {
    /// The text document's URI.
    @required
    uri: URI
}

list Diagnostics {
    member: Diagnostic
}

@enumKind("closed")
intEnum DiagnosticSeverity {
    /// Reports an error.
    ERROR = 1
    /// Reports a warning.
    WARNING = 2
    /// Reports an information.
    INFORMATION = 3
    /// Reports a hint.
    HINT = 4
}

@untaggedUnion
union DiagnosticCode {
    string: String
    integer: Integer
}

/// Structure to capture a description for an error code.
structure CodeDescription {
    /// An URI to open with more information about the diagnostic error.
    @required
    href: URI
}

@enumKind("open")
intEnum DiagnosticTag {
    /// Unused or unnecessary code.
    ///
    /// Clients are allowed to render diagnostics with this tag faded out
    /// instead of having an error squiggle.
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
structure DiagnosticRelatedInformation {
    /// The location of this related diagnostic information.
    @required
    location: Location
    /// The message of this related diagnostic information.
    @required
    message: String
}

list DiagnosticRelatedInformations {
    member: DiagnosticRelatedInformation
}

structure WorkspaceBuildTargetsResult {
    /// The build targets in this workspace that
    /// contain sources with the given language ids.
    @required
    targets: BuildTargets
}

list BuildTargets {
    member: BuildTarget
}

structure DidChangeBuildTarget {
    @required
    changes: BuildTargetEvents
}

@data
document BuildTargetEventData

structure BuildTargetEvent {
    /// The identifier for the changed build target
    @required
    target: BuildTargetIdentifier
    /// The kind of change for this build target
    kind: BuildTargetEventKind
    /// Any additional metadata about what information changed.
    data: BuildTargetEventData
}

list BuildTargetEvents {
    member: BuildTargetEvent
}

/// The `BuildTargetEventKind` information can be used by clients to trigger
/// reindexing or update the user interface with the new information.
@enumKind("closed")
intEnum BuildTargetEventKind {
    /// The build target is new.
    CREATED = 1
    /// The build target has changed.
    CHANGED = 2
    /// The build target has been deleted.
    DELETED = 3
}

structure SourcesParams {
    @required
    targets: BuildTargetIdentifiers
}

structure SourcesResult {
    @required
    items: SourcesItems
}

structure SourcesItem {
    @required
    target: BuildTargetIdentifier
    /// The text documents or and directories that belong to this build target.
    @required
    sources: SourceItems
    /// The root directories from where source files should be relativized.
    /// Example: ["file://Users/name/dev/metals/src/main/scala"]
    roots: URIs
}

list SourcesItems {
    member: SourcesItem
}

@data
document SourceItemData

structure SourceItem {
    /// Either a text document or a directory. A directory entry must end with a forward
    /// slash "/" and a directory entry implies that every nested text document within the
    /// directory belongs to this source item.
    @required
    uri: URI
    /// Type of file of the source item, such as whether it is file or directory.
    @required
    kind: SourceItemKind
    /// Indicates if this source is automatically generated by the build and is not
    /// intended to be manually edited by the user.
    @required
    generated: Boolean
    /// Language-specific metadata about this source item.
    data: SourceItemData
}

list SourceItems {
    member: SourceItem
}

@enumKind("closed")
intEnum SourceItemKind {
    /// The source item references a normal file.
    FILE = 1
    /// The source item references a directory.
    DIRECTORY = 2
}

structure InverseSourcesParams {
    @required
    textDocument: TextDocumentIdentifier
}

structure InverseSourcesResult {
    @required
    targets: BuildTargetIdentifiers
}

structure WrappedSourceItem {
    @required
    uri: String
    @required
    generatedUri: String
    @required
    topWrapper: String
    @required
    bottomWrapper: String
}

list WrappedSourceItems {
    member: WrappedSourceItem
}

structure WrappedSourcesItem {
    @required
    target: BuildTargetIdentifier
    @required
    sources: WrappedSourceItems
}

list WrappedSourcesItems {
    member: WrappedSourcesItem
}

structure WrappedSourcesParams {
    @required
    items: WrappedSourceItem
}

structure WrappedSourcesResult {
    @required
    targets: BuildTargetIdentifiers
}

structure DependencySourcesParams {
    @required
    targets: BuildTargetIdentifiers
}

structure DependencySourcesResult {
    @required
    items: DependencySourcesItems
}

structure DependencySourcesItem {
    @required
    target: BuildTargetIdentifier
    /// List of resources containing source files of the
    /// target's dependencies.
    /// Can be source files, jar files, zip files, or directories.
    @required
    sources: URIs
}

list DependencySourcesItems {
    member: DependencySourcesItem
}

structure DependencyModulesParams {
    @required
    targets: BuildTargetIdentifiers
}

structure DependencyModulesResult {
    @required
    items: DependencyModulesItems
}

list DependencyModules {
    member: DependencyModule
}

structure DependencyModulesItem {
    @required
    target: BuildTargetIdentifier
    @required
    modules: DependencyModules
}

list DependencyModulesItems {
    member: DependencyModulesItem
}

@data
document DependencyModuleData

structure DependencyModule {
    /// Module name
    @required
    name: String
    /// Module version
    @required
    version: String
    /// Language-specific metadata about this module.
    /// See MavenDependencyModule as an example.
    data: DependencyModuleData
}

structure ResourcesParams {
    @required
    targets: BuildTargetIdentifiers
}

structure ResourcesResult {
    @required
    items: ResourcesItems
}

structure ResourcesItem {
    @required
    target: BuildTargetIdentifier
    /// List of resource files.
    @required
    resources: URIs
}

list ResourcesItems {
    member: ResourcesItem
}

structure OutputPathsParams {
    @required
    targets: BuildTargetIdentifiers
}

structure OutputPathsResult {
    @required
    items: OutputPathsItems
}

structure OutputPathsItem {
    /// A build target to which output paths item belongs.
    @required
    target: BuildTargetIdentifier
    /// Output paths.
    @required
    outputPaths: OutputPathItems
}

list OutputPathsItems {
    member: OutputPathsItem
}

structure OutputPathItem {
    /// Either a file or a directory. A directory entry must end with a forward
    /// slash "/" and a directory entry implies that every nested path within the
    /// directory belongs to this output item.
    @required
    uri: URI
    /// Type of file of the output item, such as whether it is file or directory.
    @required
    kind: OutputPathItemKind
}

list OutputPathItems {
    member: OutputPathItem
}

@enumKind("closed")
intEnum OutputPathItemKind {
    /// The output path item references a normal file.
    FILE = 1
    /// The output path item references a directory.
    DIRECTORY = 2
}

/// Task start notifications may contain an arbitrary interface in their `data`
/// field. The kind of interface that is contained in a notification must be
/// specified in the `dataKind` field.
///
/// There are predefined kinds of objects for compile and test tasks, as described
/// in [[bsp#BuildTargetCompile]] and [[bsp#BuildTargetTest]]
@data
document TaskStartData

/// Task progress notifications may contain an arbitrary interface in their `data`
/// field. The kind of interface that is contained in a notification must be
/// specified in the `dataKind` field.
@data
document TaskProgressData

/// Task finish notifications may contain an arbitrary interface in their `data`
/// field. The kind of interface that is contained in a notification must be
/// specified in the `dataKind` field.
///
/// There are predefined kinds of objects for compile and test tasks, as described
/// in [[bsp#BuildTargetCompile]] and [[bsp#BuildTargetTest]]
@data
document TaskFinishData

structure TaskStartParams {
    /// Unique id of the task with optional reference to parent task id
    @required
    taskId: TaskId

    /// A unique identifier generated by the client to identify this request.
    originId: Identifier

    /// Timestamp of when the event started in milliseconds since Epoch.
    eventTime: Long

    /// Message describing the task.
    message: String

    /// Optional metadata about the task.
    /// Objects for specific tasks like compile, test, etc are specified in the protocol.
    data: TaskStartData
}

structure TaskProgressParams {
    /// Unique id of the task with optional reference to parent task id
    @required
    taskId: TaskId

    /// A unique identifier generated by the client to identify this request.
    originId: Identifier

    /// Timestamp of when the event started in milliseconds since Epoch.
    eventTime: Long,

    /// Message describing the task.
    message: String

    /// If known, total amount of work units in this task.
    total: Long

    /// If known, completed amount of work units in this task.
    progress: Long

    /// Name of a work unit. For example, "files" or "tests". May be empty.
    unit: String

    /// Optional metadata about the task.
    /// Objects for specific tasks like compile, test, etc are specified in the protocol.
    data: TaskProgressData
}

structure TaskFinishParams {
    /// Unique id of the task with optional reference to parent task id
    @required
    taskId: TaskId

    /// A unique identifier generated by the client to identify this request.
    originId: Identifier

    /// Timestamp of when the event started in milliseconds since Epoch.
    eventTime: Long,

    /// Message describing the task.
    message: String

    /// Task completion status.
    @required
    status: StatusCode

    /// Optional metadata about the task.
    /// Objects for specific tasks like compile, test, etc are specified in the protocol.
    data: TaskFinishData
}

structure CompileParams {
    /// A sequence of build targets to compile.
    @required
    targets: BuildTargetIdentifiers

    /// A unique identifier generated by the client to identify this request.
    /// The server may include this id in triggered notifications or responses.
    originId: Identifier

    /// Optional arguments to the compilation process.
    arguments: Arguments
}

list Arguments {
    member: String
}

map EnvironmentVariables {
    key: String
    value: String
}

@data
document CompileResultData

structure CompileResult {
    /// An optional request id to know the origin of this report.
    originId: Identifier

    /// A status code for the execution.
    @required
    statusCode: StatusCode

    /// A field containing language-specific information, like products
    /// of compilation or compiler-specific metadata the client needs to know.
    data: CompileResultData
}

/// The beginning of a compilation unit may be signalled to the client with a
/// `build/taskStart` notification. When the compilation unit is a build target, the
/// notification's `dataKind` field must be "compile-task" and the `data` field must
/// include a `CompileTask` object:
@dataKind(kind: "compile-task", extends: [TaskStartData])
structure CompileTask {
    @required
    target: BuildTargetIdentifier
}

/// The completion of a compilation task should be signalled with a
/// `build/taskFinish` notification. When the compilation unit is a build target,
/// the notification's `dataKind` field must be `compile-report` and the `data`
/// field must include a `CompileReport` object:
@dataKind(kind: "compile-report", extends: [TaskFinishData])
structure CompileReport {
    /// The build target that was compiled.
    @required
    target: BuildTargetIdentifier

    /// An optional request id to know the origin of this report.
    @deprecated(message: "Use the field in TaskFinishParams instead")
    originId: Identifier

    /// The total number of reported errors compiling this target.
    @required
    errors: Integer

    /// The total number of reported warnings compiling the target.
    @required
    warnings: Integer

    /// The total number of milliseconds it took to compile the target.
    time: Long

    /// The compilation was a noOp compilation.
    noOp: Boolean
}

@data
document TestParamsData

structure TestParams {
    /// A sequence of build targets to test.
    @required
    targets: BuildTargetIdentifiers

    /// A unique identifier generated by the client to identify this request.
    /// The server may include this id in triggered notifications or responses.
    originId: Identifier

    /// Optional arguments to the test execution engine.
    arguments: Arguments

    /// Optional environment variables to set before running the tests.
    environmentVariables: EnvironmentVariables

    /// Optional working directory
    workingDirectory: URI

    /// Language-specific metadata about for this test execution.
    /// See ScalaTestParams as an example.
    data: TestParamsData
}

@data
document TestResultData

structure TestResult {
    /// An optional request id to know the origin of this report.
    originId: Identifier

    /// A status code for the execution.
    @required
    statusCode: StatusCode

    /// Language-specific metadata about the test result.
    /// See ScalaTestParams as an example.
    data: TestResultData
}

/// The beginning of a testing unit may be signalled to the client with a
/// `build/taskStart` notification. When the testing unit is a build target, the
/// notification's `dataKind` field must be `test-task` and the `data` field must
/// include a `TestTask` object.
@dataKind(kind: "test-task", extends: [TaskStartData])
structure TestTask {
    @required
    target: BuildTargetIdentifier
}

@dataKind(kind: "test-report", extends: [TaskFinishData])
structure TestReport {
    @deprecated(message: "Use the field in TaskFinishParams instead")
    originId: Identifier
    /// The build target that was compiled.
    @required
    target: BuildTargetIdentifier

    /// The total number of successful tests.
    @required
    passed: Integer

    /// The total number of failed tests.
    @required
    failed: Integer

    /// The total number of ignored tests.
    @required
    ignored: Integer

    /// The total number of cancelled tests.
    @required
    cancelled: Integer

    /// The total number of skipped tests.
    @required
    skipped: Integer

    /// The total number of milliseconds tests take to run (e.g. doesn't include compile times).
    time: Long
}

@dataKind(kind: "test-start", extends: [TaskStartData])
structure TestStart {
    /// Name or description of the test.
    @required
    displayName: String

    /// Source location of the test, as LSP location.
    location: Location
}

@data
document TestFinishData

@dataKind(kind: "test-finish", extends: [TaskFinishData])
structure TestFinish {
    /// Name or description of the test.
    @required
    displayName: String

    /// Information about completion of the test, for example an error message.
    message: String

    /// Completion status of the test.
    @required
    status: TestStatus

    /// Source location of the test, as LSP location.
    location: Location

    /// Optionally, structured metadata about the test completion.
    /// For example: stack traces, expected/actual values.
    data: TestFinishData
}

@enumKind("closed")
intEnum TestStatus {
    /// The test passed successfully.
    PASSED = 1
    /// The test failed.
    FAILED = 2
    /// The test was marked as ignored.
    IGNORED = 3
    /// The test execution was cancelled.
    CANCELLED = 4
    /// The was not included in execution.
    SKIPPED = 5
}

@data
document RunParamsData

structure RunParams {
    /// The build target to run.
    @required
    target: BuildTargetIdentifier

    /// A unique identifier generated by the client to identify this request.
    /// The server may include this id in triggered notifications or responses.
    originId: Identifier

    /// Optional arguments to the executed application.
    arguments: Arguments

    /// Optional environment variables to set before running the application.
    environmentVariables: EnvironmentVariables

    /// Optional working directory
    workingDirectory: URI

    /// Language-specific metadata for this execution.
    /// See ScalaMainClass as an example.
    data: RunParamsData
}

structure RunResult {
    /// An optional request id to know the origin of this report.
    originId: Identifier

    /// A status code for the execution.
    @required
    statusCode: StatusCode
}

@data
document DebugSessionParamsData

structure DebugSessionParams {
    /// A sequence of build targets affected by the debugging action.
    @required
    targets: BuildTargetIdentifiers

    /// Language-specific metadata for this execution.
    /// See ScalaMainClass as an example.
    data: DebugSessionParamsData
}

structure DebugSessionAddress {
    /// The Debug Adapter Protocol server's connection uri
    @required
    uri: URI
}

structure CleanCacheParams {
    /// The build targets to clean.
    @required
    targets: BuildTargetIdentifiers
}

structure CleanCacheResult {
    /// Optional message to display to the user.
    message: String
    /// Indicates whether the clean cache request was performed or not.
    @required
    cleaned: Boolean
}

@unstable
structure PrintParams {
    /// The id of the request.
    @required
    originId: Identifier

    /// Relevant only for test tasks.
    /// Allows to tell the client from which task the output is coming from.
    task: TaskId

    /// Message content can contain arbitrary bytes.
    /// They should be escaped as per [javascript encoding](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Grammar_and_types#using_special_characters_in_strings)
    @required
    message: String
}

/// Notification sent from the server to the client when the target being run or tested
/// prints something to stdout.
@unstable
@jsonNotification("run/printStdout")
operation OnRunPrintStdout {
    input: PrintParams
}

/// Notification sent from the server to the client when the target being run or tested
/// prints something to stderr.
@unstable
@jsonNotification("run/printStderr")
operation OnRunPrintStderr {
    input: PrintParams
}

@unstable
structure ReadParams {
    /// The id of the request.
    @required
    originId: Identifier

    /// Relevant only for test tasks.
    /// Allows to tell the client from which task the output is coming from.
    task: TaskId

    /// Message content can contain arbitrary bytes.
    /// They should be escaped as per [javascript encoding](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Grammar_and_types#using_special_characters_in_strings)
    @required
    message: String
}

/// Notification sent from the client to the server when the user wants to send
/// input to the stdin of the running target.
@unstable
@jsonNotification("run/readStdin")
operation OnRunReadStdin {
    input: ReadParams
}
