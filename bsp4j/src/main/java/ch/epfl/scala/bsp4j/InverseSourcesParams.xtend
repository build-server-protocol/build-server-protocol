package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class InverseSourcesParams {
    @NonNull
    TextDocumentIdentifier textDocument

    new(@NonNull TextDocumentIdentifier textDocument) {
        this.textDocument = textDocument
    }
}
