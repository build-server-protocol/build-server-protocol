package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class LaunchParameters {
  @NonNull
  private String dataKind;
  
  @JsonAdapter(JsonElementTypeAdapter.Factory.class)
  private Object data;
  
  public LaunchParameters(@NonNull final String dataKind, final Object data) {
    this.dataKind = dataKind;
    this.data = data;
  }
  
  @Pure
  @NonNull
  public String getDataKind() {
    return this.dataKind;
  }
  
  public void setDataKind(@NonNull final String dataKind) {
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
    LaunchParameters other = (LaunchParameters) obj;
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
    result = prime * result + ((this.dataKind== null) ? 0 : this.dataKind.hashCode());
    return prime * result + ((this.data== null) ? 0 : this.data.hashCode());
  }
}
