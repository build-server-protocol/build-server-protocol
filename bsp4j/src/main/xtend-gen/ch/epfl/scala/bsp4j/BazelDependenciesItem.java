package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.BuildTargetIdentifier;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class BazelDependenciesItem {
  @NonNull
  private BuildTargetIdentifier target;
  
  private String dependencyPathPrefix;
  
  public BazelDependenciesItem(@NonNull final BuildTargetIdentifier target, final String dependencyPathPrefix) {
    this.target = target;
    this.dependencyPathPrefix = dependencyPathPrefix;
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
  public String getDependencyPathPrefix() {
    return this.dependencyPathPrefix;
  }
  
  public void setDependencyPathPrefix(final String dependencyPathPrefix) {
    this.dependencyPathPrefix = dependencyPathPrefix;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("dependencyPathPrefix", this.dependencyPathPrefix);
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
    BazelDependenciesItem other = (BazelDependenciesItem) obj;
    if (this.target == null) {
      if (other.target != null)
        return false;
    } else if (!this.target.equals(other.target))
      return false;
    if (this.dependencyPathPrefix == null) {
      if (other.dependencyPathPrefix != null)
        return false;
    } else if (!this.dependencyPathPrefix.equals(other.dependencyPathPrefix))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    return prime * result + ((this.dependencyPathPrefix== null) ? 0 : this.dependencyPathPrefix.hashCode());
  }
}
