package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/** **Unstable** (may change in future versions) */
@SuppressWarnings("all")
public class CargoFeaturesStateResult {
  @NonNull private List<PackageFeatures> packagesFeatures;

  public CargoFeaturesStateResult(@NonNull final List<PackageFeatures> packagesFeatures) {
    this.packagesFeatures = packagesFeatures;
  }

  @Pure
  @NonNull
  public List<PackageFeatures> getPackagesFeatures() {
    return this.packagesFeatures;
  }

  public void setPackagesFeatures(@NonNull final List<PackageFeatures> packagesFeatures) {
    this.packagesFeatures = Preconditions.checkNotNull(packagesFeatures, "packagesFeatures");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("packagesFeatures", this.packagesFeatures);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CargoFeaturesStateResult other = (CargoFeaturesStateResult) obj;
    if (this.packagesFeatures == null) {
      if (other.packagesFeatures != null) return false;
    } else if (!this.packagesFeatures.equals(other.packagesFeatures)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.packagesFeatures == null) ? 0 : this.packagesFeatures.hashCode());
  }
}
