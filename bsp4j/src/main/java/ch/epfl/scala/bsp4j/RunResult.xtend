package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class RunResult {
    String originId
    @NonNull
    StatusCode statusCode

    new(@NonNull StatusCode statusCode){
        this.statusCode = statusCode
    }
}
