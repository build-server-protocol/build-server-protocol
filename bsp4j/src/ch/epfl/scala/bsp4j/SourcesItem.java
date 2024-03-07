package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class SourcesItem {
  @NonNull
  private BuildTargetIdentifier target;

  @NonNull
  private List<SourceItem> sources;

  private List<String> roots;

  public SourcesItem(@NonNull final BuildTargetIdentifier target, @NonNull final List<SourceItem> sources) {
    this.target = target;
    this.sources = sources;
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
  @NonNull
  public List<SourceItem> getSources() {
    return this.sources;
  }

  public void setSources(@NonNull final List<SourceItem> sources) {
    this.sources = Preconditions.checkNotNull(sources, "sources");
  }

  @Pure
  public List<String> getRoots() {
    return this.roots;
  }

  public void setRoots(final List<String> roots) {
    this.roots = roots;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("sources", this.sources);
    b.add("roots", this.roots);
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
    SourcesItem other = (SourcesItem) obj;
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
    if (this.roots == null) {
      if (other.roots != null)
        return false;
    } else if (!this.roots.equals(other.roots))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    result = prime * result + ((this.sources== null) ? 0 : this.sources.hashCode());
    return prime * result + ((this.roots== null) ? 0 : this.roots.hashCode());
  }
}
