package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import java.util.List;
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Build target contains metadata about an artifact (for example library, test, or binary artifact).
 * Using vocabulary of other build tools:
 *
 * <p>* sbt: a build target is a combined project + config. Example: * a regular JVM project with
 * main and test configurations will have 2 build targets, one for main and one for test. * a single
 * configuration in a single project that contains both Java and Scala sources maps to one
 * BuildTarget. * a project with crossScalaVersions 2.11 and 2.12 containing main and test
 * configuration in each will have 4 build targets. * a Scala 2.11 and 2.12 cross-built project for
 * Scala.js and the JVM with main and test configurations will have 8 build targets. * Pants: a
 * pants target corresponds one-to-one with a BuildTarget * Bazel: a bazel target corresponds
 * one-to-one with a BuildTarget
 *
 * <p>The general idea is that the BuildTarget data structure should contain only information that
 * is fast or cheap to compute.
 */
@SuppressWarnings("all")
public class BuildTarget {
  @NonNull private BuildTargetIdentifier id;

  private String displayName;

  private String baseDirectory;

  @NonNull private List<String> tags;

  @NonNull private List<String> languageIds;

  @NonNull private List<BuildTargetIdentifier> dependencies;

  @NonNull private BuildTargetCapabilities capabilities;

  private String dataKind;

  @JsonAdapter(JsonElementTypeAdapter.Factory.class)
  private Object data;

  public BuildTarget(
      @NonNull final BuildTargetIdentifier id,
      @NonNull final List<String> tags,
      @NonNull final List<String> languageIds,
      @NonNull final List<BuildTargetIdentifier> dependencies,
      @NonNull final BuildTargetCapabilities capabilities) {
    this.id = id;
    this.tags = tags;
    this.languageIds = languageIds;
    this.dependencies = dependencies;
    this.capabilities = capabilities;
  }

  @Pure
  @NonNull
  public BuildTargetIdentifier getId() {
    return this.id;
  }

  public void setId(@NonNull final BuildTargetIdentifier id) {
    this.id = Preconditions.checkNotNull(id, "id");
  }

  @Pure
  public String getDisplayName() {
    return this.displayName;
  }

  public void setDisplayName(final String displayName) {
    this.displayName = displayName;
  }

  @Pure
  public String getBaseDirectory() {
    return this.baseDirectory;
  }

  public void setBaseDirectory(final String baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  @Pure
  @NonNull
  public List<String> getTags() {
    return this.tags;
  }

  public void setTags(@NonNull final List<String> tags) {
    this.tags = Preconditions.checkNotNull(tags, "tags");
  }

  @Pure
  @NonNull
  public List<String> getLanguageIds() {
    return this.languageIds;
  }

  public void setLanguageIds(@NonNull final List<String> languageIds) {
    this.languageIds = Preconditions.checkNotNull(languageIds, "languageIds");
  }

  @Pure
  @NonNull
  public List<BuildTargetIdentifier> getDependencies() {
    return this.dependencies;
  }

  public void setDependencies(@NonNull final List<BuildTargetIdentifier> dependencies) {
    this.dependencies = Preconditions.checkNotNull(dependencies, "dependencies");
  }

  @Pure
  @NonNull
  public BuildTargetCapabilities getCapabilities() {
    return this.capabilities;
  }

  public void setCapabilities(@NonNull final BuildTargetCapabilities capabilities) {
    this.capabilities = Preconditions.checkNotNull(capabilities, "capabilities");
  }

  @Pure
  public String getDataKind() {
    return this.dataKind;
  }

  public void setDataKind(final String dataKind) {
    this.dataKind = dataKind;
  }

  @Pure
  public Object getData() {
    return this.data;
  }

  public void setData(final Object data) {
    this.data = data;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("id", this.id);
    b.add("displayName", this.displayName);
    b.add("baseDirectory", this.baseDirectory);
    b.add("tags", this.tags);
    b.add("languageIds", this.languageIds);
    b.add("dependencies", this.dependencies);
    b.add("capabilities", this.capabilities);
    b.add("dataKind", this.dataKind);
    b.add("data", this.data);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    BuildTarget other = (BuildTarget) obj;
    if (this.id == null) {
      if (other.id != null) return false;
    } else if (!this.id.equals(other.id)) return false;
    if (this.displayName == null) {
      if (other.displayName != null) return false;
    } else if (!this.displayName.equals(other.displayName)) return false;
    if (this.baseDirectory == null) {
      if (other.baseDirectory != null) return false;
    } else if (!this.baseDirectory.equals(other.baseDirectory)) return false;
    if (this.tags == null) {
      if (other.tags != null) return false;
    } else if (!this.tags.equals(other.tags)) return false;
    if (this.languageIds == null) {
      if (other.languageIds != null) return false;
    } else if (!this.languageIds.equals(other.languageIds)) return false;
    if (this.dependencies == null) {
      if (other.dependencies != null) return false;
    } else if (!this.dependencies.equals(other.dependencies)) return false;
    if (this.capabilities == null) {
      if (other.capabilities != null) return false;
    } else if (!this.capabilities.equals(other.capabilities)) return false;
    if (this.dataKind == null) {
      if (other.dataKind != null) return false;
    } else if (!this.dataKind.equals(other.dataKind)) return false;
    if (this.data == null) {
      if (other.data != null) return false;
    } else if (!this.data.equals(other.data)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.displayName == null) ? 0 : this.displayName.hashCode());
    result = prime * result + ((this.baseDirectory == null) ? 0 : this.baseDirectory.hashCode());
    result = prime * result + ((this.tags == null) ? 0 : this.tags.hashCode());
    result = prime * result + ((this.languageIds == null) ? 0 : this.languageIds.hashCode());
    result = prime * result + ((this.dependencies == null) ? 0 : this.dependencies.hashCode());
    result = prime * result + ((this.capabilities == null) ? 0 : this.capabilities.hashCode());
    result = prime * result + ((this.dataKind == null) ? 0 : this.dataKind.hashCode());
    return prime * result + ((this.data == null) ? 0 : this.data.hashCode());
  }
}
