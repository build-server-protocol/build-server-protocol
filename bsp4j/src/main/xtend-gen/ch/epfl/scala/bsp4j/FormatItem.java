package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class FormatItem {
  @NonNull
  private BuildTargetIdentifier target;

  private List<String> uris;

  public FormatItem(@NonNull final BuildTargetIdentifier target, final List<String> uris) {
    this.target = target;
    this.uris = uris;
  }

  @Pure
  @NonNull
  public BuildTargetIdentifier getTarget() {
    return this.target;
  }

  public void setTarget(@NonNull final BuildTargetIdentifier target) {
    this.target = Preconditions.checkNotNull(target, "target");
  }

  @Pure
  public List<String> getUris() {
    return this.uris;
  }

  public void setUris(final List<String> uris) {
    this.uris = uris;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("uris", this.uris);
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
    FormatItem other = (FormatItem) obj;
    if (this.target == null) {
      if (other.target != null)
        return false;
    } else if (!this.target.equals(other.target))
      return false;
    if (this.uris == null) {
      if (other.uris != null)
        return false;
    } else if (!this.uris.equals(other.uris))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    return prime * result + ((this.uris== null) ? 0 : this.uris.hashCode());
  }
}
