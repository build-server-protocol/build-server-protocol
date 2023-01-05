package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustTarget {
  @NonNull
  private List<String> kind;

  @NonNull
  private String name;

  @NonNull
  private String src_path;

  @NonNull
  private List<String> crate_types;

  private String edition;

  private boolean doctest;

  private List<String> required_features;

  public RustTarget(@NonNull final List<String> kind, @NonNull final String name, @NonNull final String src_path, @NonNull final List<String> crate_types, final String edition, final boolean doctest, final List<String> required_features) {
    this.kind = kind;
    this.name = name;
    this.src_path = src_path;
    this.crate_types = crate_types;
    this.edition = edition;
    this.doctest = doctest;
    this.required_features = required_features;
  }

  @Pure
  @NonNull
  public List<String> getKind() {
    return this.kind;
  }

  public void setKind(@NonNull final List<String> kind) {
    this.kind = Preconditions.checkNotNull(kind, "kind");
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
  public String getSrc_path() {
    return this.src_path;
  }

  public void setSrc_path(@NonNull final String src_path) {
    this.src_path = Preconditions.checkNotNull(src_path, "src_path");
  }

  @Pure
  @NonNull
  public List<String> getCrate_types() {
    return this.crate_types;
  }

  public void setCrate_types(@NonNull final List<String> crate_types) {
    this.crate_types = Preconditions.checkNotNull(crate_types, "crate_types");
  }

  @Pure
  public String getEdition() {
    return this.edition;
  }

  public void setEdition(final String edition) {
    this.edition = edition;
  }

  @Pure
  public boolean isDoctest() {
    return this.doctest;
  }

  public void setDoctest(final boolean doctest) {
    this.doctest = doctest;
  }

  @Pure
  public List<String> getRequired_features() {
    return this.required_features;
  }

  public void setRequired_features(final List<String> required_features) {
    this.required_features = required_features;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("kind", this.kind);
    b.add("name", this.name);
    b.add("src_path", this.src_path);
    b.add("crate_types", this.crate_types);
    b.add("edition", this.edition);
    b.add("doctest", this.doctest);
    b.add("required_features", this.required_features);
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
    RustTarget other = (RustTarget) obj;
    if (this.kind == null) {
      if (other.kind != null)
        return false;
    } else if (!this.kind.equals(other.kind))
      return false;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (this.src_path == null) {
      if (other.src_path != null)
        return false;
    } else if (!this.src_path.equals(other.src_path))
      return false;
    if (this.crate_types == null) {
      if (other.crate_types != null)
        return false;
    } else if (!this.crate_types.equals(other.crate_types))
      return false;
    if (this.edition == null) {
      if (other.edition != null)
        return false;
    } else if (!this.edition.equals(other.edition))
      return false;
    if (other.doctest != this.doctest)
      return false;
    if (this.required_features == null) {
      if (other.required_features != null)
        return false;
    } else if (!this.required_features.equals(other.required_features))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.kind== null) ? 0 : this.kind.hashCode());
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.src_path== null) ? 0 : this.src_path.hashCode());
    result = prime * result + ((this.crate_types== null) ? 0 : this.crate_types.hashCode());
    result = prime * result + ((this.edition== null) ? 0 : this.edition.hashCode());
    result = prime * result + (this.doctest ? 1231 : 1237);
    return prime * result + ((this.required_features== null) ? 0 : this.required_features.hashCode());
  }
}
