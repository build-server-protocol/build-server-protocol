package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class CargoBuildTarget {
  @NonNull
  private String edition;

  @NonNull
  private List<String> required_features;

  public CargoBuildTarget(@NonNull final String edition, @NonNull final List<String> required_features) {
    this.edition = edition;
    this.required_features = required_features;
  }

  @Pure
  @NonNull
  public String getEdition() {
    return this.edition;
  }

  public void setEdition(@NonNull final String edition) {
    this.edition = Preconditions.checkNotNull(edition, "edition");
  }

  @Pure
  @NonNull
  public List<String> getRequired_features() {
    return this.required_features;
  }

  public void setRequired_features(@NonNull final List<String> required_features) {
    this.required_features = Preconditions.checkNotNull(required_features, "required_features");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("edition", this.edition);
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
    CargoBuildTarget other = (CargoBuildTarget) obj;
    if (this.edition == null) {
      if (other.edition != null)
        return false;
    } else if (!this.edition.equals(other.edition))
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
    result = prime * result + ((this.edition== null) ? 0 : this.edition.hashCode());
    return prime * result + ((this.required_features== null) ? 0 : this.required_features.hashCode());
  }
}
