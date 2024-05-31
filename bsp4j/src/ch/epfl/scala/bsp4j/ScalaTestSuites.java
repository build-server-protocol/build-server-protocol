package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class ScalaTestSuites {
  @NonNull private List<ScalaTestSuiteSelection> suites;

  @NonNull private List<String> jvmOptions;

  @NonNull private List<String> environmentVariables;

  public ScalaTestSuites(
      @NonNull final List<ScalaTestSuiteSelection> suites,
      @NonNull final List<String> jvmOptions,
      @NonNull final List<String> environmentVariables) {
    this.suites = suites;
    this.jvmOptions = jvmOptions;
    this.environmentVariables = environmentVariables;
  }

  @Pure
  @NonNull
  public List<ScalaTestSuiteSelection> getSuites() {
    return this.suites;
  }

  public void setSuites(@NonNull final List<ScalaTestSuiteSelection> suites) {
    this.suites = Preconditions.checkNotNull(suites, "suites");
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
  public List<String> getEnvironmentVariables() {
    return this.environmentVariables;
  }

  public void setEnvironmentVariables(@NonNull final List<String> environmentVariables) {
    this.environmentVariables =
        Preconditions.checkNotNull(environmentVariables, "environmentVariables");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("suites", this.suites);
    b.add("jvmOptions", this.jvmOptions);
    b.add("environmentVariables", this.environmentVariables);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ScalaTestSuites other = (ScalaTestSuites) obj;
    if (this.suites == null) {
      if (other.suites != null) return false;
    } else if (!this.suites.equals(other.suites)) return false;
    if (this.jvmOptions == null) {
      if (other.jvmOptions != null) return false;
    } else if (!this.jvmOptions.equals(other.jvmOptions)) return false;
    if (this.environmentVariables == null) {
      if (other.environmentVariables != null) return false;
    } else if (!this.environmentVariables.equals(other.environmentVariables)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.suites == null) ? 0 : this.suites.hashCode());
    result = prime * result + ((this.jvmOptions == null) ? 0 : this.jvmOptions.hashCode());
    return prime * result
        + ((this.environmentVariables == null) ? 0 : this.environmentVariables.hashCode());
  }
}
