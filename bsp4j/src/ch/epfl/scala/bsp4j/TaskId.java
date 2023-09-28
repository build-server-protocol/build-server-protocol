package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * The Task Id allows clients to _uniquely_ identify a BSP task and establish a client-parent
 * relationship with another task id.
 */
@SuppressWarnings("all")
public class TaskId {
  @NonNull private String id;

  private List<String> parents;

  public TaskId(@NonNull final String id) {
    this.id = id;
  }

  @Pure
  @NonNull
  public String getId() {
    return this.id;
  }

  public void setId(@NonNull final String id) {
    this.id = Preconditions.checkNotNull(id, "id");
  }

  @Pure
  public List<String> getParents() {
    return this.parents;
  }

  public void setParents(final List<String> parents) {
    this.parents = parents;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("id", this.id);
    b.add("parents", this.parents);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    TaskId other = (TaskId) obj;
    if (this.id == null) {
      if (other.id != null) return false;
    } else if (!this.id.equals(other.id)) return false;
    if (this.parents == null) {
      if (other.parents != null) return false;
    } else if (!this.parents.equals(other.parents)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
    return prime * result + ((this.parents == null) ? 0 : this.parents.hashCode());
  }
}
