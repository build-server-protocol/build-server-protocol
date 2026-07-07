package ch.epfl.scala.bsp4j;

import java.util.List;
import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

@SuppressWarnings("all")
public class DestinationsResult {
  @NonNull private List<Destination> destinations;

  public DestinationsResult(@NonNull final List<Destination> destinations) {
    this.destinations = destinations;
  }

  @NonNull
  public List<Destination> getDestinations() {
    return this.destinations;
  }

  public void setDestinations(@NonNull final List<Destination> destinations) {
    this.destinations = Preconditions.checkNotNull(destinations, "destinations");
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("destinations", this.destinations);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    DestinationsResult other = (DestinationsResult) obj;
    if (this.destinations == null) {
      if (other.destinations != null) return false;
    } else if (!this.destinations.equals(other.destinations)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    return 31 * 1 + ((this.destinations == null) ? 0 : this.destinations.hashCode());
  }
}
