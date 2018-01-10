package ch.epfl.scala.bsp

import ch.epfl.scala.bsp.schema._
import org.langmeta.jsonrpc.Endpoint
import scalapb_circe.JsonFormat._

object Build extends Build
trait Build {
  object initialize
      extends Endpoint[InitializeBuildParams, InitializeBuildResult]("build/initialize")
  val initialized: Endpoint[InitializedBuildParams, Unit] =
    Endpoint.notification[InitializedBuildParams]("build/initialized")
}

trait BuildTarget {
  object compile extends Endpoint[CompileParams, CompileReport]("buildTarget/compile")
}
