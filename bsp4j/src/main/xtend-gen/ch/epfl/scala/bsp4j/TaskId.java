package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class TaskId {
  @NonNull
  private String id;
  
  private String parent;
  
  public TaskId(@NonNull final String id) {
    this.id = id;
  }
  
  @Pure
  @NonNull
  public String getId() {
    return this.id;
  }
  
  public void setId(@NonNull final String id) {
    this.id = id;
  }
  
  @Pure
  public String getParent() {
    return this.parent;
  }
  
  public void setParent(final String parent) {
    this.parent = parent;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("id", this.id);
    b.add("parent", this.parent);
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
    TaskId other = (TaskId) obj;
    if (this.id == null) {
      if (other.id != null)
        return false;
    } else if (!this.id.equals(other.id))
      return false;
    if (this.parent == null) {
      if (other.parent != null)
        return false;
    } else if (!this.parent.equals(other.parent))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.id== null) ? 0 : this.id.hashCode());
    return prime * result + ((this.parent== null) ? 0 : this.parent.hashCode());
  }
}
