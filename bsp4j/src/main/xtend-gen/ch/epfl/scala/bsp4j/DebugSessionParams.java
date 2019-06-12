package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.BuildTargetIdentifier;
import ch.epfl.scala.bsp4j.LaunchParameters;
import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class DebugSessionParams {
  @NonNull
  private List<BuildTargetIdentifier> targets;
  
  @NonNull
  private LaunchParameters parameters;
  
  public DebugSessionParams(@NonNull final List<BuildTargetIdentifier> targets, final LaunchParameters parameters) {
    this.targets = targets;
    this.parameters = parameters;
  }
  
  @Pure
  @NonNull
  public List<BuildTargetIdentifier> getTargets() {
    return this.targets;
  }
  
  public void setTargets(@NonNull final List<BuildTargetIdentifier> targets) {
    this.targets = targets;
  }
  
  @Pure
  @NonNull
  public LaunchParameters getParameters() {
    return this.parameters;
  }
  
  public void setParameters(@NonNull final LaunchParameters parameters) {
    this.parameters = parameters;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("targets", this.targets);
    b.add("parameters", this.parameters);
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
    DebugSessionParams other = (DebugSessionParams) obj;
    if (this.targets == null) {
      if (other.targets != null)
        return false;
    } else if (!this.targets.equals(other.targets))
      return false;
    if (this.parameters == null) {
      if (other.parameters != null)
        return false;
    } else if (!this.parameters.equals(other.parameters))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.targets== null) ? 0 : this.targets.hashCode());
    return prime * result + ((this.parameters== null) ? 0 : this.parameters.hashCode());
  }
}
