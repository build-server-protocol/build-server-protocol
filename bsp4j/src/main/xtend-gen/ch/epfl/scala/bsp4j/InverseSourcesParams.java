package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.TextDocumentIdentifier;
import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class InverseSourcesParams {
  @NonNull
  private List<TextDocumentIdentifier> textDocuments;
  
  public InverseSourcesParams(@NonNull final List<TextDocumentIdentifier> textDocuments) {
    this.textDocuments = textDocuments;
  }
  
  @Pure
  @NonNull
  public List<TextDocumentIdentifier> getTextDocuments() {
    return this.textDocuments;
  }
  
  public void setTextDocuments(@NonNull final List<TextDocumentIdentifier> textDocuments) {
    this.textDocuments = textDocuments;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("textDocuments", this.textDocuments);
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
    InverseSourcesParams other = (InverseSourcesParams) obj;
    if (this.textDocuments == null) {
      if (other.textDocuments != null)
        return false;
    } else if (!this.textDocuments.equals(other.textDocuments))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.textDocuments== null) ? 0 : this.textDocuments.hashCode());
  }
}
