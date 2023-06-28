package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class PublishDiagnosticsParams {
    @NonNull
    TextDocumentIdentifier textDocument
    @NonNull
    BuildTargetIdentifier buildTarget
    String originId
    @NonNull
    List<Diagnostic> diagnostics
    @NonNull
    Boolean reset

    new(@NonNull TextDocumentIdentifier textDocument, @NonNull BuildTargetIdentifier buildTarget, @NonNull List<Diagnostic> diagnostics, @NonNull Boolean reset){
        this.textDocument = textDocument
        this.buildTarget = buildTarget
        this.diagnostics = diagnostics
        this.reset = reset
    }
}
