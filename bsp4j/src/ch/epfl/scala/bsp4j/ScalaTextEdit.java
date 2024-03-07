package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/**
 * A textual edit applicable to a text document.
 */
@SuppressWarnings("all")
public class ScalaTextEdit {
  @NonNull
  private Range range;

  @NonNull
  private String newText;

  public ScalaTextEdit(@NonNull final Range range, @NonNull final String newText) {
    this.range = range;
    this.newText = newText;
  }

  @NonNull
  public Range getRange() {
    return this.range;
  }

  public void setRange(@NonNull final Range range) {
    this.range = Preconditions.checkNotNull(range, "range");
  }

  @NonNull
  public String getNewText() {
    return this.newText;
  }

  public void setNewText(@NonNull final String newText) {
    this.newText = Preconditions.checkNotNull(newText, "newText");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("range", this.range);
    b.add("newText", this.newText);
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
    ScalaTextEdit other = (ScalaTextEdit) obj;
    if (this.range == null) {
      if (other.range != null)
        return false;
    } else if (!this.range.equals(other.range))
      return false;
    if (this.newText == null) {
      if (other.newText != null)
        return false;
    } else if (!this.newText.equals(other.newText))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.range== null) ? 0 : this.range.hashCode());
    return prime * result + ((this.newText== null) ? 0 : this.newText.hashCode());
  }
}
