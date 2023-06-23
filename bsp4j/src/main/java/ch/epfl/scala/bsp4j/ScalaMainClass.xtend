package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import com.google.gson.annotations.SerializedName
import java.util.List
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class ScalaMainClass {
  @SerializedName("class")
  @NonNull
  String className
  @NonNull
  List<String> arguments
  @NonNull
  List<String> jvmOptions
  List<String> environmentVariables

  new(@NonNull String className, @NonNull List<String> arguments, @NonNull List<String> jvmOptions){
    this.className = className
    this.arguments = arguments
    this.jvmOptions = jvmOptions
  }
}