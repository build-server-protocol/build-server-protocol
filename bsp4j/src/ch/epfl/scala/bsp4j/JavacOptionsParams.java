package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

@SuppressWarnings("all")
public class JavacOptionsParams {
  @NonNull private List<BuildTargetIdentifier> targets;

  public JavacOptionsParams(@NonNull final List<BuildTargetIdentifier> targets) {
    this.targets = targets;
  }

  @NonNull
  public List<BuildTargetIdentifier> getTargets() {
    return this.targets;
  }

  public void setTargets(@NonNull final List<BuildTargetIdentifier> targets) {
    this.targets = Preconditions.checkNotNull(targets, "targets");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("targets", this.targets);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    JavacOptionsParams other = (JavacOptionsParams) obj;
    if (this.targets == null) {
      if (other.targets != null) return false;
    } else if (!this.targets.equals(other.targets)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    return 31 * 1 + ((this.targets == null) ? 0 : this.targets.hashCode());
  }
}
