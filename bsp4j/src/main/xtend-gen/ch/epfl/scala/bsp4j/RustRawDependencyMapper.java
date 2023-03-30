package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustRawDependencyMapper {
  @NonNull
  private String packageId;

  @NonNull
  private List<RustRawDependency> dependencies;

  public RustRawDependencyMapper(@NonNull final String packageId, @NonNull final List<RustRawDependency> dependencies) {
    this.packageId = packageId;
    this.dependencies = dependencies;
  }

  @Pure
  @NonNull
  public String getPackageId() {
    return this.packageId;
  }

  public void setPackageId(@NonNull final String packageId) {
    this.packageId = Preconditions.checkNotNull(packageId, "packageId");
  }

  @Pure
  @NonNull
  public List<RustRawDependency> getDependencies() {
    return this.dependencies;
  }

  public void setDependencies(@NonNull final List<RustRawDependency> dependencies) {
    this.dependencies = Preconditions.checkNotNull(dependencies, "dependencies");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("packageId", this.packageId);
    b.add("dependencies", this.dependencies);
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
    RustRawDependencyMapper other = (RustRawDependencyMapper) obj;
    if (this.packageId == null) {
      if (other.packageId != null)
        return false;
    } else if (!this.packageId.equals(other.packageId))
      return false;
    if (this.dependencies == null) {
      if (other.dependencies != null)
        return false;
    } else if (!this.dependencies.equals(other.dependencies))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.packageId== null) ? 0 : this.packageId.hashCode());
    return prime * result + ((this.dependencies== null) ? 0 : this.dependencies.hashCode());
  }
}
