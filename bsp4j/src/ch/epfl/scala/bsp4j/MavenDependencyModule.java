package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * `MavenDependencyModule` is a basic data structure that contains maven-like
 * metadata. This metadata is embedded in the `data: Option[Json]` field of the `DependencyModule` definition, when the `dataKind` field contains "maven".
 */
@SuppressWarnings("all")
public class MavenDependencyModule {
  @NonNull
  private String organization;

  @NonNull
  private String name;

  @NonNull
  private String version;

  @NonNull
  private List<MavenDependencyModuleArtifact> artifacts;

  private String scope;

  public MavenDependencyModule(@NonNull final String organization, @NonNull final String name, @NonNull final String version, @NonNull final List<MavenDependencyModuleArtifact> artifacts) {
    this.organization = organization;
    this.name = name;
    this.version = version;
    this.artifacts = artifacts;
  }

  @Pure
  @NonNull
  public String getOrganization() {
    return this.organization;
  }

  public void setOrganization(@NonNull final String organization) {
    this.organization = Preconditions.checkNotNull(organization, "organization");
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
  public String getVersion() {
    return this.version;
  }

  public void setVersion(@NonNull final String version) {
    this.version = Preconditions.checkNotNull(version, "version");
  }

  @Pure
  @NonNull
  public List<MavenDependencyModuleArtifact> getArtifacts() {
    return this.artifacts;
  }

  public void setArtifacts(@NonNull final List<MavenDependencyModuleArtifact> artifacts) {
    this.artifacts = Preconditions.checkNotNull(artifacts, "artifacts");
  }

  @Pure
  public String getScope() {
    return this.scope;
  }

  public void setScope(final String scope) {
    this.scope = scope;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("organization", this.organization);
    b.add("name", this.name);
    b.add("version", this.version);
    b.add("artifacts", this.artifacts);
    b.add("scope", this.scope);
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
    MavenDependencyModule other = (MavenDependencyModule) obj;
    if (this.organization == null) {
      if (other.organization != null)
        return false;
    } else if (!this.organization.equals(other.organization))
      return false;
    if (this.name == null) {
      if (other.name != null)
        return false;
    } else if (!this.name.equals(other.name))
      return false;
    if (this.version == null) {
      if (other.version != null)
        return false;
    } else if (!this.version.equals(other.version))
      return false;
    if (this.artifacts == null) {
      if (other.artifacts != null)
        return false;
    } else if (!this.artifacts.equals(other.artifacts))
      return false;
    if (this.scope == null) {
      if (other.scope != null)
        return false;
    } else if (!this.scope.equals(other.scope))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.organization== null) ? 0 : this.organization.hashCode());
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.version== null) ? 0 : this.version.hashCode());
    result = prime * result + ((this.artifacts== null) ? 0 : this.artifacts.hashCode());
    return prime * result + ((this.scope== null) ? 0 : this.scope.hashCode());
  }
}
