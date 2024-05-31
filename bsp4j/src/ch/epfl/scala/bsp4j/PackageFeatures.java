package ch.epfl.scala.bsp4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class PackageFeatures {
  @NonNull private String packageId;

  @NonNull private List<BuildTargetIdentifier> targets;

  @NonNull private Map<String, Set<String>> availableFeatures;

  @NonNull private Set<String> enabledFeatures;

  public PackageFeatures(
      @NonNull final String packageId,
      @NonNull final List<BuildTargetIdentifier> targets,
      @NonNull final Map<String, Set<String>> availableFeatures,
      @NonNull final Set<String> enabledFeatures) {
    this.packageId = packageId;
    this.targets = targets;
    this.availableFeatures = availableFeatures;
    this.enabledFeatures = enabledFeatures;
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
  public List<BuildTargetIdentifier> getTargets() {
    return this.targets;
  }

  public void setTargets(@NonNull final List<BuildTargetIdentifier> targets) {
    this.targets = Preconditions.checkNotNull(targets, "targets");
  }

  @Pure
  @NonNull
  public Map<String, Set<String>> getAvailableFeatures() {
    return this.availableFeatures;
  }

  public void setAvailableFeatures(@NonNull final Map<String, Set<String>> availableFeatures) {
    this.availableFeatures = Preconditions.checkNotNull(availableFeatures, "availableFeatures");
  }

  @Pure
  @NonNull
  public Set<String> getEnabledFeatures() {
    return this.enabledFeatures;
  }

  public void setEnabledFeatures(@NonNull final Set<String> enabledFeatures) {
    this.enabledFeatures = Preconditions.checkNotNull(enabledFeatures, "enabledFeatures");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("packageId", this.packageId);
    b.add("targets", this.targets);
    b.add("availableFeatures", this.availableFeatures);
    b.add("enabledFeatures", this.enabledFeatures);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PackageFeatures other = (PackageFeatures) obj;
    if (this.packageId == null) {
      if (other.packageId != null) return false;
    } else if (!this.packageId.equals(other.packageId)) return false;
    if (this.targets == null) {
      if (other.targets != null) return false;
    } else if (!this.targets.equals(other.targets)) return false;
    if (this.availableFeatures == null) {
      if (other.availableFeatures != null) return false;
    } else if (!this.availableFeatures.equals(other.availableFeatures)) return false;
    if (this.enabledFeatures == null) {
      if (other.enabledFeatures != null) return false;
    } else if (!this.enabledFeatures.equals(other.enabledFeatures)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.packageId == null) ? 0 : this.packageId.hashCode());
    result = prime * result + ((this.targets == null) ? 0 : this.targets.hashCode());
    result =
        prime * result + ((this.availableFeatures == null) ? 0 : this.availableFeatures.hashCode());
    return prime * result + ((this.enabledFeatures == null) ? 0 : this.enabledFeatures.hashCode());
  }
}
