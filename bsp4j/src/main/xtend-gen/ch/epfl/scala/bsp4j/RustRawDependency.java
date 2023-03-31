package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustRawDependency {
  @NonNull
  private String name;

  private String rename;

  private String kind;

  private String target;

  private boolean optional;

  private boolean uses_default_features;

  @NonNull
  private List<String> features;

  public RustRawDependency(@NonNull final String name, final String rename, final String kind, final String target, final boolean optional, final boolean uses_default_features, @NonNull final List<String> features) {
    this.name = name;
    this.rename = rename;
    this.kind = kind;
    this.target = target;
    this.optional = optional;
    this.uses_default_features = uses_default_features;
    this.features = features;
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
  public String getRename() {
    return this.rename;
  }

  public void setRename(final String rename) {
    this.rename = rename;
  }

  @Pure
  public String getKind() {
    return this.kind;
  }

  public void setKind(final String kind) {
    this.kind = kind;
  }

  @Pure
  public String getTarget() {
    return this.target;
  }

  public void setTarget(final String target) {
    this.target = target;
  }

  @Pure
  public boolean isOptional() {
    return this.optional;
  }

  public void setOptional(final boolean optional) {
    this.optional = optional;
  }

  @Pure
  public boolean isUses_default_features() {
    return this.uses_default_features;
  }

  public void setUses_default_features(final boolean uses_default_features) {
    this.uses_default_features = uses_default_features;
  }

  @Pure
  @NonNull
  public List<String> getFeatures() {
    return this.features;
  }

  public void setFeatures(@NonNull final List<String> features) {
    this.features = Preconditions.checkNotNull(features, "features");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("name", this.name);
    b.add("rename", this.rename);
    b.add("kind", this.kind);
    b.add("target", this.target);
    b.add("optional", this.optional);
    b.add("uses_default_features", this.uses_default_features);
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
    RustRawDependency other = (RustRawDependency) obj;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (this.rename == null) {
      if (other.rename != null)
        return false;
    } else if (!this.rename.equals(other.rename))
      return false;
    if (this.kind == null) {
      if (other.kind != null)
        return false;
    } else if (!this.kind.equals(other.kind))
      return false;
    if (this.target == null) {
      if (other.target != null)
        return false;
    } else if (!this.target.equals(other.target))
      return false;
    if (other.optional != this.optional)
      return false;
    if (other.uses_default_features != this.uses_default_features)
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
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.rename== null) ? 0 : this.rename.hashCode());
    result = prime * result + ((this.kind== null) ? 0 : this.kind.hashCode());
    result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    result = prime * result + (this.optional ? 1231 : 1237);
    result = prime * result + (this.uses_default_features ? 1231 : 1237);
    return prime * result + ((this.features== null) ? 0 : this.features.hashCode());
  }
}
