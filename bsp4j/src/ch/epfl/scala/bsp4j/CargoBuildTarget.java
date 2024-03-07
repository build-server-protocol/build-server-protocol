package ch.epfl.scala.bsp4j;

import java.util.Set;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

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
  public Set<String> getRequiredFeatures() {
    return this.requiredFeatures;
  }

  public void setRequiredFeatures(@NonNull final Set<String> requiredFeatures) {
    this.requiredFeatures = Preconditions.checkNotNull(requiredFeatures, "requiredFeatures");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("edition", this.edition);
    b.add("requiredFeatures", this.requiredFeatures);
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
    if (this.requiredFeatures == null) {
      if (other.requiredFeatures != null)
        return false;
    } else if (!this.requiredFeatures.equals(other.requiredFeatures))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.edition== null) ? 0 : this.edition.hashCode());
    return prime * result + ((this.requiredFeatures== null) ? 0 : this.requiredFeatures.hashCode());
  }
}
