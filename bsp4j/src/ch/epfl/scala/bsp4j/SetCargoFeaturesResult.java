package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class SetCargoFeaturesResult {
  @NonNull private StatusCode statusCode;

  public SetCargoFeaturesResult(@NonNull final StatusCode statusCode) {
    this.statusCode = statusCode;
  }

  @Pure
  @NonNull
  public StatusCode getStatusCode() {
    return this.statusCode;
  }

  public void setStatusCode(@NonNull final StatusCode statusCode) {
    this.statusCode = Preconditions.checkNotNull(statusCode, "statusCode");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("statusCode", this.statusCode);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SetCargoFeaturesResult other = (SetCargoFeaturesResult) obj;
    if (this.statusCode == null) {
      if (other.statusCode != null) return false;
    } else if (!this.statusCode.equals(other.statusCode)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    return 31 * 1 + ((this.statusCode == null) ? 0 : this.statusCode.hashCode());
  }
}
