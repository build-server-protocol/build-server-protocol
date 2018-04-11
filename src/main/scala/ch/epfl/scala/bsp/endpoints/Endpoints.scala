package ch.epfl.scala.bsp.endpoints

import ch.epfl.scala.bsp.schema._
import org.langmeta.jsonrpc.Endpoint

import scalapb_circe.JsonFormat._

object Build extends Build
trait Build {
  object initialize
      extends Endpoint[InitializeBuildParams, InitializeBuildResult]("build/initialize")
  object initialized extends Endpoint[InitializedBuildParams, Unit]("build/initialized")
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
