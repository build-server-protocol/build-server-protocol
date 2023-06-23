package ch.epfl.scala.bsp.testkit.client.mock

import ch.epfl.scala.bsp.testkit.client.mock.MockSession.BspMockServer

import java.io.{InputStream, OutputStream}
import java.util.concurrent.{ExecutorService, Executors}
import ch.epfl.scala.bsp4j.{
  BuildServer,
  CppBuildServer,
  InitializeBuildParams,
  JavaBuildServer,
  JvmBuildServer,
  PythonBuildServer,
  ScalaBuildServer
}
import org.eclipse.lsp4j.jsonrpc.Launcher

case class MockSession(
    in: java.io.InputStream,
    out: java.io.OutputStream,
    initializeBuildParams: InitializeBuildParams,
    cleanup: () => Unit
) {

  private val executors: ExecutorService = Executors.newCachedThreadPool()
  val client = new MockClient()
  val connection: MockConnection = startServerConnection

  private def startServerConnection: MockConnection = {

    val launcher = new Launcher.Builder[BspMockServer]()
      .setRemoteInterface(classOf[BspMockServer])
      .setExecutorService(executors)
      .setInput(in)
      .setOutput(out)
      .setLocalService(client)
      .create()

    val listening = launcher.startListening
    new Thread(() =>
      try {
        listening.get()
      } catch {
        case _: Throwable => // Ignore all errors while listening to the launcher
      }
    ).start()

    val server: BspMockServer = launcher.getRemoteProxy

    val cancelable = () => {
      in.close()
      out.close()
      listening.cancel(true)
      cleanup()
      executors.shutdown()
    }

    MockConnection(server, cancelable)
  }

  case class MockConnection(server: BspMockServer, cancelable: () => Unit)
}

object MockSession {
  def apply(
      process: Process,
      initializeBuildParams: InitializeBuildParams,
      cleanup: () => Unit
  ): MockSession =
    new MockSession(process.getInputStream, process.getOutputStream, initializeBuildParams, cleanup)

  trait BspMockServer
      extends BuildServer
      with ScalaBuildServer
      with JavaBuildServer
      with JvmBuildServer
      with CppBuildServer
      with PythonBuildServer

  def apply(
      in: InputStream,
      out: OutputStream,
      initializeBuildParams: InitializeBuildParams,
      cleanup: () => Unit
  ): MockSession = new MockSession(in, out, initializeBuildParams, cleanup)
}
