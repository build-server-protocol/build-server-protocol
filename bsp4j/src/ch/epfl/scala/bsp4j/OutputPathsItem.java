package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class OutputPathsItem {
  @NonNull
  private BuildTargetIdentifier target;

  @NonNull
  private List<OutputPathItem> outputPaths;

  public OutputPathsItem(@NonNull final BuildTargetIdentifier target, @NonNull final List<OutputPathItem> outputPaths) {
    this.target = target;
    this.outputPaths = outputPaths;
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
  public List<OutputPathItem> getOutputPaths() {
    return this.outputPaths;
  }

  public void setOutputPaths(@NonNull final List<OutputPathItem> outputPaths) {
    this.outputPaths = Preconditions.checkNotNull(outputPaths, "outputPaths");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("outputPaths", this.outputPaths);
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
    OutputPathsItem other = (OutputPathsItem) obj;
    if (this.target == null) {
      if (other.target != null)
        return false;
    } else if (!this.target.equals(other.target))
      return false;
    if (this.outputPaths == null) {
      if (other.outputPaths != null)
        return false;
    } else if (!this.outputPaths.equals(other.outputPaths))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    return prime * result + ((this.outputPaths== null) ? 0 : this.outputPaths.hashCode());
  }
}
