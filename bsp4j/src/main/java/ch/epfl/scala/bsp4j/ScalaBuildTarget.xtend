package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class ScalaBuildTarget {
  @NonNull
  String scalaOrganization
  @NonNull
  String scalaVersion
  @NonNull
  String scalaBinaryVersion
  @NonNull
  ScalaPlatform platform
  @NonNull
  List<String> jars
  JvmBuildTarget jvmBuildTarget

  new(@NonNull String scalaOrganization, @NonNull String scalaVersion, @NonNull String scalaBinaryVersion, @NonNull ScalaPlatform platform, @NonNull List<String> jars){
    this.scalaOrganization = scalaOrganization
    this.scalaVersion = scalaVersion
    this.scalaBinaryVersion = scalaBinaryVersion
    this.platform = platform
    this.jars = jars
  }
}