package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import java.util.List;
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/** Diagnostic is defined as it is in the LSP. */
@SuppressWarnings("all")
public class Diagnostic {
  @NonNull private Range range;

  private DiagnosticSeverity severity;

  private Either<String, Integer> code;

  private CodeDescription codeDescription;

  private String source;

  @NonNull private String message;

  private List<Integer> tags;

  private List<DiagnosticRelatedInformation> relatedInformation;

  private String dataKind;

  @JsonAdapter(JsonElementTypeAdapter.Factory.class)
  private Object data;

  public Diagnostic(@NonNull final Range range, @NonNull final String message) {
    this.range = range;
    this.message = message;
  }

  @Pure
  @NonNull
  public Range getRange() {
    return this.range;
  }

  public void setRange(@NonNull final Range range) {
    this.range = Preconditions.checkNotNull(range, "range");
  }

  @Pure
  public DiagnosticSeverity getSeverity() {
    return this.severity;
  }

  public void setSeverity(final DiagnosticSeverity severity) {
    this.severity = severity;
  }

  @Pure
  public Either<String, Integer> getCode() {
    return this.code;
  }

  public void setCode(final Either<String, Integer> code) {
    this.code = code;
  }

  public void setCode(final String code) {
    if (code == null) {
      this.code = null;
      return;
    }
    this.code = Either.forLeft(code);
  }

  public void setCode(final Integer code) {
    if (code == null) {
      this.code = null;
      return;
    }
    this.code = Either.forRight(code);
  }

  @Pure
  public CodeDescription getCodeDescription() {
    return this.codeDescription;
  }

  public void setCodeDescription(final CodeDescription codeDescription) {
    this.codeDescription = codeDescription;
  }

  @Pure
  public String getSource() {
    return this.source;
  }

  public void setSource(final String source) {
    this.source = source;
  }

  @Pure
  @NonNull
  public String getMessage() {
    return this.message;
  }

  public void setMessage(@NonNull final String message) {
    this.message = Preconditions.checkNotNull(message, "message");
  }

  @Pure
  public List<Integer> getTags() {
    return this.tags;
  }

  public void setTags(final List<Integer> tags) {
    this.tags = tags;
  }

  @Pure
  public List<DiagnosticRelatedInformation> getRelatedInformation() {
    return this.relatedInformation;
  }

  public void setRelatedInformation(final List<DiagnosticRelatedInformation> relatedInformation) {
    this.relatedInformation = relatedInformation;
  }

  @Pure
  public String getDataKind() {
    return this.dataKind;
  }

  public void setDataKind(final String dataKind) {
    this.dataKind = dataKind;
  }

  @Pure
  public Object getData() {
    return this.data;
  }

  public void setData(final Object data) {
    this.data = data;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("range", this.range);
    b.add("severity", this.severity);
    b.add("code", this.code);
    b.add("codeDescription", this.codeDescription);
    b.add("source", this.source);
    b.add("message", this.message);
    b.add("tags", this.tags);
    b.add("relatedInformation", this.relatedInformation);
    b.add("dataKind", this.dataKind);
    b.add("data", this.data);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Diagnostic other = (Diagnostic) obj;
    if (this.range == null) {
      if (other.range != null) return false;
    } else if (!this.range.equals(other.range)) return false;
    if (this.severity == null) {
      if (other.severity != null) return false;
    } else if (!this.severity.equals(other.severity)) return false;
    if (this.code == null) {
      if (other.code != null) return false;
    } else if (!this.code.equals(other.code)) return false;
    if (this.codeDescription == null) {
      if (other.codeDescription != null) return false;
    } else if (!this.codeDescription.equals(other.codeDescription)) return false;
    if (this.source == null) {
      if (other.source != null) return false;
    } else if (!this.source.equals(other.source)) return false;
    if (this.message == null) {
      if (other.message != null) return false;
    } else if (!this.message.equals(other.message)) return false;
    if (this.tags == null) {
      if (other.tags != null) return false;
    } else if (!this.tags.equals(other.tags)) return false;
    if (this.relatedInformation == null) {
      if (other.relatedInformation != null) return false;
    } else if (!this.relatedInformation.equals(other.relatedInformation)) return false;
    if (this.dataKind == null) {
      if (other.dataKind != null) return false;
    } else if (!this.dataKind.equals(other.dataKind)) return false;
    if (this.data == null) {
      if (other.data != null) return false;
    } else if (!this.data.equals(other.data)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.range == null) ? 0 : this.range.hashCode());
    result = prime * result + ((this.severity == null) ? 0 : this.severity.hashCode());
    result = prime * result + ((this.code == null) ? 0 : this.code.hashCode());
    result =
        prime * result + ((this.codeDescription == null) ? 0 : this.codeDescription.hashCode());
    result = prime * result + ((this.source == null) ? 0 : this.source.hashCode());
    result = prime * result + ((this.message == null) ? 0 : this.message.hashCode());
    result = prime * result + ((this.tags == null) ? 0 : this.tags.hashCode());
    result =
        prime * result
            + ((this.relatedInformation == null) ? 0 : this.relatedInformation.hashCode());
    result = prime * result + ((this.dataKind == null) ? 0 : this.dataKind.hashCode());
    return prime * result + ((this.data == null) ? 0 : this.data.hashCode());
  }
}
