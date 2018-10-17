package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.BuildTargetCapabilities;
import ch.epfl.scala.bsp4j.BuildTargetIdentifier;
import ch.epfl.scala.bsp4j.BuildTargetKind;
import com.google.gson.annotations.JsonAdapter;
import java.util.List;
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class BuildTarget {
  @NonNull
  private BuildTargetIdentifier id;
  
  private String displayName;
  
  @NonNull
  private BuildTargetKind kind;
  
  private List<String> languageIds;
  
  private List<BuildTargetIdentifier> dependencies;
  
  @NonNull
  private BuildTargetCapabilities capabilities;
  
  @JsonAdapter(JsonElementTypeAdapter.Factory.class)
  private Object data;
  
  public BuildTarget(@NonNull final BuildTargetIdentifier id, @NonNull final BuildTargetKind kind, @NonNull final BuildTargetCapabilities capabilities) {
    this.id = id;
    this.kind = kind;
    this.capabilities = capabilities;
  }
  
  @Pure
  @NonNull
  public BuildTargetIdentifier getId() {
    return this.id;
  }
  
  public void setId(@NonNull final BuildTargetIdentifier id) {
    this.id = id;
  }
  
  @Pure
  public String getDisplayName() {
    return this.displayName;
  }
  
  public void setDisplayName(final String displayName) {
    this.displayName = displayName;
  }
  
  @Pure
  @NonNull
  public BuildTargetKind getKind() {
    return this.kind;
  }
  
  public void setKind(@NonNull final BuildTargetKind kind) {
    this.kind = kind;
  }
  
  @Pure
  public List<String> getLanguageIds() {
    return this.languageIds;
  }
  
  public void setLanguageIds(final List<String> languageIds) {
    this.languageIds = languageIds;
  }
  
  @Pure
  public List<BuildTargetIdentifier> getDependencies() {
    return this.dependencies;
  }
  
  public void setDependencies(final List<BuildTargetIdentifier> dependencies) {
    this.dependencies = dependencies;
  }
  
  @Pure
  @NonNull
  public BuildTargetCapabilities getCapabilities() {
    return this.capabilities;
  }
  
  public void setCapabilities(@NonNull final BuildTargetCapabilities capabilities) {
    this.capabilities = capabilities;
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
    b.add("kind", this.kind);
    b.add("languageIds", this.languageIds);
    b.add("dependencies", this.dependencies);
    b.add("capabilities", this.capabilities);
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
    BuildTarget other = (BuildTarget) obj;
    if (this.id == null) {
      if (other.id != null)
        return false;
    } else if (!this.id.equals(other.id))
      return false;
    if (this.displayName == null) {
      if (other.displayName != null)
        return false;
    } else if (!this.displayName.equals(other.displayName))
      return false;
    if (this.kind == null) {
      if (other.kind != null)
        return false;
    } else if (!this.kind.equals(other.kind))
      return false;
    if (this.languageIds == null) {
      if (other.languageIds != null)
        return false;
    } else if (!this.languageIds.equals(other.languageIds))
      return false;
    if (this.dependencies == null) {
      if (other.dependencies != null)
        return false;
    } else if (!this.dependencies.equals(other.dependencies))
      return false;
    if (this.capabilities == null) {
      if (other.capabilities != null)
        return false;
    } else if (!this.capabilities.equals(other.capabilities))
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
    result = prime * result + ((this.id== null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.displayName== null) ? 0 : this.displayName.hashCode());
    result = prime * result + ((this.kind== null) ? 0 : this.kind.hashCode());
    result = prime * result + ((this.languageIds== null) ? 0 : this.languageIds.hashCode());
    result = prime * result + ((this.dependencies== null) ? 0 : this.dependencies.hashCode());
    result = prime * result + ((this.capabilities== null) ? 0 : this.capabilities.hashCode());
    return prime * result + ((this.data== null) ? 0 : this.data.hashCode());
  }
}
