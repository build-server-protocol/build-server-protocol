package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustWorkspaceResult {
  @NonNull
  private List<RustPackage> packages;

  @NonNull
  private List<RustRawDependency> rawDependencies;

  @NonNull
  private List<RustDependency> dependencies;

  @NonNull
  private List<RustStdLib> stdLibs;

  public RustWorkspaceResult(@NonNull final List<RustPackage> packages, @NonNull final List<RustRawDependency> rawDependencies, @NonNull final List<RustDependency> dependencies, @NonNull final List<RustStdLib> stdLibs) {
    this.packages = packages;
    this.rawDependencies = rawDependencies;
    this.dependencies = dependencies;
    this.stdLibs = stdLibs;
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
  public List<RustRawDependency> getRawDependencies() {
    return this.rawDependencies;
  }

  public void setRawDependencies(@NonNull final List<RustRawDependency> rawDependencies) {
    this.rawDependencies = Preconditions.checkNotNull(rawDependencies, "rawDependencies");
  }

  @Pure
  @NonNull
  public List<RustDependency> getDependencies() {
    return this.dependencies;
  }

  public void setDependencies(@NonNull final List<RustDependency> dependencies) {
    this.dependencies = Preconditions.checkNotNull(dependencies, "dependencies");
  }

  @Pure
  @NonNull
  public List<RustStdLib> getStdLibs() {
    return this.stdLibs;
  }

  public void setStdLibs(@NonNull final List<RustStdLib> stdLibs) {
    this.stdLibs = Preconditions.checkNotNull(stdLibs, "stdLibs");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("packages", this.packages);
    b.add("rawDependencies", this.rawDependencies);
    b.add("dependencies", this.dependencies);
    b.add("stdLibs", this.stdLibs);
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
    RustWorkspaceResult other = (RustWorkspaceResult) obj;
    if (this.packages == null) {
      if (other.packages != null)
        return false;
    } else if (!this.packages.equals(other.packages))
      return false;
    if (this.rawDependencies == null) {
      if (other.rawDependencies != null)
        return false;
    } else if (!this.rawDependencies.equals(other.rawDependencies))
      return false;
    if (this.dependencies == null) {
      if (other.dependencies != null)
        return false;
    } else if (!this.dependencies.equals(other.dependencies))
      return false;
    if (this.stdLibs == null) {
      if (other.stdLibs != null)
        return false;
    } else if (!this.stdLibs.equals(other.stdLibs))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.packages== null) ? 0 : this.packages.hashCode());
    result = prime * result + ((this.rawDependencies== null) ? 0 : this.rawDependencies.hashCode());
    result = prime * result + ((this.dependencies== null) ? 0 : this.dependencies.hashCode());
    return prime * result + ((this.stdLibs== null) ? 0 : this.stdLibs.hashCode());
  }
}
