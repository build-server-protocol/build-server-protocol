package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import java.util.List;
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class DebugSessionParams {
  @NonNull
  private List<BuildTargetIdentifier> targets;

  private String dataKind;

  @JsonAdapter(JsonElementTypeAdapter.Factory.class)
  private Object data;

  public DebugSessionParams(@NonNull final List<BuildTargetIdentifier> targets) {
    this.targets = targets;
  }

  @Pure
  @NonNull
  public List<BuildTargetIdentifier> getTargets() {
    return this.targets;
  }

  public void setTargets(@NonNull final List<BuildTargetIdentifier> targets) {
    this.targets = Preconditions.checkNotNull(targets, "targets");
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
    b.add("targets", this.targets);
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
    DebugSessionParams other = (DebugSessionParams) obj;
    if (this.targets == null) {
      if (other.targets != null)
        return false;
    } else if (!this.targets.equals(other.targets))
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
    result = prime * result + ((this.targets== null) ? 0 : this.targets.hashCode());
    result = prime * result + ((this.dataKind== null) ? 0 : this.dataKind.hashCode());
    return prime * result + ((this.data== null) ? 0 : this.data.hashCode());
  }
}
