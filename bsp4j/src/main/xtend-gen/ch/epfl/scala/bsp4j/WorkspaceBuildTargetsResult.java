package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class WorkspaceBuildTargetsResult {
  private List<BuildTarget> targets;

  public WorkspaceBuildTargetsResult(@NonNull final List<BuildTarget> targets) {
    this.targets = targets;
  }

  @Pure
  public List<BuildTarget> getTargets() {
    return this.targets;
  }

  public void setTargets(final List<BuildTarget> targets) {
    this.targets = targets;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("targets", this.targets);
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
    WorkspaceBuildTargetsResult other = (WorkspaceBuildTargetsResult) obj;
    if (this.targets == null) {
      if (other.targets != null)
        return false;
    } else if (!this.targets.equals(other.targets))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.targets== null) ? 0 : this.targets.hashCode());
  }
}
