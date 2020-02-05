package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.BuildTargetIdentifier;
import java.util.List;
import java.util.Map;
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
  
  @NonNull
  private String workingDirectory;
  
  @NonNull
  private Map<String, String> environmentVariables;
  
  public JvmEnvironmentEntry(@NonNull final BuildTargetIdentifier target, @NonNull final List<String> classpath, @NonNull final List<String> jvmOptions, @NonNull final String workingDirectory, @NonNull final Map<String, String> environmentVariables) {
    this.target = target;
    this.classpath = classpath;
    this.jvmOptions = jvmOptions;
    this.workingDirectory = workingDirectory;
    this.environmentVariables = environmentVariables;
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
  
  @Pure
  @NonNull
  public String getWorkingDirectory() {
    return this.workingDirectory;
  }
  
  public void setWorkingDirectory(@NonNull final String workingDirectory) {
    this.workingDirectory = workingDirectory;
  }
  
  @Pure
  @NonNull
  public Map<String, String> getEnvironmentVariables() {
    return this.environmentVariables;
  }
  
  public void setEnvironmentVariables(@NonNull final Map<String, String> environmentVariables) {
    this.environmentVariables = environmentVariables;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("classpath", this.classpath);
    b.add("jvmOptions", this.jvmOptions);
    b.add("workingDirectory", this.workingDirectory);
    b.add("environmentVariables", this.environmentVariables);
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
    if (this.workingDirectory == null) {
      if (other.workingDirectory != null)
        return false;
    } else if (!this.workingDirectory.equals(other.workingDirectory))
      return false;
    if (this.environmentVariables == null) {
      if (other.environmentVariables != null)
        return false;
    } else if (!this.environmentVariables.equals(other.environmentVariables))
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
    result = prime * result + ((this.jvmOptions== null) ? 0 : this.jvmOptions.hashCode());
    result = prime * result + ((this.workingDirectory== null) ? 0 : this.workingDirectory.hashCode());
    return prime * result + ((this.environmentVariables== null) ? 0 : this.environmentVariables.hashCode());
  }
}
