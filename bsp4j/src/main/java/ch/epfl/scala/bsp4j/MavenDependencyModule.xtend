package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class MavenDependencyModule {
  @NonNull
  String organization
  @NonNull
  String name
  @NonNull
  String version
  @NonNull
  List<MavenDependencyModuleArtifact> artifacts
  String scope

  new(@NonNull String organization, @NonNull String name, @NonNull String version, @NonNull List<MavenDependencyModuleArtifact> artifacts){
    this.organization = organization
    this.name = name
    this.version = version
    this.artifacts = artifacts
  }
}
