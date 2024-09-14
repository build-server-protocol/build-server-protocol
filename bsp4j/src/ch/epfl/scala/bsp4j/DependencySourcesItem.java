package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

@SuppressWarnings("all")
public class DependencySourcesItem {
  @NonNull
  private BuildTargetIdentifier target;

  @NonNull
  private List<String> sources;

  public DependencySourcesItem(@NonNull final BuildTargetIdentifier target, @NonNull final List<String> sources) {
    this.target = target;
    this.sources = sources;
  }

  @NonNull
  public BuildTargetIdentifier getTarget() {
    return this.target;
  }

  public void setTarget(@NonNull final BuildTargetIdentifier target) {
    this.target = Preconditions.checkNotNull(target, "target");
  }

  @NonNull
  public List<String> getSources() {
    return this.sources;
  }

  public void setSources(@NonNull final List<String> sources) {
    this.sources = Preconditions.checkNotNull(sources, "sources");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("sources", this.sources);
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
    DependencySourcesItem other = (DependencySourcesItem) obj;
    if (this.target == null) {
      if (other.target != null)
        return false;
    } else if (!this.target.equals(other.target))
      return false;
    if (this.sources == null) {
      if (other.sources != null)
        return false;
    } else if (!this.sources.equals(other.sources))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    return prime * result + ((this.sources== null) ? 0 : this.sources.hashCode());
  }
}
