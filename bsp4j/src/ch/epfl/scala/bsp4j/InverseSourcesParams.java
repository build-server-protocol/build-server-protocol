package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

@SuppressWarnings("all")
public class InverseSourcesParams {
  @NonNull
  private TextDocumentIdentifier textDocument;

  public InverseSourcesParams(@NonNull final TextDocumentIdentifier textDocument) {
    this.textDocument = textDocument;
  }

  @NonNull
  public TextDocumentIdentifier getTextDocument() {
    return this.textDocument;
  }

  public void setTextDocument(@NonNull final TextDocumentIdentifier textDocument) {
    this.textDocument = Preconditions.checkNotNull(textDocument, "textDocument");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("textDocument", this.textDocument);
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
    InverseSourcesParams other = (InverseSourcesParams) obj;
    if (this.textDocument == null) {
      if (other.textDocument != null)
        return false;
    } else if (!this.textDocument.equals(other.textDocument))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    return 31 * 1 + ((this.textDocument== null) ? 0 : this.textDocument.hashCode());
  }
}
