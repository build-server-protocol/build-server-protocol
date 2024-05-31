package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;

public interface CancelExtension {
  /** Like the language server protocol, a notification to ask the server to cancel a request. */
  @JsonNotification("$/cancelRequest")
  void cancelRequest(CancelRequestParams params);
}
