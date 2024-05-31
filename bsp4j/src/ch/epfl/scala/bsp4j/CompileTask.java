package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * The beginning of a compilation unit may be signalled to the client with a `build/taskStart`
 * notification. When the compilation unit is a build target, the notification's `dataKind` field
 * must be "compile-task" and the `data` field must include a `CompileTask` object:
 */
@SuppressWarnings("all")
public class CompileTask {
  @NonNull private BuildTargetIdentifier target;

  public CompileTask(@NonNull final BuildTargetIdentifier target) {
    this.target = target;
  }

  @Pure
  @NonNull
  public BuildTargetIdentifier getTarget() {
    return this.target;
  }

  public void setTarget(@NonNull final BuildTargetIdentifier target) {
    this.target = Preconditions.checkNotNull(target, "target");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CompileTask other = (CompileTask) obj;
    if (this.target == null) {
      if (other.target != null) return false;
    } else if (!this.target.equals(other.target)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.target == null) ? 0 : this.target.hashCode());
  }
}
