package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.BuildTargetIdentifier;
import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class ResourcesItem {
  @NonNull
  private List<BuildTargetIdentifier> targets;
  
  @NonNull
  private List<String> resources;
  
  public ResourcesItem(@NonNull final List<BuildTargetIdentifier> targets, @NonNull final List<String> resources) {
    this.targets = targets;
    this.resources = resources;
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
  public List<String> getResources() {
    return this.resources;
  }
  
  public void setResources(@NonNull final List<String> resources) {
    this.resources = resources;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("targets", this.targets);
    b.add("resources", this.resources);
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
    ResourcesItem other = (ResourcesItem) obj;
    if (this.targets == null) {
      if (other.targets != null)
        return false;
    } else if (!this.targets.equals(other.targets))
      return false;
    if (this.resources == null) {
      if (other.resources != null)
        return false;
    } else if (!this.resources.equals(other.resources))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.targets== null) ? 0 : this.targets.hashCode());
    return prime * result + ((this.resources== null) ? 0 : this.resources.hashCode());
  }
}
