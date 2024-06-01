package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

@SuppressWarnings("all")
public class PublishDiagnosticsParams {
  @NonNull private TextDocumentIdentifier textDocument;

  @NonNull private BuildTargetIdentifier buildTarget;

  private String originId;

  @NonNull private List<Diagnostic> diagnostics;

  @NonNull private Boolean reset;

  public PublishDiagnosticsParams(
      @NonNull final TextDocumentIdentifier textDocument,
      @NonNull final BuildTargetIdentifier buildTarget,
      @NonNull final List<Diagnostic> diagnostics,
      @NonNull final Boolean reset) {
    this.textDocument = textDocument;
    this.buildTarget = buildTarget;
    this.diagnostics = diagnostics;
    this.reset = reset;
  }

  @NonNull
  public TextDocumentIdentifier getTextDocument() {
    return this.textDocument;
  }

  public void setTextDocument(@NonNull final TextDocumentIdentifier textDocument) {
    this.textDocument = Preconditions.checkNotNull(textDocument, "textDocument");
  }

  @NonNull
  public BuildTargetIdentifier getBuildTarget() {
    return this.buildTarget;
  }

  public void setBuildTarget(@NonNull final BuildTargetIdentifier buildTarget) {
    this.buildTarget = Preconditions.checkNotNull(buildTarget, "buildTarget");
  }

  public String getOriginId() {
    return this.originId;
  }

  public void setOriginId(final String originId) {
    this.originId = originId;
  }

  @NonNull
  public List<Diagnostic> getDiagnostics() {
    return this.diagnostics;
  }

  public void setDiagnostics(@NonNull final List<Diagnostic> diagnostics) {
    this.diagnostics = Preconditions.checkNotNull(diagnostics, "diagnostics");
  }

  @NonNull
  public Boolean getReset() {
    return this.reset;
  }

  public void setReset(@NonNull final Boolean reset) {
    this.reset = Preconditions.checkNotNull(reset, "reset");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("textDocument", this.textDocument);
    b.add("buildTarget", this.buildTarget);
    b.add("originId", this.originId);
    b.add("diagnostics", this.diagnostics);
    b.add("reset", this.reset);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PublishDiagnosticsParams other = (PublishDiagnosticsParams) obj;
    if (this.textDocument == null) {
      if (other.textDocument != null) return false;
    } else if (!this.textDocument.equals(other.textDocument)) return false;
    if (this.buildTarget == null) {
      if (other.buildTarget != null) return false;
    } else if (!this.buildTarget.equals(other.buildTarget)) return false;
    if (this.originId == null) {
      if (other.originId != null) return false;
    } else if (!this.originId.equals(other.originId)) return false;
    if (this.diagnostics == null) {
      if (other.diagnostics != null) return false;
    } else if (!this.diagnostics.equals(other.diagnostics)) return false;
    if (this.reset == null) {
      if (other.reset != null) return false;
    } else if (!this.reset.equals(other.reset)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.textDocument == null) ? 0 : this.textDocument.hashCode());
    result = prime * result + ((this.buildTarget == null) ? 0 : this.buildTarget.hashCode());
    result = prime * result + ((this.originId == null) ? 0 : this.originId.hashCode());
    result = prime * result + ((this.diagnostics == null) ? 0 : this.diagnostics.hashCode());
    return prime * result + ((this.reset == null) ? 0 : this.reset.hashCode());
  }
}
