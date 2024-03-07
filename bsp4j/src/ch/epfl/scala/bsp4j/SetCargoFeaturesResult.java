package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/**
 * **Unstable** (may change in future versions)
 */
@SuppressWarnings("all")
public class SetCargoFeaturesResult {
  @NonNull
  private StatusCode statusCode;

  public SetCargoFeaturesResult(@NonNull final StatusCode statusCode) {
    this.statusCode = statusCode;
  }

  @NonNull
  public StatusCode getStatusCode() {
    return this.statusCode;
  }

  public void setStatusCode(@NonNull final StatusCode statusCode) {
    this.statusCode = Preconditions.checkNotNull(statusCode, "statusCode");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("statusCode", this.statusCode);
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
    SetCargoFeaturesResult other = (SetCargoFeaturesResult) obj;
    if (this.statusCode == null) {
      if (other.statusCode != null)
        return false;
    } else if (!this.statusCode.equals(other.statusCode))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    return 31 * 1 + ((this.statusCode== null) ? 0 : this.statusCode.hashCode());
  }
}
