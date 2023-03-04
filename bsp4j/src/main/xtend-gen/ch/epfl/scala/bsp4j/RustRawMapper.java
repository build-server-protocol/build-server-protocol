package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustRawMapper {
  @NonNull
  private String packageId;

  @NonNull
  private String rawId;

  public RustRawMapper(@NonNull final String packageId, @NonNull final String rawId) {
    this.packageId = packageId;
    this.rawId = rawId;
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
  public String getRawId() {
    return this.rawId;
  }

  public void setRawId(@NonNull final String rawId) {
    this.rawId = Preconditions.checkNotNull(rawId, "rawId");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("packageId", this.packageId);
    b.add("rawId", this.rawId);
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
    RustRawMapper other = (RustRawMapper) obj;
    if (this.packageId == null) {
      if (other.packageId != null)
        return false;
    } else if (!this.packageId.equals(other.packageId))
      return false;
    if (this.rawId == null) {
      if (other.rawId != null)
        return false;
    } else if (!this.rawId.equals(other.rawId))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.packageId== null) ? 0 : this.packageId.hashCode());
    return prime * result + ((this.rawId== null) ? 0 : this.rawId.hashCode());
  }
}
