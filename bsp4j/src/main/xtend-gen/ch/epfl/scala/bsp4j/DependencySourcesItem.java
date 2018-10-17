package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.BuildTargetIdentifier;
import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class DependencySourcesItem {
  @NonNull
  private List<BuildTargetIdentifier> targets;
  
  @NonNull
  private List<String> sources;
  
  public DependencySourcesItem(@NonNull final List<BuildTargetIdentifier> targets, @NonNull final List<String> sources) {
    this.targets = targets;
    this.sources = sources;
  }
  
  @Pure
  @NonNull
  public List<BuildTargetIdentifier> getTargets() {
    return this.targets;
  }
  
  public void setTargets(@NonNull final List<BuildTargetIdentifier> targets) {
    this.targets = targets;
  }
  
  @Pure
  @NonNull
  public List<String> getSources() {
    return this.sources;
  }
  
  public void setSources(@NonNull final List<String> sources) {
    this.sources = sources;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("targets", this.targets);
    b.add("sources", this.sources);
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
    DependencySourcesItem other = (DependencySourcesItem) obj;
    if (this.targets == null) {
      if (other.targets != null)
        return false;
    } else if (!this.targets.equals(other.targets))
      return false;
    if (this.sources == null) {
      if (other.sources != null)
        return false;
    } else if (!this.sources.equals(other.sources))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.targets== null) ? 0 : this.targets.hashCode());
    return prime * result + ((this.sources== null) ? 0 : this.sources.hashCode());
  }
}
