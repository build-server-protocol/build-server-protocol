---
title: Build Server Protocol
---

# 1. Build Server Protocol

This document describes version 1.0 of the build server protocol.

Edits to this specification can be made via a pull request against this markdown document.

## 1.1. Motivation

The problem this document aims to address is the multiplied effort required to integrate between
available language servers/editors and build tools. Currently, every language server must implement
a custom integration for each supported build tool in order to extract compilation information such
as classpaths, source directories or compiler diagnostics. Likewise, new build tools are expected to
integrate with all available IDEs. The growing number of language servers and build tools in the
wider programming community means tooling developers spend a lot of time working on these
integrations.

The Build Server Protocol aims to define common functionality that both build tools (servers) and
language servers/editors (clients) understand The goal is to simplify integrations between these
tools to provide the best experience to end users (developers).

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
      - [1.6.1.5. Show message](#1615-show-message)
      - [1.6.1.6. Log message](#1616-log-message)
      - [1.6.1.7. Publish Diagnostics](#1617-publish-diagnostics)
    - [1.6.2. Workspace Build Targets Request](#162-workspace-build-targets-request)
    - [1.6.3. Build Target Changed Notification](#163-build-target-changed-notification)
    - [1.6.4. Build Target Text Documents Request](#164-build-target-text-documents-request)
    - [1.6.5. Text Document Build Targets Request](#165-text-document-build-targets-request)
    - [1.6.6. Dependency Sources Request](#166-dependency-sources-request)
    - [1.6.7. Resources Request](#167-resources-request)
    - [1.6.8. Compile Request](#168-compile-request)
    - [1.6.9. Test Request](#169-test-request)
    - [1.6.10. Run Request](#1610-run-request)
  - [1.7. Extensions](#17-extensions)
    - [1.7.1. Scala](#171-scala)
      - [1.7.1.1. Scala Build Target](#1711-scala-build-target)
      - [1.7.1.2. Scalac Options Request](#1712-scalac-options-request)
      - [1.7.1.3. Scala Test Classes Request](#1713-scala-test-classes-request)
      - [1.7.1.4. Scala Main Classes Request](#1714-scala-main-classes-request)
    - [1.7.2. Sbt](#172-sbt)
      - [1.7.2.1. Sbt Build Target](#1721-sbt-build-target)
  - [1.8. Appendix](#18-appendix)
    - [1.8.1. Protobuf schema definitions](#181-protobuf-schema-definitions)
    - [1.8.2. Scala Bindings](#182-scala-bindings)
    - [1.8.3. FAQ](#183-faq)

## 1.2. Background

The Build Server Protocol takes inspiration from the Language Server Protocol (LSP).
Unlike in the Language Server Protocol, the language server or editor is referred to as the “client” and a build tool such as sbt/pants/gradle/bazel is referred to as the “server”.

The best way to read this document is by considering it as a wishlist from the perspective of an IDE developer.
Consider this document as our personal vision for how a bi-directional communication protocol between a build tool and language server could look like.

The code listings in this document are written using Scala syntax.
Every data strucuture in this document has a direct translation to JSON and Protobuf.
See [Appendix](#15-appendix) for schema definitions that can be used to automatically generate
bindings for different target languages.


## 1.3. Status

The Build Server Protocol is not an approved standard. Everything in this
document is subject to change and open for discussions, including core data
structures.

A protocol is only worth as much as the quality of the clients and servers that implement the
protocol. There is a lot of activity in this area.

On the client side, IntelliJ is the first client of the Build Server
Protocol. There are several other language servers, like [Dotty
IDE](https://github.com/lampepfl/dotty) and
[scalameta/metals](https://github.com/scalameta/metals), that are working on
integrations with BSP servers. There are future editor integrations under
way.

On the server side, [bloop](https://github.com/scalacenter/bloop) is the
first server to implement BSP. There are ongoing efforts to implement BSP
in popular build tools like [sbt](https://github.com/sbt/sbt/issues/3890).

The Build Server Protocol has been designed to be language-agnostic. We're
looking for ways to collaborate with other programming language communities
and build tool authors.

The best way to share your thoughts on the Build Server Protocol or to get
involved in its development is to open an issue or pull request to this
repository. Any help on developing integrations will be much appreciated.

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

  /** The type of build target. Useful for an IDE to show
    * targets with different kinds in the UI. */
  def kind: Int
  
  /** The capabilities of this build target. */
  def capabilities: BuildTargetCapabilities

  /** The set of languages that this target contains.
    * The ID string for each language is defined in the LSP. */
  def languageIds: List[String]

  /** Language-specific metadata about this target.
    * See ScalaBuildTarget as an example. */
  def data: Option[Json] // Note, matches `any` in the LSP.
}

object BuildTargetKind {
  final val Library = 1
  final val Test = 2
  final val IntegrationTest = 3
  final val Bench = 4
}

trait BuildTargetCapabilities {
  /** This target can be compiled by the BSP server. */
  def canCompile: Boolean
  /** This target can be tested by the BSP server. */
  def canTest: Boolean
  /** This target can be run by the BSP server. */
  def canRun: Boolean
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
  def capabilities: BuildServerCapabilities
}

trait BuildServerCapabilities {
  /** The languages the server supports compilation via method buildTarget/compile. */
  def compileProvider: {
    def languageIds: List[String]
  }
  
  /** The languages the server supports test execution via method buildTarget/test */
  def testProvider: {
    def languageIds: List[String]
  }
  
  /** The languages the server supports run via method buildTarget/run */
  def runProvider: {
    def languageIds: List[String]
  }
  
  /** The server can provide a list of targets that contain a
    * single text document via the method textDocument/buildTargets */
  def textDocumentBuildTargetsProvider: Boolean
  
  /** The server provides sources for library dependencies
    * via method buildTarget/dependencySources */
  def dependencySourcesProvider: Boolean
  
  /** The server provides all the resource dependencies
    * via method buildTarget/resources */
  def resourcesProvider: Boolean
  
  /** The server sends notifications to the client on build
    * target change events via buildTarget/didChange */
  def buildTargetChangedProvider: Boolean
}
```

Clients can use these capabilities to notify users what BSP endpoints can
and cannot be used and why.

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

#### 1.6.1.5. Show message

The show message notification is sent from a server to a client to ask the
client to display a particular message in the user interface.

Notification:

* method: ‘build/showMessage’
* params: ShowMessageParams defined as follows:

```scala
trait ShowMessageParams {
  /** The message type. See {@link MessageType}. */
  def type: Int

  /** The message id. */
  def id: string

  /** The parent id if any. */
  def parentId: Option[String]
  
  /** The request id that originated this notification. */
  def requestId: Option[String]

  /** The actual message. */
  def message: String
}
```

where `MessageType` is defined as follows:

```scala
object MessageType {
  /** An error message. */
  final val Error = 1
  /** A warning message. */
  final val Warning = 2
  /** An information message. */
  final val Info = 3
  /** A log message. */
  final val Log = 4
}
```

A `build/showMessage` notification is similar to LSP's `window/showMessage`, except for a few new
additions.

The `id` and optional `parentId` fields allow clients to structure logs in a hierarchical way (in a
tree fashion, with dropdowns, ...) to ease readability.

The `requestId` field helps clients know which request originated a notification in case several
requests are handled by the client at the same time. It will only be populated if the client
defined it in the request that triggered this notification.

#### 1.6.1.6. Log message

The log message notification is sent from the server to the client to ask the
client to log a particular message.

Notification:

* method: ‘build/logMessage’
* params: LogMessageParams defined as follows:

```scala
trait LogMessageParams {
  /** The message type. See {@link MessageType} */
  def type: Int
  
  /** The message id. */
  def id: String

  /** The parent id if any. */
  def parentId: Option[String]
  
  /** The request id that originated this notification. */
  def requestId: Option[String]

  /** The actual message */
  def message: String
}
```

Where type is defined as `build/showMessage`.

A `build/logMessage` notification is similar to LSP's `window/logMessage`, except for a few new
additions.

The `id` and optional `parentId` fields allow clients to structure logs in a hierarchical way (in a
tree fashion, with dropdowns, et cetera) to ease readability.

The `requestId` field helps clients know which request originated a notification in case several
requests are handled by the client at the same time. It will only be populated if the client
defined it in the request that triggered this notification.

#### 1.6.1.7. Publish Diagnostics

The Diagnostics notification are sent from the server to the client to signal results of validation
runs.

Unlike the language server protocol, diagnostics are “owned” by the client so it is the client's
responsibility to manage their lifetime and clear them if necessary. Clients generate new
diagnostics by calling `buildTarget/compile`.

Notification:

* method: `build/publishDiagnostics`
* params: `PublishDiagnosticsParams` defined as follows:

```scala
trait PublishDiagnosticsParams {
  /** The uri of the document where diagnostics are published. */
  def uri: DocumentUri
  
  /** The request id that originated this notification. */
  def requestId: Option[String]
  
  /** The diagnostics to be published by the client. */
  def diagnostics: List[Diagnostic]
}
```

The definition of `PublishDiagnosticsParams` is similar to LSP's but contains the addition of an
optional `requestId` field. Clients can use this id to know which request originated the
notification. This field will be defined if the client defined it in the original request that
triggered this notification.

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
  def kind: Option[Int]

  /** Any additional metadata about what information changed. */
  def data: Option[Json]
}
```

where the `kind` is defined as follows:

```scala
object BuildTargetEventKind {
  /** The build target is new. */
  val Created = 1
  
  /** The build target has changed. */
  val Changed = 2
  
  /** The build target has been deleted. */
  val Deleted = 3
}
```

The `BuildTargetEventKind` information can be used by clients to trigger reindexing or update the
user interface with the new information.

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
    * Can be source files, jar files, zip files, or directories. */
  def sources: List[URI]
}
```

### 1.6.7. Resources Request

The build target resources request is sent from the client to the server to query for the list of
resources of a given list of build targets.

A resource is a data dependency required to be present in the runtime classpath when a build target
is run or executed. The server communicates during the initialize handshake whether this method is
supported or not.

This request can be used by a client to highlight the resources in a project view, for example.

* method: `buildTarget/resources`
* params: `ResourcesParams`

```scala
trait ResourcesParams {
  def targets: List[BuildTargetIdentifier]
}
```

Response:

* result: `ResourcesResult`, defined as follows

```scala
trait ResourcesResult {
  def items: List[ResourceItem]
}
trait ResourceItem {
  def target: BuildTargetIdentifier
  /** List of resource files. */
  def resources: List[URI]
}
```

### 1.6.8. Compile Request

The compile build target request is sent from the client to the server to compile the given list of build targets.
The server communicates during the initialize handshake whether this method is supported or not.
This method can for example be used by a language server before `textDocument/rename` to ensure that all workspace sources typecheck correctly and are up-to-date.

* method: `buildTarget/compile`
* params: `CompileParams`

```scala
trait CompileParams {
  /** A sequence of build targets to compile. */
  def targets: List[BuildTargetIdentifier]
  
  /** A unique identifier generated by the client to identify this request.
    * The server may include this id in triggered notifications or responses. */
  def requestId: Option[String]

  /** Optional arguments to the compilation process. */
  def arguments: List[Json]
}
```

Response:

* result: `CompileReport`, defined as follows
* error: JSON-RPC code and message set in case an exception happens during the request.

```scala
trait CompileResult {
  def data: Option[Json] // Note, matches `any | null` in the LSP.
  
  /** An optional request id to know the origin of this report. */
  def requestId: Option[String]
}
```

The field `data` may contain language-specific information, like the products of compilation.

Notification:

* method: `buildTarget/compileReport`
* params: `CompileReport` defined as follows:

```scala
trait CompileReport {
  /** The build target that was compiled. */
  def target: BuildTargetIdentifier
  
  /** An optional request id to know the origin of this report. */
  def requestId: Option[String]

  /** The total number of reported errors compiling this target. */
  def errors: Int

  /** The total number of reported warnings compiling the target. */
  def warnings: Int

  /** The total number of milliseconds it took to compile the target. */
  def time: Option[Int]
}
```

The server is free to send any number of `build/publishDiagnostics` and `build/logMessage`
notifications during compilation before completing the response. The client is free to forward these
messages to the LSP editor client.

The client will get a `requestId` field in `CompileReport` or `CompileResult` if the `requestId`
field in the `CompileParams` is defined.

### 1.6.9. Test Request

The test build target request is sent from the client to the server to test the given list of build targets.
The server communicates during the initialize handshake whether this method is supported or not.

* method: `buildTarget/test`
* params: `TestParams`

```scala
trait TestParams {
  /** A sequence of build targets to test. */
  def targets: List[BuildTargetIdentifier]
  
  /** A unique identifier generated by the client to identify this request.
    * The server may include this id in triggered notifications or responses. */
  def requestId: Option[String]
  
  /** Optional arguments to the test execution. */
  def arguments: List[Json]
}
```

Response:

* result: `TestResult`, defined as follows
* error: JSON-RPC code and message set in case an exception happens during the request.

```scala
trait TestResult {
  def data: Option[Json] // Note, matches `any | null` in the LSP.
    
  /** An optional request id to know the origin of this report. */
  def requestId: Option[String]
}
```

The field `data` may contain test-related language-specific information.

Notification:

* method: `buildTarget/testReport`
* params: `TestReport` defined as follows:

```scala
trait TestReport {
  /** The build target that was compiled. */
  def target: BuildTargetIdentifier
  
  /** An optional request id to know the origin of this report. */
  def requestId: Option[String]
  
  /** The total number of successful tests. */
  def passed: Int

  /** The total number of failed tests. */
  def failed: Int

  /** The total number of milliseconds tests take to run (e.g. doesn't include compile times). */
  def time: Option[Int]
}
```

The `TestReport` notification will be sent as the test execution of build targets completes.

This request may trigger a compilation on the selected build targets. The server is free to send any
number of `build/compileReport`, `build/publishDiagnostics` and `build/logMessage` notifications
during compilation before completing the response.

The client will get a `requestId` field in the `TestReport` or `TestResult` if the `requestId` field
in the `TestParams` is defined.

### 1.6.10. Run Request

The run request is sent from the client to the server to run a build target. The server communicates
during the initialize handshake whether this method is supported or not.

* method: `buildTarget/run`
* params: `RunParams`

```scala
trait RunParams {
  /** The build target to run. */
  def target: BuildTargetIdentifier
  
  /** A unique identifier generated by the client to identify this request.
    * The server may include this id in triggered notifications or responses. */
  def requestId: Option[String]
  
  /** Optional arguments to the test execution. */
  def arguments: Option[Json]
}
```

Note that an empty run request is valid. Run will be executed in the target as specified in the
build tool.

Response:

* result: `RunResult`, defined as follows
* error: JSON-RPC code and message set in case an exception happens during the request.

```scala
trait RunResult {
  /** An optional request id to know the origin of this report. */
  def requestId: Option[String] 
}
```

This request may trigger a compilation on the selected build targets. The server is free to send any
number of `build/compileReport`, `build/publishDiagnostics` and `build/logMessage` notifications
during compilation before completing the response.

The client will get a `requestId` field in `RunResult` if the `requestId` field in the
`RunParams` is defined.

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
  def platform: Int
  
  /** A sequence of Scala jars. */
  def scalaJars: List[String]
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
    def classpath: List[String]
    
    /** The output directory for classfiles produced by this target */
    def classDirectory: String
}
```

#### 1.7.1.3. Scala Test Classes Request

The build target scala test options request is sent from the client to the server to query for the
list of fully qualified names of test clases in a given list of targets.

This method can for example be used by a client to:
  
* Show a list of the discovered classes that can be tested.
* Attach a "Run test suite" button above the definition of a test suite via `textDocument/codeLens`.

(To render the code lens, the language server needs to map the fully qualified names of the test
targets to the defining source file via `textDocument/definition`. Then, once users click on the
button, the language server can pass the fully qualified name of the test class as an argument to
the `buildTarget/test` request.)

* method: `buildTarget/scalaTestClasses`
* params: `ScalaTestClassesParams`

```scala
trait ScalaTestClassesParams {
  def targets: List[BuildTargetIdentifier]
  
  /** An optional number uniquely identifying a client request. */
  def requestId: Option[String]
}
```

Response:

* result: `ScalaTestClassesResult`, defined as follows
* error: code and message set in case an exception happens during
  shutdown request.

```scala
trait ScalaTestClassesResult {
  def items: List[ScalaTestClassesItem]
  
  /** An optional id of the request that triggered this result. */
  def requestId: Option[String]

}

trait ScalaTestClassesItem {
  /** The build target that contains the test classes. */
  def target: BuildTargetIdentifier

  /** The fully qualified names of the test classes in this target */
  def classes: List[String]
}
```

This request may trigger a compilation on the selected build targets. The server is free to send any
number of `build/compileReport`, `build/publishDiagnostics` and `build/logMessage` notifications
during compilation before completing the response.

The client will get a `requestId` field in `ScalaTestClassesResult` if the `requestId` field in the
`ScalaTestClassesParams` is defined.

#### 1.7.1.4. Scala Main Classes Request

The build target main classes request is sent from the client to the server to query for the list of
main classes that can be fed as arguments to `buildTarget/run`. This method can be used for the same
use cases than the [Scala Test Classes Request](#1.7.1.3.-scala-test-classes-request) enables.

* method: `buildTarget/scalaMainClasses`
* params: `ScalaMainClassesParams`

```scala
trait ScalaMainClassesParams {
  def targets: List[BuildTargetIdentifier]
  
  /** An optional number uniquely identifying a client request. */
  def requestId: Option[String]
}
```

Response:

* result: `ScalaMainClassesResult`, defined as follows
* error: code and message set in case an exception happens during shutdown request.

```scala
trait ScalaMainClassesResult {
  def items: List[ScalaMainClassesItem]
  
  /** An optional id of the request that triggered this result. */
  def requestId: Option[String]

}

trait ScalaMainClassesItem {
  /** The build target that contains the test classes. */
  def target: BuildTargetIdentifier

  /** The main class item. */
  def mainClass: ScalaMainClass
}

trait ScalaMainClass {
  /** The main class to run. */
  def class: String

  /** The user arguments to the main entrypoint. */
  def arguments: List[String]
  
  /** The java options the application. */
  def javaOptions: List[String]
}

```

This request may trigger a compilation on the selected build targets. The server is free to send any
number of `build/compileReport`, `build/publishDiagnostics` and `build/logMessage` notifications
during compilation before completing the response.

The client will get a `requestId` field in `ScalaMainClassesResult` if the `requestId` field in the
`ScalaMainClassesParams` is defined.

### 1.7.2. Sbt

The following section contains sbt-specific extensions to the build server protocol. This extension
allows BSP clients to provide language support for sbt build files.

`SbtBuildTarget` is a basic data structure that contains sbt-specific metadata for providing editor
support for sbt build files. This metadata is embedded in the `data: Option[Json]` field of the
`BuildTarget` definition.

```scala
trait SbtBuildTarget {
  /** An optional parent if the target has an sbt meta project. */
  def parent: Option[BuildTargetIdentifier]
  
  /** A sequence of Scala imports that are automatically imported in the sbt build files. */
  def autoImports: List[String]
  
  /** The classpath for the sbt build (including sbt jars). */
  def classpath: List[String]
  
  /** The scala version to compile this target. */
  def scalaVersion: String
  
  /** The sbt version. Useful to support version-dependent syntax. */
  def sbtVersion: String

  /** A sequence of Scala jars. */
  def scalaJars: List[String]
}
```

#### 1.7.2.1. Sbt Build Target

## 1.8. Appendix

### 1.8.1. Protobuf schema definitions

The data structures presented in this document are accompanied by protobuf schema definitions.
See [bsp.proto](../src/main/protobuf/bsp.proto).

### 1.8.2. Scala Bindings

A Scala library implementation of this communication protocol is available in this repository.
The public API of this library currently has three direct Scala dependencies:

* ScalaPB - for generation of Scala sources from protobuf schema
* Monix - for asynchronous programming primitives
* Circe - for JSON serialization and parsing of protocol data structures

If there is demand, it should be possible to refactor out all three dependencies to provide a zero dependency core module.

### 1.8.3. FAQ

**Q**: What's the relationship between BSP and LSP?

**A**: They are complementary protocols.
While LSP specifies endpoints for communication between an **editor acting as client** and language server, BSP specifies endpoints between a **language server acting as client** and build server.
For example, in order to respond to a `textDocument/definition` request from an editor client, a language server could query a build tool via BSP for the classpath of a module.

**Q**: What's the relationship between implementations of BSP and implementations of LSP like
[dragos/dragos-vscode-scala](https://github.com/dragos/dragos-vscode-scala),
[Dotty IDE](https://marketplace.visualstudio.com/items?itemName=lampepfl.dotty) or
[Metals](https://github.com/scalameta/metals)?

**A**: Currently, those language servers each implement custom integrations for each supported build tool to extract build metadata.
Those language servers could instead implement a BSP client to extract build metadata from any build tools that implement BSP, sharing a single BSP server implementation.
Likewise, a new build tool could implement a BSP server and support a wide range of language servers out-of-the-box.

**Q**: Should non-Scala participants in the protocol generate data types from `bsp.proto` or is it preferable to use pre-generated artifacts in maven (or other repos)?

**A**: BSP uses JSON on the wire like LSP, it is not necessary to use `bsp.proto`.
The `bsp.proto` schema is provided as a language-agnostic reference schema for the shape of BSP data structures, similarly to how LSP messages are defined using TypeScript interfaces.
Like with LSP, it is left to BSP participant to figure out how to produce JSON payloads with BSP data structures.
