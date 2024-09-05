---
title: Java Bindings
sidebar_label: Java
---

The
[`ch.epfl.scala:bsp4j`](https://mvnrepository.com/artifact/ch.epfl.scala/bsp4j)
module is a Java library that is available from Maven Central. The module has
one external dependency on the [eclipse/lsp4j](https://github.com/eclipse/lsp4j)
library.

## Installation

Add the following snippet to your build to add dependency on `bsp4j`.

### Gradle

```groovy
compile group: 'ch.epfl.scala', name: 'bsp4j', version: '@LIBRARY_VERSION@'
```

### Maven

```xml
<dependency>
    <groupId>ch.epfl.scala</groupId>
    <artifactId>bsp4j</artifactId>
    <version>@LIBRARY_VERSION@</version>
</dependency>
```

### sbt

```scala
libraryDependencies += "ch.epfl.scala" % "bsp4j" % "@LIBRARY_VERSION@"
```

## Examples

```scala mdoc:invisible
import java.io.OutputStream
import java.io.InputStream
def buildInputStream(): InputStream = new InputStream {
  def read(): Int = -1
}
def buildOutputStream(): OutputStream = new OutputStream {
  def write(x: Int): Unit = ()
}
```

### Client

First, begin by obtaining an input and output stream to communicate with the
build server.

```scala mdoc:silent
val output: java.io.OutputStream = buildOutputStream()
val input: java.io.InputStream = buildInputStream()
```

Next, implement the `BuildClient` interface. Replace the `???` dummy
implementations with the logic of your build client.

```scala mdoc:silent
import java.util.concurrent._
import ch.epfl.scala.bsp4j._
import org.eclipse.lsp4j.jsonrpc.Launcher

class MyClient extends BuildClient {
  def onBuildLogMessage(params: LogMessageParams): Unit = ???
  def onBuildPublishDiagnostics(params: PublishDiagnosticsParams): Unit = ???
  def onBuildShowMessage(params: ShowMessageParams): Unit = ???
  def onBuildTargetDidChange(params: DidChangeBuildTarget): Unit = ???
  def onBuildTaskFinish(params: TaskFinishParams): Unit = ???
  def onBuildTaskProgress(params: TaskProgressParams): Unit = ???
  def onBuildTaskStart(params: TaskStartParams): Unit = ???
  def onRunPrintStdout(params: PrintParams): Unit = ???
  def onRunPrintStderr(params: PrintParams): Unit = ???
}
val localClient = new MyClient()
```

Optionally, create a custom `ExecutorService` to run client responses

```scala mdoc:silent
import java.util.concurrent._
val es = Executors.newFixedThreadPool(1)
```

Next, wire the client implementation together with the remote build server.

```scala mdoc:silent
val launcher = new Launcher.Builder[BuildServer]()
  .setOutput(output)
  .setInput(input)
  .setLocalService(localClient)
  .setExecutorService(es)
  .setRemoteInterface(classOf[BuildServer])
  .create()
```

Next, obtain an instance of the remote `BuildServer` via `getRemoteProxy()`.

```scala mdoc:silent
val server = launcher.getRemoteProxy
```

Next, start listening to the remote build server on a separate thread. The
`.get()` method call is blocking during the lifetime of BSP session.

```scala mdoc:silent
new Thread {
  override def run() = launcher.startListening().get()
}
```

Next, trigger the initialize handshake with the remote server.

```scala mdoc:silent
val workspace = java.nio.file.Paths.get(".").toAbsolutePath().normalize()
val initializeResult = server.buildInitialize(new InitializeBuildParams(
  "MyClient", // name of this client
  "1.0.0", // version of this client
  Bsp4j.PROTOCOL_VERSION,
  workspace.toUri().toString(),
  new BuildClientCapabilities(java.util.Collections.singletonList("scala"))
))
```

After receiving the initialize response, send the `build/initialized`
notification.

```scala mdoc:silent
initializeResult.thenAccept(_ => server.onBuildInitialized())
```

After sending the `build/initialized` notification, you can send any BSP
requests and notications such as `workspace/buildTargets`,
`buildTarget/compile`.

To close the BSP session, send the `build/shutdown` request followed by a
`build/exit` notification.

```scala mdoc:silent
server.buildShutdown().thenAccept(new java.util.function.Consumer[Object] {
  def accept(x: Object): Unit = {
    server.onBuildExit()
  }
})

```

```scala mdoc:invisible
es.shutdown()
```

### Server

First, implement the `BuildServer` interface.

```scala mdoc:reset:silent
import java.util.concurrent._
import ch.epfl.scala.bsp4j._
import org.eclipse.lsp4j.jsonrpc.Launcher

class MyBuildServer extends BuildServer {
  var client: BuildClient = null // will be updated later
  def buildInitialize(params: InitializeBuildParams): CompletableFuture[InitializeBuildResult] = ???
  def buildShutdown(): CompletableFuture[Object] = ???
  def buildTargetCleanCache(params: CleanCacheParams): CompletableFuture[CleanCacheResult] = ???
  def buildTargetCompile(params: CompileParams): CompletableFuture[CompileResult] = ???
  def buildTargetDependencySources(params: DependencySourcesParams): CompletableFuture[DependencySourcesResult] = ???
  def buildTargetDependencyModules(params: DependencyModulesParams): CompletableFuture[DependencyModulesResult] = ???
  def buildTargetInverseSources(params: InverseSourcesParams): CompletableFuture[InverseSourcesResult] = ???
  def buildTargetWrappedSources(params: WrappedSourcesParams): CompletableFuture[WrappedSourcesResult] = ???
  def buildTargetResources(params: ResourcesParams): CompletableFuture[ResourcesResult] = ???
  def buildTargetOutputPaths(params: OutputPathsParams): CompletableFuture[OutputPathsResult] = ???
  def buildTargetRun(params: RunParams): CompletableFuture[RunResult] = ???
  def buildTargetSources(params: SourcesParams): CompletableFuture[SourcesResult] = ???
  def buildTargetTest(params: TestParams): CompletableFuture[TestResult] = ???
  def debugSessionStart(params: DebugSessionParams): CompletableFuture[DebugSessionAddress] = ???
  def onBuildExit(): Unit = ???
  def onBuildInitialized(): Unit = ???
  def workspaceBuildTargets(): CompletableFuture[WorkspaceBuildTargetsResult] = ???
  def workspaceReload(): CompletableFuture[Object] = ???
  def onRunReadStdin(params: ReadParams): Unit = ???
}
val localServer = new MyBuildServer()
```

Next, construct a launcher for the remote build client.

```scala mdoc:silent
val launcher = new Launcher.Builder[BuildClient]()
  .setOutput(System.out)
  .setInput(System.in)
  .setLocalService(localServer)
  .setRemoteInterface(classOf[BuildClient])
  .create()
```

Next, update the remote build client reference in `localServer`.

```scala mdoc:silent
localServer.client = launcher.getRemoteProxy()
```

Finally, in a `main` method wire everything together.

```scala mdoc:silent
def main(args: Array[String]): Unit = {
  launcher.startListening().get() // listen until BSP session is over.
}
```
