package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/** Structure describing how to start a BSP server and the capabilities it supports. */
@SuppressWarnings("all")
public class BspConnectionDetails {
  @NonNull private String name;

  @NonNull private List<String> argv;

  @NonNull private String version;

  @NonNull private String bspVersion;

  @NonNull private List<String> languages;

  public BspConnectionDetails(
      @NonNull final String name,
      @NonNull final List<String> argv,
      @NonNull final String version,
      @NonNull final String bspVersion,
      @NonNull final List<String> languages) {
    this.name = name;
    this.argv = argv;
    this.version = version;
    this.bspVersion = bspVersion;
    this.languages = languages;
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
  public List<String> getArgv() {
    return this.argv;
  }

  public void setArgv(@NonNull final List<String> argv) {
    this.argv = Preconditions.checkNotNull(argv, "argv");
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
  public String getBspVersion() {
    return this.bspVersion;
  }

  public void setBspVersion(@NonNull final String bspVersion) {
    this.bspVersion = Preconditions.checkNotNull(bspVersion, "bspVersion");
  }

  @Pure
  @NonNull
  public List<String> getLanguages() {
    return this.languages;
  }

  public void setLanguages(@NonNull final List<String> languages) {
    this.languages = Preconditions.checkNotNull(languages, "languages");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("name", this.name);
    b.add("argv", this.argv);
    b.add("version", this.version);
    b.add("bspVersion", this.bspVersion);
    b.add("languages", this.languages);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    BspConnectionDetails other = (BspConnectionDetails) obj;
    if (this.name == null) {
      if (other.name != null) return false;
    } else if (!this.name.equals(other.name)) return false;
    if (this.argv == null) {
      if (other.argv != null) return false;
    } else if (!this.argv.equals(other.argv)) return false;
    if (this.version == null) {
      if (other.version != null) return false;
    } else if (!this.version.equals(other.version)) return false;
    if (this.bspVersion == null) {
      if (other.bspVersion != null) return false;
    } else if (!this.bspVersion.equals(other.bspVersion)) return false;
    if (this.languages == null) {
      if (other.languages != null) return false;
    } else if (!this.languages.equals(other.languages)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.argv == null) ? 0 : this.argv.hashCode());
    result = prime * result + ((this.version == null) ? 0 : this.version.hashCode());
    result = prime * result + ((this.bspVersion == null) ? 0 : this.bspVersion.hashCode());
    return prime * result + ((this.languages == null) ? 0 : this.languages.hashCode());
  }
}
