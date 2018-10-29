package tests

import ch.epfl.scala.bsp.{endpoints => s}
import ch.epfl.scala.bsp4j._
import difflib.DiffUtils
import java.io.InputStream
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.io.PrintWriter
import java.util
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
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
  // "out" aggregates output requests
  val out = new StringBuilder
  var outN = new AtomicInteger()
  // "in" aggregates incoming requests
  val in = new StringBuilder
  var inN = new AtomicInteger()
  def recordMessage(sb: StringBuilder, e: Any, i: Int): Unit = {
    sb.append(s"=====\n")
      .append(s"= $i\n")
      .append(s"=====\n")
      .append(e.toString)
      .append("\n")
  }
  def recordIn[T](e: T): T = {
    recordMessage(in, e, inN.incrementAndGet()); e
  }
  def recordOut[T](e: T): T = {
    recordMessage(out, e, outN.incrementAndGet()); e
  }

  // Notifications for messages that have no params.
  case object BuildInitialized
  case object BuildShutdown
  case object BuildExit
  case object WorkspaceBuildTargets

  // java.util.List helpers to construct `ArrayList` like lsp4j.
  def emptyList[T](): util.List[T] = new util.ArrayList[T]
  def singletonList[T](e: T): util.List[T] = {
    val lst = new util.ArrayList[T]
    lst.add(e)
    lst
  }

  // Constants for for incoming/outgoing messages.
  val buildTargetUri = new BuildTargetIdentifier("build-target-identifier")
  val buildTargetUris = singletonList(buildTargetUri)
  val textDocumentUri = "file:///Application.scala"
  val textDocumentIdentifier = new TextDocumentIdentifier("tti")
  val textDocumentIdentifiers = singletonList(textDocumentIdentifier)

  // Java build client that records every notification from the other side.
  val recordingJavaClient: BuildClient = new BuildClient {
    override def onBuildShowMessage(params: ShowMessageParams): Unit =
      recordIn(params)
    override def onBuildLogMessage(params: LogMessageParams): Unit =
      recordIn(params)
    override def onBuildPublishDiagnostics(params: PublishDiagnosticsParams): Unit =
      recordIn(params)
    override def onBuildTargetDidChange(params: DidChangeBuildTarget): Unit =
      recordIn(params)
    override def onBuildTargetCompileReport(params: CompileReport): Unit =
      recordIn(params)
    override def onBuildTargetTestReport(params: TestReport): Unit =
      recordIn(params)
  }

  // Java server client that responds with hardcoded constants and records incoming messages.
  val hardcodedJavaServer: BuildServer = new BuildServer {
    override def buildInitialize(
        params: InitializeBuildParams): CompletableFuture[InitializeBuildResult] = {
      recordIn(params)
      CompletableFuture.completedFuture {
        new InitializeBuildResult(
          new BuildServerCapabilities(
            new CompileProvider(singletonList("scala")),
            new TestProvider(singletonList("scala")),
            new RunProvider(singletonList("scala")),
            true,
            true,
            true,
            true
          )
        )
      }
    }
    override def onBuildInitialized(): Unit =
      recordIn(BuildInitialized)
    override def buildShutdown(): CompletableFuture[Object] = {
      recordIn(BuildShutdown)
      CompletableFuture.completedFuture(null)
    }
    override def onBuildExit(): Unit =
      recordIn(BuildExit)
    override def workspaceBuildTargets(): CompletableFuture[WorkspaceBuildTargetsResult] = {
      recordIn(WorkspaceBuildTargets)
      CompletableFuture.completedFuture {
        val capabilities = new BuildTargetCapabilities(true, true, true)
        val target = new BuildTarget(
          buildTargetUri,
          singletonList("tag"),
          singletonList("scala"),
          buildTargetUris,
          capabilities
        )
        new WorkspaceBuildTargetsResult(singletonList(target))
      }
    }
    override def buildTargetSources(params: SourcesParams): CompletableFuture[SourcesResult] = {
      recordIn(params)
      CompletableFuture.completedFuture {
        val item =
          new SourcesItem(buildTargetUri,
                          singletonList(new SourceItem(textDocumentIdentifier.getUri, true)))
        new SourcesResult(singletonList(item))
      }
    }
    override def buildTargetInverseSources(
        params: InverseSourcesParams): CompletableFuture[InverseSourcesResult] = {
      recordIn(params)
      CompletableFuture.completedFuture {
        new InverseSourcesResult(singletonList(buildTargetUri))
      }
    }
    override def buildTargetDependencySources(
        params: DependencySourcesParams): CompletableFuture[DependencySourcesResult] = {
      recordIn(params)
      CompletableFuture.completedFuture {
        val item = new DependencySourcesItem(buildTargetUri, singletonList(textDocumentUri))
        new DependencySourcesResult(singletonList(item))
      }
    }
    override def buildTargetResources(
        params: ResourcesParams): CompletableFuture[ResourcesResult] = {
      recordIn(params)
      CompletableFuture.completedFuture {
        val item = new ResourcesItem(buildTargetUri, singletonList(textDocumentUri))
        new ResourcesResult(singletonList(item))
      }
    }
    override def buildTargetCompile(params: CompileParams): CompletableFuture[CompileResult] = {
      recordIn(params)
      CompletableFuture.completedFuture {
        new CompileResult(StatusCode.OK)
      }
    }
    override def buildTargetTest(params: TestParams): CompletableFuture[TestResult] = {
      recordIn(params)
      CompletableFuture.completedFuture {
        new TestResult(StatusCode.CANCELLED)
      }
    }
    override def buildTargetRun(params: RunParams): CompletableFuture[RunResult] = {
      recordIn(params)
      CompletableFuture.completedFuture {
        new RunResult(StatusCode.ERROR)
      }
    }
    override def buildTargetCleanCache(
        params: CleanCacheParams): CompletableFuture[CleanCacheResult] = {
      recordIn(params)
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
    def recordNotification[A](endpoint: Endpoint[A, Unit]): Services = {
      services.notification(endpoint)(recordIn(_))
    }
    def forwardNotification[A](endpoint: Endpoint[A, Unit])(
        implicit client: JsonRpcClient): Services = {
      services.notification(endpoint)(a => endpoint.notify(a))
    }
  }

  // Scala build client that records all notifications.
  def recordingScalaClient(implicit client: JsonRpcClient): Services =
    Services
      .empty(scribe.Logger.root)
      .recordNotification(s.Build.showMessage)
      .recordNotification(s.Build.logMessage)
      .recordNotification(s.Build.publishDiagnostics)
      .recordNotification(s.BuildTarget.didChange)
      .recordNotification(s.BuildTarget.compileReport)
      .recordNotification(s.BuildTarget.testReport)

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

  // Uncomment to trace every JSON in/out message to stdout:
  // System.setProperty("debug", "true")
  def debugPrintWriter(prefix: String): PrintWriter = {
    if (System.getProperty("debug") != null) {
      new PrintWriter(System.out) {
        override def print(s: String): Unit = {
          super.print(prefix)
          super.print(": ")
          super.print(s)
        }
      }
    } else {
      null
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
    scribe.info(patch.getDeltas.asScala.toString)
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

    val java1Launcher = new Launcher.Builder[BuildServer]()
      .setRemoteInterface(classOf[BuildServer])
      .setLocalService(recordingJavaClient)
      .traceMessages(debugPrintWriter("java1"))
      .setInput(inJava1)
      .setOutput(outJava1)
      .create()
    val java2Launcher = new Launcher.Builder[BuildClient]()
      .setRemoteInterface(classOf[BuildClient])
      .setLocalService(hardcodedJavaServer)
      .traceMessages(debugPrintWriter("java2"))
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
              recordOut(
                new InitializeBuildParams(
                  "file:///workspace/",
                  new BuildClientCapabilities(
                    singletonList("scala")
                  )
                )
              )
            )
            .toScala
        }
        _ = {
          recordOut(BuildInitialized)
          scala1.onBuildInitialized()
        }
        sources <- scala1.buildTargetSources(recordOut(new SourcesParams(buildTargetUris))).toScala
        inverseSources <- scala1
          .buildTargetInverseSources(recordOut(new InverseSourcesParams(textDocumentIdentifier)))
          .toScala
        resources <- scala1
          .buildTargetResources(recordOut(new ResourcesParams(buildTargetUris)))
          .toScala
        clean <- scala1
          .buildTargetCleanCache(recordOut(new CleanCacheParams(buildTargetUris)))
          .toScala
        compile <- scala1.buildTargetCompile(recordOut(new CompileParams(buildTargetUris))).toScala
        run <- scala1.buildTargetRun(recordOut(new RunParams(buildTargetUri, emptyList()))).toScala
        test <- scala1
          .buildTargetTest(recordOut(new TestParams(buildTargetUris, emptyList())))
          .toScala
        _ <- {
          recordOut(BuildShutdown)
          scala1.buildShutdown().toScala
        }
        _ = {
          recordOut(BuildExit)
          scala1.onBuildExit()
        }
        // NOTE(olafur): important to not finish test with a notification to avoid Thread.sleep
        // for notification to deliver.
        workspace <- {
          recordOut(WorkspaceBuildTargets)
          scala1.workspaceBuildTargets().toScala
        }
      } yield {
        val obtained = in.toString()
        val expected = out.toString()
        assertNoDiff(obtained, expected)
      }
      Await.result(result, Duration("5s"))
    } finally {
      allResources.cancel()
    }
  }
}
