package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class JvmMainClass {
    @NonNull
    String className
    @NonNull
    List<String> arguments

    new(@NonNull String className, @NonNull List<String> arguments) {
        this.className = className
        this.arguments = arguments
    }
}
