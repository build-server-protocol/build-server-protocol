package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustDepKindInfo {
  @NonNull private String kind;

  private String target;

  public RustDepKindInfo(@NonNull final String kind) {
    this.kind = kind;
  }

  @Pure
  @NonNull
  public String getKind() {
    return this.kind;
  }

  public void setKind(@NonNull final String kind) {
    this.kind = Preconditions.checkNotNull(kind, "kind");
  }

  @Pure
  public String getTarget() {
    return this.target;
  }

  public void setTarget(final String target) {
    this.target = target;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("kind", this.kind);
    b.add("target", this.target);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    RustDepKindInfo other = (RustDepKindInfo) obj;
    if (this.kind == null) {
      if (other.kind != null) return false;
    } else if (!this.kind.equals(other.kind)) return false;
    if (this.target == null) {
      if (other.target != null) return false;
    } else if (!this.target.equals(other.target)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.kind == null) ? 0 : this.kind.hashCode());
    return prime * result + ((this.target == null) ? 0 : this.target.hashCode());
  }
}
