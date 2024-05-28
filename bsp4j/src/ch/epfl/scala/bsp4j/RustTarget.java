package ch.epfl.scala.bsp4j;

import java.util.List;
import java.util.Set;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * `RustTarget` contains data of the target as defined in Cargo metadata.
 */
@SuppressWarnings("all")
public class RustTarget {
  @NonNull
  private String name;

  @NonNull
  private String crateRootUrl;

  @NonNull
  private RustTargetKind kind;

  private List<RustCrateType> crateTypes;

  @NonNull
  private String edition;

  @NonNull
  private Boolean doctest;

  private Set<String> requiredFeatures;

  public RustTarget(@NonNull final String name, @NonNull final String crateRootUrl, @NonNull final RustTargetKind kind, @NonNull final String edition, @NonNull final Boolean doctest) {
    this.name = name;
    this.crateRootUrl = crateRootUrl;
    this.kind = kind;
    this.edition = edition;
    this.doctest = doctest;
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
  public String getCrateRootUrl() {
    return this.crateRootUrl;
  }

  public void setCrateRootUrl(@NonNull final String crateRootUrl) {
    this.crateRootUrl = Preconditions.checkNotNull(crateRootUrl, "crateRootUrl");
  }

  @Pure
  @NonNull
  public RustTargetKind getKind() {
    return this.kind;
  }

  public void setKind(@NonNull final RustTargetKind kind) {
    this.kind = Preconditions.checkNotNull(kind, "kind");
  }

  @Pure
  public List<RustCrateType> getCrateTypes() {
    return this.crateTypes;
  }

  public void setCrateTypes(final List<RustCrateType> crateTypes) {
    this.crateTypes = crateTypes;
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
  public Boolean getDoctest() {
    return this.doctest;
  }

  public void setDoctest(@NonNull final Boolean doctest) {
    this.doctest = Preconditions.checkNotNull(doctest, "doctest");
  }

  @Pure
  public Set<String> getRequiredFeatures() {
    return this.requiredFeatures;
  }

  public void setRequiredFeatures(final Set<String> requiredFeatures) {
    this.requiredFeatures = requiredFeatures;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("name", this.name);
    b.add("crateRootUrl", this.crateRootUrl);
    b.add("kind", this.kind);
    b.add("crateTypes", this.crateTypes);
    b.add("edition", this.edition);
    b.add("doctest", this.doctest);
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
    RustTarget other = (RustTarget) obj;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (this.crateRootUrl == null) {
      if (other.crateRootUrl != null)
        return false;
    } else if (!this.crateRootUrl.equals(other.crateRootUrl))
      return false;
    if (this.kind == null) {
      if (other.kind != null)
        return false;
    } else if (!this.kind.equals(other.kind))
      return false;
    if (this.crateTypes == null) {
      if (other.crateTypes != null)
        return false;
    } else if (!this.crateTypes.equals(other.crateTypes))
      return false;
    if (this.edition == null) {
      if (other.edition != null)
        return false;
    } else if (!this.edition.equals(other.edition))
      return false;
    if (this.doctest == null) {
      if (other.doctest != null)
        return false;
    } else if (!this.doctest.equals(other.doctest))
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
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.crateRootUrl== null) ? 0 : this.crateRootUrl.hashCode());
    result = prime * result + ((this.kind== null) ? 0 : this.kind.hashCode());
    result = prime * result + ((this.crateTypes== null) ? 0 : this.crateTypes.hashCode());
    result = prime * result + ((this.edition== null) ? 0 : this.edition.hashCode());
    result = prime * result + ((this.doctest== null) ? 0 : this.doctest.hashCode());
    return prime * result + ((this.requiredFeatures== null) ? 0 : this.requiredFeatures.hashCode());
  }
}
