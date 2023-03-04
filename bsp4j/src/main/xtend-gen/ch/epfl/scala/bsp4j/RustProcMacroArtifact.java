package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustProcMacroArtifact {
  @NonNull
  private String path;

  @NonNull
  private String hash;

  public RustProcMacroArtifact(@NonNull final String path, @NonNull final String hash) {
    this.path = path;
    this.hash = hash;
  }

  @Pure
  @NonNull
  public String getPath() {
    return this.path;
  }

  public void setPath(@NonNull final String path) {
    this.path = Preconditions.checkNotNull(path, "path");
  }

  @Pure
  @NonNull
  public String getHash() {
    return this.hash;
  }

  public void setHash(@NonNull final String hash) {
    this.hash = Preconditions.checkNotNull(hash, "hash");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("path", this.path);
    b.add("hash", this.hash);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RustProcMacroArtifact other = (RustProcMacroArtifact) obj;
    if (this.path == null) {
      if (other.path != null)
        return false;
    } else if (!this.path.equals(other.path))
      return false;
    if (this.hash == null) {
      if (other.hash != null)
        return false;
    } else if (!this.hash.equals(other.hash))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.path== null) ? 0 : this.path.hashCode());
    return prime * result + ((this.hash== null) ? 0 : this.hash.hashCode());
  }
}
