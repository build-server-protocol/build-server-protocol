package ch.epfl.scala.bsp.mock

import ch.epfl.scala.bsp
import ch.epfl.scala.bsp._
import ch.epfl.scala.bsp.mock.mockServers.BspResponse
import monix.eval.Task
import scribe.Logger

import scala.meta.jsonrpc.Services

abstract class AbstractMockServer {
  val logger = new Logger
  val services: Services = Services
    .empty(logger)
    .requestAsync(endpoints.Build.initialize)(initialize)
    .notification(endpoints.Build.initialized)(initialized)
    .request(endpoints.Build.shutdown)(shutdown)
    .notificationAsync(endpoints.Build.exit)(exit(_))
    .requestAsync(endpoints.Workspace.buildTargets)(buildTargets)
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
  def buildTargets(request: WorkspaceBuildTargetsRequest): BspResponse[WorkspaceBuildTargets]
  def dependencySources(params: DependencySourcesParams): BspResponse[DependencySourcesResult]
  def inverseSources(params: InverseSourcesParams): BspResponse[InverseSourcesResult]
  def scalacOptions(params: ScalacOptionsParams): BspResponse[ScalacOptionsResult]
  def compile(params: CompileParams): BspResponse[CompileResult]
  def test(params: TestParams): BspResponse[TestResult]
  def run(params: RunParams): BspResponse[RunResult]
}
