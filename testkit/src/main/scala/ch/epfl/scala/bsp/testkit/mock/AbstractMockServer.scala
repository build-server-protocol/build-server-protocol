package ch.epfl.scala.bsp.testkit.mock

import ch.epfl.scala.bsp
import ch.epfl.scala.bsp._
import mockServers.BspResponse
import io.circe.Json
import io.circe.syntax._
import monix.eval.Task
import monix.execution.Ack
import scribe.Logger

import scala.concurrent.Future
import scala.meta.jsonrpc.{LanguageClient, Services}

abstract class AbstractMockServer {
  implicit val client: LanguageClient

  val logger: Logger
  val services: Services = Services
    .empty(logger)
    .requestAsync(endpoints.Build.initialize)(initialize)
    .notification(endpoints.Build.initialized)(initialized)
    .request(endpoints.Build.shutdown)(shutdown)
    .notificationAsync(endpoints.Build.exit)(exit(_))
    .requestAsync(endpoints.Workspace.buildTargets)(buildTargets)
    .requestAsync(endpoints.BuildTarget.sources)(sources)
    .requestAsync(endpoints.BuildTarget.dependencySources)(dependencySources)
    .requestAsync(endpoints.BuildTarget.inverseSources)(inverseSources)
    .requestAsync(endpoints.BuildTarget.scalacOptions)(scalacOptions(_))
    .requestAsync(endpoints.BuildTarget.compile)(compile(_))
    .requestAsync(endpoints.BuildTarget.test)(test(_))
    .requestAsync(endpoints.BuildTarget.run)(run(_))

  def initialize(params: InitializeBuildParams): BspResponse[InitializeBuildResult]
  def initialized(params: InitializedBuildParams): Unit
  def shutdown(shutdown: bsp.Shutdown): Unit
  def exit(exit: Exit): Task[Unit]
  def buildTargets(request: WorkspaceBuildTargetsRequest): BspResponse[WorkspaceBuildTargetsResult]
  def sources(params: SourcesParams): BspResponse[SourcesResult]
  def dependencySources(params: DependencySourcesParams): BspResponse[DependencySourcesResult]
  def inverseSources(params: InverseSourcesParams): BspResponse[InverseSourcesResult]
  def scalacOptions(params: ScalacOptionsParams): BspResponse[ScalacOptionsResult]
  def compile(params: CompileParams): BspResponse[CompileResult]
  def test(params: TestParams): BspResponse[TestResult]
  def run(params: RunParams): BspResponse[RunResult]


  // notification helpers

  def logMessage(message: String,
                 messageType: MessageType = MessageType.Info,
                 task: Option[TaskId] = None,
                 origin: Option[String] = None): Future[Ack] = {
    endpoints.Build.logMessage.notify(
      LogMessageParams(messageType, task, origin, message)
    )
  }

  def showMessage(message: String,
                  messageType: MessageType = MessageType.Info,
                  task: Option[TaskId] = None,
                  origin: Option[String] = None): Future[Ack] = {
    endpoints.Build.showMessage.notify(
      ShowMessageParams(MessageType.Info, task, origin, message)
    )
  }

  def publishDiagnostics(doc: TextDocumentIdentifier, target: BuildTargetIdentifier, diagnostics: List[Diagnostic],
                        origin: Option[String] = None): Future[Ack] = {
    endpoints.Build.publishDiagnostics.notify(
      PublishDiagnosticsParams(doc, target, origin, diagnostics)
    )
  }

  def taskStart(taskId: TaskId, message: String, dataKind: Option[String], data: Option[Json]): Future[Ack] = {
    val time = Some(System.currentTimeMillis())
    endpoints.Build.taskStart.notify(
      TaskStartParams(taskId, time, Some(message), dataKind, None))
  }

  def taskProgress(taskId: TaskId, message: String, total: Long, progress: Long, dataKind: Option[String], data: Option[Json]): Future[Ack] = {
    val time = Some(System.currentTimeMillis())
    endpoints.Build.taskProgress.notify(
      TaskProgressParams(taskId, time, Some(message), Some(total), Some(progress), Some("units"), dataKind, data)
    )
  }

  def taskFinish(taskId: TaskId, message: String, statusCode: StatusCode, dataKind: Option[String], data: Option[Json]): Future[Ack] = {
    val time = Some(System.currentTimeMillis())
    endpoints.Build.taskFinish.notify(
      TaskFinishParams(taskId, time, Some(message), statusCode, dataKind, data)
    )
  }

  def compileStart(taskId: TaskId, message: String, target: BuildTargetIdentifier): Future[Ack] = {
    val data = CompileTask(target).asJson
    taskStart(taskId, message, Some(TaskDataKind.CompileTask), Some(data))
  }

  def compileReport(taskId: TaskId, message: String, target: BuildTargetIdentifier, status: StatusCode): Future[Ack] = {
    val origin = taskId.parents.flatMap(_.headOption)
    val data = status match {
      case StatusCode.Ok => CompileReport(target, origin, 0, 0, Some(1))
      case StatusCode.Error => CompileReport(target, origin, 1, 0, Some(1))
      case StatusCode.Cancelled => CompileReport(target, origin, 0, 1, Some(1))
    }
    taskFinish(taskId, message, status, Some(TaskDataKind.CompileReport), Some(data.asJson))
  }
}
