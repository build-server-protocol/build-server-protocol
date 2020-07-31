package ch.epfl.scala.bsp.testkit.client.mock

import java.net.URI
import java.util.concurrent.Executors

import ch.epfl.scala.bsp4j.{BuildServer, InitializeBuildParams}
import org.eclipse.lsp4j.jsonrpc.Launcher

case class MockSession(process: Process, root: URI, compilerOutput: URI, initializeBuildParams: InitializeBuildParams, cleanup: () => Unit) {

  val connection: MockConnection = startServerConnection
  val client = new MockClient

  private def startServerConnection: MockConnection = {

    val launcher = new Launcher.Builder[BuildServer]()
      .setRemoteInterface(classOf[BuildServer])
      .setExecutorService(Executors.newCachedThreadPool())
      .setInput(process.getInputStream)
      .setOutput(process.getOutputStream)
      .setLocalService(client)
      .create()

    val listening = launcher.startListening
    new Thread {
      listening.get()
    }
    val server: BuildServer = launcher.getRemoteProxy

    client.onConnectWithServer(server)

    val cancelable = () => {
      process.getInputStream.close()
      process.getOutputStream.close()
      listening.cancel(true)
      cleanup()
    }


    MockConnection(server, cancelable)
  }

  case class MockConnection(server: BuildServer, cancelable: () => Unit)
}
