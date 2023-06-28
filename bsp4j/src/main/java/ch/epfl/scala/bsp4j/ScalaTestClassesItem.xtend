package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class ScalaTestClassesItem {
    @NonNull
    BuildTargetIdentifier target
    String framework
    @NonNull
    List<String> classes

    new(@NonNull BuildTargetIdentifier target, @NonNull List<String> classes){
        this.target = target
        this.classes = classes
    }
}
