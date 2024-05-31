package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class TestStart {
  @NonNull private String displayName;

  private Location location;

  public TestStart(@NonNull final String displayName) {
    this.displayName = displayName;
  }

  @Pure
  @NonNull
  public String getDisplayName() {
    return this.displayName;
  }

  public void setDisplayName(@NonNull final String displayName) {
    this.displayName = Preconditions.checkNotNull(displayName, "displayName");
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
    b.add("displayName", this.displayName);
    b.add("location", this.location);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    TestStart other = (TestStart) obj;
    if (this.displayName == null) {
      if (other.displayName != null) return false;
    } else if (!this.displayName.equals(other.displayName)) return false;
    if (this.location == null) {
      if (other.location != null) return false;
    } else if (!this.location.equals(other.location)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.displayName == null) ? 0 : this.displayName.hashCode());
    return prime * result + ((this.location == null) ? 0 : this.location.hashCode());
  }
}
