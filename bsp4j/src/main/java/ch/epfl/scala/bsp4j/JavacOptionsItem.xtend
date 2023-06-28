package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class JavacOptionsItem {
    @NonNull
    BuildTargetIdentifier target
    @NonNull
    List<String> options
    @NonNull
    List<String> classpath
    @NonNull
    String classDirectory

    new(@NonNull BuildTargetIdentifier target, @NonNull List<String> options, @NonNull List<String> classpath, @NonNull String classDirectory){
        this.target = target
        this.options = options
        this.classpath = classpath
        this.classDirectory = classDirectory
    }
}
