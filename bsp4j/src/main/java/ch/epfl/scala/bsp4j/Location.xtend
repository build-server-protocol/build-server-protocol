package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class Location {
    @NonNull
    String uri
    @NonNull
    Range range

    new(@NonNull String uri, @NonNull Range range) {
        this.uri = uri
        this.range = range
    }
}
