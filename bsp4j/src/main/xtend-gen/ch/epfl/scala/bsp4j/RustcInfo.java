package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustcInfo {
  @NonNull
  private String sysroot;

  @NonNull
  private String srcSysroot;

  @NonNull
  private String version;

  @NonNull
  private String host;

  public RustcInfo(@NonNull final String sysroot, @NonNull final String srcSysroot, @NonNull final String version, @NonNull final String host) {
    this.sysroot = sysroot;
    this.srcSysroot = srcSysroot;
    this.version = version;
    this.host = host;
  }

  @Pure
  @NonNull
  public String getSysroot() {
    return this.sysroot;
  }

  public void setSysroot(@NonNull final String sysroot) {
    this.sysroot = Preconditions.checkNotNull(sysroot, "sysroot");
  }

  @Pure
  @NonNull
  public String getSrcSysroot() {
    return this.srcSysroot;
  }

  public void setSrcSysroot(@NonNull final String srcSysroot) {
    this.srcSysroot = Preconditions.checkNotNull(srcSysroot, "srcSysroot");
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
  public String getHost() {
    return this.host;
  }

  public void setHost(@NonNull final String host) {
    this.host = Preconditions.checkNotNull(host, "host");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("sysroot", this.sysroot);
    b.add("srcSysroot", this.srcSysroot);
    b.add("version", this.version);
    b.add("host", this.host);
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
    RustcInfo other = (RustcInfo) obj;
    if (this.sysroot == null) {
      if (other.sysroot != null)
        return false;
    } else if (!this.sysroot.equals(other.sysroot))
      return false;
    if (this.srcSysroot == null) {
      if (other.srcSysroot != null)
        return false;
    } else if (!this.srcSysroot.equals(other.srcSysroot))
      return false;
    if (this.version == null) {
      if (other.version != null)
        return false;
    } else if (!this.version.equals(other.version))
      return false;
    if (this.host == null) {
      if (other.host != null)
        return false;
    } else if (!this.host.equals(other.host))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.sysroot== null) ? 0 : this.sysroot.hashCode());
    result = prime * result + ((this.srcSysroot== null) ? 0 : this.srcSysroot.hashCode());
    result = prime * result + ((this.version== null) ? 0 : this.version.hashCode());
    return prime * result + ((this.host== null) ? 0 : this.host.hashCode());
  }
}
