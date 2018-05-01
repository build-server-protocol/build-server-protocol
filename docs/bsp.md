---
title: Build Server Protocol
---

# 1. Build Server Protocol

This document is a thought experiment for how a potential Build Server Protocol (BSP) would look like.
The document is written by Ólafur Páll Geirsson, Jorge Vicente Cantero with feedback from Guillaume Martres and Eugene Burmako.

The Build Server Protocol is not an official Scala Center project nor is it an approved standard.
Everything in this document is subject to change and open for discussions.
Including core data structures.

## 1.1. Motivation

The problem this document aims to address is the multiplied effort required to integrate between available Scala language servers and build tools.
Currently, every language server must implement custom integrations for the most popular Scala Build tools in order to extract compilation information such as classpaths and source directories.
Likewise, new Scala build tools are expected to integrate with all available Scala IDEs.
This explosion of integrations is a growing problem due to the recent proliferation of both Scala language servers and build tools supporting Scala.

## 1.2. Background

The Build Server Protocol takes inspiration from the Language Server Protocol (LSP).
Unlike in the Language Server Protocol, the language server is referred to as the “client” and a build tool such as sbt/pants/gradle/bazel is referred to as the “server”.

The best way to read this document is by considering it as a wishlist from the perspective of an IDE developer.
Consider this document as our personal vision for how a bi-directional communication protocol between a build tool and language server could look like.

The code listings in this document are written using Scala syntax.
Every data strucuture in this document has a direct translation to JSON and Protobuf.
See [Appendix](#15-appendix) for schema definitions that can be used to automatically generate
bindings for different target languages.

<!-- TOC -->

- [1. Build Server Protocol](#1-build-server-protocol)
    - [1.1. Motivation](#11-motivation)
    - [1.2. Background](#12-background)
    - [1.3. Status](#13-status)
    - [1.4. Base protocol](#14-base-protocol)
    - [1.5. Basic Json Structures](#15-basic-json-structures)
        - [1.5.1. Build Target](#151-build-target)
        - [1.5.2. Build Target Identifier](#152-build-target-identifier)
    - [1.6. Actual Protocol](#16-actual-protocol)
        - [1.6.1. Server Lifetime](#161-server-lifetime)
            - [1.6.1.1. Initialize Build Request](#1611-initialize-build-request)
            - [1.6.1.2. Initialized Build Notification](#1612-initialized-build-notification)
            - [1.6.1.3. Shutdown Build Request](#1613-shutdown-build-request)
            - [1.6.1.4. Exit Build Notification](#1614-exit-build-notification)
        - [1.6.2. Workspace Build Targets Request](#162-workspace-build-targets-request)
        - [1.6.3. Build Target Changed Notification](#163-build-target-changed-notification)
        - [1.6.4. Build Target Text Documents Request](#164-build-target-text-documents-request)
        - [1.6.5. Text Document Build Targets Request](#165-text-document-build-targets-request)
        - [1.6.6. Dependency Sources Request](#166-dependency-sources-request)
        - [1.6.7. Compile Request](#167-compile-request)
        - [1.6.8. Test Request](#168-test-request)
    - [1.7. Extensions](#17-extensions)
        - [1.7.1. Scala](#171-scala)
            - [1.7.1.1. Scala Build Target](#1711-scala-build-target)
            - [1.7.1.2. Scalac Options Request](#1712-scalac-options-request)
            - [1.7.1.3. Scala Test Classes Request](#1713-scala-test-classes-request)
    - [1.8. Appendix](#18-appendix)
        - [1.8.1. Protobuf schema definitions](#181-protobuf-schema-definitions)
        - [1.8.2. Scala Bindings](#182-scala-bindings)

<!-- /TOC -->



## 1.3. Status

A protocol is only worth as much as the quality of the available clients and servers that implement the protocol.
A proof-of-concept integration between [scalameta/metals](https://github.com/scalameta/metals) and [scalacenter/bloop](https://github.com/scalacenter/bloop) using the Build Server Protocol is in the works.
See [sbt/sbt#3890](https://github.com/sbt/sbt/issues/3890) for a discussion on the next steps for adding BSP support in sbt.

The best way to share your thoughts on the Build Server Protocol is to open an issue or pull request to this repository.

## 1.4. Base protocol

The base protocol is identical to the language server base protocol. See
<https://microsoft.github.io/language-server-protocol/specification> for
more details.

Like the language server protocol, the build server protocol defines a
set of JSON-RPC request, response and notification messages which are
exchanged using the base protocol.

## 1.5. Basic Json Structures

In addition to basic data structures in the language server protocol,
the build server protocol defines the following additional data
structures.

### 1.5.1. Build Target

Build target contains metadata about an artifact (for example library, test, or binary artifact).
Using vocabulary of other build tools:

* sbt: a build target is a combined project + config. Example:
  * a regular JVM project with main and test configurations will have
    2 build targets, one for main and one for test.
  * a single configuration in a single project that contains both Java and Scala sources maps to one BuildTarget.
  * a project with crossScalaVersions 2.11 and 2.12 containing main and test configuration in each will have 4 build targets.
  * a Scala 2.11 and 2.12 cross-built project for Scala.js and the JVM with main and test configurations will have 8 build targets.
* pants: a pants target corresponds one-to-one with a BuildTarget
* bazel: a bazel target corresponds one-to-one with a BuildTarget

The general idea is that the BuildTarget data structure should contain only information that is is fast or cheap to compute.

```scala
trait BuildTarget {

  /** The target’s unique identifier */
  def id: BuildTargetIdentifier

  /** A human readable name for this target.
    * May be presented in the user interface.
    * Should be unique if possible.
    * The id.uri is used if None. */
  def displayName: Option[String]

  /** The category of this build target. */
  def kind: List[BuildTargetKind]

  /** The set of languages that this target contains.
    * The ID string for each language is defined in the LSP. */
  def languageIds: List[String]

  /** Language-specific metadata about this target.
    * See ScalaBuildTarget as an example. */
  def data: Option[Json] // Note, matches `any` in the LSP.
}

object BuildTargetKind {
  val Library = 1
  /** This target can be compiled and tested via
    * method buildTarget/test */
  val Test = 2
  /** This target can be tested via method
    * buildTarget/test and may run slower compared to a Test. */
  val IntegrationTest = 3
  /** This target can be run via method buildTarget/run */
  val Main = 4
}
```

### 1.5.2. Build Target Identifier

A unique identifier for a target.

```scala
trait BuildTargetIdentifer {
  /** The target’s URI */
  def uri: URI
}
```

## 1.6. Actual Protocol

Unlike the language server protocol, the build server protocol does not
support dynamic registration of capabilities.
The motivation for this change is simplicity.
If a motivating example for dynamic registration comes up this decision can be reconsidered.
The server and client capabilities must be communicated through the initialize request.

### 1.6.1. Server Lifetime

Like the language server protocol, the current protocol specification
defines that the lifetime of a build server is managed by the client
(e.g. a language server like Dotty IDE). It is up to the client to
decide when to start (process-wise) and when to shutdown a server.

#### 1.6.1.1. Initialize Build Request

Like the language server protocol, the initialize request is sent as the
first request from the client to the server. If the server receives a
request or notification before the initialize request it should act as
follows:

* For a request the response should be an error with code: -32002.
  The message can be picked by the server.

* Notifications should be dropped, except for the exit notification.
  This will allow the exit of a server without an initialize
  request.

Until the server has responded to the initialize request with an
InitializeBuildResult, the client must not send any additional requests
or notifications to the server.

Request:

* method: ‘build/initialize’

* params: InitializeBuildParams defined as follows

```scala
trait InitializeBuildParams {

  /** The rootUri of the workspace */
  def rootUri: DocumentUri

  /** The capabilities of the client */
  def capabilities: BuildClientCapabilities

}

trait BuildClientCapabilities {
    /** The languages that this client supports.
      * The ID strings for each language is defined in the LSP.
      * The server must never respond with build targets for other
      * languages than those that appear in this list. */
    def languageIds: List[String]
}
```

Response:

* result: InitializeBuildResult defined as follows

```scala
trait InitializeBuildResult {
  /** The capabilities of the build server */
  capabilities: BuildServerCapabilities
}

trait BuildServerCapabilities {
  /** The server can compile targets via method buildTarget/compile */
  compileProvider: Boolean
  /** The server can test targets via method buildTarget/test */
  testProvider: Boolean
  /** The server can provide a list of targets that contain a
    * single text document via the method textDocument/buildTargets */
  textDocumentBuildTargetsProvider: Boolean
  /** The server provides sources for library dependencies
    * via method buildTarget/dependencySources */
  dependencySourcesProvider: Boolean
  /** The server sends notifications to the client on build
    * target change events via buildTarget/didChange */
  buildTargetChangedProvider: Boolean
}
```

#### 1.6.1.2. Initialized Build Notification

Like the language server protocol, the initialized notification is sent
from the client to the server after the client received the result of
the initialize request but before the client is sending any other
request or notification to the server. The server can use the
initialized notification for example to initialize intensive computation
such as dependency resolution or compilation. The initialized
notification may only be sent once.

Notification:

* method: ‘build/initialized’
* params: InitializedBuildParams defined as follows

```scala
trait InitializedBuildParams {

}
```

#### 1.6.1.3. Shutdown Build Request

Like the language server protocol, the shutdown build request is sent
from the client to the server. It asks the server to shut down, but to
not exit (otherwise the response might not be delivered correctly to the
client). There is a separate exit notification that asks the server to
exit.

Request:

* method: ‘shutdown’
* params: `null`

Response:

* result: `null`
* error: code and message set in case an exception happens during
  shutdown request.

#### 1.6.1.4. Exit Build Notification

Like the language server protocol, a notification to ask the server to
exit its process. The server should exit with success code 0 if the
shutdown request has been received before; otherwise with error code 1.

Notification:

* method: ‘exit’
* params: `null`

### 1.6.2. Workspace Build Targets Request

The workspace build targets request is sent from the client to the server to ask for the list of all available build targets in the workspace.

Request:

* method: 'workspace/buildTargets'
* params: `WorkspaceBuildTargetsParams`, defined as follows

```scala
trait WorkspaceBuildTargetsParams {
}
```

Response:

* result: `WorkspaceBuildTargetsResult`, defined as follows

```scala
trait WorkspaceBuildTargetsResult {
  /** The build targets in this workspace that
    * contain sources with the given language ids. */
  def targets: List[BuildTarget]
}
```

### 1.6.3. Build Target Changed Notification

The build target changed notification is sent from the server to the client to signal a change in a build target.
The server communicates during the initialize handshake whether this method is supported or not.

Notification:

* method: 'buildTarget/didChange'
* params: `DidChangeBuildTargetParams` defined as follows:

```scala
trait DidChangeBuildTargetParams {
  def changes: List[BuildTargetEvent]
}
trait BuildTargetEvent {
  /** The identifier for the changed build target */
  def uri: URI
  /** The kind of change for this build target */
  def kind: Option[BuildTargetEventKind]

  /** Any additional metadata about what information changed. */
  def data: Option[Json]
}
object BuildTargetEventKind {
  val Created = 1
  val Changed = 2
  val Deleted = 3
}
```

### 1.6.4. Build Target Text Documents Request

The build target text documents request is sent from the client to the server to query for the list of source files that are part of a given list of build targets.

* method: `buildTarget/textDocuments`
* params: `BuildTargetTextDocumentsParams`

```scala
trait BuildTargetTextDocumentsParams {
  def targets: List[BuildTargetIdentifier]
}
```

Response:

* result: `BuildTargetTextDocumentsResponse`, defined as follows

```scala
trait BuildTargetTextDocumentsResponse {
  /** The source files used by this target */
  def textDocuments: List[TextDocumentIdentifier]
}
```

### 1.6.5. Text Document Build Targets Request

The text document build targets request is sent from the client to the server to query for the list of targets containing the given text document.
The server communicates during the initialize handshake whether this method is supported or not.

This request may be considered as the inverse of `buildTarget/textDocuments`.
This method can be used by a language server on `textDocument/didOpen` to lookup which compiler instance to use to compile that given text document.
In the case there are multiple targets (for example different platforms: JVM/JS, or x86/ARM) containing the same source file, the language server may present in the editor multiple options via `textDocument/codeLens` to configure how to dis-ambiguate.

* method: `textDocument/buildTargets`
* params: `TextDocumentBuildTargetsParams`, defined as follows

```scala
trait TextDocumentBuildTargetsParams {
  def textDocument: TextDocumentIdentifier
}
```

Response:

* result: `TextDocumentBuildTargetsResult`, defined as follows

```scala
trait TextDocumentBuildTargetsResult {
  def targets: List[BuildTargetIdentifier]
}
```

### 1.6.6. Dependency Sources Request

The build target dependency sources request is sent from the client to the server to query for the list of sources for the dependency classpath of a given list of build targets.
The server communicates during the initialize handshake whether this method is supported or not.
This method can be used by a language server on `textDocument/definition` to "Go to definition" from project sources to dependency sources.

* method: `buildTarget/dependencySources`
* params: `DependencySourcesParams`

```scala
trait DependencySourcesParams {
  def targets: List[BuildTargetIdentifier]
}
```

Response:

* result: `DependencySourcesResult`, defined as follows

```scala
trait DependencySourcesResult {
  def items: List[DependencySourcesItem]
}
trait DependencySourcesItem {
  def target: BuildTargetIdentifier
  /** List of resources containing source files of the
    * target's dependencies.
    * Can be jar files, zip files, or directories. */
  def sources: List[URI]
}
```

### 1.6.7. Compile Request

The compile build target request is sent from the client to the server to compile the given list of build targets.
The server communicates during the initialize handshake whether this method is supported or not.
This method can for example be used by a language server before `textDocument/rename` to ensure that all workspace sources typecheck correctly and are up-to-date.

* method: `buildTarget/compile`
* params: `CompileParams`

```scala
trait CompileParams {
  def targets: List[BuildTargetIdentifier]

  /** Optional arguments to the compilation process. */
  def arguments: List[Json]
}
```

Response:

* result: `CompileReport`, defined as follows

```scala
trait CompileReport {
  def items: List[CompileReportItem]
}
trait CompileReportItem {

  /** The total number of reported errors compiling this target. */
  def errors: Long

  /** The total number of reported warnings compiling the target. */
  def warnings: Long

  /** The total number of milliseconds it took to compile the target. */
  def time: Option[Long]

  /** The total number of lines of code in the given target. */
  def linesOfCode: Option[Long]

}
```

The server is free to send any number of `textDocument/publishDiagnostics` and `window/logMessage` notifications during compilation before completing the response.
The client is free to forward these messages to the LSP editor client.

### 1.6.8. Test Request

The test build target request is sent from the client to the server to test the given list of build targets.
The server communicates during the initialize handshake whether this method is supported or not.

* method: `buildTarget/Test`
* params: `TestParams`

```scala
trait TestParams {
  def targets: List[BuildTargetIdentifier]
  /** Optional arguments to the test execution. */
  def arguments: List[Json]
}
```

Response:

* result: `TestReport`, defined as follows

```scala
trait TestReport {
  def items: List[TestReportItem]
}
trait TestReportItem {

  /** The compile times before executing tests if the target.
    * An empty field means the target may have already
    * been compiled beforehand. */
  def compileReport: Option[CompileReportItem]

  /** The total number of successful tests. */
  def passed: Long

  /** The total number of failed tests. */
  def failed: Long

  /** The total number of milliseconds it took to run the tests.
    * Should not include compile times. */
  def time: Option[Long]
}
```

The server is free to send any number of `textDocument/publishDiagnostics` and `window/logMessage` notifications during compilation before completing the response.
The client is free to forward these messages to the LSP editor client.

## 1.7. Extensions

The build server protocol is designed to be extended with language specific data structures and methods.

### 1.7.1. Scala

The following section contains Scala-specific extensions to the build server protocol.

#### 1.7.1.1. Scala Build Target

`ScalaBuildTarget` is a basic data structure that contains scala-specific metadata for compiling a target containing Scala sources.
This metadata is embedded in the `data: Option[Json]` field of the `BuildTarget` definition.

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
  def platform: ScalaPlatform

}

object ScalaPlatform {
  val JVM = 1
  val JS = 2
  val Native = 3
}
```

#### 1.7.1.2. Scalac Options Request

The build target scalac options request is sent from the client to the server to query for the list of compiler options necessary to compile in a given list of targets.

* method: `buildTarget/scalacOptions`
* params: `ScalacOptionsParams`

```scala
trait ScalacOptionsParams {
  def targets: List[BuildTargetIdentifier]
}
```

Response:

* result: `ScalacOptionsResult`, defined as follows

```scala
trait ScalacOptionsResult {
  def items: List[ScalcOptionItem]
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
    def classpath: List[String]
    /** The output directory for classfiles produced by this target */
    def classDirectory: String
}
```

#### 1.7.1.3. Scala Test Classes Request

The build target scalac options request is sent from the client to the server to query for the list of fully qualified names of test clases in a given list of targets.
This method can for example be used by a language server by using `textDocument/codeLens` to attach a "Run test suite" button above the definition of a test suite.
By clicking on the button, the language server can pass the fully qualified name of the test class as an argument to the `buildTarget/test` request.

* method: `buildTarget/scalaTestClasses`
* params: `ScalaTestClassesParams`

```scala
trait ScalaTestClassesParams {
  def targets: List[BuildTargetIdentifier]
}
```

Response:

* result: `ScalaTestClassesResult`, defined as follows

```scala
trait ScalaTestClassesResult {
  def items: List[ScalaTestClassesItem]
}
trait ScalaTestClassesItem {
    def target: BuildTargetIdentifier
    /** The fully qualified names of the test classes in this target */
    def classes: List[String]
}
```

## 1.8. Appendix

### 1.8.1. Protobuf schema definitions

The data structures presented in this document are accompanied by protobuf schema definitions.
See [bsp.proto](src/main/protobuf/bsp.proto).

### 1.8.2. Scala Bindings

A Scala library implementation of this communication protocol is available in this repository.
The public API of this library currently has three direct Scala dependencies:

* ScalaPB - for generation of Scala sources from protobuf schema
* Monix - for asynchronous programming primitives
* Circe - for JSON serialization and parsing of protocol data structures

If there is demand, it should be possible to refactor out all three dependencies to provide a zero dependency core module.
