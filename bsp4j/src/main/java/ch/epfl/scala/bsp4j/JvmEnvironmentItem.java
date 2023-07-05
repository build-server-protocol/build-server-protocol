package ch.epfl.scala.bsp4j;

import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class JvmEnvironmentItem {
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

  private List<JvmMainClass> mainClasses;

  public JvmEnvironmentItem(@NonNull final BuildTargetIdentifier target, @NonNull final List<String> classpath, @NonNull final List<String> jvmOptions, @NonNull final String workingDirectory, @NonNull final Map<String, String> environmentVariables) {
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
    this.target = Preconditions.checkNotNull(target, "target");
  }

  @Pure
  @NonNull
  public List<String> getClasspath() {
    return this.classpath;
  }

  public void setClasspath(@NonNull final List<String> classpath) {
    this.classpath = Preconditions.checkNotNull(classpath, "classpath");
  }

  @Pure
  @NonNull
  public List<String> getJvmOptions() {
    return this.jvmOptions;
  }

  public void setJvmOptions(@NonNull final List<String> jvmOptions) {
    this.jvmOptions = Preconditions.checkNotNull(jvmOptions, "jvmOptions");
  }

  @Pure
  @NonNull
  public String getWorkingDirectory() {
    return this.workingDirectory;
  }

  public void setWorkingDirectory(@NonNull final String workingDirectory) {
    this.workingDirectory = Preconditions.checkNotNull(workingDirectory, "workingDirectory");
  }

  @Pure
  @NonNull
  public Map<String, String> getEnvironmentVariables() {
    return this.environmentVariables;
  }

  public void setEnvironmentVariables(@NonNull final Map<String, String> environmentVariables) {
    this.environmentVariables = Preconditions.checkNotNull(environmentVariables, "environmentVariables");
  }

  @Pure
  public List<JvmMainClass> getMainClasses() {
    return this.mainClasses;
  }

  public void setMainClasses(final List<JvmMainClass> mainClasses) {
    this.mainClasses = mainClasses;
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
    b.add("mainClasses", this.mainClasses);
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
    JvmEnvironmentItem other = (JvmEnvironmentItem) obj;
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
    if (this.mainClasses == null) {
      if (other.mainClasses != null)
        return false;
    } else if (!this.mainClasses.equals(other.mainClasses))
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
    result = prime * result + ((this.environmentVariables== null) ? 0 : this.environmentVariables.hashCode());
    return prime * result + ((this.mainClasses== null) ? 0 : this.mainClasses.hashCode());
  }
}
