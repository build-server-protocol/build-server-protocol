package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import java.util.List;
import java.util.Map;
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter;
import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

@SuppressWarnings("all")
public class TestParams {
  @NonNull
  private List<BuildTargetIdentifier> targets;

  private String originId;

  private List<String> arguments;

  private Map<String, String> environmentVariables;

  private String workingDirectory;

  private String dataKind;

  @JsonAdapter(JsonElementTypeAdapter.Factory.class)
  private Object data;

  public TestParams(@NonNull final List<BuildTargetIdentifier> targets) {
    this.targets = targets;
  }

  @NonNull
  public List<BuildTargetIdentifier> getTargets() {
    return this.targets;
  }

  public void setTargets(@NonNull final List<BuildTargetIdentifier> targets) {
    this.targets = Preconditions.checkNotNull(targets, "targets");
  }

  public String getOriginId() {
    return this.originId;
  }

  public void setOriginId(final String originId) {
    this.originId = originId;
  }

  public List<String> getArguments() {
    return this.arguments;
  }

  public void setArguments(final List<String> arguments) {
    this.arguments = arguments;
  }

  public Map<String, String> getEnvironmentVariables() {
    return this.environmentVariables;
  }

  public void setEnvironmentVariables(final Map<String, String> environmentVariables) {
    this.environmentVariables = environmentVariables;
  }

  public String getWorkingDirectory() {
    return this.workingDirectory;
  }

  public void setWorkingDirectory(final String workingDirectory) {
    this.workingDirectory = workingDirectory;
  }

  public String getDataKind() {
    return this.dataKind;
  }

  public void setDataKind(final String dataKind) {
    this.dataKind = dataKind;
  }

  public Object getData() {
    return this.data;
  }

  public void setData(final Object data) {
    this.data = data;
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("targets", this.targets);
    b.add("originId", this.originId);
    b.add("arguments", this.arguments);
    b.add("environmentVariables", this.environmentVariables);
    b.add("workingDirectory", this.workingDirectory);
    b.add("dataKind", this.dataKind);
    b.add("data", this.data);
    return b.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TestParams other = (TestParams) obj;
    if (this.targets == null) {
      if (other.targets != null)
        return false;
    } else if (!this.targets.equals(other.targets))
      return false;
    if (this.originId == null) {
      if (other.originId != null)
        return false;
    } else if (!this.originId.equals(other.originId))
      return false;
    if (this.arguments == null) {
      if (other.arguments != null)
        return false;
    } else if (!this.arguments.equals(other.arguments))
      return false;
    if (this.environmentVariables == null) {
      if (other.environmentVariables != null)
        return false;
    } else if (!this.environmentVariables.equals(other.environmentVariables))
      return false;
    if (this.workingDirectory == null) {
      if (other.workingDirectory != null)
        return false;
    } else if (!this.workingDirectory.equals(other.workingDirectory))
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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.targets== null) ? 0 : this.targets.hashCode());
    result = prime * result + ((this.originId== null) ? 0 : this.originId.hashCode());
    result = prime * result + ((this.arguments== null) ? 0 : this.arguments.hashCode());
    result = prime * result + ((this.environmentVariables== null) ? 0 : this.environmentVariables.hashCode());
    result = prime * result + ((this.workingDirectory== null) ? 0 : this.workingDirectory.hashCode());
    result = prime * result + ((this.dataKind== null) ? 0 : this.dataKind.hashCode());
    return prime * result + ((this.data== null) ? 0 : this.data.hashCode());
  }
}
