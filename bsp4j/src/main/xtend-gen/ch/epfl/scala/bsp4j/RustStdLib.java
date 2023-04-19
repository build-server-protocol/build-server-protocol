package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustStdLib {
  @NonNull
  private String rustcSysroot;

  @NonNull
  private String rustcSrcSysroot;

  @NonNull
  private String cargoBinPath;

  @NonNull
  private String procMacroSrv;

  public RustStdLib(@NonNull final String rustcSysroot, @NonNull final String rustcSrcSysroot, @NonNull final String cargoBinPath, @NonNull final String procMacroSrv) {
    this.rustcSysroot = rustcSysroot;
    this.rustcSrcSysroot = rustcSrcSysroot;
    this.cargoBinPath = cargoBinPath;
    this.procMacroSrv = procMacroSrv;
  }

  @Pure
  @NonNull
  public String getRustcSysroot() {
    return this.rustcSysroot;
  }

  public void setRustcSysroot(@NonNull final String rustcSysroot) {
    this.rustcSysroot = Preconditions.checkNotNull(rustcSysroot, "rustcSysroot");
  }

  @Pure
  @NonNull
  public String getRustcSrcSysroot() {
    return this.rustcSrcSysroot;
  }

  public void setRustcSrcSysroot(@NonNull final String rustcSrcSysroot) {
    this.rustcSrcSysroot = Preconditions.checkNotNull(rustcSrcSysroot, "rustcSrcSysroot");
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
  public String getProcMacroSrv() {
    return this.procMacroSrv;
  }

  public void setProcMacroSrv(@NonNull final String procMacroSrv) {
    this.procMacroSrv = Preconditions.checkNotNull(procMacroSrv, "procMacroSrv");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("rustcSysroot", this.rustcSysroot);
    b.add("rustcSrcSysroot", this.rustcSrcSysroot);
    b.add("cargoBinPath", this.cargoBinPath);
    b.add("procMacroSrv", this.procMacroSrv);
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
    RustStdLib other = (RustStdLib) obj;
    if (this.rustcSysroot == null) {
      if (other.rustcSysroot != null)
        return false;
    } else if (!this.rustcSysroot.equals(other.rustcSysroot))
      return false;
    if (this.rustcSrcSysroot == null) {
      if (other.rustcSrcSysroot != null)
        return false;
    } else if (!this.rustcSrcSysroot.equals(other.rustcSrcSysroot))
      return false;
    if (this.cargoBinPath == null) {
      if (other.cargoBinPath != null)
        return false;
    } else if (!this.cargoBinPath.equals(other.cargoBinPath))
      return false;
    if (this.procMacroSrv == null) {
      if (other.procMacroSrv != null)
        return false;
    } else if (!this.procMacroSrv.equals(other.procMacroSrv))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.rustcSysroot== null) ? 0 : this.rustcSysroot.hashCode());
    result = prime * result + ((this.rustcSrcSysroot== null) ? 0 : this.rustcSrcSysroot.hashCode());
    result = prime * result + ((this.cargoBinPath== null) ? 0 : this.cargoBinPath.hashCode());
    return prime * result + ((this.procMacroSrv== null) ? 0 : this.procMacroSrv.hashCode());
  }
}
