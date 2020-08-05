package ch.epfl.scala.bsp.testkit.client.mock

import java.io.{InputStream, OutputStream}
import java.util.concurrent.Executors

import ch.epfl.scala.bsp4j.{BuildServer, InitializeBuildParams}
import org.eclipse.lsp4j.jsonrpc.Launcher

case class MockSession(
    in: java.io.InputStream,
    out: java.io.OutputStream,
    initializeBuildParams: InitializeBuildParams,
    cleanup: () => Unit
) {

  val client = new MockClient()
  val connection: MockConnection = startServerConnection

  private def startServerConnection: MockConnection = {

    val launcher = new Launcher.Builder[BuildServer]()
      .setRemoteInterface(classOf[BuildServer])
      .setExecutorService(Executors.newCachedThreadPool())
      .setInput(in)
      .setOutput(out)
      .setLocalService(client)
      .create()

    val listening = launcher.startListening
    new Thread (() => listening.get()).start()

    val server: BuildServer = launcher.getRemoteProxy

    client.onConnectWithServer(server)

    val cancelable = () => {
      in.close()
      out.close()
      listening.cancel(true)
      cleanup()
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
