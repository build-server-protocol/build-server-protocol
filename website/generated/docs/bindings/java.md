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
compile group: 'ch.epfl.scala', name: 'bsp4j', version: '2.1.0-M6.alpha+2-7c10d649+20230719-2300-SNAPSHOT'
```

### Maven

```xml
<dependency>
    <groupId>ch.epfl.scala</groupId>
    <artifactId>bsp4j</artifactId>
    <version>2.1.0-M6.alpha+2-7c10d649+20230719-2300-SNAPSHOT</version>
</dependency>
```

### sbt

```scala
libraryDependencies += "ch.epfl.scala" % "bsp4j" % "2.1.0-M6.alpha+2-7c10d649+20230719-2300-SNAPSHOT"
```

## Examples

### Client

First, begin by obtaining an input and output stream to communicate with the
build server.

```scala
val output: java.io.OutputStream = buildOutputStream()
val input: java.io.InputStream = buildInputStream()
```

Next, implement the `BuildClient` interface. Replace the `???` dummy
implementations with the logic of your build client.

```scala
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
}
val localClient = new MyClient()
```

Optionally, create a custom `ExecutorService` to run client responses

```scala
import java.util.concurrent._
val es = Executors.newFixedThreadPool(1)
// es: ExecutorService = java.util.concurrent.ThreadPoolExecutor@79abfab7[Terminated, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 0]
```

Next, wire the client implementation together with the remote build server.

```scala
val launcher = new Launcher.Builder[BuildServer]()
  .setOutput(output)
  .setInput(input)
  .setLocalService(localClient)
  .setExecutorService(es)
  .setRemoteInterface(classOf[BuildServer])
  .create()
// launcher: Launcher[BuildServer] = org.eclipse.lsp4j.jsonrpc.StandardLauncher@2e3584c5
```

Next, obtain an instance of the remote `BuildServer` via `getRemoteProxy()`.

```scala
val server = launcher.getRemoteProxy
// server: BuildServer = EndpointProxy for org.eclipse.lsp4j.jsonrpc.RemoteEndpoint@cdfc350
```

Next, start listening to the remote build server on a separate thread. The
`.get()` method call is blocking during the lifetime of BSP session.

```scala
new Thread {
  override def run() = launcher.startListening().get()
}
// res0: Thread = Thread[#535,Thread-78,5,main]
```

Next, trigger the initialize handshake with the remote server.

```scala
val workspace = java.nio.file.Paths.get(".").toAbsolutePath().normalize()
// workspace: java.nio.file.Path = /home/andrzej/code/jetbrains/build-server-protocol
val initializeResult = server.buildInitialize(new InitializeBuildParams(
  "MyClient", // name of this client
  "1.0.0", // version of this client
  "2.1.0-M6.alpha+2-7c10d649+20230719-2300-SNAPSHOT", // BSP version
  workspace.toUri().toString(),
  new BuildClientCapabilities(java.util.Collections.singletonList("scala"))
))
// initializeResult: CompletableFuture[InitializeBuildResult] = org.eclipse.lsp4j.jsonrpc.RemoteEndpoint$1@30377e08[Not completed, 1 dependents]
```

After receiving the initialize response, send the `build/initialized`
notification.

```scala
initializeResult.thenAccept(_ => server.onBuildInitialized())
// res1: CompletableFuture[Void] = java.util.concurrent.CompletableFuture@2ada169c[Not completed]
```

After sending the `build/initialized` notification, you can send any BSP
requests and notications such as `workspace/buildTargets`,
`buildTarget/compile`.

To close the BSP session, send the `build/shutdown` request followed by a
`build/exit` notification.

```scala
server.buildShutdown().thenAccept(new java.util.function.Consumer[Object] {
  def accept(x: Object): Unit = {
    server.onBuildExit()
  }
})
// res2: CompletableFuture[Void] = java.util.concurrent.CompletableFuture@28cbbcae[Not completed]
```

### Server

First, implement the `BuildServer` interface.

```scala
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
}
val localServer = new MyBuildServer()
// localServer: MyBuildServer = repl.MdocSession$MdocApp4$MyBuildServer@65452de3
```

Next, construct a launcher for the remote build client.

```scala
val launcher = new Launcher.Builder[BuildClient]()
  .setOutput(System.out)
  .setInput(System.in)
  .setLocalService(localServer)
  .setRemoteInterface(classOf[BuildClient])
  .create()
// launcher: Launcher[BuildClient] = org.eclipse.lsp4j.jsonrpc.StandardLauncher@324cda33
```

Next, update the remote build client reference in `localServer`.

```scala
localServer.client = launcher.getRemoteProxy()
```

Finally, in a `main` method wire everything together.

```scala
def main(args: Array[String]): Unit = {
  launcher.startListening().get() // listen until BSP session is over.
}
```
