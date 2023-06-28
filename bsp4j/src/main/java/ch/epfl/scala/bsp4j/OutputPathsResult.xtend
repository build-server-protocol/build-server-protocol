package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class OutputPathsResult {
    @NonNull
    List<OutputPathsItem> items

    new(@NonNull List<OutputPathsItem> items) {
        this.items = items
    }
}
