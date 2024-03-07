package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;

/**
 * The debug session will connect to a running process. The DAP client will send the port of the running process later.
 */
@SuppressWarnings("all")
public class ScalaAttachRemote {
  public ScalaAttachRemote() {
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    return 1;
  }
}
