package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class MavenDependencyModuleArtifact {
    @NonNull
    String uri
    String classifier

    new(@NonNull String uri){
        this.uri = uri
    }
}
