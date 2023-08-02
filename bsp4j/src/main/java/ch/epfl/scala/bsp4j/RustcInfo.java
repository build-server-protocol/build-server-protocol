package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustcInfo {
  @NonNull
  private String sysrootPath;

  @NonNull
  private String srcSysrootPath;

  @NonNull
  private String version;

  @NonNull
  private String host;

  public RustcInfo(@NonNull final String sysrootPath, @NonNull final String srcSysrootPath, @NonNull final String version, @NonNull final String host) {
    this.sysrootPath = sysrootPath;
    this.srcSysrootPath = srcSysrootPath;
    this.version = version;
    this.host = host;
  }

  @Pure
  @NonNull
  public String getSysrootPath() {
    return this.sysrootPath;
  }

  public void setSysrootPath(@NonNull final String sysrootPath) {
    this.sysrootPath = Preconditions.checkNotNull(sysrootPath, "sysrootPath");
  }

  @Pure
  @NonNull
  public String getSrcSysrootPath() {
    return this.srcSysrootPath;
  }

  public void setSrcSysrootPath(@NonNull final String srcSysrootPath) {
    this.srcSysrootPath = Preconditions.checkNotNull(srcSysrootPath, "srcSysrootPath");
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
    b.add("sysrootPath", this.sysrootPath);
    b.add("srcSysrootPath", this.srcSysrootPath);
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
    if (this.sysrootPath == null) {
      if (other.sysrootPath != null)
        return false;
    } else if (!this.sysrootPath.equals(other.sysrootPath))
      return false;
    if (this.srcSysrootPath == null) {
      if (other.srcSysrootPath != null)
        return false;
    } else if (!this.srcSysrootPath.equals(other.srcSysrootPath))
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
    result = prime * result + ((this.sysrootPath== null) ? 0 : this.sysrootPath.hashCode());
    result = prime * result + ((this.srcSysrootPath== null) ? 0 : this.srcSysrootPath.hashCode());
    result = prime * result + ((this.version== null) ? 0 : this.version.hashCode());
    return prime * result + ((this.host== null) ? 0 : this.host.hashCode());
  }
}
