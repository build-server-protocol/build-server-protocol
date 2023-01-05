package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustPackage {
  @NonNull
  private String name;

  @NonNull
  private String version;

  @NonNull
  private List<String> authors;

  private String description;

  private String repository;

  private String license;

  private String license_file;

  private String source;

  @NonNull
  private String id;

  @NonNull
  private String manifest_path;

  @NonNull
  private List<RustTarget> targets;

  private String edition;

  @NonNull
  private List<RustFeature> features;

  @NonNull
  private List<RustRawDependency> dependencies;

  public RustPackage(@NonNull final String name, @NonNull final String version, @NonNull final List<String> authors, final String description, final String repository, final String license, final String license_file, final String source, @NonNull final String id, @NonNull final String manifest_path, @NonNull final List<RustTarget> targets, final String edition, @NonNull final List<RustFeature> features, @NonNull final List<RustRawDependency> dependencies) {
    this.name = name;
    this.version = version;
    this.authors = authors;
    this.description = description;
    this.repository = repository;
    this.license = license;
    this.license_file = license_file;
    this.source = source;
    this.id = id;
    this.manifest_path = manifest_path;
    this.targets = targets;
    this.edition = edition;
    this.features = features;
    this.dependencies = dependencies;
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
  public List<String> getAuthors() {
    return this.authors;
  }

  public void setAuthors(@NonNull final List<String> authors) {
    this.authors = Preconditions.checkNotNull(authors, "authors");
  }

  @Pure
  public String getDescription() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  @Pure
  public String getRepository() {
    return this.repository;
  }

  public void setRepository(final String repository) {
    this.repository = repository;
  }

  @Pure
  public String getLicense() {
    return this.license;
  }

  public void setLicense(final String license) {
    this.license = license;
  }

  @Pure
  public String getLicense_file() {
    return this.license_file;
  }

  public void setLicense_file(final String license_file) {
    this.license_file = license_file;
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
  public String getId() {
    return this.id;
  }

  public void setId(@NonNull final String id) {
    this.id = Preconditions.checkNotNull(id, "id");
  }

  @Pure
  @NonNull
  public String getManifest_path() {
    return this.manifest_path;
  }

  public void setManifest_path(@NonNull final String manifest_path) {
    this.manifest_path = Preconditions.checkNotNull(manifest_path, "manifest_path");
  }

  @Pure
  @NonNull
  public List<RustTarget> getTargets() {
    return this.targets;
  }

  public void setTargets(@NonNull final List<RustTarget> targets) {
    this.targets = Preconditions.checkNotNull(targets, "targets");
  }

  @Pure
  public String getEdition() {
    return this.edition;
  }

  public void setEdition(final String edition) {
    this.edition = edition;
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
    b.add("name", this.name);
    b.add("version", this.version);
    b.add("authors", this.authors);
    b.add("description", this.description);
    b.add("repository", this.repository);
    b.add("license", this.license);
    b.add("license_file", this.license_file);
    b.add("source", this.source);
    b.add("id", this.id);
    b.add("manifest_path", this.manifest_path);
    b.add("targets", this.targets);
    b.add("edition", this.edition);
    b.add("features", this.features);
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
    RustPackage other = (RustPackage) obj;
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
    if (this.authors == null) {
      if (other.authors != null)
        return false;
    } else if (!this.authors.equals(other.authors))
      return false;
    if (this.description == null) {
      if (other.description != null)
        return false;
    } else if (!this.description.equals(other.description))
      return false;
    if (this.repository == null) {
      if (other.repository != null)
        return false;
    } else if (!this.repository.equals(other.repository))
      return false;
    if (this.license == null) {
      if (other.license != null)
        return false;
    } else if (!this.license.equals(other.license))
      return false;
    if (this.license_file == null) {
      if (other.license_file != null)
        return false;
    } else if (!this.license_file.equals(other.license_file))
      return false;
    if (this.source == null) {
      if (other.source != null)
        return false;
    } else if (!this.source.equals(other.source))
      return false;
    if (this.id == null) {
      if (other.id != null)
        return false;
    } else if (!this.id.equals(other.id))
      return false;
    if (this.manifest_path == null) {
      if (other.manifest_path != null)
        return false;
    } else if (!this.manifest_path.equals(other.manifest_path))
      return false;
    if (this.targets == null) {
      if (other.targets != null)
        return false;
    } else if (!this.targets.equals(other.targets))
      return false;
    if (this.edition == null) {
      if (other.edition != null)
        return false;
    } else if (!this.edition.equals(other.edition))
      return false;
    if (this.features == null) {
      if (other.features != null)
        return false;
    } else if (!this.features.equals(other.features))
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
    result = prime * result + ((this.name== null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.version== null) ? 0 : this.version.hashCode());
    result = prime * result + ((this.authors== null) ? 0 : this.authors.hashCode());
    result = prime * result + ((this.description== null) ? 0 : this.description.hashCode());
    result = prime * result + ((this.repository== null) ? 0 : this.repository.hashCode());
    result = prime * result + ((this.license== null) ? 0 : this.license.hashCode());
    result = prime * result + ((this.license_file== null) ? 0 : this.license_file.hashCode());
    result = prime * result + ((this.source== null) ? 0 : this.source.hashCode());
    result = prime * result + ((this.id== null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.manifest_path== null) ? 0 : this.manifest_path.hashCode());
    result = prime * result + ((this.targets== null) ? 0 : this.targets.hashCode());
    result = prime * result + ((this.edition== null) ? 0 : this.edition.hashCode());
    result = prime * result + ((this.features== null) ? 0 : this.features.hashCode());
    return prime * result + ((this.dependencies== null) ? 0 : this.dependencies.hashCode());
  }
}
