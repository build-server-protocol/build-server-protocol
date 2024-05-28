package ch.epfl.scala.bsp4j;

import java.util.Set;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class SetCargoFeaturesParams {
  @NonNull private String packageId;

  @NonNull private Set<String> features;

  public SetCargoFeaturesParams(
      @NonNull final String packageId, @NonNull final Set<String> features) {
    this.packageId = packageId;
    this.features = features;
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
  public Set<String> getFeatures() {
    return this.features;
  }

  public void setFeatures(@NonNull final Set<String> features) {
    this.features = Preconditions.checkNotNull(features, "features");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("packageId", this.packageId);
    b.add("features", this.features);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SetCargoFeaturesParams other = (SetCargoFeaturesParams) obj;
    if (this.packageId == null) {
      if (other.packageId != null) return false;
    } else if (!this.packageId.equals(other.packageId)) return false;
    if (this.features == null) {
      if (other.features != null) return false;
    } else if (!this.features.equals(other.features)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.packageId == null) ? 0 : this.packageId.hashCode());
    return prime * result + ((this.features == null) ? 0 : this.features.hashCode());
  }
}
