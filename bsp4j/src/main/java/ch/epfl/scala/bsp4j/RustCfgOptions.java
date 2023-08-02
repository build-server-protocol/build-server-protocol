package ch.epfl.scala.bsp4j;

import java.util.List;
import java.util.Map;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RustCfgOptions {
  private Map<String, List<String>> keyValueOptions;

  private List<String> nameOptions;

  public RustCfgOptions() {
  }

  @Pure
  public Map<String, List<String>> getKeyValueOptions() {
    return this.keyValueOptions;
  }

  public void setKeyValueOptions(final Map<String, List<String>> keyValueOptions) {
    this.keyValueOptions = keyValueOptions;
  }

  @Pure
  public List<String> getNameOptions() {
    return this.nameOptions;
  }

  public void setNameOptions(final List<String> nameOptions) {
    this.nameOptions = nameOptions;
  }

  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("keyValueOptions", this.keyValueOptions);
    b.add("nameOptions", this.nameOptions);
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
    RustCfgOptions other = (RustCfgOptions) obj;
    if (this.keyValueOptions == null) {
      if (other.keyValueOptions != null)
        return false;
    } else if (!this.keyValueOptions.equals(other.keyValueOptions))
      return false;
    if (this.nameOptions == null) {
      if (other.nameOptions != null)
        return false;
    } else if (!this.nameOptions.equals(other.nameOptions))
      return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.keyValueOptions== null) ? 0 : this.keyValueOptions.hashCode());
    return prime * result + ((this.nameOptions== null) ? 0 : this.nameOptions.hashCode());
  }
}
