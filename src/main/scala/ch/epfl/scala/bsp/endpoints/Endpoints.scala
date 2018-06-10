package ch.epfl.scala.bsp
package endpoints

import scala.meta.jsonrpc.Endpoint

object Build extends Build
trait Build {
  object initialize
      extends Endpoint[InitializeBuildParams, InitializeBuildResult]("build/initialize")
  object initialized extends Endpoint[InitializedBuildParams, Unit]("build/initialized")
  object showMessage extends Endpoint[ShowMessageParams, Unit]("build/showMessage")
  object logMessage extends Endpoint[LogMessageParams, Unit]("build/logMessage")
}

object BuildTarget extends BuildTarget
trait BuildTarget {
  object compile extends Endpoint[CompileParams, CompileReport]("buildTarget/compile")
  object dependencySources
      extends Endpoint[DependencySourcesParams, DependencySources]("buildTarget/dependencySources")
  object resources
      extends Endpoint[DependencySourcesParams, DependencySources]("buildTarget/dependencySources")
  object scalacOptions
      extends Endpoint[ScalacOptionsParams, ScalacOptions]("buildTarget/scalacOptions")
  object scalaTestClasses
      extends Endpoint[ScalaTestClassesParams, ScalaTestClasses]("buildTarget/scalaTestClasses")
}

object Workspace extends Workspace
trait Workspace {
  object buildTargets
      extends Endpoint[WorkspaceBuildTargetsRequest, WorkspaceBuildTargets](
        "workspace/buildTargets")
}
