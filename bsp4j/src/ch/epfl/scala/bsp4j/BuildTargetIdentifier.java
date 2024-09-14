package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/**
 * A unique identifier for a target, can use any URI-compatible encoding as long as it is unique within the workspace.
 * Clients should not infer metadata out of the URI structure such as the path or query parameters, use `BuildTarget` instead.
 */
@SuppressWarnings("all")
public class BuildTargetIdentifier {
  @NonNull
  private String uri;

  public BuildTargetIdentifier(@NonNull final String uri) {
    this.uri = uri;
  }

  @NonNull
  public String getUri() {
    return this.uri;
  }

  public void setUri(@NonNull final String uri) {
    this.uri = Preconditions.checkNotNull(uri, "uri");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("uri", this.uri);
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
    BuildTargetIdentifier other = (BuildTargetIdentifier) obj;
    if (this.uri == null) {
      if (other.uri != null)
        return false;
    } else if (!this.uri.equals(other.uri))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    return 31 * 1 + ((this.uri== null) ? 0 : this.uri.hashCode());
  }
}
