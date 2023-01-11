package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustFeature {
  @NonNull
  private String name;

  @NonNull
  private List<String> deps;

  public RustFeature(@NonNull final String name, @NonNull final List<String> deps) {
    this.name = name;
    this.deps = deps;
  }

  @Pure
  @NonNull
  public String getName() {
    return this.name;
  }

  public void setName(@NonNull final String name) {
    this.name = Preconditions.checkNotNull(name, "name");
  }

  @Pure
  @NonNull
  public List<String> getDeps() {
    return this.deps;
  }

  public void setDeps(@NonNull final List<String> deps) {
    this.deps = Preconditions.checkNotNull(deps, "deps");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("name", this.name);
    b.add("deps", this.deps);
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
    RustFeature other = (RustFeature) obj;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (this.deps == null) {
      if (other.deps != null)
        return false;
    } else if (!this.deps.equals(other.deps))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    return prime * result + ((this.deps== null) ? 0 : this.deps.hashCode());
  }
}
