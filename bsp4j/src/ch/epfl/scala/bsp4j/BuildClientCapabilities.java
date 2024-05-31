package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class BuildClientCapabilities {
  @NonNull private List<String> languageIds;

  private Boolean jvmCompileClasspathReceiver;

  public BuildClientCapabilities(@NonNull final List<String> languageIds) {
    this.languageIds = languageIds;
  }

  @Pure
  @NonNull
  public List<String> getLanguageIds() {
    return this.languageIds;
  }

  public void setLanguageIds(@NonNull final List<String> languageIds) {
    this.languageIds = Preconditions.checkNotNull(languageIds, "languageIds");
  }

  @Pure
  public Boolean getJvmCompileClasspathReceiver() {
    return this.jvmCompileClasspathReceiver;
  }

  public void setJvmCompileClasspathReceiver(final Boolean jvmCompileClasspathReceiver) {
    this.jvmCompileClasspathReceiver = jvmCompileClasspathReceiver;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("languageIds", this.languageIds);
    b.add("jvmCompileClasspathReceiver", this.jvmCompileClasspathReceiver);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    BuildClientCapabilities other = (BuildClientCapabilities) obj;
    if (this.languageIds == null) {
      if (other.languageIds != null) return false;
    } else if (!this.languageIds.equals(other.languageIds)) return false;
    if (this.jvmCompileClasspathReceiver == null) {
      if (other.jvmCompileClasspathReceiver != null) return false;
    } else if (!this.jvmCompileClasspathReceiver.equals(other.jvmCompileClasspathReceiver))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.languageIds == null) ? 0 : this.languageIds.hashCode());
    return prime * result
        + ((this.jvmCompileClasspathReceiver == null)
            ? 0
            : this.jvmCompileClasspathReceiver.hashCode());
  }
}
