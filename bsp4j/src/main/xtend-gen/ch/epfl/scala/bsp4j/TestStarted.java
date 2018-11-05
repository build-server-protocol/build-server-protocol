package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.Location;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class TestStarted {
  @NonNull
  private String description;
  
  private Location location;
  
  @Pure
  @NonNull
  public String getDescription() {
    return this.description;
  }
  
  public void setDescription(@NonNull final String description) {
    this.description = description;
  }
  
  @Pure
  public Location getLocation() {
    return this.location;
  }
  
  public void setLocation(final Location location) {
    this.location = location;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("description", this.description);
    b.add("location", this.location);
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
    TestStarted other = (TestStarted) obj;
    if (this.description == null) {
      if (other.description != null)
        return false;
    } else if (!this.description.equals(other.description))
      return false;
    if (this.location == null) {
      if (other.location != null)
        return false;
    } else if (!this.location.equals(other.location))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.description== null) ? 0 : this.description.hashCode());
    return prime * result + ((this.location== null) ? 0 : this.location.hashCode());
  }
}
