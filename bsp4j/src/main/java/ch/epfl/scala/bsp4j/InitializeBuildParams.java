package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class InitializeBuildParams {
  @NonNull
  private String displayName;

  @NonNull
  private String version;

  @NonNull
  private String bspVersion;

  @NonNull
  private String rootUri;

  @NonNull
  private BuildClientCapabilities capabilities;

  private String dataKind;

  @JsonAdapter(JsonElementTypeAdapter.Factory.class)
  private Object data;

  public InitializeBuildParams(@NonNull final String displayName, @NonNull final String version, @NonNull final String bspVersion, @NonNull final String rootUri, @NonNull final BuildClientCapabilities capabilities) {
    this.displayName = displayName;
    this.version = version;
    this.bspVersion = bspVersion;
    this.rootUri = rootUri;
    this.capabilities = capabilities;
  }

  @Pure
  @NonNull
  public String getDisplayName() {
    return this.displayName;
  }

  public void setDisplayName(@NonNull final String displayName) {
    this.displayName = Preconditions.checkNotNull(displayName, "displayName");
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
  public String getRootUri() {
    return this.rootUri;
  }

  public void setRootUri(@NonNull final String rootUri) {
    this.rootUri = Preconditions.checkNotNull(rootUri, "rootUri");
  }

  @Pure
  @NonNull
  public BuildClientCapabilities getCapabilities() {
    return this.capabilities;
  }

  public void setCapabilities(@NonNull final BuildClientCapabilities capabilities) {
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
    b.add("displayName", this.displayName);
    b.add("version", this.version);
    b.add("bspVersion", this.bspVersion);
    b.add("rootUri", this.rootUri);
    b.add("capabilities", this.capabilities);
    b.add("dataKind", this.dataKind);
    b.add("data", this.data);
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
    InitializeBuildParams other = (InitializeBuildParams) obj;
    if (this.displayName == null) {
      if (other.displayName != null)
        return false;
    } else if (!this.displayName.equals(other.displayName))
      return false;
    if (this.version == null) {
      if (other.version != null)
        return false;
    } else if (!this.version.equals(other.version))
      return false;
    if (this.bspVersion == null) {
      if (other.bspVersion != null)
        return false;
    } else if (!this.bspVersion.equals(other.bspVersion))
      return false;
    if (this.rootUri == null) {
      if (other.rootUri != null)
        return false;
    } else if (!this.rootUri.equals(other.rootUri))
      return false;
    if (this.capabilities == null) {
      if (other.capabilities != null)
        return false;
    } else if (!this.capabilities.equals(other.capabilities))
      return false;
    if (this.dataKind == null) {
      if (other.dataKind != null)
        return false;
    } else if (!this.dataKind.equals(other.dataKind))
      return false;
    if (this.data == null) {
      if (other.data != null)
        return false;
    } else if (!this.data.equals(other.data))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.displayName== null) ? 0 : this.displayName.hashCode());
    result = prime * result + ((this.version== null) ? 0 : this.version.hashCode());
    result = prime * result + ((this.bspVersion== null) ? 0 : this.bspVersion.hashCode());
    result = prime * result + ((this.rootUri== null) ? 0 : this.rootUri.hashCode());
    result = prime * result + ((this.capabilities== null) ? 0 : this.capabilities.hashCode());
    result = prime * result + ((this.dataKind== null) ? 0 : this.dataKind.hashCode());
    return prime * result + ((this.data== null) ? 0 : this.data.hashCode());
  }
}
