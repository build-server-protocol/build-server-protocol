package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustResolveNode {
  @NonNull
  private String id;

  @NonNull
  private List<String> dependencies;

  private List<RustDep> deps;

  private List<String> features;

  public RustResolveNode(@NonNull final String id, @NonNull final List<String> dependencies, final List<RustDep> deps, final List<String> features) {
    this.id = id;
    this.dependencies = dependencies;
    this.deps = deps;
    this.features = features;
  }

  @Pure
  @NonNull
  public String getId() {
    return this.id;
  }

  public void setId(@NonNull final String id) {
    this.id = Preconditions.checkNotNull(id, "id");
  }

  @Pure
  @NonNull
  public List<String> getDependencies() {
    return this.dependencies;
  }

  public void setDependencies(@NonNull final List<String> dependencies) {
    this.dependencies = Preconditions.checkNotNull(dependencies, "dependencies");
  }

  @Pure
  public List<RustDep> getDeps() {
    return this.deps;
  }

  public void setDeps(final List<RustDep> deps) {
    this.deps = deps;
  }

  @Pure
  public List<String> getFeatures() {
    return this.features;
  }

  public void setFeatures(final List<String> features) {
    this.features = features;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("id", this.id);
    b.add("dependencies", this.dependencies);
    b.add("deps", this.deps);
    b.add("features", this.features);
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
    RustResolveNode other = (RustResolveNode) obj;
    if (this.id == null) {
      if (other.id != null)
        return false;
    } else if (!this.id.equals(other.id))
      return false;
    if (this.dependencies == null) {
      if (other.dependencies != null)
        return false;
    } else if (!this.dependencies.equals(other.dependencies))
      return false;
    if (this.deps == null) {
      if (other.deps != null)
        return false;
    } else if (!this.deps.equals(other.deps))
      return false;
    if (this.features == null) {
      if (other.features != null)
        return false;
    } else if (!this.features.equals(other.features))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.id== null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.dependencies== null) ? 0 : this.dependencies.hashCode());
    result = prime * result + ((this.deps== null) ? 0 : this.deps.hashCode());
    return prime * result + ((this.features== null) ? 0 : this.features.hashCode());
  }
}
