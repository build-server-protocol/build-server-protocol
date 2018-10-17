package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.BuildClientCapabilities;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class InitializeBuildParams {
  @NonNull
  private String rootUri;
  
  @NonNull
  private BuildClientCapabilities capabilities;
  
  public InitializeBuildParams(@NonNull final String rootUri, @NonNull final BuildClientCapabilities capabilities) {
    this.rootUri = rootUri;
    this.capabilities = capabilities;
  }
  
  @Pure
  @NonNull
  public String getRootUri() {
    return this.rootUri;
  }
  
  public void setRootUri(@NonNull final String rootUri) {
    this.rootUri = rootUri;
  }
  
  @Pure
  @NonNull
  public BuildClientCapabilities getCapabilities() {
    return this.capabilities;
  }
  
  public void setCapabilities(@NonNull final BuildClientCapabilities capabilities) {
    this.capabilities = capabilities;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("rootUri", this.rootUri);
    b.add("capabilities", this.capabilities);
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
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.rootUri== null) ? 0 : this.rootUri.hashCode());
    return prime * result + ((this.capabilities== null) ? 0 : this.capabilities.hashCode());
  }
}
