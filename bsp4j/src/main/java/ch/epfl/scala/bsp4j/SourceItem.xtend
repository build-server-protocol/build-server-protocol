package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class SourceItem {
    @NonNull
    String uri
    @NonNull
    SourceItemKind kind
    @NonNull
    Boolean generated

    new(@NonNull String uri, @NonNull SourceItemKind kind, @NonNull Boolean generated) {
        this.uri = uri
        this.kind = kind
        this.generated = generated
    }
}
