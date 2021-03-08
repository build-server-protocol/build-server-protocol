package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class CppBuildTarget {
  @NonNull
  private String version;
  
  @NonNull
  private List<String> copts;
  
  @NonNull
  private List<String> linkopts;
  
  private boolean linkshared;
  
  private String compiler;
  
  private String cCompiler;
  
  private String cppCompiler;
  
  public CppBuildTarget(@NonNull final String version, @NonNull final List<String> copts, @NonNull final List<String> linkopts, final String compiler, final String cCompiler, final String cppCompiler) {
    this.version = version;
    this.copts = copts;
    this.linkopts = linkopts;
    this.linkshared = this.linkshared;
    this.compiler = compiler;
    this.cCompiler = cCompiler;
    this.cppCompiler = cppCompiler;
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
  public List<String> getCopts() {
    return this.copts;
  }
  
  public void setCopts(@NonNull final List<String> copts) {
    this.copts = Preconditions.checkNotNull(copts, "copts");
  }
  
  @Pure
  @NonNull
  public List<String> getLinkopts() {
    return this.linkopts;
  }
  
  public void setLinkopts(@NonNull final List<String> linkopts) {
    this.linkopts = Preconditions.checkNotNull(linkopts, "linkopts");
  }
  
  @Pure
  public boolean isLinkshared() {
    return this.linkshared;
  }
  
  public void setLinkshared(final boolean linkshared) {
    this.linkshared = linkshared;
  }
  
  @Pure
  public String getCompiler() {
    return this.compiler;
  }
  
  public void setCompiler(final String compiler) {
    this.compiler = compiler;
  }
  
  @Pure
  public String getCCompiler() {
    return this.cCompiler;
  }
  
  public void setCCompiler(final String cCompiler) {
    this.cCompiler = cCompiler;
  }
  
  @Pure
  public String getCppCompiler() {
    return this.cppCompiler;
  }
  
  public void setCppCompiler(final String cppCompiler) {
    this.cppCompiler = cppCompiler;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("version", this.version);
    b.add("copts", this.copts);
    b.add("linkopts", this.linkopts);
    b.add("linkshared", this.linkshared);
    b.add("compiler", this.compiler);
    b.add("cCompiler", this.cCompiler);
    b.add("cppCompiler", this.cppCompiler);
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
    CppBuildTarget other = (CppBuildTarget) obj;
    if (this.version == null) {
      if (other.version != null)
        return false;
    } else if (!this.version.equals(other.version))
      return false;
    if (this.copts == null) {
      if (other.copts != null)
        return false;
    } else if (!this.copts.equals(other.copts))
      return false;
    if (this.linkopts == null) {
      if (other.linkopts != null)
        return false;
    } else if (!this.linkopts.equals(other.linkopts))
      return false;
    if (other.linkshared != this.linkshared)
      return false;
    if (this.compiler == null) {
      if (other.compiler != null)
        return false;
    } else if (!this.compiler.equals(other.compiler))
      return false;
    if (this.cCompiler == null) {
      if (other.cCompiler != null)
        return false;
    } else if (!this.cCompiler.equals(other.cCompiler))
      return false;
    if (this.cppCompiler == null) {
      if (other.cppCompiler != null)
        return false;
    } else if (!this.cppCompiler.equals(other.cppCompiler))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.version== null) ? 0 : this.version.hashCode());
    result = prime * result + ((this.copts== null) ? 0 : this.copts.hashCode());
    result = prime * result + ((this.linkopts== null) ? 0 : this.linkopts.hashCode());
    result = prime * result + (this.linkshared ? 1231 : 1237);
    result = prime * result + ((this.compiler== null) ? 0 : this.compiler.hashCode());
    result = prime * result + ((this.cCompiler== null) ? 0 : this.cCompiler.hashCode());
    return prime * result + ((this.cppCompiler== null) ? 0 : this.cppCompiler.hashCode());
  }
}
