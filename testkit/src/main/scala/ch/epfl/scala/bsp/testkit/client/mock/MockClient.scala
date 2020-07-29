package ch.epfl.scala.bsp.testkit.client.mock

import ch.epfl.scala.bsp4j._

//TODO
class MockClient extends BuildClient {
  override def onBuildShowMessage(params: ShowMessageParams): Unit = ???

  override def onBuildLogMessage(params: LogMessageParams): Unit = ???

  override def onBuildTaskStart(params: TaskStartParams): Unit = ???

  override def onBuildTaskProgress(params: TaskProgressParams): Unit = ???

  override def onBuildTaskFinish(params: TaskFinishParams): Unit = ???

  override def onBuildPublishDiagnostics(params: PublishDiagnosticsParams): Unit = ???

  override def onBuildTargetDidChange(params: DidChangeBuildTarget): Unit = ???
}