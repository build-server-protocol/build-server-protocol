package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

@SuppressWarnings("all")
public class OutputPathsItem {
  @NonNull private BuildTargetIdentifier target;

  @NonNull private List<OutputPathItem> outputPaths;

  public OutputPathsItem(
      @NonNull final BuildTargetIdentifier target,
      @NonNull final List<OutputPathItem> outputPaths) {
    this.target = target;
    this.outputPaths = outputPaths;
  }

  @NonNull
  public BuildTargetIdentifier getTarget() {
    return this.target;
  }

  public void setTarget(@NonNull final BuildTargetIdentifier target) {
    this.target = Preconditions.checkNotNull(target, "target");
  }

  @NonNull
  public List<OutputPathItem> getOutputPaths() {
    return this.outputPaths;
  }

  public void setOutputPaths(@NonNull final List<OutputPathItem> outputPaths) {
    this.outputPaths = Preconditions.checkNotNull(outputPaths, "outputPaths");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("outputPaths", this.outputPaths);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    OutputPathsItem other = (OutputPathsItem) obj;
    if (this.target == null) {
      if (other.target != null) return false;
    } else if (!this.target.equals(other.target)) return false;
    if (this.outputPaths == null) {
      if (other.outputPaths != null) return false;
    } else if (!this.outputPaths.equals(other.outputPaths)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target == null) ? 0 : this.target.hashCode());
    return prime * result + ((this.outputPaths == null) ? 0 : this.outputPaths.hashCode());
  }
}
