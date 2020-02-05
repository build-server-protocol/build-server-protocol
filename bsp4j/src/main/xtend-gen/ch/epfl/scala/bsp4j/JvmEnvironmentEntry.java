package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.BuildTargetIdentifier;
import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class JvmEnvironmentEntry {
  @NonNull
  private BuildTargetIdentifier target;
  
  @NonNull
  private List<String> classpath;
  
  @NonNull
  private List<String> jvmOptions;
  
  public JvmEnvironmentEntry(@NonNull final BuildTargetIdentifier target, @NonNull final List<String> classpath, @NonNull final List<String> jvmOptions) {
    this.target = target;
    this.classpath = classpath;
    this.jvmOptions = jvmOptions;
  }
  
  @Pure
  @NonNull
  public BuildTargetIdentifier getTarget() {
    return this.target;
  }
  
  public void setTarget(@NonNull final BuildTargetIdentifier target) {
    this.target = target;
  }
  
  @Pure
  @NonNull
  public List<String> getClasspath() {
    return this.classpath;
  }
  
  public void setClasspath(@NonNull final List<String> classpath) {
    this.classpath = classpath;
  }
  
  @Pure
  @NonNull
  public List<String> getJvmOptions() {
    return this.jvmOptions;
  }
  
  public void setJvmOptions(@NonNull final List<String> jvmOptions) {
    this.jvmOptions = jvmOptions;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("classpath", this.classpath);
    b.add("jvmOptions", this.jvmOptions);
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
    JvmEnvironmentEntry other = (JvmEnvironmentEntry) obj;
    if (this.target == null) {
      if (other.target != null)
        return false;
    } else if (!this.target.equals(other.target))
      return false;
    if (this.classpath == null) {
      if (other.classpath != null)
        return false;
    } else if (!this.classpath.equals(other.classpath))
      return false;
    if (this.jvmOptions == null) {
      if (other.jvmOptions != null)
        return false;
    } else if (!this.jvmOptions.equals(other.jvmOptions))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    result = prime * result + ((this.classpath== null) ? 0 : this.classpath.hashCode());
    return prime * result + ((this.jvmOptions== null) ? 0 : this.jvmOptions.hashCode());
  }
}
