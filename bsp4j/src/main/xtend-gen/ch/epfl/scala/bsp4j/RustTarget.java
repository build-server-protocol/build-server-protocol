package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustTarget {
  @NonNull
  private String name;

  @NonNull
  private String crateRootUrl;

  @NonNull
  private String packageRootUrl;

  @NonNull
  private String kind;

  private String edition;

  private boolean doctest;

  private List<String> requiredFeatures;

  public RustTarget(@NonNull final String name, @NonNull final String crateRootUrl, @NonNull final String packageRootUrl, @NonNull final String kind, final String edition, final boolean doctest, final List<String> requiredFeatures) {
    this.kind = kind;
    this.name = name;
    this.crateRootUrl = crateRootUrl;
    this.packageRootUrl = packageRootUrl;
    this.edition = edition;
    this.doctest = doctest;
    this.requiredFeatures = requiredFeatures;
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
  public String getPackageRootUrl() {
    return this.packageRootUrl;
  }

  public void setPackageRootUrl(@NonNull final String packageRootUrl) {
    this.packageRootUrl = Preconditions.checkNotNull(packageRootUrl, "packageRootUrl");
  }

  @Pure
  @NonNull
  public String getKind() {
    return this.kind;
  }

  public void setKind(@NonNull final String kind) {
    this.kind = Preconditions.checkNotNull(kind, "kind");
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
  public List<String> getRequiredFeatures() {
    return this.requiredFeatures;
  }

  public void setRequiredFeatures(final List<String> requiredFeatures) {
    this.requiredFeatures = requiredFeatures;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("name", this.name);
    b.add("crateRootUrl", this.crateRootUrl);
    b.add("packageRootUrl", this.packageRootUrl);
    b.add("kind", this.kind);
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
    if (this.packageRootUrl == null) {
      if (other.packageRootUrl != null)
        return false;
    } else if (!this.packageRootUrl.equals(other.packageRootUrl))
      return false;
    if (this.kind == null) {
      if (other.kind != null)
        return false;
    } else if (!this.kind.equals(other.kind))
      return false;
    if (this.edition == null) {
      if (other.edition != null)
        return false;
    } else if (!this.edition.equals(other.edition))
      return false;
    if (other.doctest != this.doctest)
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
    result = prime * result + ((this.packageRootUrl== null) ? 0 : this.packageRootUrl.hashCode());
    result = prime * result + ((this.kind== null) ? 0 : this.kind.hashCode());
    result = prime * result + ((this.edition== null) ? 0 : this.edition.hashCode());
    result = prime * result + (this.doctest ? 1231 : 1237);
    return prime * result + ((this.requiredFeatures== null) ? 0 : this.requiredFeatures.hashCode());
  }
}
