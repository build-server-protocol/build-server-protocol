package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class ScalaAction {
    @NonNull
    String title
    String description
    ScalaWorkspaceEdit edit

    new(@NonNull String title) {
        this.title = title
    }
}
