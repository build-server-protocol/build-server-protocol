package ch.epfl.scala.bsp4j;

import java.util.Set;
import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/**
 * `CargoBuildTarget` is a basic data structure that contains
 * cargo-specific metadata.
 */
@SuppressWarnings("all")
public class CargoBuildTarget {
  @NonNull
  private String edition;

  @NonNull
  private Set<String> requiredFeatures;

  public CargoBuildTarget(@NonNull final String edition, @NonNull final Set<String> requiredFeatures) {
    this.edition = edition;
    this.requiredFeatures = requiredFeatures;
  }

  @NonNull
  public String getEdition() {
    return this.edition;
  }

  public void setEdition(@NonNull final String edition) {
    this.edition = Preconditions.checkNotNull(edition, "edition");
  }

  @NonNull
  public Set<String> getRequiredFeatures() {
    return this.requiredFeatures;
  }

  public void setRequiredFeatures(@NonNull final Set<String> requiredFeatures) {
    this.requiredFeatures = Preconditions.checkNotNull(requiredFeatures, "requiredFeatures");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("edition", this.edition);
    b.add("requiredFeatures", this.requiredFeatures);
    return b.toString();
  }

  @Override
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
    if (this.requiredFeatures == null) {
      if (other.requiredFeatures != null)
        return false;
    } else if (!this.requiredFeatures.equals(other.requiredFeatures))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.edition== null) ? 0 : this.edition.hashCode());
    return prime * result + ((this.requiredFeatures== null) ? 0 : this.requiredFeatures.hashCode());
  }
}
