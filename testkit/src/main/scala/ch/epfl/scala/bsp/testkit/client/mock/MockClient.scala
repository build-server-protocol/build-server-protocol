package ch.epfl.scala.bsp.testkit.client.mock

import java.util.concurrent.{LinkedBlockingQueue, TimeUnit}

import ch.epfl.scala.bsp4j._

import scala.annotation.tailrec
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

class MockClient extends BuildClient {
  private val showMessages = new LinkedBlockingQueue[ShowMessageParams]
  private val logMessages = new LinkedBlockingQueue[LogMessageParams]
  private val taskStart = new LinkedBlockingQueue[TaskStartParams]
  private val taskProgress = new LinkedBlockingQueue[TaskProgressParams]
  private val taskFinish = new LinkedBlockingQueue[TaskFinishParams]
  private val publishDiagnostics = new LinkedBlockingQueue[PublishDiagnosticsParams]
  private val didChangeBuildTarget = new LinkedBlockingQueue[DidChangeBuildTarget]

  private val pollInterval: Long = 500

  override def onBuildShowMessage(params: ShowMessageParams): Unit = showMessages.put(params)

  override def onBuildLogMessage(params: LogMessageParams): Unit = logMessages.put(params)

  override def onBuildTaskStart(params: TaskStartParams): Unit = taskStart.put(params)

  override def onBuildTaskProgress(params: TaskProgressParams): Unit = taskProgress.put(params)

  override def onBuildTaskFinish(params: TaskFinishParams): Unit = taskFinish.put(params)

  override def onBuildPublishDiagnostics(params: PublishDiagnosticsParams): Unit =
    publishDiagnostics.put(params)

  override def onBuildTargetDidChange(params: DidChangeBuildTarget): Unit =
    didChangeBuildTarget.put(params)

  private def convertDuration(duration: Duration) = {
    duration.toMillis / pollInterval
  }

  def pollShowMessages(duration: Duration): Option[ShowMessageParams] =
    poll(showMessages, convertDuration(duration))
  def pollLogMessages(duration: Duration): Option[LogMessageParams] =
    poll(logMessages, convertDuration(duration))
  def pollTaskStart(duration: Duration): Option[TaskStartParams] =
    poll(taskStart, convertDuration(duration))
  def pollTaskProgress(duration: Duration): Option[TaskProgressParams] =
    poll(taskProgress, convertDuration(duration))
  def pollTaskFinish(duration: Duration): Option[TaskFinishParams] =
    poll(taskFinish, convertDuration(duration))
  def pollPublishDiagnostics(duration: Duration): Option[PublishDiagnosticsParams] =
    poll(publishDiagnostics, convertDuration(duration))
  def pollBuildTargetDidChange(duration: Duration): Option[DidChangeBuildTarget] =
    poll(didChangeBuildTarget, convertDuration(duration))

  @tailrec
  private def poll[T](queue: LinkedBlockingQueue[T], n: Long): Option[T] = {
    if (n > 1)
      queue.safeDequeue() match {
        case Success(value) => Some(value.asInstanceOf[T])
        case Failure(_)     => poll(queue, n - 1)
      } else
      None
  }

  implicit class SafeQueue[T](queue: LinkedBlockingQueue[T]) {
    def safeDequeue(): Try[T] = Try(queue.poll(pollInterval, TimeUnit.MILLISECONDS))
  }
}
