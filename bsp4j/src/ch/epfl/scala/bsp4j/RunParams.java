package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class RunParams {
  @NonNull private BuildTargetIdentifier target;

  private String originId;

  private List<String> arguments;

  private Map<String, String> environmentVariables;

  private String workingDirectory;

  private String dataKind;

  @JsonAdapter(JsonElementTypeAdapter.Factory.class)
  private Object data;

  public RunParams(@NonNull final BuildTargetIdentifier target) {
    this.target = target;
  }

  @Pure
  @NonNull
  public BuildTargetIdentifier getTarget() {
    return this.target;
  }

  public void setTarget(@NonNull final BuildTargetIdentifier target) {
    this.target = Preconditions.checkNotNull(target, "target");
  }

  @Pure
  public String getOriginId() {
    return this.originId;
  }

  public void setOriginId(final String originId) {
    this.originId = originId;
  }

  @Pure
  public List<String> getArguments() {
    return this.arguments;
  }

  public void setArguments(final List<String> arguments) {
    this.arguments = arguments;
  }

  @Pure
  public Map<String, String> getEnvironmentVariables() {
    return this.environmentVariables;
  }

  public void setEnvironmentVariables(final Map<String, String> environmentVariables) {
    this.environmentVariables = environmentVariables;
  }

  @Pure
  public String getWorkingDirectory() {
    return this.workingDirectory;
  }

  public void setWorkingDirectory(final String workingDirectory) {
    this.workingDirectory = workingDirectory;
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
    b.add("target", this.target);
    b.add("originId", this.originId);
    b.add("arguments", this.arguments);
    b.add("environmentVariables", this.environmentVariables);
    b.add("workingDirectory", this.workingDirectory);
    b.add("dataKind", this.dataKind);
    b.add("data", this.data);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    RunParams other = (RunParams) obj;
    if (this.target == null) {
      if (other.target != null) return false;
    } else if (!this.target.equals(other.target)) return false;
    if (this.originId == null) {
      if (other.originId != null) return false;
    } else if (!this.originId.equals(other.originId)) return false;
    if (this.arguments == null) {
      if (other.arguments != null) return false;
    } else if (!this.arguments.equals(other.arguments)) return false;
    if (this.environmentVariables == null) {
      if (other.environmentVariables != null) return false;
    } else if (!this.environmentVariables.equals(other.environmentVariables)) return false;
    if (this.workingDirectory == null) {
      if (other.workingDirectory != null) return false;
    } else if (!this.workingDirectory.equals(other.workingDirectory)) return false;
    if (this.dataKind == null) {
      if (other.dataKind != null) return false;
    } else if (!this.dataKind.equals(other.dataKind)) return false;
    if (this.data == null) {
      if (other.data != null) return false;
    } else if (!this.data.equals(other.data)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target == null) ? 0 : this.target.hashCode());
    result = prime * result + ((this.originId == null) ? 0 : this.originId.hashCode());
    result = prime * result + ((this.arguments == null) ? 0 : this.arguments.hashCode());
    result =
        prime * result
            + ((this.environmentVariables == null) ? 0 : this.environmentVariables.hashCode());
    result =
        prime * result + ((this.workingDirectory == null) ? 0 : this.workingDirectory.hashCode());
    result = prime * result + ((this.dataKind == null) ? 0 : this.dataKind.hashCode());
    return prime * result + ((this.data == null) ? 0 : this.data.hashCode());
  }
}
