package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class CppBuildTarget {
  @NonNull
  private String version;
  
  private String compiler;
  
  private String cCompiler;
  
  private String cppCompiler;
  
  public CppBuildTarget(@NonNull final String version, final String compiler, final String cCompiler, final String cppCompiler) {
    this.version = version;
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
    result = prime * result + ((this.compiler== null) ? 0 : this.compiler.hashCode());
    result = prime * result + ((this.cCompiler== null) ? 0 : this.cCompiler.hashCode());
    return prime * result + ((this.cppCompiler== null) ? 0 : this.cppCompiler.hashCode());
  }
}
