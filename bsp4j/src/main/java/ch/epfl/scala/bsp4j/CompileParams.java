package ch.epfl.scala.bsp4j;

import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class CompileParams {
  @NonNull
  private List<BuildTargetIdentifier> targets;

  private String originId;

  private List<String> arguments;

  private Map<String, String> environmentVariables;

  public CompileParams(@NonNull final List<BuildTargetIdentifier> targets) {
    this.targets = targets;
  }

  @Pure
  @NonNull
  public List<BuildTargetIdentifier> getTargets() {
    return this.targets;
  }

  public void setTargets(@NonNull final List<BuildTargetIdentifier> targets) {
    this.targets = Preconditions.checkNotNull(targets, "targets");
  }

  @Pure
  public String getOriginId() {
    return this.originId;
  }

  public void setOriginId(final String originId) {
    this.originId = originId;
  }

  @Pure
  public List<String> getArguments() {
    return this.arguments;
  }

  public void setArguments(final List<String> arguments) {
    this.arguments = arguments;
  }

  @Pure
  public Map<String, String> getEnvironmentVariables() {
    return this.environmentVariables;
  }

  public void setEnvironmentVariables(final Map<String, String> environmentVariables) {
    this.environmentVariables = environmentVariables;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("targets", this.targets);
    b.add("originId", this.originId);
    b.add("arguments", this.arguments);
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
    CompileParams other = (CompileParams) obj;
    if (this.targets == null) {
      if (other.targets != null)
        return false;
    } else if (!this.targets.equals(other.targets))
      return false;
    if (this.originId == null) {
      if (other.originId != null)
        return false;
    } else if (!this.originId.equals(other.originId))
      return false;
    if (this.arguments == null) {
      if (other.arguments != null)
        return false;
    } else if (!this.arguments.equals(other.arguments))
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
    result = prime * result + ((this.targets== null) ? 0 : this.targets.hashCode());
    result = prime * result + ((this.originId== null) ? 0 : this.originId.hashCode());
    result = prime * result + ((this.arguments== null) ? 0 : this.arguments.hashCode());
    return prime * result + ((this.environmentVariables== null) ? 0 : this.environmentVariables.hashCode());
  }
}
