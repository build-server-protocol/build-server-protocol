$version: "2"

namespace bsp

use jsonrpc#data
use jsonrpc#enumKind

@data(kind: "scala", extends: BuildTargetData)
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

list ScalacOptions {
    member: String
}

structure ScalacOptionsItem {
    @required
    target: BuildTargetIdentifier
    @required
    options: ScalacOptions
    @required
    classpath: Classpath
    @required
    classDirectory: String
}

structure ScalacOptionsParams {
    @required
    targets: BuildTargetIdentifiers
}

list ScalacOptionsItems {
    member: ScalacOptionsItem
}

structure ScalacOptionsResult {
    @required
    items: ScalacOptionsItems
}

structure ScalaMainClass {
    @required
    @jsonName("class")
    className: String
    @required
    arguments: Arguments
    @required
    jvmOptions: JvmOptions
    environmentVariables: EnvironmentVariablesList // TODO: inconsistent, `EnvironmentVariables` should be used instead
}


list ScalaMainClasses {
    member: ScalaMainClass
}

structure ScalaMainClassesItem {
    @required
    target: BuildTargetIdentifier
    @required
    classes: ScalaMainClasses
}

structure ScalaMainClassesParams {
    @required
    targets: BuildTargetIdentifiers
    originId: String
}

list ScalaMainClassesItems {
    member: ScalaMainClassesItem
}

structure ScalaMainClassesResult {
    @required
    items: ScalaMainClassesItems
}

list ScalaTestClasses {
    member: String
}

structure ScalaTestClassesItem {
    @required
    target: BuildTargetIdentifier
    @required
    classes: ScalaTestClasses
    framework: String
}

structure ScalaTestClassesParams {
    @required
    targets: BuildTargetIdentifiers
    originId: String
}

list ScalaTestClassesItems {
    member: ScalaTestClassesItem
}

structure ScalaTestClassesResult {
    @required
    items: ScalaTestClassesItems
}

structure ScalaTestParams {
    testClasses: ScalaTestClassesItems
    jvmOptions: JvmOptions
}

list ScalaTestSuiteSelections {
    member: ScalaTestSuiteSelection
}

list EnvironmentVariablesList {
    member: String
}

structure ScalaTestSuites {
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
