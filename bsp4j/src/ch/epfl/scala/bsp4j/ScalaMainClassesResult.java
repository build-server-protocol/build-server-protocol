package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class ScalaMainClassesResult {
  @NonNull
  private List<ScalaMainClassesItem> items;

  private String originId;

  public ScalaMainClassesResult(@NonNull final List<ScalaMainClassesItem> items) {
    this.items = items;
  }

  @Pure
  @NonNull
  public List<ScalaMainClassesItem> getItems() {
    return this.items;
  }

  public void setItems(@NonNull final List<ScalaMainClassesItem> items) {
    this.items = Preconditions.checkNotNull(items, "items");
  }

  @Pure
  public String getOriginId() {
    return this.originId;
  }

  public void setOriginId(final String originId) {
    this.originId = originId;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("items", this.items);
    b.add("originId", this.originId);
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
    ScalaMainClassesResult other = (ScalaMainClassesResult) obj;
    if (this.items == null) {
      if (other.items != null)
        return false;
    } else if (!this.items.equals(other.items))
      return false;
    if (this.originId == null) {
      if (other.originId != null)
        return false;
    } else if (!this.originId.equals(other.originId))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.items== null) ? 0 : this.items.hashCode());
    return prime * result + ((this.originId== null) ? 0 : this.originId.hashCode());
  }
}
