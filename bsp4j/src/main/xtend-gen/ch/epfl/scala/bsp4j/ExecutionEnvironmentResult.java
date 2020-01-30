package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.ClasspathItem;
import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class ExecutionEnvironmentResult {
  @NonNull
  private String workingDirectory;
  
  @NonNull
  private Map<String, String> environmentVariables;
  
  @NonNull
  private List<ClasspathItem> items;
  
  public ExecutionEnvironmentResult(@NonNull final List<ClasspathItem> items, @NonNull final String workingDirectory, @NonNull final Map<String, String> environmentVariables) {
    this.items = items;
    this.workingDirectory = workingDirectory;
    this.environmentVariables = environmentVariables;
  }
  
  @Pure
  @NonNull
  public String getWorkingDirectory() {
    return this.workingDirectory;
  }
  
  public void setWorkingDirectory(@NonNull final String workingDirectory) {
    this.workingDirectory = workingDirectory;
  }
  
  @Pure
  @NonNull
  public Map<String, String> getEnvironmentVariables() {
    return this.environmentVariables;
  }
  
  public void setEnvironmentVariables(@NonNull final Map<String, String> environmentVariables) {
    this.environmentVariables = environmentVariables;
  }
  
  @Pure
  @NonNull
  public List<ClasspathItem> getItems() {
    return this.items;
  }
  
  public void setItems(@NonNull final List<ClasspathItem> items) {
    this.items = items;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("workingDirectory", this.workingDirectory);
    b.add("environmentVariables", this.environmentVariables);
    b.add("items", this.items);
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
    ExecutionEnvironmentResult other = (ExecutionEnvironmentResult) obj;
    if (this.workingDirectory == null) {
      if (other.workingDirectory != null)
        return false;
    } else if (!this.workingDirectory.equals(other.workingDirectory))
      return false;
    if (this.environmentVariables == null) {
      if (other.environmentVariables != null)
        return false;
    } else if (!this.environmentVariables.equals(other.environmentVariables))
      return false;
    if (this.items == null) {
      if (other.items != null)
        return false;
    } else if (!this.items.equals(other.items))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.workingDirectory== null) ? 0 : this.workingDirectory.hashCode());
    result = prime * result + ((this.environmentVariables== null) ? 0 : this.environmentVariables.hashCode());
    return prime * result + ((this.items== null) ? 0 : this.items.hashCode());
  }
}
