package ch.epfl.scala.bsp4j

import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class MavenDependencyModule {
  @NonNull String organization
  @NonNull String name
  @NonNull String version
  @NonNull List<MavenDependencyModuleArtifact> artifacts
  String scope
  new(
    @NonNull String organization,
    @NonNull String name,
    @NonNull String version,
    @NonNull List<MavenDependencyModuleArtifact> artifacts
  ) {
    this.organization = organization
    this.name = name
    this.version = version
    this.artifacts = artifacts
  }
}

@JsonRpcData
class MavenDependencyModuleArtifact {
  @NonNull String uri
  String classifier

  new (@NonNull String uri) {
    this.uri = uri
  }
}
