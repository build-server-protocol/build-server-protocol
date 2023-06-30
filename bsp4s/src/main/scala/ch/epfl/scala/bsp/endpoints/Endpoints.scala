package ch.epfl.scala.bsp
package endpoints

import jsonrpc4s.Endpoint
import jsonrpc4s.Endpoint.unitCodec
object Build extends Build
trait Build {
  object showMessage extends Endpoint[ShowMessageParams, Unit]("build/showMessage")
  object logMessage extends Endpoint[LogMessageParams, Unit]("build/logMessage")
  object publishDiagnostics
      extends Endpoint[PublishDiagnosticsParams, Unit]("build/publishDiagnostics")
  object taskStart extends Endpoint[TaskStartParams, Unit]("build/taskStart")
  object taskProgress extends Endpoint[TaskProgressParams, Unit]("build/taskProgress")
  object taskFinish extends Endpoint[TaskFinishParams, Unit]("build/taskFinish")
  object initialize
      extends Endpoint[InitializeBuildParams, InitializeBuildResult]("build/initialize")
  object initialized extends Endpoint[Unit, Unit]("build/initialized")
  object shutdown extends Endpoint[Unit, Unit]("build/shutdown")
  object exit extends Endpoint[Unit, Unit]("build/exit")
}
object BuildTarget extends BuildTarget
trait BuildTarget {
  object didChange extends Endpoint[DidChangeBuildTarget, Unit]("buildTarget/didChange")
  object sources extends Endpoint[SourcesParams, SourcesResult]("buildTarget/sources")
  object inverseSources
      extends Endpoint[InverseSourcesParams, InverseSourcesResult]("buildTarget/inverseSources")
  object dependencySources
      extends Endpoint[DependencySourcesParams, DependencySourcesResult](
        "buildTarget/dependencySources"
      )
  object dependencyModules
      extends Endpoint[DependencyModulesParams, DependencyModulesResult](
        "buildTarget/dependencyModules"
      )
  object resources extends Endpoint[ResourcesParams, ResourcesResult]("buildTarget/resources")
  object outputPaths
      extends Endpoint[OutputPathsParams, OutputPathsResult]("buildTarget/outputPaths")
  object compile extends Endpoint[CompileParams, CompileResult]("buildTarget/compile")
  object run extends Endpoint[RunParams, RunResult]("buildTarget/run")
  object test extends Endpoint[TestParams, TestResult]("buildTarget/test")
  object cleanCache extends Endpoint[CleanCacheParams, CleanCacheResult]("buildTarget/cleanCache")
  object cppOptions extends Endpoint[CppOptionsParams, CppOptionsResult]("buildTarget/cppOptions")
  object javacOptions
      extends Endpoint[JavacOptionsParams, JavacOptionsResult]("buildTarget/javacOptions")
  object jvmTestEnvironment
      extends Endpoint[JvmTestEnvironmentParams, JvmTestEnvironmentResult](
        "buildTarget/jvmTestEnvironment"
      )
  object jvmRunEnvironment
      extends Endpoint[JvmRunEnvironmentParams, JvmRunEnvironmentResult](
        "buildTarget/jvmRunEnvironment"
      )
  object pythonOptions
      extends Endpoint[PythonOptionsParams, PythonOptionsResult]("buildTarget/pythonOptions")
  object scalacOptions
      extends Endpoint[ScalacOptionsParams, ScalacOptionsResult]("buildTarget/scalacOptions")
  object scalaTestClasses
      extends Endpoint[ScalaTestClassesParams, ScalaTestClassesResult](
        "buildTarget/scalaTestClasses"
      )
  object scalaMainClasses
      extends Endpoint[ScalaMainClassesParams, ScalaMainClassesResult](
        "buildTarget/scalaMainClasses"
      )
}
object Workspace extends Workspace
trait Workspace {
  object buildTargets extends Endpoint[Unit, WorkspaceBuildTargetsResult]("workspace/buildTargets")
  object reload extends Endpoint[Unit, Unit]("workspace/reload")
}
object DebugSession extends DebugSession
trait DebugSession {
  object start extends Endpoint[DebugSessionParams, DebugSessionAddress]("debugSession/start")
}
