package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.BuildServerCapabilities;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class InitializeBuildResult {
  @NonNull
  private BuildServerCapabilities capabilities;
  
  public InitializeBuildResult(@NonNull final BuildServerCapabilities capabilities) {
    this.capabilities = capabilities;
  }
  
  @Pure
  @NonNull
  public BuildServerCapabilities getCapabilities() {
    return this.capabilities;
  }
  
  public void setCapabilities(@NonNull final BuildServerCapabilities capabilities) {
    this.capabilities = capabilities;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
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
    InitializeBuildResult other = (InitializeBuildResult) obj;
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
    return 31 * 1 + ((this.capabilities== null) ? 0 : this.capabilities.hashCode());
  }
}
