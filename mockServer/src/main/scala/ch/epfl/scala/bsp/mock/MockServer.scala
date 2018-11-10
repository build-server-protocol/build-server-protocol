package ch.epfl.scala.bsp.mock

import java.io.{File, InputStream, OutputStream}

import monix.eval.Task
import monix.execution.{ExecutionModel, Scheduler}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.meta.jsonrpc._

object MockServer {

  implicit val scheduler: Scheduler = Scheduler(
    java.util.concurrent.Executors.newFixedThreadPool(4),
    ExecutionModel.AlwaysAsyncExecution
  )

  def main(args: Array[String]): Unit = {
    args.headOption match {
      case Some("config") =>
        // TODO create bsp config in .bsp dir
      case Some("bsp") =>
        val cwd = new File(".")
        val running = serverTask(new HappyMockServer(cwd), System.in, System.out).runAsync
        Await.ready(running, Duration.Inf)
      case _ =>
        System.err.println(
          """supported commands:
            |config - create .bsp configuration in working directory
            |bsp - start bsp server
            |""".stripMargin)
    }
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
