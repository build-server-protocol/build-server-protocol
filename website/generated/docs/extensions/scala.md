---
id: scala
title: Scala Extension
sidebar_label: Scala
---

The following section contains Scala-specific extensions to the build server
protocol.

## BSP version

`2.2.0`

## BSP Server remote interface

### BuildTargetScalacOptions: request

The build target scalac options request is sent from the client to the server to
query for the list of compiler options necessary to compile in a given list of
targets.

- method: `buildTarget/scalacOptions`
- params: `ScalacOptionsParams`
- result: `ScalacOptionsResult`

#### ScalacOptionsParams

```ts
export interface ScalacOptionsParams {
  targets: BuildTargetIdentifier[];
}
```

#### ScalacOptionsResult

```ts
export interface ScalacOptionsResult {
  items: ScalacOptionsItem[];
}
```

#### ScalacOptionsItem

```ts
export interface ScalacOptionsItem {
  target: BuildTargetIdentifier;

  /** Additional arguments to the compiler.
   * For example, -deprecation. */
  options: string[];

  /** The dependency classpath for this target, must be
   * identical to what is passed as arguments to
   * the -classpath flag in the command line interface
   * of scalac. */
  classpath: string[];

  /** The output directory for classfiles produced by this target */
  classDirectory: string;
}
```

### BuildTargetScalaTestClasses: request

**Deprecated**: Use buildTarget/jvmTestEnvironment instead

The Scala build target test classes request is sent from the client to the
server to query for the list of fully qualified names of test classes in a given
list of targets.

This method can for example be used by a client to:

- Show a list of the discovered classes that can be tested.
- Attach a "Run test suite" button above the definition of a test suite via
  `textDocument/codeLens`.

(To render the code lens, the language server needs to map the fully qualified
names of the test targets to the defining source file via
`textDocument/definition`. Then, once users click on the button, the language
server can pass the fully qualified name of the test class as an argument to the
`buildTarget/test` request.)

This request may trigger a compilation on the selected build targets. The server
is free to send any number of `build/task*`, `build/publishDiagnostics` and
`build/logMessage` notifications during compilation before completing the
response.

The client will get a `originId` field in `ScalaTestClassesResult` if the
`originId` field in the `ScalaTestClassesParams` is defined.

- method: `buildTarget/scalaTestClasses`
- params: `ScalaTestClassesParams`
- result: `ScalaTestClassesResult`

#### ScalaTestClassesParams

```ts
export interface ScalaTestClassesParams {
  targets: BuildTargetIdentifier[];

  /** An optional number uniquely identifying a client request. */
  originId?: string;
}
```

#### ScalaTestClassesResult

```ts
export interface ScalaTestClassesResult {
  /** An optional id of the request that triggered this result. */
  items: ScalaTestClassesItem[];
}
```

#### ScalaTestClassesItem

```ts
export interface ScalaTestClassesItem {
  /** The build target that contains the test classes. */
  target: BuildTargetIdentifier;

  /** Name of the the framework to which classes belong.
   * It's optional in order to maintain compatibility, however it is expected
   * from the newer implementations to not leave that field unspecified. */
  framework?: string;

  /** The fully qualified names of the test classes in this target */
  classes: string[];
}
```

### BuildTargetScalaMainClasses: request

**Deprecated**: Use buildTarget/jvmRunEnvironment instead

The build target main classes request is sent from the client to the server to
query for the list of main classes that can be fed as arguments to
`buildTarget/run`. This method can be used for the same use cases than the
[Scala Test Classes Request](#scala-test-classes-request) enables.
This request may trigger a compilation on the selected build targets. The server
is free to send any number of `build/taskStart`, `build/taskProgress`,
`build/taskFinish`, `build/publishDiagnostics` and `build/logMessage`
notifications during compilation before completing the response.
The client will get a `originId` field in `ScalaMainClassesResult` if the
`originId` field in the `ScalaMainClassesParams` is defined.

- method: `buildTarget/scalaMainClasses`
- params: `ScalaMainClassesParams`
- result: `ScalaMainClassesResult`

#### ScalaMainClassesParams

```ts
export interface ScalaMainClassesParams {
  targets: BuildTargetIdentifier[];

  /** An optional number uniquely identifying a client request. */
  originId?: string;
}
```

#### ScalaMainClassesResult

```ts
export interface ScalaMainClassesResult {
  items: ScalaMainClassesItem[];

  /** An optional id of the request that triggered this result. */
  originId?: string;
}
```

#### ScalaMainClassesItem

```ts
export interface ScalaMainClassesItem {
  /** The build target that contains the test classes. */
  target: BuildTargetIdentifier;

  /** The main class item. */
  classes: ScalaMainClass[];
}
```

#### ScalaMainClass

```ts
export interface ScalaMainClass {
  /** The main class to run. */
  className: string;

  /** The user arguments to the main entrypoint.
   * Deprecated: Use `buildTarget/run` params instead */
  arguments: string[];

  /** The jvm options for the application. */
  jvmOptions: string[];

  /** The environment variables for the application.
   * Deprecated: Use `buildTarget/run` params instead */
  environmentVariables?: string[];
}
```

## RunParamsData kinds

### ScalaMainClass

This structure is embedded in
the `data?: RunParamsData` field, when
the `dataKind` field contains `"scala-main-class"`.

## DebugSessionParamsData kinds

### ScalaAttachRemote

This structure is embedded in
the `data?: DebugSessionParamsData` field, when
the `dataKind` field contains `"scala-attach-remote"`.

#### ScalaAttachRemote

The debug session will connect to a running process. The DAP client will send the port of the running process later.

```ts
export interface ScalaAttachRemote {}
```

### ScalaMainClass

This structure is embedded in
the `data?: DebugSessionParamsData` field, when
the `dataKind` field contains `"scala-main-class"`.

## BuildTargetData kinds

### ScalaBuildTarget

This structure is embedded in
the `data?: BuildTargetData` field, when
the `dataKind` field contains `"scala"`.

#### ScalaBuildTarget

`ScalaBuildTarget` is a basic data structure that contains scala-specific
metadata for compiling a target containing Scala sources.

```ts
export interface ScalaBuildTarget {
  /** The Scala organization that is used for a target. */
  scalaOrganization: string;

  /** The scala version to compile this target */
  scalaVersion: string;

  /** The binary version of scalaVersion.
   * For example, 2.12 if scalaVersion is 2.12.4. */
  scalaBinaryVersion: string;

  /** The target platform for this target */
  platform: ScalaPlatform;

  /** A sequence of Scala jars such as scala-library, scala-compiler and scala-reflect. */
  jars: URI[];

  /** The jvm build target describing jdk to be used */
  jvmBuildTarget?: JvmBuildTarget;
}
```

#### ScalaPlatform

```ts
export enum ScalaPlatform {
  Jvm = 1,

  Js = 2,

  Native = 3,
}
```

## DiagnosticData kinds

### ScalaDiagnostic

This structure is embedded in
the `data?: DiagnosticData` field, when
the `dataKind` field contains `"scala"`.

#### ScalaDiagnostic

`ScalaDiagnostic` is a data structure that contains Scala-specific
metadata generated by Scala compilation.

```ts
export interface ScalaDiagnostic {
  /** Actions (also known as quick fixes) that are able to either fix or address
   * the issue that is causing this diagnostic. */
  actions?: ScalaAction[];
}
```

#### ScalaAction

A Scala action represents a change that can be performed in code.
See also [LSP: Code Action Request](https://microsoft.github.io/language-server-protocol/specifications/lsp/3.17/specification/#textDocument_codeAction).

**Note**: In LSP, `CodeAction` appears only as a response to a `textDocument/codeAction` request,
whereas ScalaAction is intended to be returned as `Diagnostics.data.actions`.

```ts
export interface ScalaAction {
  /** A short, human-readable, title for this code action. */
  title: string;

  /** A description that may be shown to the user client side to explain the action. */
  description?: string;

  /** The workspace edit this code action performs. */
  edit?: ScalaWorkspaceEdit;
}
```

#### ScalaWorkspaceEdit

A workspace edit represents changes to many resources managed in the workspace.

```ts
export interface ScalaWorkspaceEdit {
  changes: ScalaTextEdit[];
}
```

#### ScalaTextEdit

A textual edit applicable to a text document.

```ts
export interface ScalaTextEdit {
  /** The range of the text document to be manipulated. To insert
   * text into a document create a range where start === end. */
  range: Range;

  /** The string to be inserted. For delete operations use an
   * empty string. */
  newText: string;
}
```

## TestParamsData kinds

### ScalaTestParams

This structure is embedded in
the `data?: TestParamsData` field, when
the `dataKind` field contains `"scala-test"`.

#### ScalaTestParams

`ScalaTestParams` contains scala-specific metadata for testing Scala targets.

```ts
export interface ScalaTestParams {
  /** The test classes to be run in this test execution.
   * It is the result of `buildTarget/scalaTestClasses`. */
  testClasses?: ScalaTestClassesItem[];

  /** The JVM options to run tests with. They replace any options
   * that are defined by the build server if defined. */
  jvmOptions?: string[];
}
```

### ScalaTestSuiteClasses

This structure is embedded in
the `data?: TestParamsData` field, when
the `dataKind` field contains `"scala-test-suites"`.

#### ScalaTestSuiteClasses

Each element of this array is a fully qualified class name.

```ts
export type ScalaTestSuiteClasses = string[];
```

### ScalaTestSuites

This structure is embedded in
the `data?: TestParamsData` field, when
the `dataKind` field contains `"scala-test-suites-selection"`.

#### ScalaTestSuites

```ts
export interface ScalaTestSuites {
  /** The fully qualified names of the test classes in this target and the tests in this test classes */
  suites: ScalaTestSuiteSelection[];

  /** Additional jvmOptions which will be passed to the forked JVM */
  jvmOptions: string[];

  /** Enviroment variables should be an array of strings in format KEY=VALUE
   * Deprecated: Use `buildTarget/test` params instead */
  environmentVariables: string[];
}
```

#### ScalaTestSuiteSelection

```ts
export interface ScalaTestSuiteSelection {
  /** Fully qualified name of the test suite class */
  className: string;

  /** List of tests which should be run within this test suite.
   * Empty collection means that all of them are supposed to be executed. */
  tests: string[];
}
```
