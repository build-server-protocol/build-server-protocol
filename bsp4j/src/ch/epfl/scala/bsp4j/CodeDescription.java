package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Structure to capture a description for an error code.
 */
@SuppressWarnings("all")
public class CodeDescription {
  @NonNull
  private String href;

  public CodeDescription(@NonNull final String href) {
    this.href = href;
  }

  @Pure
  @NonNull
  public String getHref() {
    return this.href;
  }

  public void setHref(@NonNull final String href) {
    this.href = Preconditions.checkNotNull(href, "href");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("href", this.href);
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
    CodeDescription other = (CodeDescription) obj;
    if (this.href == null) {
      if (other.href != null)
        return false;
    } else if (!this.href.equals(other.href))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.href== null) ? 0 : this.href.hashCode());
  }
}
