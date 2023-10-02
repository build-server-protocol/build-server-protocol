package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class TestFinish {
  @NonNull
  private String displayName;

  private String message;

  @NonNull
  private TestStatus status;

  private Location location;

  private String dataKind;

  @JsonAdapter(JsonElementTypeAdapter.Factory.class)
  private Object data;

  public TestFinish(@NonNull final String displayName, @NonNull final TestStatus status) {
    this.displayName = displayName;
    this.status = status;
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
  public String getMessage() {
    return this.message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  @Pure
  @NonNull
  public TestStatus getStatus() {
    return this.status;
  }

  public void setStatus(@NonNull final TestStatus status) {
    this.status = Preconditions.checkNotNull(status, "status");
  }

  @Pure
  public Location getLocation() {
    return this.location;
  }

  public void setLocation(final Location location) {
    this.location = location;
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
    b.add("displayName", this.displayName);
    b.add("message", this.message);
    b.add("status", this.status);
    b.add("location", this.location);
    b.add("dataKind", this.dataKind);
    b.add("data", this.data);
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
    TestFinish other = (TestFinish) obj;
    if (this.displayName == null) {
      if (other.displayName != null)
        return false;
    } else if (!this.displayName.equals(other.displayName))
      return false;
    if (this.message == null) {
      if (other.message != null)
        return false;
    } else if (!this.message.equals(other.message))
      return false;
    if (this.status == null) {
      if (other.status != null)
        return false;
    } else if (!this.status.equals(other.status))
      return false;
    if (this.location == null) {
      if (other.location != null)
        return false;
    } else if (!this.location.equals(other.location))
      return false;
    if (this.dataKind == null) {
      if (other.dataKind != null)
        return false;
    } else if (!this.dataKind.equals(other.dataKind))
      return false;
    if (this.data == null) {
      if (other.data != null)
        return false;
    } else if (!this.data.equals(other.data))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.displayName== null) ? 0 : this.displayName.hashCode());
    result = prime * result + ((this.message== null) ? 0 : this.message.hashCode());
    result = prime * result + ((this.status== null) ? 0 : this.status.hashCode());
    result = prime * result + ((this.location== null) ? 0 : this.location.hashCode());
    result = prime * result + ((this.dataKind== null) ? 0 : this.dataKind.hashCode());
    return prime * result + ((this.data== null) ? 0 : this.data.hashCode());
  }
}
