package ch.epfl.scala.bsp4j

import java.util.List
import com.google.gson.annotations.SerializedName
import org.eclipse.lsp4j.jsonrpc.validation.NonNull
import org.eclipse.lsp4j.generator.JsonRpcData

@JsonRpcData
class JavacOptionsParams {
  @NonNull List<BuildTargetIdentifier> targets
  new(@NonNull List<BuildTargetIdentifier> targets) {
    this.targets = targets
  }
}

@JsonRpcData
class JavacOptionsResult {
  @NonNull List<JavacOptionsItem> items
  new(@NonNull List<JavacOptionsItem> items) {
    this.items = items
  }
}

@JsonRpcData
class JavacOptionsItem {
  @NonNull BuildTargetIdentifier target
  @NonNull List<String> options
  @NonNull List<String> classpath
  @NonNull String classDirectory
  new(@NonNull BuildTargetIdentifier target, @NonNull List<String> options, @NonNull List<String> classpath,
      @NonNull String classDirectory) {
    this.target = target
    this.options = options
    this.classpath = classpath
    this.classDirectory = classDirectory
   }
}
