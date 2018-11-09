package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.ScalaTestClassesItem;
import com.google.gson.annotations.JsonAdapter;
import java.util.List;
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class ScalaTestParams {
  @NonNull
  private List<ScalaTestClassesItem> testClasses;
  
  @JsonAdapter(JsonElementTypeAdapter.Factory.class)
  private Object data;
  
  public ScalaTestParams(@NonNull final List<ScalaTestClassesItem> testClasses) {
    this.testClasses = testClasses;
  }
  
  @Pure
  @NonNull
  public List<ScalaTestClassesItem> getTestClasses() {
    return this.testClasses;
  }
  
  public void setTestClasses(@NonNull final List<ScalaTestClassesItem> testClasses) {
    this.testClasses = testClasses;
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
    b.add("testClasses", this.testClasses);
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
    ScalaTestParams other = (ScalaTestParams) obj;
    if (this.testClasses == null) {
      if (other.testClasses != null)
        return false;
    } else if (!this.testClasses.equals(other.testClasses))
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
    result = prime * result + ((this.testClasses== null) ? 0 : this.testClasses.hashCode());
    return prime * result + ((this.data== null) ? 0 : this.data.hashCode());
  }
}
