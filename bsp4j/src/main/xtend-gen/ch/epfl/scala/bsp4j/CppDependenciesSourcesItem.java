package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.BuildTargetIdentifier;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class CppDependenciesSourcesItem {
  @NonNull
  private BuildTargetIdentifier target;
  
  private String packageNamePrefix;
  
  public CppDependenciesSourcesItem(@NonNull final BuildTargetIdentifier target) {
    this.target = target;
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
  public String getPackageNamePrefix() {
    return this.packageNamePrefix;
  }
  
  public void setPackageNamePrefix(final String packageNamePrefix) {
    this.packageNamePrefix = packageNamePrefix;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("packageNamePrefix", this.packageNamePrefix);
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
    CppDependenciesSourcesItem other = (CppDependenciesSourcesItem) obj;
    if (this.target == null) {
      if (other.target != null)
        return false;
    } else if (!this.target.equals(other.target))
      return false;
    if (this.packageNamePrefix == null) {
      if (other.packageNamePrefix != null)
        return false;
    } else if (!this.packageNamePrefix.equals(other.packageNamePrefix))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    return prime * result + ((this.packageNamePrefix== null) ? 0 : this.packageNamePrefix.hashCode());
  }
}
