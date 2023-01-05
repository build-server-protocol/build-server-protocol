package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustDep {
  @NonNull
  private String pkg;

  private String name;

  private List<RustDepKindInfo> dep_kinds;

  public RustDep(@NonNull final String pkg, final String name, final List<RustDepKindInfo> dep_kinds) {
    this.pkg = pkg;
    this.name = name;
    this.dep_kinds = dep_kinds;
  }

  @Pure
  @NonNull
  public String getPkg() {
    return this.pkg;
  }

  public void setPkg(@NonNull final String pkg) {
    this.pkg = Preconditions.checkNotNull(pkg, "pkg");
  }

  @Pure
  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Pure
  public List<RustDepKindInfo> getDep_kinds() {
    return this.dep_kinds;
  }

  public void setDep_kinds(final List<RustDepKindInfo> dep_kinds) {
    this.dep_kinds = dep_kinds;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("pkg", this.pkg);
    b.add("name", this.name);
    b.add("dep_kinds", this.dep_kinds);
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
    RustDep other = (RustDep) obj;
    if (this.pkg == null) {
      if (other.pkg != null)
        return false;
    } else if (!this.pkg.equals(other.pkg))
      return false;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (this.dep_kinds == null) {
      if (other.dep_kinds != null)
        return false;
    } else if (!this.dep_kinds.equals(other.dep_kinds))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.pkg== null) ? 0 : this.pkg.hashCode());
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    return prime * result + ((this.dep_kinds== null) ? 0 : this.dep_kinds.hashCode());
  }
}
