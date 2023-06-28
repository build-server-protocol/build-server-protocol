package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class CppOptionsResult {
    @NonNull
    List<CppOptionsItem> items

    new(@NonNull List<CppOptionsItem> items){
        this.items = items
    }
}
