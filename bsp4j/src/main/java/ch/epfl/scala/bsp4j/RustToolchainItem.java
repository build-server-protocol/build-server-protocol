package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustToolchainItem {
  private RustcInfo rustStdLib;

  @NonNull
  private String cargoBinPath;

  @NonNull
  private String procMacroSrvPath;

  public RustToolchainItem(@NonNull final String cargoBinPath, @NonNull final String procMacroSrvPath) {
    this.cargoBinPath = cargoBinPath;
    this.procMacroSrvPath = procMacroSrvPath;
  }

  @Pure
  public RustcInfo getRustStdLib() {
    return this.rustStdLib;
  }

  public void setRustStdLib(final RustcInfo rustStdLib) {
    this.rustStdLib = rustStdLib;
  }

  @Pure
  @NonNull
  public String getCargoBinPath() {
    return this.cargoBinPath;
  }

  public void setCargoBinPath(@NonNull final String cargoBinPath) {
    this.cargoBinPath = Preconditions.checkNotNull(cargoBinPath, "cargoBinPath");
  }

  @Pure
  @NonNull
  public String getProcMacroSrvPath() {
    return this.procMacroSrvPath;
  }

  public void setProcMacroSrvPath(@NonNull final String procMacroSrvPath) {
    this.procMacroSrvPath = Preconditions.checkNotNull(procMacroSrvPath, "procMacroSrvPath");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("rustStdLib", this.rustStdLib);
    b.add("cargoBinPath", this.cargoBinPath);
    b.add("procMacroSrvPath", this.procMacroSrvPath);
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
    RustToolchainItem other = (RustToolchainItem) obj;
    if (this.rustStdLib == null) {
      if (other.rustStdLib != null)
        return false;
    } else if (!this.rustStdLib.equals(other.rustStdLib))
      return false;
    if (this.cargoBinPath == null) {
      if (other.cargoBinPath != null)
        return false;
    } else if (!this.cargoBinPath.equals(other.cargoBinPath))
      return false;
    if (this.procMacroSrvPath == null) {
      if (other.procMacroSrvPath != null)
        return false;
    } else if (!this.procMacroSrvPath.equals(other.procMacroSrvPath))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.rustStdLib== null) ? 0 : this.rustStdLib.hashCode());
    result = prime * result + ((this.cargoBinPath== null) ? 0 : this.cargoBinPath.hashCode());
    return prime * result + ((this.procMacroSrvPath== null) ? 0 : this.procMacroSrvPath.hashCode());
  }
}
