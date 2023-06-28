package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class OutputPathItem {
    @NonNull
    String uri
    @NonNull
    OutputPathItemKind kind

    new(@NonNull String uri, @NonNull OutputPathItemKind kind) {
        this.uri = uri
        this.kind = kind
    }
}
