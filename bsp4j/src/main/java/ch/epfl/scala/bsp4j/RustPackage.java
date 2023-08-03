package ch.epfl.scala.bsp4j;

import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustPackage {
  @NonNull
  private String id;

  @NonNull
  private String name;

  @NonNull
  private String version;

  @NonNull
  private String origin;

  @NonNull
  private Integer edition;

  private String source;

  @NonNull
  private List<RustBuildTarget> targets;

  @NonNull
  private List<RustBuildTarget> allTargets;

  @NonNull
  private List<RustFeature> features;

  @NonNull
  private List<String> enabledFeatures;

  private RustCfgOptions cfgOptions;

  private Map<String, String> env;

  private String outDirUrl;

  private String procMacroArtifact;

  public RustPackage(@NonNull final String id, @NonNull final String name, @NonNull final String version, @NonNull final String origin, @NonNull final Integer edition, @NonNull final List<RustBuildTarget> targets, @NonNull final List<RustBuildTarget> allTargets, @NonNull final List<RustFeature> features, @NonNull final List<String> enabledFeatures) {
    this.id = id;
    this.name = name;
    this.version = version;
    this.origin = origin;
    this.edition = edition;
    this.targets = targets;
    this.allTargets = allTargets;
    this.features = features;
    this.enabledFeatures = enabledFeatures;
  }

  @Pure
  @NonNull
  public String getId() {
    return this.id;
  }

  public void setId(@NonNull final String id) {
    this.id = Preconditions.checkNotNull(id, "id");
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
  public String getOrigin() {
    return this.origin;
  }

  public void setOrigin(@NonNull final String origin) {
    this.origin = Preconditions.checkNotNull(origin, "origin");
  }

  @Pure
  @NonNull
  public Integer getEdition() {
    return this.edition;
  }

  public void setEdition(@NonNull final Integer edition) {
    this.edition = Preconditions.checkNotNull(edition, "edition");
  }

  @Pure
  public String getSource() {
    return this.source;
  }

  public void setSource(final String source) {
    this.source = source;
  }

  @Pure
  @NonNull
  public List<RustBuildTarget> getTargets() {
    return this.targets;
  }

  public void setTargets(@NonNull final List<RustBuildTarget> targets) {
    this.targets = Preconditions.checkNotNull(targets, "targets");
  }

  @Pure
  @NonNull
  public List<RustBuildTarget> getAllTargets() {
    return this.allTargets;
  }

  public void setAllTargets(@NonNull final List<RustBuildTarget> allTargets) {
    this.allTargets = Preconditions.checkNotNull(allTargets, "allTargets");
  }

  @Pure
  @NonNull
  public List<RustFeature> getFeatures() {
    return this.features;
  }

  public void setFeatures(@NonNull final List<RustFeature> features) {
    this.features = Preconditions.checkNotNull(features, "features");
  }

  @Pure
  @NonNull
  public List<String> getEnabledFeatures() {
    return this.enabledFeatures;
  }

  public void setEnabledFeatures(@NonNull final List<String> enabledFeatures) {
    this.enabledFeatures = Preconditions.checkNotNull(enabledFeatures, "enabledFeatures");
  }

  @Pure
  public RustCfgOptions getCfgOptions() {
    return this.cfgOptions;
  }

  public void setCfgOptions(final RustCfgOptions cfgOptions) {
    this.cfgOptions = cfgOptions;
  }

  @Pure
  public Map<String, String> getEnv() {
    return this.env;
  }

  public void setEnv(final Map<String, String> env) {
    this.env = env;
  }

  @Pure
  public String getOutDirUrl() {
    return this.outDirUrl;
  }

  public void setOutDirUrl(final String outDirUrl) {
    this.outDirUrl = outDirUrl;
  }

  @Pure
  public String getProcMacroArtifact() {
    return this.procMacroArtifact;
  }

  public void setProcMacroArtifact(final String procMacroArtifact) {
    this.procMacroArtifact = procMacroArtifact;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("id", this.id);
    b.add("name", this.name);
    b.add("version", this.version);
    b.add("origin", this.origin);
    b.add("edition", this.edition);
    b.add("source", this.source);
    b.add("targets", this.targets);
    b.add("allTargets", this.allTargets);
    b.add("features", this.features);
    b.add("enabledFeatures", this.enabledFeatures);
    b.add("cfgOptions", this.cfgOptions);
    b.add("env", this.env);
    b.add("outDirUrl", this.outDirUrl);
    b.add("procMacroArtifact", this.procMacroArtifact);
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
    RustPackage other = (RustPackage) obj;
    if (this.id == null) {
      if (other.id != null)
        return false;
    } else if (!this.id.equals(other.id))
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
    if (this.origin == null) {
      if (other.origin != null)
        return false;
    } else if (!this.origin.equals(other.origin))
      return false;
    if (this.edition == null) {
      if (other.edition != null)
        return false;
    } else if (!this.edition.equals(other.edition))
      return false;
    if (this.source == null) {
      if (other.source != null)
        return false;
    } else if (!this.source.equals(other.source))
      return false;
    if (this.targets == null) {
      if (other.targets != null)
        return false;
    } else if (!this.targets.equals(other.targets))
      return false;
    if (this.allTargets == null) {
      if (other.allTargets != null)
        return false;
    } else if (!this.allTargets.equals(other.allTargets))
      return false;
    if (this.features == null) {
      if (other.features != null)
        return false;
    } else if (!this.features.equals(other.features))
      return false;
    if (this.enabledFeatures == null) {
      if (other.enabledFeatures != null)
        return false;
    } else if (!this.enabledFeatures.equals(other.enabledFeatures))
      return false;
    if (this.cfgOptions == null) {
      if (other.cfgOptions != null)
        return false;
    } else if (!this.cfgOptions.equals(other.cfgOptions))
      return false;
    if (this.env == null) {
      if (other.env != null)
        return false;
    } else if (!this.env.equals(other.env))
      return false;
    if (this.outDirUrl == null) {
      if (other.outDirUrl != null)
        return false;
    } else if (!this.outDirUrl.equals(other.outDirUrl))
      return false;
    if (this.procMacroArtifact == null) {
      if (other.procMacroArtifact != null)
        return false;
    } else if (!this.procMacroArtifact.equals(other.procMacroArtifact))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.id== null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.version== null) ? 0 : this.version.hashCode());
    result = prime * result + ((this.origin== null) ? 0 : this.origin.hashCode());
    result = prime * result + ((this.edition== null) ? 0 : this.edition.hashCode());
    result = prime * result + ((this.source== null) ? 0 : this.source.hashCode());
    result = prime * result + ((this.targets== null) ? 0 : this.targets.hashCode());
    result = prime * result + ((this.allTargets== null) ? 0 : this.allTargets.hashCode());
    result = prime * result + ((this.features== null) ? 0 : this.features.hashCode());
    result = prime * result + ((this.enabledFeatures== null) ? 0 : this.enabledFeatures.hashCode());
    result = prime * result + ((this.cfgOptions== null) ? 0 : this.cfgOptions.hashCode());
    result = prime * result + ((this.env== null) ? 0 : this.env.hashCode());
    result = prime * result + ((this.outDirUrl== null) ? 0 : this.outDirUrl.hashCode());
    return prime * result + ((this.procMacroArtifact== null) ? 0 : this.procMacroArtifact.hashCode());
  }
}
