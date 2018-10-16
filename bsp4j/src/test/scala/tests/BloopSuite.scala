package tests

import bloop.Cli
import bloop.cli.CommonOptions
import bloop.engine.NoPool
import ch.epfl.scala.bsp4j._
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util
import java.util.Collections
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.scalasbt.ipcsocket.UnixDomainSocket
import org.scalatest.FunSuite
import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.util.Random

trait BloopServer extends BuildServer with ScalaBuildServer
class BloopClient extends BuildClient {
  val showMessages = ListBuffer.empty[ShowMessageParams]
  val logMessages = ListBuffer.empty[LogMessageParams]
  val diagnostics = ListBuffer.empty[PublishDiagnosticsParams]
  val compileReports = ListBuffer.empty[CompileReport]
  def reset(): Unit = {
    showMessages.clear()
    logMessages.clear()
    diagnostics.clear()
    compileReports.clear()
  }
  override def onBuildShowMessage(params: ShowMessageParams): Unit = showMessages += params
  override def onBuildLogMessage(params: LogMessageParams): Unit = logMessages += params
  override def onBuildPublishDiagnostics(params: PublishDiagnosticsParams): Unit = diagnostics += params
  override def onBuildTargetDidChange(params: DidChangeBuildTarget): Unit = pprint.log(params)
  override def buildRegisterFileWatcher(
      params: RegisterFileWatcherParams): CompletableFuture[RegisterFileWatcherResult] = null
  override def buildCancelFileWatcher(
      params: CancelFileWatcherParams): CompletableFuture[CancelFileWatcherResult] = null
  override def onBuildTargetCompileReport(params: CompileReport): Unit = compileReports += params
}

class BloopSuite extends FunSuite {
  private val props = new java.util.Properties()
  props.load(this.getClass.getClassLoader.getResourceAsStream("bsp4j.properties"))
  private val bloopDirectory = Paths.get(props.getProperty("bloopDirectory"))

  def exec(args: String*): Unit = {
    import sys.process._
    val exit = Process(args, cwd = bloopDirectory.toFile).!
    assert(exit == 0)
  }

  def callBloop(args: String*): Unit = {
    val ps = System.out
    val common = CommonOptions(
      workingDirectory = bloopDirectory.toString,
      out = ps,
      err = ps,
      ngout = ps,
      ngerr = ps
    )
    val action = Cli.parse(args.toArray, common)
    Cli.run(action, NoPool, args.toArray)
  }

  def connectToBuildServer(localClient: BuildClient): (BloopServer, Cancelable) = {
    val tmp = Files.createTempDirectory("bsp")
    val id = java.lang.Long.toString(Random.nextLong(), Character.MAX_RADIX)
    val socket = tmp.resolve(s"$id.socket")
    socket.toFile.deleteOnExit()
    val args = List[String](
      "bsp",
      "--protocol",
      "local",
      "--socket",
      socket.toString
    )
    val bspProcess = new Thread {
      override def run(): Unit = {
        callBloop(args: _*)
      }
    }
    bspProcess.start()
    waitForFileToBeCreated(socket, 100, 20)
    val bloop = new UnixDomainSocket(socket.toFile.getCanonicalPath)
    val es = Executors.newCachedThreadPool()

    val launcher = new Launcher.Builder[BloopServer]()
//      .traceMessages(new PrintWriter(System.out))
      .setRemoteInterface(classOf[BloopServer])
      .setExecutorService(es)
      .setInput(bloop.getInputStream)
      .setOutput(bloop.getOutputStream)
      .setLocalService(localClient)
      .create()
    launcher.startListening()
    val bsp = launcher.getRemoteProxy
    localClient.onConnectWithServer(bsp)
    val cancelable = Cancelable { () =>
      Cancelable.cancelAll(
        List(
          Cancelable(() => bloop.close()),
          Cancelable(() => bspProcess.interrupt()),
          Cancelable(() => es.shutdown()),
        )
      )
    }
    (bsp, cancelable)
  }

  private def waitForFileToBeCreated(
      path: Path,
      retryDelayMillis: Long,
      maxRetries: Int
  ): Unit = {
    if (maxRetries > 0) {
      if (Files.exists(path)) ()
      else {
        Thread.sleep(retryDelayMillis)
        waitForFileToBeCreated(path, retryDelayMillis, maxRetries - 1)
      }
    } else {
      sys.error(s"no file: $path")
    }
  }

  private val gson = new Gson()

  implicit class XtensionBuildTarget(buildTarget: BuildTarget) {
    def asScalaBuildTarget: ScalaBuildTarget = {
      gson.fromJson[ScalaBuildTarget](buildTarget.getData.asInstanceOf[JsonElement],
                                      classOf[ScalaBuildTarget])
    }
  }

  def assertWorkspaceBuildTargets(server: BloopServer): Unit = {
    // workspace/buildTargets
    val buildTargets = server.workspaceBuildTargets().get().getTargets.asScala
    assert(buildTargets.length == 6)
    val scalaBuildTargets = buildTargets.map(_.asScalaBuildTarget)
    scalaBuildTargets.foreach { scalaBuildTarget =>
      assert(scalaBuildTarget.getScalaVersion == "2.12.7")
      val scalaJars = scalaBuildTarget.getJars.asScala
      List("scala-compiler", "scala-reflect", "scala-library").foreach { scalaJar =>
        assert(scalaJars.exists(_.contains(scalaJar)), (scalaJars, scalaJar))
      }
      // FIXME: scalaBinaryVersion should be 2.12 https://github.com/scalacenter/bloop/issues/677
      assert(scalaBuildTarget.getScalaBinaryVersion == "2.12.7")
    }
  }

  def getBuildTargets(server: BloopServer): util.List[BuildTargetIdentifier] =
    server.workspaceBuildTargets().get().getTargets.asScala.map(_.getId).asJava
  def assertScalacOptions(server: BloopServer): Unit = {
    val scalacOptionsParams = new ScalacOptionsParams(getBuildTargets(server))
    val scalacOptionsResult = server.buildTargetScalacOptions(scalacOptionsParams).get
    val scalacOptionsItems = scalacOptionsResult.getItems.asScala
    scalacOptionsItems.foreach { item =>
      val options = item.getOptions.asScala
      val uri = item.getTarget.getUri
      if (uri.endsWith("a") || uri.endsWith("a-test")) {
        assert(options.contains("-Yrangepos"))
        assert(options.exists(_.contains("semanticdb-scalac")))
      } else if (uri.endsWith("b") || uri.endsWith("b-test")) {
        assert(options.isEmpty)
      }

      val classpath = item.getClasspath.asScala
      assert(classpath.nonEmpty)
      assert(classpath.exists(_.contains("scala-library")))
      if (uri.endsWith("a")) {
        assert(classpath.exists(_.contains("ujson")))
      }
    }
  }

  def assertCompile(server: BloopServer, client: BloopClient): Unit = {
    client.reset()
    val params = new CompileParams(getBuildTargets(server))
    params.setArguments(new JsonArray)
    val compileResult = server.buildTargetCompile(params).get()
    // FIXME: originId should be non-null https://github.com/scalacenter/bloop/issues/679
    assert(compileResult.getOriginId == null)
    assert(client.logMessages.nonEmpty)
    assert(client.diagnostics.nonEmpty)
    assert(client.compileReports.nonEmpty)
  }

  def assertServerCapabilities(serverCapabilities: BuildServerCapabilities): Unit = {
    val compiles = serverCapabilities.getCompileProvider.getLanguageIds.asScala
    val runs = serverCapabilities.getCompileProvider.getLanguageIds.asScala
    val tests = serverCapabilities.getCompileProvider.getLanguageIds.asScala
    assert(compiles == Seq("scala", "java"))
    assert(runs == Seq("scala", "java"))
    assert(tests == Seq("scala", "java"))
  }

  def assertServerEndpoints(server: BloopServer, client: BloopClient): Unit = {
    assertWorkspaceBuildTargets(server)
    assertScalacOptions(server)
    assertCompile(server, client)
    // FIXME: not yet implemented
    // - buildTarget/test
    // - buildTarget/run
    // - buildTarget/mainClasses
    // - buildTarget/scalaMainClasses
  }

  test("end to end") {
    if (!Files.exists(bloopDirectory.resolve("target"))) {
      exec("sbt", "bloopInstall")
    }
    val client = new BloopClient
    val (server, cancel) = connectToBuildServer(client)
    try {
      val capabilities = new BuildClientCapabilities(Collections.singletonList("scala"), false)
      val initializeParams = new InitializeBuildParams(bloopDirectory.toUri.toString, capabilities)
      val serverCapabilities = server.buildInitialize(initializeParams).get().getCapabilities
      server.onBuildInitialized()
      try {
        assertServerCapabilities(serverCapabilities)
        assertServerEndpoints(server, client)
      } finally {
        try server.buildShutdown().get()
        finally server.onBuildExit()
      }
    } finally {
      Thread.sleep(1000) // complete server.exit()
      cancel.cancel()
    }
  }
}
