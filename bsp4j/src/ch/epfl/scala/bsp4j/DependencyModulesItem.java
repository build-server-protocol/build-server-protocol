package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class DependencyModulesItem {
  @NonNull private BuildTargetIdentifier target;

  @NonNull private List<DependencyModule> modules;

  public DependencyModulesItem(
      @NonNull final BuildTargetIdentifier target, @NonNull final List<DependencyModule> modules) {
    this.target = target;
    this.modules = modules;
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
  public List<DependencyModule> getModules() {
    return this.modules;
  }

  public void setModules(@NonNull final List<DependencyModule> modules) {
    this.modules = Preconditions.checkNotNull(modules, "modules");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("modules", this.modules);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    DependencyModulesItem other = (DependencyModulesItem) obj;
    if (this.target == null) {
      if (other.target != null) return false;
    } else if (!this.target.equals(other.target)) return false;
    if (this.modules == null) {
      if (other.modules != null) return false;
    } else if (!this.modules.equals(other.modules)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target == null) ? 0 : this.target.hashCode());
    return prime * result + ((this.modules == null) ? 0 : this.modules.hashCode());
  }
}
