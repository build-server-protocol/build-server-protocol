package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustDependency {
  @NonNull private String pkg;

  private String name;

  private List<RustDepKindInfo> depKinds;

  public RustDependency(@NonNull final String pkg) {
    this.pkg = pkg;
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
    b.add("pkg", this.pkg);
    b.add("name", this.name);
    b.add("depKinds", this.depKinds);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    RustDependency other = (RustDependency) obj;
    if (this.pkg == null) {
      if (other.pkg != null) return false;
    } else if (!this.pkg.equals(other.pkg)) return false;
    if (this.name == null) {
      if (other.name != null) return false;
    } else if (!this.name.equals(other.name)) return false;
    if (this.depKinds == null) {
      if (other.depKinds != null) return false;
    } else if (!this.depKinds.equals(other.depKinds)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.pkg == null) ? 0 : this.pkg.hashCode());
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
    return prime * result + ((this.depKinds == null) ? 0 : this.depKinds.hashCode());
  }
}
