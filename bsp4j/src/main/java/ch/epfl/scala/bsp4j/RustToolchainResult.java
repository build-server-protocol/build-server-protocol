package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustToolchainResult {
  @NonNull
  private List<RustToolchainItem> toolchains;

  public RustToolchainResult(@NonNull final List<RustToolchainItem> toolchains) {
    this.toolchains = toolchains;
  }

  @Pure
  @NonNull
  public List<RustToolchainItem> getToolchains() {
    return this.toolchains;
  }

  public void setToolchains(@NonNull final List<RustToolchainItem> toolchains) {
    this.toolchains = Preconditions.checkNotNull(toolchains, "toolchains");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("toolchains", this.toolchains);
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
    RustToolchainResult other = (RustToolchainResult) obj;
    if (this.toolchains == null) {
      if (other.toolchains != null)
        return false;
    } else if (!this.toolchains.equals(other.toolchains))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.toolchains== null) ? 0 : this.toolchains.hashCode());
  }
}
