package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class ResourcesItem {
  @NonNull
  private BuildTargetIdentifier target;

  @NonNull
  private List<String> resources;

  public ResourcesItem(@NonNull final BuildTargetIdentifier target, @NonNull final List<String> resources) {
    this.target = target;
    this.resources = resources;
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
  public List<String> getResources() {
    return this.resources;
  }

  public void setResources(@NonNull final List<String> resources) {
    this.resources = Preconditions.checkNotNull(resources, "resources");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
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
    if (this.target == null) {
      if (other.target != null)
        return false;
    } else if (!this.target.equals(other.target))
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
    result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    return prime * result + ((this.resources== null) ? 0 : this.resources.hashCode());
  }
}
