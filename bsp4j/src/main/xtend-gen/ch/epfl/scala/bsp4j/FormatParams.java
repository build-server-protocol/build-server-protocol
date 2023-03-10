package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class FormatParams {
  @NonNull
  private List<FormatItem> formatItems;

  public FormatParams(@NonNull final List<FormatItem> formatItems) {
    this.formatItems = formatItems;
  }

  @Pure
  @NonNull
  public List<FormatItem> getFormatItems() {
    return this.formatItems;
  }

  public void setFormatItems(@NonNull final List<FormatItem> formatItems) {
    this.formatItems = Preconditions.checkNotNull(formatItems, "formatItems");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("formatItems", this.formatItems);
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
    FormatParams other = (FormatParams) obj;
    if (this.formatItems == null) {
      if (other.formatItems != null)
        return false;
    } else if (!this.formatItems.equals(other.formatItems))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.formatItems== null) ? 0 : this.formatItems.hashCode());
  }
}
