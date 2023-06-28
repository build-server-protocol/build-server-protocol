package ch.epfl.scala.bsp4j

import org.eclipse.lsp4j.generator.JsonRpcData
import org.eclipse.lsp4j.jsonrpc.validation.NonNull

@JsonRpcData
class TestReport {
  String originId
  @NonNull
  BuildTargetIdentifier target
  @NonNull
  Integer passed
  @NonNull
  Integer failed
  @NonNull
  Integer ignored
  @NonNull
  Integer cancelled
  @NonNull
  Integer skipped
  Long time

  new(@NonNull BuildTargetIdentifier target, @NonNull Integer passed, @NonNull Integer failed, @NonNull Integer ignored, @NonNull Integer cancelled, @NonNull Integer skipped){
    this.target = target
    this.passed = passed
    this.failed = failed
    this.ignored = ignored
    this.cancelled = cancelled
    this.skipped = skipped
  }
}
