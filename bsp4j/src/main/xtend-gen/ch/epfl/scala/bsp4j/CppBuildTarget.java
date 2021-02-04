package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.CppCompiler;
import ch.epfl.scala.bsp4j.CppPlatform;
import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class CppBuildTarget {
  @NonNull
  private CppPlatform platform;
  
  @NonNull
  private List<String> options;
  
  private CppCompiler compiler;
  
  private String cCompiler;
  
  private String cppCompiler;
  
  public CppBuildTarget(@NonNull final CppPlatform platform, @NonNull final List<String> options, final CppCompiler compiler, final String cCompiler, final String cppCompiler) {
    this.platform = platform;
    this.options = options;
    this.compiler = compiler;
    this.cCompiler = cCompiler;
    this.cppCompiler = cppCompiler;
  }
  
  @Pure
  @NonNull
  public CppPlatform getPlatform() {
    return this.platform;
  }
  
  public void setPlatform(@NonNull final CppPlatform platform) {
    this.platform = Preconditions.checkNotNull(platform, "platform");
  }
  
  @Pure
  @NonNull
  public List<String> getOptions() {
    return this.options;
  }
  
  public void setOptions(@NonNull final List<String> options) {
    this.options = Preconditions.checkNotNull(options, "options");
  }
  
  @Pure
  public CppCompiler getCompiler() {
    return this.compiler;
  }
  
  public void setCompiler(final CppCompiler compiler) {
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
    b.add("platform", this.platform);
    b.add("options", this.options);
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
    if (this.platform == null) {
      if (other.platform != null)
        return false;
    } else if (!this.platform.equals(other.platform))
      return false;
    if (this.options == null) {
      if (other.options != null)
        return false;
    } else if (!this.options.equals(other.options))
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
    result = prime * result + ((this.platform== null) ? 0 : this.platform.hashCode());
    result = prime * result + ((this.options== null) ? 0 : this.options.hashCode());
    result = prime * result + ((this.compiler== null) ? 0 : this.compiler.hashCode());
    result = prime * result + ((this.cCompiler== null) ? 0 : this.cCompiler.hashCode());
    return prime * result + ((this.cppCompiler== null) ? 0 : this.cppCompiler.hashCode());
  }
}
