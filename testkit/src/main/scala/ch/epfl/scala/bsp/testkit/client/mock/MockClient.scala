package ch.epfl.scala.bsp.testkit.client.mock

import java.util.{Timer, TimerTask}

import ch.epfl.scala.bsp4j._

import scala.collection.mutable
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Future, Promise}

class MockClient extends BuildClient {
  private val showMessages = new mutable.ListBuffer[ShowMessageParams]
  def getShowMessages: List[ShowMessageParams] = showMessages.toList
  private val logMessages = new mutable.ListBuffer[LogMessageParams]
  def getLogMessages: List[LogMessageParams] = logMessages.toList
  private val taskStart = new mutable.ListBuffer[TaskStartParams]
  def getTaskStart: List[TaskStartParams] = taskStart.toList
  private val taskProgress = new mutable.ListBuffer[TaskProgressParams]
  def getTaskProgress: List[TaskProgressParams] = taskProgress.toList
  private val taskFinish = new mutable.ListBuffer[TaskFinishParams]
  def getTaskFinish: List[TaskFinishParams] = taskFinish.toList
  private val publishDiagnostics = new mutable.ListBuffer[PublishDiagnosticsParams]
  def getPublishDiagnostics: List[PublishDiagnosticsParams] = publishDiagnostics.toList
  private val didChangeBuildTarget = new mutable.ListBuffer[DidChangeBuildTarget]
  def getDidChangeBuildTarget: List[DidChangeBuildTarget] = didChangeBuildTarget.toList
  private val stdOut = new mutable.ListBuffer[PrintParams]
  def getStdOut: List[PrintParams] = stdOut.toList
  private val stdErr = new mutable.ListBuffer[PrintParams]
  def getStdErr: List[PrintParams] = stdErr.toList

  private val interval = 15.milliseconds

  override def onBuildShowMessage(params: ShowMessageParams): Unit = showMessages += params

  override def onBuildLogMessage(params: LogMessageParams): Unit = logMessages += params

  override def onBuildTaskStart(params: TaskStartParams): Unit = taskStart += params

  override def onBuildTaskProgress(params: TaskProgressParams): Unit = taskProgress += params

  override def onBuildTaskFinish(params: TaskFinishParams): Unit = taskFinish += params

  override def onBuildPublishDiagnostics(params: PublishDiagnosticsParams): Unit =
    publishDiagnostics += params

  override def onBuildTargetDidChange(params: DidChangeBuildTarget): Unit =
    didChangeBuildTarget += params

  override def onRunPrintStdout(params: PrintParams): Unit =
    stdOut += params

  override def onRunPrintStderr(params: PrintParams): Unit =
    stdErr += params

  def poll[T](notifications: List[T], duration: Duration, condition: T => Boolean): Future[T] = {
    val promise = Promise[T]()
    val timer = new Timer()
    timer.scheduleAtFixedRate(
      new TimerTask {
        private var durationLeft = duration
        override def run(): Unit = {
          notifications.find(condition) match {
            case Some(value) =>
              promise.success(value)
              timer.cancel()
            case None =>
              durationLeft = durationLeft - interval
              if (durationLeft.toSeconds <= 0) {
                promise.failure(new Throwable("No notification found"))
                timer.cancel()
              }
          }
        }
      },
      0,
      interval.toMillis
    )
    promise.future
  }

}
