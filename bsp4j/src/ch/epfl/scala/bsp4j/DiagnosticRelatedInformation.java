package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Represents a related message and source code location for a diagnostic. This should be used to
 * point to code locations that cause or are related to a diagnostics, e.g when duplicating a symbol
 * in a scope.
 */
@SuppressWarnings("all")
public class DiagnosticRelatedInformation {
  @NonNull private Location location;

  @NonNull private String message;

  public DiagnosticRelatedInformation(
      @NonNull final Location location, @NonNull final String message) {
    this.location = location;
    this.message = message;
  }

  @Pure
  @NonNull
  public Location getLocation() {
    return this.location;
  }

  public void setLocation(@NonNull final Location location) {
    this.location = Preconditions.checkNotNull(location, "location");
  }

  @Pure
  @NonNull
  public String getMessage() {
    return this.message;
  }

  public void setMessage(@NonNull final String message) {
    this.message = Preconditions.checkNotNull(message, "message");
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("location", this.location);
    b.add("message", this.message);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    DiagnosticRelatedInformation other = (DiagnosticRelatedInformation) obj;
    if (this.location == null) {
      if (other.location != null) return false;
    } else if (!this.location.equals(other.location)) return false;
    if (this.message == null) {
      if (other.message != null) return false;
    } else if (!this.message.equals(other.message)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.location == null) ? 0 : this.location.hashCode());
    return prime * result + ((this.message == null) ? 0 : this.message.hashCode());
  }
}
