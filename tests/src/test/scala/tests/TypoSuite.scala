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
import java.util.Collections
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.scalatest.FunSuite
import scala.collection.JavaConverters._
import scala.compat.java8.FutureConverters._
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.meta.jsonrpc.Connection
import scala.meta.jsonrpc.Endpoint
import scala.meta.jsonrpc.InputOutput
import scala.meta.jsonrpc.JsonRpcClient
import scala.meta.jsonrpc.Services

class TypoSuite extends FunSuite {

  // Constants for for incoming/outgoing messages.
  val buildTargetUri = new BuildTargetIdentifier("build-target-identifier")
  val buildTargetUris = Collections.singletonList(buildTargetUri)
  val textDocumentUri = "file:///Application.scala"
  val textDocumentIdentifier = new TextDocumentIdentifier("tti")
  val textDocumentIdentifiers = Collections.singletonList(textDocumentIdentifier)

  // Java build client that ignored all notifications.
  val silentJavaClient: BuildClient = new BuildClient {
    override def onBuildShowMessage(params: ShowMessageParams): Unit =
      ()
    override def onBuildLogMessage(params: LogMessageParams): Unit =
      ()
    override def onBuildPublishDiagnostics(params: PublishDiagnosticsParams): Unit =
      ()
    override def onBuildTargetDidChange(params: DidChangeBuildTarget): Unit =
      ()
    override def onBuildTargetCompileReport(params: CompileReport): Unit =
      ()
    override def onBuildTargetTestReport(params: TestReport): Unit =
      ()
  }

  // Java server client that responds with hardcoded constants
  val hardcodedJavaServer: BuildServer = new BuildServer {
    override def buildInitialize(
        params: InitializeBuildParams): CompletableFuture[InitializeBuildResult] = {
      CompletableFuture.completedFuture {
        val capabilities = new BuildServerCapabilities()
        capabilities.setCompileProvider(new CompileProvider(Collections.singletonList("scala")))
        capabilities.setTestProvider(new TestProvider(Collections.singletonList("scala")))
        capabilities.setRunProvider(new RunProvider(Collections.singletonList("scala")))
        capabilities.setInverseSourcesProvider(true)
        capabilities.setDependencySourcesProvider(true)
        capabilities.setResourcesProvider(true)
        capabilities.setBuildTargetChangedProvider(true)
        new InitializeBuildResult(capabilities)
      }
    }
    override def onBuildInitialized(): Unit =
      ()
    override def buildShutdown(): CompletableFuture[Object] = {
      CompletableFuture.completedFuture(null)
    }
    override def onBuildExit(): Unit =
      ()
    override def workspaceBuildTargets(): CompletableFuture[WorkspaceBuildTargetsResult] = {
      CompletableFuture.completedFuture {
        val capabilities = new BuildTargetCapabilities(true, true, true)
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
            Collections.singletonList(new SourceItem(textDocumentIdentifier.getUri, true)))
        new SourcesResult(Collections.singletonList(item))
      }
    }
    override def buildTargetInverseSources(
        params: InverseSourcesParams): CompletableFuture[InverseSourcesResult] = {
      CompletableFuture.completedFuture {
        new InverseSourcesResult(Collections.singletonList(buildTargetUri))
      }
    }
    override def buildTargetDependencySources(
        params: DependencySourcesParams): CompletableFuture[DependencySourcesResult] = {
      CompletableFuture.completedFuture {
        val item =
          new DependencySourcesItem(buildTargetUri, Collections.singletonList(textDocumentUri))
        new DependencySourcesResult(Collections.singletonList(item))
      }
    }
    override def buildTargetResources(
        params: ResourcesParams): CompletableFuture[ResourcesResult] = {
      CompletableFuture.completedFuture {
        val item = new ResourcesItem(buildTargetUri, Collections.singletonList(textDocumentUri))
        new ResourcesResult(Collections.singletonList(item))
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
    override def buildTargetCleanCache(
        params: CleanCacheParams): CompletableFuture[CleanCacheResult] = {
      CompletableFuture.completedFuture {
        new CleanCacheResult("clean", true)
      }
    }
  }

  // extension methods to abstract over lsp4s service creation.
  implicit class XtensionServices(services: Services) {
    def forwardRequest[A, B](endpoint: Endpoint[A, B])(implicit client: JsonRpcClient): Services = {
      services.requestAsync(endpoint)(a => endpoint.request(a))
    }
    def ignoreNotification[A](endpoint: Endpoint[A, Unit]): Services = {
      services.notification(endpoint)(_ => ())
    }
    def forwardNotification[A](endpoint: Endpoint[A, Unit])(
        implicit client: JsonRpcClient): Services = {
      services.notification(endpoint)(a => endpoint.notify(a))
    }
  }

  // Scala build client that records all notifications.
  def silentScalaClient(implicit client: JsonRpcClient): Services =
    Services
      .empty(scribe.Logger.root)
      .ignoreNotification(s.Build.showMessage)
      .ignoreNotification(s.Build.logMessage)
      .ignoreNotification(s.Build.publishDiagnostics)
      .ignoreNotification(s.BuildTarget.didChange)
      .ignoreNotification(s.BuildTarget.compileReport)
      .ignoreNotification(s.BuildTarget.testReport)

  // Scala build server that delegates all requests to a client.
  def forwardingScalaServer(implicit client: JsonRpcClient): Services = {
    Services
      .empty(scribe.Logger.root)
      .forwardRequest(s.Build.initialize)
      .forwardRequest(s.Build.shutdown)
      .forwardRequest(s.Workspace.buildTargets)
      .forwardRequest(s.BuildTarget.sources)
      .forwardRequest(s.BuildTarget.inverseSources)
      .forwardRequest(s.BuildTarget.resources)
      .forwardRequest(s.BuildTarget.compile)
      .forwardRequest(s.BuildTarget.run)
      .forwardRequest(s.BuildTarget.test)
      .forwardRequest(s.BuildTarget.cleanCache)
      .forwardNotification(s.Build.initialized)
      .forwardNotification(s.Build.exit)
  }

  val gson = new GsonBuilder().setPrettyPrinting().create()
  val gsonParser = new JsonParser()
  def traceWriter(baos: ByteArrayOutputStream): PrintWriter = {
    // Normalize JSON so that bsp4s and bsp4j produce identical strings
    def normalizeJson(json: String): String = {
      val obj = gsonParser.parse(json).getAsJsonObject
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

  def startScalaConnection(in: InputStream, out: OutputStream)(fn: JsonRpcClient => Services)(
      implicit s: Scheduler): Connection = {
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
    val scala2 = java2Launcher.getRemoteProxy

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
                "file:///workspace/",
                new BuildClientCapabilities(
                  Collections.singletonList("scala")
                )
              )
            )
            .toScala
        }
        _ = scala1.onBuildInitialized()
        sources <- scala1.buildTargetSources(new SourcesParams(buildTargetUris)).toScala
        inverseSources <- scala1
          .buildTargetInverseSources(new InverseSourcesParams(textDocumentIdentifier))
          .toScala
        resources <- scala1
          .buildTargetResources(new ResourcesParams(buildTargetUris))
          .toScala
        clean <- scala1
          .buildTargetCleanCache(new CleanCacheParams(buildTargetUris))
          .toScala
        compile <- scala1.buildTargetCompile(new CompileParams(buildTargetUris)).toScala
        run <- scala1.buildTargetRun(new RunParams(buildTargetUri)).toScala
        test <- scala1
          .buildTargetTest(new TestParams(buildTargetUris))
          .toScala
        _ <- scala1.buildShutdown().toScala
        _ = scala1.onBuildExit()
        // NOTE(olafur): important to not finish test with a notification to avoid Thread.sleep
        // for notification to deliver.
        workspace <- scala1.workspaceBuildTargets().toScala
      } yield {
        val obtained = trace1.toString()
        val expected = trace2.toString()
        assertNoDiff(obtained, expected)
      }
      Await.result(result, Duration("5s"))
    } finally {
      allResources.cancel()
    }
  }
}
