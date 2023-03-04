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
  private List<RustRawMapper> packageToRawMapper;

  @NonNull
  private List<RustDepMapper> packageToDepMapper;

  public RustWorkspaceResult(@NonNull final List<RustPackage> packages, @NonNull final List<RustRawDependency> rawDependencies, @NonNull final List<RustRawMapper> packageToRawMapper, @NonNull final List<RustDepMapper> packageToDepMapper) {
    this.packages = packages;
    this.rawDependencies = rawDependencies;
    this.packageToRawMapper = packageToRawMapper;
    this.packageToDepMapper = packageToDepMapper;
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
  public List<RustRawMapper> getPackageToRawMapper() {
    return this.packageToRawMapper;
  }

  public void setPackageToRawMapper(@NonNull final List<RustRawMapper> packageToRawMapper) {
    this.packageToRawMapper = Preconditions.checkNotNull(packageToRawMapper, "packageToRawMapper");
  }

  @Pure
  @NonNull
  public List<RustDepMapper> getPackageToDepMapper() {
    return this.packageToDepMapper;
  }

  public void setPackageToDepMapper(@NonNull final List<RustDepMapper> packageToDepMapper) {
    this.packageToDepMapper = Preconditions.checkNotNull(packageToDepMapper, "packageToDepMapper");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("packages", this.packages);
    b.add("rawDependencies", this.rawDependencies);
    b.add("packageToRawMapper", this.packageToRawMapper);
    b.add("packageToDepMapper", this.packageToDepMapper);
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
    if (this.packageToRawMapper == null) {
      if (other.packageToRawMapper != null)
        return false;
    } else if (!this.packageToRawMapper.equals(other.packageToRawMapper))
      return false;
    if (this.packageToDepMapper == null) {
      if (other.packageToDepMapper != null)
        return false;
    } else if (!this.packageToDepMapper.equals(other.packageToDepMapper))
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
    result = prime * result + ((this.packageToRawMapper== null) ? 0 : this.packageToRawMapper.hashCode());
    return prime * result + ((this.packageToDepMapper== null) ? 0 : this.packageToDepMapper.hashCode());
  }
}
