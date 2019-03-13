package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.BuildTargetIdentifier;
import ch.epfl.scala.bsp4j.Diagnostic;
import ch.epfl.scala.bsp4j.TextDocumentIdentifier;
import java.util.List;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class PublishDiagnosticsParams {
  @NonNull
  private TextDocumentIdentifier textDocument;
  
  @NonNull
  private BuildTargetIdentifier buildTarget;
  
  @NonNull
  private List<Diagnostic> diagnostics;
  
  private String originId;
  
  public PublishDiagnosticsParams(@NonNull final TextDocumentIdentifier textDocument, @NonNull final BuildTargetIdentifier buildTarget, @NonNull final List<Diagnostic> diagnostics) {
    this.textDocument = textDocument;
    this.buildTarget = buildTarget;
    this.diagnostics = diagnostics;
  }
  
  @Pure
  @NonNull
  public TextDocumentIdentifier getTextDocument() {
    return this.textDocument;
  }
  
  public void setTextDocument(@NonNull final TextDocumentIdentifier textDocument) {
    this.textDocument = textDocument;
  }
  
  @Pure
  @NonNull
  public BuildTargetIdentifier getBuildTarget() {
    return this.buildTarget;
  }
  
  public void setBuildTarget(@NonNull final BuildTargetIdentifier buildTarget) {
    this.buildTarget = buildTarget;
  }
  
  @Pure
  @NonNull
  public List<Diagnostic> getDiagnostics() {
    return this.diagnostics;
  }
  
  public void setDiagnostics(@NonNull final List<Diagnostic> diagnostics) {
    this.diagnostics = diagnostics;
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
    b.add("textDocument", this.textDocument);
    b.add("buildTarget", this.buildTarget);
    b.add("diagnostics", this.diagnostics);
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
    PublishDiagnosticsParams other = (PublishDiagnosticsParams) obj;
    if (this.textDocument == null) {
      if (other.textDocument != null)
        return false;
    } else if (!this.textDocument.equals(other.textDocument))
      return false;
    if (this.buildTarget == null) {
      if (other.buildTarget != null)
        return false;
    } else if (!this.buildTarget.equals(other.buildTarget))
      return false;
    if (this.diagnostics == null) {
      if (other.diagnostics != null)
        return false;
    } else if (!this.diagnostics.equals(other.diagnostics))
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
    result = prime * result + ((this.textDocument== null) ? 0 : this.textDocument.hashCode());
    result = prime * result + ((this.buildTarget== null) ? 0 : this.buildTarget.hashCode());
    result = prime * result + ((this.diagnostics== null) ? 0 : this.diagnostics.hashCode());
    return prime * result + ((this.originId== null) ? 0 : this.originId.hashCode());
  }
}
