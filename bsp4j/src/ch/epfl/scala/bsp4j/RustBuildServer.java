package ch.epfl.scala.bsp4j;

import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

public interface RustBuildServer {
  /**
   * *Unstable** (may change in future versions) The Rust workspace request is sent from the client
   * to the server to query for the information about project's workspace for the given list of
   * build targets.
   *
   * <p>The request is essential to connect and work with `intellij-rust` plugin.
   *
   * <p>The request may take a long time, as it may require building a project to some extent (for
   * example with `cargo check` command).
   */
  @JsonRequest("buildTarget/rustWorkspace")
  CompletableFuture<RustWorkspaceResult> rustWorkspace(RustWorkspaceParams params);
}
