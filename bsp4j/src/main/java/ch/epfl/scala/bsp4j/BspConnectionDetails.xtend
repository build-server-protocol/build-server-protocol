package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class BspConnectionDetails {
    @NonNull
    String name
    @NonNull
    List<String> argv
    @NonNull
    String version
    @NonNull
    String bspVersion
    @NonNull
    List<String> languages

    new(@NonNull String name, @NonNull List<String> argv, @NonNull String version, @NonNull String bspVersion, @NonNull List<String> languages){
        this.name = name
        this.argv = argv
        this.version = version
        this.bspVersion = bspVersion
        this.languages = languages
    }
}
