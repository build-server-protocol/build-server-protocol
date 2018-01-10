package ch.epfl.scala.bsp

import org.langmeta.jsonrpc.Endpoint

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
