$version: "2"

namespace bsp.scala

use bsp#Arguments
use bsp#BuildTargetData
use bsp#BuildTargetIdentifier
use bsp#BuildTargetIdentifiers
use bsp#DebugSessionParamsData
use bsp#TestParamsData
use bsp#URIs
use bsp.jvm#Classpath
use bsp.jvm#JvmBuildTarget
use bsp.jvm#JvmOptions
use jsonrpc#dataKind
use jsonrpc#enumKind
use jsonrpc#jsonRPC
use jsonrpc#jsonRequest

@jsonRPC
service ScalaBuildServer {
    operations: [
        ScalacOptions,
        ScalaTestClasses,
        ScalaMainClasses,
    ]
}

/// `ScalaBuildTarget` is a basic data structure that contains scala-specific
/// metadata for compiling a target containing Scala sources. This metadata is
/// embedded in the `data: Option[Json]` field of the `BuildTarget` definition, when
/// the `dataKind` field contains "scala".
@dataKind(kind: "scala", extends: BuildTargetData)
@tags(["basic"])
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
/// This metadata is embedded in the `data: Option[Json]` field of the
/// `buildTarget/test` request when the `dataKind` field contains "scala-test".
@dataKind(kind: "scala-test", extends: TestParamsData)
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
operation ScalacOptions {
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
    @required
    /// Additional arguments to the compiler.
    /// For example, -deprecation.
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
operation ScalaTestClasses {
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
    @required
    /// An optional id of the request that triggered this result.
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
//
//The client will get a `originId` field in `ScalaMainClassesResult` if the
//`originId` field in the `ScalaMainClassesParams` is defined.
@jsonRequest("buildTarget/scalaMainClasses")
operation ScalaMainClasses {
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

@dataKind(kind: "scala-main-class", extends: DebugSessionParamsData)
structure ScalaMainClass {
    /// The main class to run.
    @required
    @jsonName("class")
    className: String
    /// The user arguments to the main entrypoint.
    @required
    arguments: Arguments
    /// The jvm options for the application.
    @required
    jvmOptions: JvmOptions
    /// The environment variables for the application.
    environmentVariables: EnvironmentVariablesList // TODO: inconsistent, `EnvironmentVariables` should be used instead
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

list EnvironmentVariablesList {
    member: String
}

@dataKind(kind: "scala-test-suites", extends: DebugSessionParamsData)
///  Each element of that array is a fully qualified class name.
list ScalaTestSuiteClasses {
    member: String
}

@dataKind(kind: "scala-test-suites-selection", extends: DebugSessionParamsData)
structure ScalaTestSuites {
    /// The fully qualified names of the test classes in this target and the tests in this test classes
    @required
    suites: ScalaTestSuiteSelections
    /// Additional jvmOptions which will be passed to the forked JVM
    @required
    jvmOptions: JvmOptions
    /// Enviroment variables should be an array of strings in format KEY=VALUE
    @required
    environmentVariables: EnvironmentVariablesList // TODO: inconsistent, `EnvironmentVariables` should be used instead
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
