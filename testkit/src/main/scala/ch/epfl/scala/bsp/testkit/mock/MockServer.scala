package ch.epfl.scala.bsp.testkit.mock

import java.io._
import java.nio.file.Files
import java.util.concurrent.{ExecutorService, Future}

import ch.epfl.scala.bsp4j.{BspConnectionDetails, BuildClient}
import com.google.gson.Gson
import org.eclipse.lsp4j.jsonrpc.Launcher

import scala.jdk.CollectionConverters._

object MockServer {

  val executorService: ExecutorService = java.util.concurrent.Executors.newFixedThreadPool(4)

  def main(args: Array[String]): Unit = {
    val cwd = new File(".").getCanonicalFile

    args.headOption match {
      case Some("config") =>
        val scriptPath = System.getProperty("script.path")
        val scriptFile = new File(scriptPath)

        val configFile = new File(cwd, ".bsp/mockserver.json")
        if (createConnectionFile(scriptFile, configFile))
          println(s"config file written: $configFile")
        else
          println(s"file could not be created: $configFile")

      case Some("bsp") =>
        val server = new HappyMockServer(cwd)
        val launcher = clientLauncher(server, System.in, System.out)
        val client = launcher.getRemoteProxy
        server.client = client
        val running = launcher.startListening()
        System.err.println("Mock server listening")
        running.get()
      case _ =>
        System.err.println("""supported commands:
            |config - create .bsp configuration in working directory
            |bsp - start bsp server
            |""".stripMargin)
    }
  }

  def createConnectionFile(scriptFile: File, file: File): Boolean = {

    val parent = file.getParentFile
    (parent.isDirectory || parent.mkdirs()) && {
      val details = new BspConnectionDetails(
        "BSP Mock Server",
        List(s"$scriptFile", "bsp").asJava,
        "1.0",
        "2.0",
        List("java", "scala").asJava
      )
      val detailsJson = new Gson().toJson(details)
      val json = List(detailsJson).asJava
      Files.write(file.toPath, json)
      file.isFile
    }
  }

  case class LocalMockServer(
      running: Future[_],
      clientIn: PipedInputStream,
      clientOut: PipedOutputStream
  )

  def startMockServer(testBaseDir: File): LocalMockServer = {

    val clientIn = new PipedInputStream()
    val serverOut = new PipedOutputStream(clientIn)
    val serverIn = new PipedInputStream()
    val clientOut = new PipedOutputStream(serverIn)

    val server = new HappyMockServer(testBaseDir)
    val launcher = clientLauncher(server, serverIn, serverOut)
    val client = launcher.getRemoteProxy
    server.client = client

    val running = launcher.startListening()
    LocalMockServer(running, clientIn, clientOut)
  }

  def clientLauncher(
      server: AbstractMockServer,
      in: InputStream,
      out: OutputStream
  ): Launcher[BuildClient] =
    new Launcher.Builder[BuildClient]()
      .setOutput(out)
      .setInput(in)
      .setLocalService(server)
      .setExecutorService(executorService)
      .setRemoteInterface(classOf[BuildClient])
      .create()

}
