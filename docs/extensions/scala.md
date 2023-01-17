---
id: scala
title: Scala Extension
sidebar_label: Scala
---

The following section contains Scala-specific extensions to the build server
protocol.

## Scala Build Target

`ScalaBuildTarget` is a basic data structure that contains scala-specific
metadata for compiling a target containing Scala sources. This metadata is
embedded in the `data: Option[Json]` field of the `BuildTarget` definition, when
the `dataKind` field contains "scala".

```ts
export interface ScalaBuildTarget {
  /** The Scala organization that is used for a target. */
  scalaOrganization: String;

  /** The scala version to compile this target */
  scalaVersion: String;

  /** The binary version of scalaVersion.
   * For example, 2.12 if scalaVersion is 2.12.4. */
  scalaBinaryVersion: String;

  /** The target platform for this target */
  platform: Int;

  /** A sequence of Scala jars such as scala-library, scala-compiler and scala-reflect. */
  jars: String[];

  /** The jvm build target describing jdk to be used */
  jvmBuildTarget?: JvmBuildTarget;
}

export namespace ScalaPlatform {
  export const JVM = 1;
  export const JS = 2;
  export const Native = 3;
}
```

## Scala Test Params

`ScalaTestParams` contains scala-specific metadata for testing Scala targets.
This metadata is embedded in the `data: Option[Json]` field of the
`buildTarget/test` request when the `dataKind` field contains "scala-test".

```ts
export interface ScalaTestParams {
  /** The test classes to be run in this test execution.
   * It is the result of `buildTarget/scalaTestClasses`. */
  testClasses?: ScalaTestClassesItem[];

  /** The JVM options to run tests with. They replace any options
   * that are defined by the build server if defined.
   */
  jvmOptions?: String[];
}
```

## Scalac Options Request

The build target scalac options request is sent from the client to the server to
query for the list of compiler options necessary to compile in a given list of
targets.

- method: `buildTarget/scalacOptions`
- params: `ScalacOptionsParams`

```ts
export interface ScalacOptionsParams {
  targets: BuildTargetIdentifier[];
}
```

Response:

- result: `ScalacOptionsResult`, defined as follows

```ts
export interface ScalacOptionsResult {
  items: List[ScalacOptionsItem];
}

export interface ScalacOptionsItem {
  target: BuildTargetIdentifier;

  /** Additional arguments to the compiler.
   * For example, -deprecation. */
  options: List[String];

  /** The dependency classpath for this target, must be
   * identical to what is passed as arguments to
   * the -classpath flag in the command line interface
   * of scalac. */
  classpath: List[Uri];

  /** The output directory for classfiles produced by this target */
  classDirectory: Uri;
}
```

## Scala Test Classes Request

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

- method: `buildTarget/scalaTestClasses`
- params: `ScalaTestClassesParams`

```ts
export interface ScalaTestClassesParams {
  targets: BuildTargetIdentifier[];

  /** An optional number uniquely identifying a client request. */
  originId?: String;
}
```

Response:

- result: `ScalaTestClassesResult`, defined as follows
- error: code and message set in case an exception happens during shutdown
  request.

```ts
export interface ScalaTestClassesResult {
  items: ScalaTestClassesItem[];

  /** An optional id of the request that triggered this result. */
  originId?: String;
}

export interface ScalaTestClassesItem {
  /** The build target that contains the test classes. */
  target: BuildTargetIdentifier;

  /**
   * Name of the the framework to which classes belong.
   * It's optional in order to maintain compatibility, however it is expected
   * from the newer implementations to not leave that field unspecified.
   */
  framework?: String;

  /** The fully qualified names of the test classes in this target */
  classes: String[];
}
```

This request may trigger a compilation on the selected build targets. The server
is free to send any number of `build/task*`, `build/publishDiagnostics` and
`build/logMessage` notifications during compilation before completing the
response.

The client will get a `originId` field in `ScalaTestClassesResult` if the
`originId` field in the `ScalaTestClassesParams` is defined.

## Scala Main Classes Request

The build target main classes request is sent from the client to the server to
query for the list of main classes that can be fed as arguments to
`buildTarget/run`. This method can be used for the same use cases than the
[Scala Test Classes Request](#scala-test-classes-request) enables.

- method: `buildTarget/scalaMainClasses`
- params: `ScalaMainClassesParams`

```ts
export interface ScalaMainClassesParams {
  targets: BuildTargetIdentifier[];

  /** An optional number uniquely identifying a client request. */
  originId?: String;
}
```

Response:

- result: `ScalaMainClassesResult`, defined as follows
- error: code and message set in case an exception happens during shutdown
  request.

```ts
export interface ScalaMainClassesResult {
  items: ScalaMainClassesItem[];

  /** An optional id of the request that triggered this result. */
  originId?: String;
}

export interface ScalaMainClassesItem {
  /** The build target that contains the test classes. */
  target: BuildTargetIdentifier;

  /** The main class item. */
  classes: ScalaMainClass[];
}

export interface ScalaMainClass {
  /** The main class to run. */
  class: String;

  /** The user arguments to the main entrypoint. */
  arguments: String[];

  /** The jvm options for the application. */
  jvmOptions: String[];

  /** The environment variables for the application. */
  environmentVariables?: String[];
}
```

This request may trigger a compilation on the selected build targets. The server
is free to send any number of `build/taskStart`, `build/taskProgress`,
`build/taskFinish`, `build/publishDiagnostics` and `build/logMessage`
notifications during compilation before completing the response.

The client will get a `originId` field in `ScalaMainClassesResult` if the
`originId` field in the `ScalaMainClassesParams` is defined.

## Scala specific data kinds for debugging


A Scala build server which implements the `debugSession/start` endpoint, should
support following data kinds, which are held in `data`.

- `scala-main-class`, for which `data` has following shape

```ts
interface ScalaMainClass {
    class: string;
    arguments: string[];
    jvmOptions: string[];
    environmentVariables: string[];
}
```

- `scala-test-suites`, for which `data` is a `string[]`. Each element of that
  array is a fully qualified class name.

- `scala-test-suites-selection`, for which `data` has following shape

```ts
interface ScalaTestSuites {
    /** The fully qualified names of the test classes in this target and the tests in this test classes */
    suites: ScalaTestSuiteSelection[];
    jvmOptions: string[];
    environmentVariables: string[];
}

interface ScalaTestSuiteSelection {
    /** The test class to run. */
    className: string;
    /** The selected tests to run. */
    tests: string[];
}
```

- `scala-attach-remote`, for which data is an array of
  [BuildTargetIdentifier](https://github.com/build-server-protocol/build-server-protocol/blob/master/bsp4j/src/main/xtend-gen/ch/epfl/scala/bsp4j/BuildTargetIdentifier.java).

You can find the bsp4j classes for this in the following places:

  - [ScalaTestSuites.java](https://github.com/build-server-protocol/build-server-protocol/blob/master/bsp4j/src/main/xtend-gen/ch/epfl/scala/bsp4j/ScalaTestSuites.java)
  - [ScalaTestSuiteSelection.java](https://github.com/build-server-protocol/build-server-protocol/blob/master/bsp4j/src/main/xtend-gen/ch/epfl/scala/bsp4j/ScalaTestSuiteSelection.java)
