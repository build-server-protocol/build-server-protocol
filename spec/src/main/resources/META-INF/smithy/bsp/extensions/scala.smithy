$version: "2"

namespace bsp.scala

use bsp#Arguments
use bsp#BuildTargetData
use bsp#BuildTargetIdentifier
use bsp#BuildTargetIdentifiers
use bsp#DebugSessionParamsData
use bsp#DiagnosticData
use bsp#Range
use bsp#RunParamsData
use bsp#TestParamsData
use bsp#URIs
use bsp.jvm#Classpath
use bsp.jvm#JvmBuildTarget
use bsp.jvm#JvmOptions
use traits#dataKind
use traits#enumKind
use traits#jsonRPC
use traits#jsonRequest

@jsonRPC
service ScalaBuildServer {
    operations: [
        BuildTargetScalacOptions,
        BuildTargetScalaTestClasses,
        BuildTargetScalaMainClasses,
    ]
}

/// `ScalaBuildTarget` is a basic data structure that contains scala-specific
/// metadata for compiling a target containing Scala sources.
@dataKind(kind: "scala", extends: [BuildTargetData])
structure ScalaBuildTarget {
    /// The Scala organization that is used for a target.
    @required
    scalaOrganization: String
    /// The scala version to compile this target
    @required
    scalaVersion: String
    /// The binary version of scalaVersion.
    /// For example, 2.12 if scalaVersion is 2.12.4.
    @required
    scalaBinaryVersion: String
    /// The target platform for this target
    @required
    platform: ScalaPlatform
    /// A sequence of Scala jars such as scala-library, scala-compiler and scala-reflect.
    @required
    jars: URIs
    /// The jvm build target describing jdk to be used
    jvmBuildTarget: JvmBuildTarget
}

@enumKind("closed")
intEnum ScalaPlatform {
    JVM = 1
    JS = 2
    NATIVE = 3
}

/// `ScalaTestParams` contains scala-specific metadata for testing Scala targets.
@dataKind(kind: "scala-test", extends: [TestParamsData])
structure ScalaTestParams {
    /// The test classes to be run in this test execution.
    /// It is the result of `buildTarget/scalaTestClasses`.
    testClasses: ScalaTestClassesItems
    /// The JVM options to run tests with. They replace any options
    /// that are defined by the build server if defined.
    jvmOptions: JvmOptions
}

/// The build target scalac options request is sent from the client to the server to
/// query for the list of compiler options necessary to compile in a given list of
/// targets.
@jsonRequest("buildTarget/scalacOptions")
operation BuildTargetScalacOptions {
    input: ScalacOptionsParams
    output: ScalacOptionsResult
}

structure ScalacOptionsParams {
    @required
    targets: BuildTargetIdentifiers
}

structure ScalacOptionsResult {
    @required
    items: ScalacOptionsItems
}

structure ScalacOptionsItem {
    @required
    target: BuildTargetIdentifier
    /// Additional arguments to the compiler.
    /// For example, -deprecation.
    @required
    options: ScalacOptionsList
    /// The dependency classpath for this target, must be
    /// identical to what is passed as arguments to
    /// the -classpath flag in the command line interface
    /// of scalac.
    @required
    classpath: Classpath
    /// The output directory for classfiles produced by this target
    @required
    classDirectory: String
}

list ScalacOptionsItems {
    member: ScalacOptionsItem
}

list ScalacOptionsList {
    member: String
}

/// The Scala build target test classes request is sent from the client to the
/// server to query for the list of fully qualified names of test classes in a given
/// list of targets.
///
/// This method can for example be used by a client to:
///
/// - Show a list of the discovered classes that can be tested.
/// - Attach a "Run test suite" button above the definition of a test suite via
///  `textDocument/codeLens`.
///
/// (To render the code lens, the language server needs to map the fully qualified
/// names of the test targets to the defining source file via
/// `textDocument/definition`. Then, once users click on the button, the language
/// server can pass the fully qualified name of the test class as an argument to the
/// `buildTarget/test` request.)
///
/// This request may trigger a compilation on the selected build targets. The server
/// is free to send any number of `build/task*`, `build/publishDiagnostics` and
/// `build/logMessage` notifications during compilation before completing the
/// response.
///
/// The client will get a `originId` field in `ScalaTestClassesResult` if the
/// `originId` field in the `ScalaTestClassesParams` is defined.
@jsonRequest("buildTarget/scalaTestClasses")
@deprecated(message: "Use buildTarget/jvmTestEnvironment instead")
operation BuildTargetScalaTestClasses {
    input: ScalaTestClassesParams
    output: ScalaTestClassesResult
}

structure ScalaTestClassesParams {
    @required
    targets: BuildTargetIdentifiers
    /// An optional number uniquely identifying a client request.
    originId: String
}

structure ScalaTestClassesResult {
    /// An optional id of the request that triggered this result.
    @required
    items: ScalaTestClassesItems
}

list ScalaTestClassesItems {
    member: ScalaTestClassesItem
}

structure ScalaTestClassesItem {
    /// The build target that contains the test classes.
    @required
    target: BuildTargetIdentifier
    /// Name of the the framework to which classes belong.
    /// It's optional in order to maintain compatibility, however it is expected
    /// from the newer implementations to not leave that field unspecified.
    framework: String
    /// The fully qualified names of the test classes in this target
    @required
    classes: ScalaTestClassesList
}

list ScalaTestClassesList {
    member: String
}

/// The build target main classes request is sent from the client to the server to
/// query for the list of main classes that can be fed as arguments to
/// `buildTarget/run`. This method can be used for the same use cases than the
/// [Scala Test Classes Request](#scala-test-classes-request) enables.
/// This request may trigger a compilation on the selected build targets. The server
/// is free to send any number of `build/taskStart`, `build/taskProgress`,
/// `build/taskFinish`, `build/publishDiagnostics` and `build/logMessage`
/// notifications during compilation before completing the response.
/// The client will get a `originId` field in `ScalaMainClassesResult` if the
/// `originId` field in the `ScalaMainClassesParams` is defined.
@jsonRequest("buildTarget/scalaMainClasses")
@deprecated(message: "Use buildTarget/jvmRunEnvironment instead")
operation BuildTargetScalaMainClasses {
    input: ScalaMainClassesParams
    output: ScalaMainClassesResult
}

structure ScalaMainClassesParams {
    @required
    targets: BuildTargetIdentifiers
    /// An optional number uniquely identifying a client request.
    originId: String
}

structure ScalaMainClassesResult {
    @required
    items: ScalaMainClassesItems
    /// An optional id of the request that triggered this result.
    originId: String
}

structure ScalaMainClassesItem {
    /// The build target that contains the test classes.
    @required
    target: BuildTargetIdentifier
    /// The main class item.
    @required
    classes: ScalaMainClassesList
}

@dataKind(kind: "scala-main-class", extends: [DebugSessionParamsData, RunParamsData])
structure ScalaMainClass {
    /// The main class to run.
    @required
    @jsonName("class")
    className: String
    /// The user arguments to the main entrypoint.
    @required
    @deprecated(message: "Use `buildTarget/run` params instead", since: "2.2.0")
    arguments: Arguments
    /// The jvm options for the application.
    @required
    jvmOptions: JvmOptions
    /// The environment variables for the application.
    @deprecated(message: "Use `buildTarget/run` params instead", since: "2.2.0")
    environmentVariables: EnvironmentVariablesList
}

list ScalaMainClassesList {
    member: ScalaMainClass
}

list ScalaMainClassesItems {
    member: ScalaMainClassesItem
}

list ScalaTestSuiteSelections {
    member: ScalaTestSuiteSelection
}

@deprecated(message: "Use `EnvironmentVariables` (a map) instead", since: "2.2.0")
list EnvironmentVariablesList {
    member: String
}

/// Each element of this array is a fully qualified class name.
@dataKind(kind: "scala-test-suites", extends: [TestParamsData])
list ScalaTestSuiteClasses {
    member: String
}

/// The debug session will connect to a running process. The DAP client will send the port of the running process later.
@dataKind(kind: "scala-attach-remote", extends: [DebugSessionParamsData])
structure ScalaAttachRemote {
}

@dataKind(kind: "scala-test-suites-selection", extends: [TestParamsData])
structure ScalaTestSuites {
    /// The fully qualified names of the test classes in this target and the tests in this test classes
    @required
    suites: ScalaTestSuiteSelections
    /// Additional jvmOptions which will be passed to the forked JVM
    @required
    jvmOptions: JvmOptions
    /// Enviroment variables should be an array of strings in format KEY=VALUE
    @required
    @deprecated(message: "Use `buildTarget/test` params instead", since: "2.2.0")
    environmentVariables: EnvironmentVariablesList
}

list ScalaTestSuiteSelectionTests {
    member: String
}

structure ScalaTestSuiteSelection {
    /// Fully qualified name of the test suite class
    @required
    className: String // TODO: inconsistent, should be renamed to `class` in JSON
    /// List of tests which should be run within this test suite.
    /// Empty collection means that all of them are supposed to be executed.
    @required
    tests: ScalaTestSuiteSelectionTests
}

/// `ScalaDiagnostic` is a data structure that contains Scala-specific
/// metadata generated by Scala compilation.
@dataKind(kind: "scala", extends: [DiagnosticData])
structure ScalaDiagnostic {
    /// Actions (also known as quick fixes) that are able to either fix or address
    /// the issue that is causing this diagnostic.
    actions: ScalaActions
}

/// A Scala action represents a change that can be performed in code.
/// See also [LSP: Code Action Request](https://microsoft.github.io/language-server-protocol/specifications/lsp/3.17/specification/#textDocument_codeAction).
///
/// **Note**: In LSP, `CodeAction` appears only as a response to a `textDocument/codeAction` request,
/// whereas ScalaAction is intended to be returned as `Diagnostics.data.actions`.
structure ScalaAction {
    /// A short, human-readable, title for this code action.
    @required
    title: String
    /// A description that may be shown to the user client side to explain the action.
    description: String
    /// The workspace edit this code action performs.
    edit: ScalaWorkspaceEdit
}

list ScalaActions {
    member: ScalaAction
}

/// A workspace edit represents changes to many resources managed in the workspace.
structure ScalaWorkspaceEdit {
    @required
    changes: ScalaTextEdits
}

/// A textual edit applicable to a text document.
structure ScalaTextEdit {
    /// The range of the text document to be manipulated. To insert
    /// text into a document create a range where start === end.
    @required
    range: Range
    /// The string to be inserted. For delete operations use an
    /// empty string.
    @required
    newText: String
}

list ScalaTextEdits {
    member: ScalaTextEdit
}
