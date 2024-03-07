package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

@SuppressWarnings("all")
public class ScalaMainClassesResult {
  @NonNull
  private List<ScalaMainClassesItem> items;

  private String originId;

  public ScalaMainClassesResult(@NonNull final List<ScalaMainClassesItem> items) {
    this.items = items;
  }

  @NonNull
  public List<ScalaMainClassesItem> getItems() {
    return this.items;
  }

  public void setItems(@NonNull final List<ScalaMainClassesItem> items) {
    this.items = Preconditions.checkNotNull(items, "items");
  }

  public String getOriginId() {
    return this.originId;
  }

  public void setOriginId(final String originId) {
    this.originId = originId;
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("items", this.items);
    b.add("originId", this.originId);
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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.items== null) ? 0 : this.items.hashCode());
    return prime * result + ((this.originId== null) ? 0 : this.originId.hashCode());
  }
}
