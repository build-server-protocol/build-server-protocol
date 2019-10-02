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

```scala
trait ScalaBuildTarget {
  /** The Scala organization that is used for a target. */
  def scalaOrganization: String

  /** The scala version to compile this target */
  def scalaVersion: String

  /** The binary version of scalaVersion.
    * For example, 2.12 if scalaVersion is 2.12.4. */
  def scalaBinaryVersion: String

  /** The target platform for this target */
  def platform: Int

  /** A sequence of Scala jars such as scala-library, scala-compiler and scala-reflect. */
  def jars: List[String]
}

object ScalaPlatform {
  val JVM = 1
  val JS = 2
  val Native = 3
}
```

## Scala Test Params

`ScalaTestParams` contains scala-specific metadata for testing Scala targets.
This metadata is embedded in the `data: Option[Json]` field of the
`buildTarget/test` request when the `dataKind` field contains "scala-test".

```scala
trait ScalaTestParams {
  /** The test classes to be run in this test execution.
    * It is the result of `buildTarget/scalaTestClasses`. */
  def testClasses: Option[List[ScalaTestClassesItem]]
}
```

## Scalac Options Request

The build target scalac options request is sent from the client to the server to
query for the list of compiler options necessary to compile in a given list of
targets.

- method: `buildTarget/scalacOptions`
- params: `ScalacOptionsParams`

```scala
trait ScalacOptionsParams {
  def targets: List[BuildTargetIdentifier]
}
```

Response:

- result: `ScalacOptionsResult`, defined as follows

```scala
trait ScalacOptionsResult {
  def items: List[ScalacOptionsItem]
}

trait ScalacOptionsItem {
    def target: BuildTargetIdentifier

    /** Additional arguments to the compiler.
      * For example, -deprecation. */
    def options: List[String]

    /** The dependency classpath for this target, must be
      * identical to what is passed as arguments to
      * the -classpath flag in the command line interface
      * of scalac. */
    def classpath: List[Uri]

    /** The output directory for classfiles produced by this target */
    def classDirectory: Uri
}
```

## Scala Test Classes Request

The build target scala test options request is sent from the client to the
server to query for the list of fully qualified names of test clases in a given
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

```scala
trait ScalaTestClassesParams {
  def targets: List[BuildTargetIdentifier]

  /** An optional number uniquely identifying a client request. */
  def originId: Option[String]
}
```

Response:

- result: `ScalaTestClassesResult`, defined as follows
- error: code and message set in case an exception happens during shutdown
  request.

```scala
trait ScalaTestClassesResult {
  def items: List[ScalaTestClassesItem]

  /** An optional id of the request that triggered this result. */
  def originId: Option[String]

}

trait ScalaTestClassesItem {
  /** The build target that contains the test classes. */
  def target: BuildTargetIdentifier

  /** The fully qualified names of the test classes in this target */
  def classes: List[String]
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

```scala
trait ScalaMainClassesParams {
  def targets: List[BuildTargetIdentifier]

  /** An optional number uniquely identifying a client request. */
  def originId: Option[String]
}
```

Response:

- result: `ScalaMainClassesResult`, defined as follows
- error: code and message set in case an exception happens during shutdown
  request.

```scala
trait ScalaMainClassesResult {
  def items: List[ScalaMainClassesItem]

  /** An optional id of the request that triggered this result. */
  def originId: Option[String]

}

trait ScalaMainClassesItem {
  /** The build target that contains the test classes. */
  def target: BuildTargetIdentifier

  /** The main class item. */
  def classes: List[ScalaMainClass]
}

trait ScalaMainClass {
  /** The main class to run. */
  def class: String

  /** The user arguments to the main entrypoint. */
  def arguments: List[String]

  /** The jvm options for the application. */
  def jvmOptions: List[String]
}

```

This request may trigger a compilation on the selected build targets. The server
is free to send any number of `build/taskStart`, `build/taskProgress`,
`build/taskFinish`, `build/publishDiagnostics` and `build/logMessage`
notifications during compilation before completing the response.

The client will get a `originId` field in `ScalaMainClassesResult` if the
`originId` field in the `ScalaMainClassesParams` is defined.
