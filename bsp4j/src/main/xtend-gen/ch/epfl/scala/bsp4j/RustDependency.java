package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustDependency {
  @NonNull
  private String source;

  @NonNull
  private String target;

  private String name;

  private List<RustDepKindInfo> depKinds;

  public RustDependency(@NonNull final String source, @NonNull final String target, final String name, final List<RustDepKindInfo> depKinds) {
    this.source = source;
    this.target = target;
    this.name = name;
    this.depKinds = depKinds;
  }

  @Pure
  @NonNull
  public String getSource() {
    return this.source;
  }

  public void setSource(@NonNull final String source) {
    this.source = Preconditions.checkNotNull(source, "source");
  }

  @Pure
  @NonNull
  public String getTarget() {
    return this.target;
  }

  public void setTarget(@NonNull final String target) {
    this.target = Preconditions.checkNotNull(target, "target");
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
    b.add("source", this.source);
    b.add("target", this.target);
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
    if (this.source == null) {
      if (other.source != null)
        return false;
    } else if (!this.source.equals(other.source))
      return false;
    if (this.target == null) {
      if (other.target != null)
        return false;
    } else if (!this.target.equals(other.target))
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
    result = prime * result + ((this.source== null) ? 0 : this.source.hashCode());
    result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    return prime * result + ((this.depKinds== null) ? 0 : this.depKinds.hashCode());
  }
}
