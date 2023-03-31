package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustDependency {
  @NonNull
  private String id;

  private String name;

  private List<RustDepKindInfo> depKinds;

  public RustDependency(@NonNull final String id, final String name, final List<RustDepKindInfo> depKinds) {
    this.id = id;
    this.name = name;
    this.depKinds = depKinds;
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
  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Pure
  public List<RustDepKindInfo> getDepKinds() {
    return this.depKinds;
  }

  public void setDepKinds(final List<RustDepKindInfo> depKinds) {
    this.depKinds = depKinds;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("id", this.id);
    b.add("name", this.name);
    b.add("depKinds", this.depKinds);
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
    RustDependency other = (RustDependency) obj;
    if (this.id == null) {
      if (other.id != null)
        return false;
    } else if (!this.id.equals(other.id))
      return false;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (this.depKinds == null) {
      if (other.depKinds != null)
        return false;
    } else if (!this.depKinds.equals(other.depKinds))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.id== null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    return prime * result + ((this.depKinds== null) ? 0 : this.depKinds.hashCode());
  }
}
