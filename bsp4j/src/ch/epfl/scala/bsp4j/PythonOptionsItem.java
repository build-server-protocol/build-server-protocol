package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class PythonOptionsItem {
  @NonNull
  private BuildTargetIdentifier target;

  @NonNull
  private List<String> interpreterOptions;

  public PythonOptionsItem(@NonNull final BuildTargetIdentifier target, @NonNull final List<String> interpreterOptions) {
    this.target = target;
    this.interpreterOptions = interpreterOptions;
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
  public List<String> getInterpreterOptions() {
    return this.interpreterOptions;
  }

  public void setInterpreterOptions(@NonNull final List<String> interpreterOptions) {
    this.interpreterOptions = Preconditions.checkNotNull(interpreterOptions, "interpreterOptions");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("interpreterOptions", this.interpreterOptions);
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
    PythonOptionsItem other = (PythonOptionsItem) obj;
    if (this.target == null) {
      if (other.target != null)
        return false;
    } else if (!this.target.equals(other.target))
      return false;
    if (this.interpreterOptions == null) {
      if (other.interpreterOptions != null)
        return false;
    } else if (!this.interpreterOptions.equals(other.interpreterOptions))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    return prime * result + ((this.interpreterOptions== null) ? 0 : this.interpreterOptions.hashCode());
  }
}
