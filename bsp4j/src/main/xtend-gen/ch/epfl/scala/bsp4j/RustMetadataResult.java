package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustMetadataResult {
  @NonNull
  private List<RustPackage> packages;

  @NonNull
  private List<RustResolveNode> dependencies;

  @NonNull
  private Integer version;

  @NonNull
  private List<String> workspaceMembers;

  @NonNull
  private String workspaceRoot;

  public RustMetadataResult(@NonNull final List<RustPackage> packages, @NonNull final List<RustResolveNode> dependencies, @NonNull final Integer version, @NonNull final List<String> workspaceMembers, @NonNull final String workspaceRoot) {
    this.packages = packages;
    this.dependencies = dependencies;
    this.version = version;
    this.workspaceMembers = workspaceMembers;
    this.workspaceRoot = workspaceRoot;
  }

  @Pure
  @NonNull
  public List<RustPackage> getPackages() {
    return this.packages;
  }

  public void setPackages(@NonNull final List<RustPackage> packages) {
    this.packages = Preconditions.checkNotNull(packages, "packages");
  }

  @Pure
  @NonNull
  public List<RustResolveNode> getDependencies() {
    return this.dependencies;
  }

  public void setDependencies(@NonNull final List<RustResolveNode> dependencies) {
    this.dependencies = Preconditions.checkNotNull(dependencies, "dependencies");
  }

  @Pure
  @NonNull
  public Integer getVersion() {
    return this.version;
  }

  public void setVersion(@NonNull final Integer version) {
    this.version = Preconditions.checkNotNull(version, "version");
  }

  @Pure
  @NonNull
  public List<String> getWorkspaceMembers() {
    return this.workspaceMembers;
  }

  public void setWorkspaceMembers(@NonNull final List<String> workspaceMembers) {
    this.workspaceMembers = Preconditions.checkNotNull(workspaceMembers, "workspaceMembers");
  }

  @Pure
  @NonNull
  public String getWorkspaceRoot() {
    return this.workspaceRoot;
  }

  public void setWorkspaceRoot(@NonNull final String workspaceRoot) {
    this.workspaceRoot = Preconditions.checkNotNull(workspaceRoot, "workspaceRoot");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("packages", this.packages);
    b.add("dependencies", this.dependencies);
    b.add("version", this.version);
    b.add("workspaceMembers", this.workspaceMembers);
    b.add("workspaceRoot", this.workspaceRoot);
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
    RustMetadataResult other = (RustMetadataResult) obj;
    if (this.packages == null) {
      if (other.packages != null)
        return false;
    } else if (!this.packages.equals(other.packages))
      return false;
    if (this.dependencies == null) {
      if (other.dependencies != null)
        return false;
    } else if (!this.dependencies.equals(other.dependencies))
      return false;
    if (this.version == null) {
      if (other.version != null)
        return false;
    } else if (!this.version.equals(other.version))
      return false;
    if (this.workspaceMembers == null) {
      if (other.workspaceMembers != null)
        return false;
    } else if (!this.workspaceMembers.equals(other.workspaceMembers))
      return false;
    if (this.workspaceRoot == null) {
      if (other.workspaceRoot != null)
        return false;
    } else if (!this.workspaceRoot.equals(other.workspaceRoot))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.packages== null) ? 0 : this.packages.hashCode());
    result = prime * result + ((this.dependencies== null) ? 0 : this.dependencies.hashCode());
    result = prime * result + ((this.version== null) ? 0 : this.version.hashCode());
    result = prime * result + ((this.workspaceMembers== null) ? 0 : this.workspaceMembers.hashCode());
    return prime * result + ((this.workspaceRoot== null) ? 0 : this.workspaceRoot.hashCode());
  }
}
