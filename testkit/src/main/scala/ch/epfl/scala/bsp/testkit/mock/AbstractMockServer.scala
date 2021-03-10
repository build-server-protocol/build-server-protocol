package ch.epfl.scala.bsp.testkit.mock

import ch.epfl.scala.bsp4j._
import com.google.gson.GsonBuilder

import scala.jdk.CollectionConverters._

abstract class AbstractBuildServer extends BuildServer with ScalaBuildServer with JvmBuildServer with JavaBuildServer with CppBuildServer

abstract class AbstractMockServer extends AbstractBuildServer {
  var client: BuildClient

  private val gson = new GsonBuilder()
    .setPrettyPrinting()
    .create()

  // notification helpers

  def logMessage(message: String,
                 messageType: MessageType = MessageType.INFORMATION,
                 task: Option[TaskId] = None,
                 origin: Option[String] = None): Unit = {
    val params = new LogMessageParams(messageType, message)
    task.foreach(params.setTask)
    origin.foreach(params.setOriginId)

    client.onBuildLogMessage(params)
  }

  def showMessage(message: String,
                  messageType: MessageType = MessageType.INFORMATION,
                  task: Option[TaskId] = None,
                  origin: Option[String] = None): Unit = {
    val params = new ShowMessageParams(messageType, message)
    task.foreach(params.setTask)
    origin.foreach(params.setOriginId)

    client.onBuildShowMessage(params)
  }

  def publishDiagnostics(doc: TextDocumentIdentifier,
                         target: BuildTargetIdentifier,
                         diagnostics: List[Diagnostic],
                         origin: Option[String] = None,
                         reset: Boolean = false): Unit = {
    val params = new PublishDiagnosticsParams(doc, target, diagnostics.asJava, reset)
    origin.foreach(params.setOriginId)

    client.onBuildPublishDiagnostics(params)
  }

  def taskStart(taskId: TaskId,
                message: String,
                dataKind: Option[String],
                data: Option[AnyRef]): Unit = {
    val time = System.currentTimeMillis()
    val params = new TaskStartParams(taskId)
    params.setEventTime(time)
    params.setMessage(message)
    dataKind.foreach(params.setDataKind)
    data.foreach(d => params.setData(gson.toJsonTree(d)))

    client.onBuildTaskStart(params)
  }

  def taskProgress(taskId: TaskId,
                   message: String,
                   total: Long,
                   progress: Long,
                   dataKind: Option[String],
                   data: Option[AnyRef]): Unit = {
    val time = System.currentTimeMillis()
    val params = new TaskProgressParams(taskId)
    params.setEventTime(time)
    params.setMessage(message)
    params.setTotal(total)
    params.setProgress(progress)
    dataKind.foreach(params.setDataKind)
    data.foreach(d => params.setData(gson.toJsonTree(d)))

    client.onBuildTaskProgress(params)
  }

  def taskFinish(taskId: TaskId,
                 message: String,
                 statusCode: StatusCode,
                 dataKind: Option[String],
                 data: Option[AnyRef]): Unit = {
    val time = System.currentTimeMillis()
    val params = new TaskFinishParams(taskId, statusCode)
    params.setEventTime(time)
    params.setMessage(message)
    params.setStatus(statusCode)
    dataKind.foreach(params.setDataKind)
    data.foreach(d => params.setData(gson.toJsonTree(d)))

    client.onBuildTaskFinish(params)
  }

  def compileStart(taskId: TaskId, message: String, target: BuildTargetIdentifier): Unit = {
    val data = new CompileTask(target)
    taskStart(taskId, message, Some(TaskDataKind.COMPILE_TASK), Some(data))
  }

  def compileReport(taskId: TaskId,
                    message: String,
                    target: BuildTargetIdentifier,
                    status: StatusCode,
                    time: Long = System.currentTimeMillis): Unit = {
    val origin = taskId.getParents.asScala.headOption
    val data = status match {
      case StatusCode.OK => new CompileReport(target, 0, 0)
      case StatusCode.ERROR => new CompileReport(target, 1, 0)
      case StatusCode.CANCELLED => new CompileReport(target, 0, 1)
    }
    origin.foreach(data.setOriginId)
    data.setTime(time)

    taskFinish(taskId, message, status, Some(TaskDataKind.COMPILE_REPORT), Some(data))
  }
}
