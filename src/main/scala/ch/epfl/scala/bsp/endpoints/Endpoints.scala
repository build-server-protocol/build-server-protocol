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
}

object Workspace extends Workspace
trait Workspace {
  object buildTargets
      extends Endpoint[WorkspaceBuildTargetsRequest, WorkspaceBuildTargets](
        "workspace/buildTargets")
}
