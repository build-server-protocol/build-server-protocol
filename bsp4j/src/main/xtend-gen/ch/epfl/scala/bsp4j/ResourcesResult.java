package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class ResourcesResult {
  @NonNull
  private List<ResourcesItem> items;

  public ResourcesResult(@NonNull final List<ResourcesItem> items) {
    this.items = items;
  }

  @Pure
  @NonNull
  public List<ResourcesItem> getItems() {
    return this.items;
  }

  public void setItems(@NonNull final List<ResourcesItem> items) {
    this.items = Preconditions.checkNotNull(items, "items");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("items", this.items);
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
    ResourcesResult other = (ResourcesResult) obj;
    if (this.items == null) {
      if (other.items != null)
        return false;
    } else if (!this.items.equals(other.items))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.items== null) ? 0 : this.items.hashCode());
  }
}
