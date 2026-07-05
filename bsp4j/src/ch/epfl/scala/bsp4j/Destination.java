package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

@SuppressWarnings("all")
public class Destination {
  @NonNull private DestinationIdentifier id;

  @NonNull private String displayName;

  public Destination(@NonNull final DestinationIdentifier id, @NonNull final String displayName) {
    this.id = id;
    this.displayName = displayName;
  }

  @NonNull
  public DestinationIdentifier getId() {
    return this.id;
  }

  public void setId(@NonNull final DestinationIdentifier id) {
    this.id = Preconditions.checkNotNull(id, "id");
  }

  @NonNull
  public String getDisplayName() {
    return this.displayName;
  }

  public void setDisplayName(@NonNull final String displayName) {
    this.displayName = Preconditions.checkNotNull(displayName, "displayName");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("id", this.id);
    b.add("displayName", this.displayName);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Destination other = (Destination) obj;
    if (this.id == null) {
      if (other.id != null) return false;
    } else if (!this.id.equals(other.id)) return false;
    if (this.displayName == null) {
      if (other.displayName != null) return false;
    } else if (!this.displayName.equals(other.displayName)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
    return prime * result + ((this.displayName == null) ? 0 : this.displayName.hashCode());
  }
}
