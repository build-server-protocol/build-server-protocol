package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

@SuppressWarnings("all")
public class OutputPathItem {
  @NonNull private String uri;

  @NonNull private OutputPathItemKind kind;

  public OutputPathItem(@NonNull final String uri, @NonNull final OutputPathItemKind kind) {
    this.uri = uri;
    this.kind = kind;
  }

  @NonNull
  public String getUri() {
    return this.uri;
  }

  public void setUri(@NonNull final String uri) {
    this.uri = Preconditions.checkNotNull(uri, "uri");
  }

  @NonNull
  public OutputPathItemKind getKind() {
    return this.kind;
  }

  public void setKind(@NonNull final OutputPathItemKind kind) {
    this.kind = Preconditions.checkNotNull(kind, "kind");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("uri", this.uri);
    b.add("kind", this.kind);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    OutputPathItem other = (OutputPathItem) obj;
    if (this.uri == null) {
      if (other.uri != null) return false;
    } else if (!this.uri.equals(other.uri)) return false;
    if (this.kind == null) {
      if (other.kind != null) return false;
    } else if (!this.kind.equals(other.kind)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.uri == null) ? 0 : this.uri.hashCode());
    return prime * result + ((this.kind == null) ? 0 : this.kind.hashCode());
  }
}
