package tests

import ch.epfl.scala.bsp.{endpoints => s}
import ch.epfl.scala.bsp4j._
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import difflib.DiffUtils
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintWriter
import java.util
import java.util.Collections
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import jsonrpc4s.Services
import jsonrpc4s.Endpoint
import jsonrpc4s.RpcClient
import jsonrpc4s.Connection
import jsonrpc4s.InputOutput
import monix.eval.Task
import jsonrpc4s.RpcSuccess
import jsonrpc4s.RpcFailure
import scala.util.Failure
import scala.util.Success

class TypoSuite extends AnyFunSuite {

  // Constants for for incoming/outgoing messages.
  val buildTargetUri = new BuildTargetIdentifier("build-target-identifier")
  val buildTargetUris: util.List[BuildTargetIdentifier] = Collections.singletonList(buildTargetUri)
  val textDocumentUri = "file:///Application.scala"
  val textDocumentIdentifier = new TextDocumentIdentifier("tti")
  val textDocumentIdentifiers: util.List[TextDocumentIdentifier] =
    Collections.singletonList(textDocumentIdentifier)
  val outputPathUri = "file:///target/"

  // Java build client that ignores all notifications.
  val silentJavaClient: BuildClient = new BuildClient {
    override def onBuildShowMessage(params: ShowMessageParams): Unit =
      ()
    override def onBuildLogMessage(params: LogMessageParams): Unit =
      ()
    override def onBuildPublishDiagnostics(params: PublishDiagnosticsParams): Unit =
      ()
    override def onBuildTargetDidChange(params: DidChangeBuildTarget): Unit =
      ()
    override def onBuildTaskStart(params: TaskStartParams): Unit =
      ()
    override def onBuildTaskProgress(params: TaskProgressParams): Unit =
      ()
    override def onBuildTaskFinish(params: TaskFinishParams): Unit =
      ()
    override def onRunPrintStdout(params: PrintParams): Unit =
      ()
    override def onRunPrintStderr(params: PrintParams): Unit =
      ()
  }

  // Java build server that responds with hardcoded constants
  val hardcodedJavaServer: BuildServer = new BuildServer {

    override def buildInitialize(
        params: InitializeBuildParams
    ): CompletableFuture[InitializeBuildResult] = {
      CompletableFuture.completedFuture {
        val capabilities = new BuildServerCapabilities()
        capabilities.setCompileProvider(new CompileProvider(Collections.singletonList("scala")))
        capabilities.setTestProvider(new TestProvider(Collections.singletonList("scala")))
        capabilities.setRunProvider(new RunProvider(Collections.singletonList("scala")))
        capabilities.setDebugProvider(new DebugProvider(Collections.singletonList("scala")))
        capabilities.setInverseSourcesProvider(true)
        capabilities.setWrappedSourcesProvider(true)
        capabilities.setDependencySourcesProvider(true)
        capabilities.setResourcesProvider(true)
        capabilities.setBuildTargetChangedProvider(true)
        capabilities.setCanReload(true)
        capabilities.setDependencyModulesProvider(true)
        new InitializeBuildResult("test-server", "1.0.0", "2.0.0-M1", capabilities)
      }
    }
    override def onBuildInitialized(): Unit =
      ()

    override def workspaceReload(): CompletableFuture[Object] =
      CompletableFuture.completedFuture(null)

    override def buildShutdown(): CompletableFuture[Object] = {
      CompletableFuture.completedFuture(null)
    }
    override def onBuildExit(): Unit =
      ()
    override def workspaceBuildTargets(): CompletableFuture[WorkspaceBuildTargetsResult] = {
      CompletableFuture.completedFuture {
        val capabilities = new BuildTargetCapabilities()
        capabilities.setCanCompile(true)
        capabilities.setCanTest(true)
        capabilities.setCanRun(true)
        capabilities.setCanDebug(true)
        val target = new BuildTarget(
          buildTargetUri,
          Collections.singletonList("tag"),
          Collections.singletonList("scala"),
          buildTargetUris,
          capabilities
        )
        new WorkspaceBuildTargetsResult(Collections.singletonList(target))
      }
    }
    override def buildTargetSources(params: SourcesParams): CompletableFuture[SourcesResult] = {
      CompletableFuture.completedFuture {
        val item =
          new SourcesItem(
            buildTargetUri,
            Collections.singletonList(
              new SourceItem(textDocumentIdentifier.getUri, SourceItemKind.FILE, true)
            )
          )
        new SourcesResult(Collections.singletonList(item))
      }
    }
    override def buildTargetInverseSources(
        params: InverseSourcesParams
    ): CompletableFuture[InverseSourcesResult] = {
      CompletableFuture.completedFuture {
        new InverseSourcesResult(Collections.singletonList(buildTargetUri))
      }
    }
    override def buildTargetWrappedSources(
        params: WrappedSourcesParams
    ): CompletableFuture[WrappedSourcesResult] = {
      CompletableFuture.completedFuture {
        new WrappedSourcesResult(params.map(param => WrappedSourcesItem(param.target, List.empty)))
      }
    }
    override def buildTargetDependencySources(
        params: DependencySourcesParams
    ): CompletableFuture[DependencySourcesResult] = {
      CompletableFuture.completedFuture {
        val item =
          new DependencySourcesItem(buildTargetUri, Collections.singletonList(textDocumentUri))
        new DependencySourcesResult(Collections.singletonList(item))
      }
    }
    override def buildTargetResources(
        params: ResourcesParams
    ): CompletableFuture[ResourcesResult] = {
      CompletableFuture.completedFuture {
        val item = new ResourcesItem(buildTargetUri, Collections.singletonList(textDocumentUri))
        new ResourcesResult(Collections.singletonList(item))
      }
    }
    override def buildTargetOutputPaths(
        params: OutputPathsParams
    ): CompletableFuture[OutputPathsResult] = {
      CompletableFuture.completedFuture {
        val item = new OutputPathsItem(
          buildTargetUri,
          Collections.singletonList(new OutputPathItem(outputPathUri, OutputPathItemKind.DIRECTORY))
        )
        new OutputPathsResult(Collections.singletonList(item))
      }
    }
    override def buildTargetCompile(params: CompileParams): CompletableFuture[CompileResult] = {
      CompletableFuture.completedFuture {
        new CompileResult(StatusCode.OK)
      }
    }
    override def buildTargetTest(params: TestParams): CompletableFuture[TestResult] = {
      CompletableFuture.completedFuture {
        new TestResult(StatusCode.CANCELLED)
      }
    }
    override def buildTargetRun(params: RunParams): CompletableFuture[RunResult] = {
      CompletableFuture.completedFuture {
        new RunResult(StatusCode.ERROR)
      }
    }

    override def debugSessionStart(
        params: DebugSessionParams
    ): CompletableFuture[DebugSessionAddress] =
      CompletableFuture.completedFuture {
        new DebugSessionAddress("tcp://127.0.0.1:51379")
      }

    override def buildTargetCleanCache(
        params: CleanCacheParams
    ): CompletableFuture[CleanCacheResult] = {
      CompletableFuture.completedFuture {
        new CleanCacheResult(true)
      }
    }

    override def buildTargetDependencyModules(
        params: DependencyModulesParams
    ): CompletableFuture[DependencyModulesResult] = {
      CompletableFuture.completedFuture {
        val item =
          new DependencyModulesItem(
            buildTargetUri,
            Collections.singletonList {
              val item = new DependencyModule("org-some-library", "0.0.1")
              val jvmArtifact =
                new MavenDependencyModuleArtifact("uri")
              val jvmModuleData =
                new MavenDependencyModule("org", "some-library", "0.0.1", List(jvmArtifact).asJava)
              item.setData(jvmModuleData)
              item.setDataKind("maven")
              item
            }
          )
        new DependencyModulesResult(List(item).asJava)
      }
    }

    override def onRunReadStdin(params: ReadParams): Unit = ()
  }

  // extension methods to abstract over lsp4s service creation.
  implicit class XtensionServices(services: Services) {
    def forwardRequest[A, B](endpoint: Endpoint[A, B])(implicit client: RpcClient): Services = {
      services.requestAsync(endpoint) { a =>
        endpoint.request(a).flatMap {
          case err: RpcFailure[B]   => Task.raiseError(err)
          case RpcSuccess(value, _) => Task.now(value)
        }
      }
    }
    def ignoreNotification[A](endpoint: Endpoint[A, Unit]): Services = {
      services.notification(endpoint)(_ => ())
    }
    def forwardNotification[A](
        endpoint: Endpoint[A, Unit]
    )(implicit client: RpcClient): Services = {
      services.notification(endpoint)(a => endpoint.notify(a))
    }
  }

  // Scala build client that records all notifications.
  def silentScalaClient(implicit client: RpcClient): Services =
    Services
      .empty(scribe.Logger.root)
      .ignoreNotification(s.Build.showMessage)
      .ignoreNotification(s.Build.logMessage)
      .ignoreNotification(s.Build.taskStart)
      .ignoreNotification(s.Build.taskProgress)
      .ignoreNotification(s.Build.taskFinish)
      .ignoreNotification(s.Build.publishDiagnostics)
      .ignoreNotification(s.BuildTarget.didChange)

  // Scala build server that delegates all requests to a client.
  def forwardingScalaServer(implicit client: RpcClient): Services = {
    Services
      .empty(scribe.Logger.root)
      .forwardRequest(s.Build.initialize)
      .forwardRequest(s.Build.shutdown)
      .forwardRequest(s.Workspace.buildTargets)
      .forwardRequest(s.Workspace.reload)
      .forwardRequest(s.BuildTarget.sources)
      .forwardRequest(s.BuildTarget.dependencyModules)
      .forwardRequest(s.BuildTarget.inverseSources)
      .forwardRequest(s.BuildTarget.resources)
      .forwardRequest(s.BuildTarget.outputPaths)
      .forwardRequest(s.BuildTarget.compile)
      .forwardRequest(s.BuildTarget.run)
      .forwardRequest(s.BuildTarget.test)
      .forwardRequest(s.BuildTarget.cleanCache)
      .forwardNotification(s.Build.initialized)
      .forwardNotification(s.Build.exit)
  }

  val gson: Gson = new GsonBuilder().setPrettyPrinting().create()
  val gsonParser = new JsonParser()
  def traceWriter(baos: ByteArrayOutputStream): PrintWriter = {
    // Normalize JSON so that bsp4s and bsp4j produce identical strings
    def normalizeJson(jsonTrace: String): String = {
      val params = "Params: "
      val result = "Result: "
      val error = "Error: "
      val paramsIndex = jsonTrace.indexOf(params)
      val startIndex =
        (if (paramsIndex < 0) jsonTrace.indexOf(result)
         else paramsIndex) + params.length()
      val errorIndex = jsonTrace.indexOf(error, startIndex)
      val endIndex =
        if (errorIndex < 0) jsonTrace.length()
        else errorIndex
      val json = jsonTrace.substring(startIndex, endIndex)
      val elem = gsonParser.parse(json)
      if (elem.isJsonObject()) {
        val obj = elem.getAsJsonObject
        List("params", "result").foreach { key =>
          if (obj.get(key) == JsonNull.INSTANCE) {
            // bsp4j uses `params: null` when bsp4s `params: {}`
            obj.add(key, new JsonObject())
          }
        }
        if (obj.get("id") != null) {
          // bsp4s and bsp4j may not have the same ID request numbers
          obj.add("id", new JsonPrimitive("1"))
        }
        gson.toJson(obj)
      } else if (elem.isJsonNull()) {
        gson.toJson(new JsonObject())
      } else {
        fail(s"unexpected JSON: $json")
      }
    }
    new PrintWriter(baos) {
      override def print(json: String): Unit = {
        super.print(normalizeJson(json))
      }
    }
  }

  /** Fails the test case with a readable unified diff if obtained != expected */
  def assertNoDiff(obtained: String, expected: String): Unit = {
    def lines(string: String): java.util.List[String] =
      string.replace("\r\n", "\n").split("\n").toSeq.asJava
    val patch = DiffUtils.diff(lines(obtained), lines(expected))
    if (!patch.getDeltas.isEmpty) {
      val diff = DiffUtils
        .generateUnifiedDiff(
          "in",
          "out",
          lines(obtained),
          patch,
          3
        )
        .asScala
        .mkString("\n")
      fail(diff)
    }
  }

  def startScalaConnection(in: InputStream, out: OutputStream)(
      fn: RpcClient => Services
  )(implicit s: Scheduler): Connection = {
    val logger = scribe.Logger.root
    Connection(new InputOutput(in, out), logger, logger)(fn)
  }

  // This test case asserts that every BSP request/notification/response is unchanged
  // after the following roundtrip: ls4pj -> lsp4s -> lsp4j
  // Every request from the original lsp4j client is sent to a lsp4s server that delegates
  // the request to a lsp4j server that responds with a concrete message:
  // lsp4j ----> lsp4s       lsp4j
  // lsp4j       lsp4s ----> lsp4j
  // lsp4j       lsp4s <---- lsp4j
  // lsp4j <---- lsp4s       lsp4j
  // The objective of this test is to ensure that lsp4s and lsp4j can communicate
  // and agree which fields are optional and which fields are required.
  test("roundtrip") {
    implicit val scheduler: SchedulerService =
      Scheduler(Executors.newCachedThreadPool())

    val inJava1 = new PipedInputStream()
    val inJava2 = new PipedInputStream()
    val inScala1 = new PipedInputStream()
    val inScala2 = new PipedInputStream()

    val outJava1 = new PipedOutputStream(inScala1)
    val outScala1 = new PipedOutputStream(inJava1)
    val outJava2 = new PipedOutputStream(inScala2)
    val outScala2 = new PipedOutputStream(inJava2)

    val scala2Connection =
      startScalaConnection(inScala2, outScala2)(forwardingScalaServer(_))
    val scala1Connection =
      startScalaConnection(inScala1, outScala1)(_ => forwardingScalaServer(scala2Connection.client))

    val trace1 = new ByteArrayOutputStream()
    val trace2 = new ByteArrayOutputStream()

    val java1Launcher = new Launcher.Builder[BuildServer]()
      .setRemoteInterface(classOf[BuildServer])
      .setLocalService(silentJavaClient)
      .traceMessages(traceWriter(trace1))
      .setInput(inJava1)
      .setOutput(outJava1)
      .create()
    val java2Launcher = new Launcher.Builder[BuildClient]()
      .setRemoteInterface(classOf[BuildClient])
      .setLocalService(hardcodedJavaServer)
      .traceMessages(traceWriter(trace2))
      .setInput(inJava2)
      .setOutput(outJava2)
      .create()
    val java1Listening = java1Launcher.startListening()
    val java2Listening = java2Launcher.startListening()
    val scala1 = java1Launcher.getRemoteProxy

    val allResources = new OpenCancelable()
      .add(() => inJava1.close())
      .add(() => inJava2.close())
      .add(() => inScala1.close())
      .add(() => inScala2.close())
      .add(() => outJava1.close())
      .add(() => outJava2.close())
      .add(() => outScala1.close())
      .add(() => outScala2.close())
      .add(() => java1Listening.cancel(true))
      .add(() => java2Listening.cancel(true))
      .add(() => scala1Connection.cancel())
      .add(() => scala2Connection.cancel())
      .add(() => scheduler.shutdown())

    try {
      val result: Future[Unit] = for {
        initialize <- {
          scala1
            .buildInitialize(
              new InitializeBuildParams(
                "test-client",
                "1.0.0",
                "2.0.0-M1",
                "file:///workspace/",
                new BuildClientCapabilities(
                  Collections.singletonList("scala")
                )
              )
            )
            .toScala
        }
        sources <- scala1.buildTargetSources(new SourcesParams(buildTargetUris)).toScala
        depModules <- scala1
          .buildTargetDependencyModules(new DependencyModulesParams(buildTargetUris))
          .toScala
        inverseSources <- scala1
          .buildTargetInverseSources(new InverseSourcesParams(textDocumentIdentifier))
          .toScala
        resources <- scala1
          .buildTargetResources(new ResourcesParams(buildTargetUris))
          .toScala
        outputPaths <- scala1
          .buildTargetOutputPaths(new OutputPathsParams(buildTargetUris))
          .toScala
        clean <- scala1
          .buildTargetCleanCache(new CleanCacheParams(buildTargetUris))
          .toScala
        compile <- scala1.buildTargetCompile(new CompileParams(buildTargetUris)).toScala
        run <- scala1.buildTargetRun(new RunParams(buildTargetUri)).toScala
        test <- scala1
          .buildTargetTest(new TestParams(buildTargetUris))
          .toScala
        _ <- scala1.workspaceReload().toScala
        _ <- scala1.buildShutdown().toScala
        workspace <- scala1.workspaceBuildTargets().toScala
      } yield {
        val obtained = trace1.toString()
        val expected = trace2.toString()
        assertNoDiff(obtained, expected)
      }
      Await.result(result, Duration("30s"))
    } finally {
      allResources.cancel()
    }
  }
}
