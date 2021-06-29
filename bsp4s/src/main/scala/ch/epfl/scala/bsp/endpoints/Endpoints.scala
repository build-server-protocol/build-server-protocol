package ch.epfl.scala.bsp
package endpoints

import jsonrpc4s.Endpoint
import jsonrpc4s.Endpoint.unitCodec

object Build extends Build
trait Build {
  object initialize
      extends Endpoint[InitializeBuildParams, InitializeBuildResult]("build/initialize")
  object initialized extends Endpoint[InitializedBuildParams, Unit]("build/initialized")
  object exit extends Endpoint[Exit, Unit]("build/exit")
  object shutdown extends Endpoint[Shutdown, Unit]("build/shutdown")
  object showMessage extends Endpoint[ShowMessageParams, Unit]("build/showMessage")
  object logMessage extends Endpoint[LogMessageParams, Unit]("build/logMessage")
  object publishDiagnostics
      extends Endpoint[PublishDiagnosticsParams, Unit]("build/publishDiagnostics")
  object taskStart extends Endpoint[TaskStartParams, Unit]("build/taskStart")
  object taskProgress extends Endpoint[TaskProgressParams, Unit]("build/taskProgress")
  object taskFinish extends Endpoint[TaskFinishParams, Unit]("build/taskFinish")
}

object BuildTarget extends BuildTarget
trait BuildTarget {
  object compile extends Endpoint[CompileParams, CompileResult]("buildTarget/compile")
  object test extends Endpoint[TestParams, TestResult]("buildTarget/test")
  object run extends Endpoint[RunParams, RunResult]("buildTarget/run")
  object cleanCache extends Endpoint[CleanCacheParams, CleanCacheResult]("buildTarget/cleanCache")

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

  // Scala specific endpoints
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
  object jvmTestEnvironment
      extends Endpoint[JvmTestEnvironmentParams, JvmTestEnvironmentResult](
        "buildTarget/jvmTestEnvironment"
      )
  object jvmRunEnvironment
      extends Endpoint[JvmRunEnvironmentParams, JvmRunEnvironmentResult](
        "buildTarget/jvmRunEnvironment"
      )
  // Java specific endpoints
  object javacOptions
      extends Endpoint[JavacOptionsParams, JavacOptionsResult](
        "buildTarget/javacOptions"
      )
  // C++ specific endpoints
  object cppOptions
      extends Endpoint[CppOptionsParams, CppOptionsResult](
        "buildTarget/cppOptions"
      )
}

object Workspace extends Workspace
trait Workspace {
  object buildTargets
      extends Endpoint[WorkspaceBuildTargetsRequest, WorkspaceBuildTargetsResult](
        "workspace/buildTargets"
      )
  object reload extends Endpoint[Reload, Unit]("workspace/reload")
}

object DebugSession extends DebugSession

trait DebugSession {
  object start extends Endpoint[DebugSessionParams, DebugSessionAddress]("debugSession/start")
}
