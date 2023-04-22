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

  public RustStdLib(@NonNull final String rustcSysroot, @NonNull final String rustcSrcSysroot) {
    this.rustcSysroot = rustcSysroot;
    this.rustcSrcSysroot = rustcSrcSysroot;
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

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("rustcSysroot", this.rustcSysroot);
    b.add("rustcSrcSysroot", this.rustcSrcSysroot);
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
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.rustcSysroot== null) ? 0 : this.rustcSysroot.hashCode());
    return prime * result + ((this.rustcSrcSysroot== null) ? 0 : this.rustcSrcSysroot.hashCode());
  }
}
