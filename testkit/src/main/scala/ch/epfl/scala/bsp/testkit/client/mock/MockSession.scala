package ch.epfl.scala.bsp.testkit.client.mock

import java.io.{InputStream, OutputStream}
import java.util.concurrent.{ExecutorService, Executors}
import ch.epfl.scala.bsp4j.{BuildServer, InitializeBuildParams}
import org.eclipse.lsp4j.jsonrpc.Launcher

import scala.util.Try

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

    val launcher = new Launcher.Builder[BuildServer]()
      .setRemoteInterface(classOf[BuildServer])
      .setExecutorService(executors)
      .setInput(in)
      .setOutput(out)
      .setLocalService(client)
      .create()

    val listening = launcher.startListening
    new Thread (() => Try(listening.get())).start()

    val server: BuildServer = launcher.getRemoteProxy

    client.onConnectWithServer(server)

    val cancelable = () => {
      in.close()
      out.close()
      listening.cancel(true)
      cleanup()
      executors.shutdown()
    }

    MockConnection(server, cancelable)
  }

  case class MockConnection(server: BuildServer, cancelable: () => Unit)
}

object MockSession {
  def apply(
      process: Process,
      initializeBuildParams: InitializeBuildParams,
      cleanup: () => Unit
  ): MockSession =
    new MockSession(process.getInputStream, process.getOutputStream, initializeBuildParams, cleanup)

  def apply(
      in: InputStream,
      out: OutputStream,
      initializeBuildParams: InitializeBuildParams,
      cleanup: () => Unit
  ): MockSession = new MockSession(in, out, initializeBuildParams, cleanup)
}
