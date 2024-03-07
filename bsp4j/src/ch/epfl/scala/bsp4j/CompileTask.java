package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/**
 * The beginning of a compilation unit may be signalled to the client with a
 * `build/taskStart` notification. When the compilation unit is a build target, the
 * notification's `dataKind` field must be "compile-task" and the `data` field must
 * include a `CompileTask` object:
 */
@SuppressWarnings("all")
public class CompileTask {
  @NonNull
  private BuildTargetIdentifier target;

  public CompileTask(@NonNull final BuildTargetIdentifier target) {
    this.target = target;
  }

  @NonNull
  public BuildTargetIdentifier getTarget() {
    return this.target;
  }

  public void setTarget(@NonNull final BuildTargetIdentifier target) {
    this.target = Preconditions.checkNotNull(target, "target");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CompileTask other = (CompileTask) obj;
    if (this.target == null) {
      if (other.target != null)
        return false;
    } else if (!this.target.equals(other.target))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    return 31 * 1 + ((this.target== null) ? 0 : this.target.hashCode());
  }
}
