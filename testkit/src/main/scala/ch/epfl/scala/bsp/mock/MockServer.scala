package ch.epfl.scala.bsp.mock

import java.io._
import java.nio.file.Files

import ch.epfl.scala.bsp.BspConnectionDetails
import io.circe.syntax._
import monix.eval.Task
import monix.execution.{CancelableFuture, ExecutionModel, Scheduler}
import scribe.Logger

import scala.collection.JavaConverters._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.meta.jsonrpc._

object MockServer {

  implicit val scheduler: Scheduler = Scheduler(
    java.util.concurrent.Executors.newFixedThreadPool(4),
    ExecutionModel.AlwaysAsyncExecution
  )

  def main(args: Array[String]): Unit = {
    val cwd = new File(".").getCanonicalFile

    args.headOption match {
      case Some("config") =>
        val scriptPath = System.getProperty("script.path")
        val scriptFile = new File(scriptPath)

        val configFile = new File(cwd,".bsp/mockserver.json")
        if (createConnectionFile(scriptFile, configFile))
          println(s"config file written: $configFile")
        else
          println(s"file could not be created: $configFile")

      case Some("bsp") =>
        val logger = new Logger
        val client = LanguageClient.fromOutputStream(System.out, logger)
        val running = serverTask(new HappyMockServer(cwd, logger, client), System.in, System.out).runAsync
        System.err.println("Mock server listening")
        Await.ready(running, Duration.Inf)
      case _ =>
        System.err.println(
          """supported commands:
            |config - create .bsp configuration in working directory
            |bsp - start bsp server
            |""".stripMargin)
    }
  }

  def createConnectionFile(scriptFile: File, file: File): Boolean = {

    val parent = file.getParentFile
    (parent.isDirectory || parent.mkdirs()) && {
      val details = BspConnectionDetails(
        name = "BSP Mock Server",
        argv = List(s"$scriptFile", "bsp"),
        version = "1.0",
        bspVersion = "2.0",
        languages = List("java","scala")
      )
      val json = List(details.asJson.toString()).asJava
      Files.write(file.toPath, json)
      file.isFile
    }
  }

  case class LocalMockServer(running: CancelableFuture[_], clientIn: PipedInputStream, clientOut: PipedOutputStream)

  def startMockServer(testBaseDir: File): LocalMockServer = {

    val clientIn = new PipedInputStream()
    val serverOut = new PipedOutputStream(clientIn)
    val serverIn = new PipedInputStream()
    val clientOut = new PipedOutputStream(serverIn)

    val logger = new Logger
    val client = LanguageClient.fromOutputStream(serverOut, logger)
    val mock = new HappyMockServer(testBaseDir, logger, client)
    val running = MockServer.serverTask(mock, serverIn, serverOut).runAsync
    LocalMockServer(running, clientIn, clientOut)
  }

  def serverTask(server: AbstractMockServer, in: InputStream, out: OutputStream): Task[Unit] = {

    val client = new LanguageClient(out, server.logger)
    val messages = BaseProtocolMessage.fromInputStream(in, server.logger)
    val languageServer = new LanguageServer(messages, client, server.services, scheduler, server.logger)

    languageServer.startTask
      .onErrorHandleWith { err =>
        Task.now {
          System.err.println(s"Mock BSP server failed with ${ err.getMessage}")
          err.printStackTrace(System.err)
        }
      }
  }

}
