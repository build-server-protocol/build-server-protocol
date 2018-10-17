package ch.epfl.scala.bsp4j;

import ch.epfl.scala.bsp4j.BuildTargetIdentifier;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class CompileReport {
  @NonNull
  private BuildTargetIdentifier target;
  
  private String originId;
  
  @NonNull
  private Integer errors;
  
  @NonNull
  private Integer warnings;
  
  private Long time;
  
  public CompileReport(@NonNull final BuildTargetIdentifier target, final Integer errors, final Integer warnings) {
    this.target = target;
    this.errors = errors;
    this.warnings = warnings;
  }
  
  @Pure
  @NonNull
  public BuildTargetIdentifier getTarget() {
    return this.target;
  }
  
  public void setTarget(@NonNull final BuildTargetIdentifier target) {
    this.target = target;
  }
  
  @Pure
  public String getOriginId() {
    return this.originId;
  }
  
  public void setOriginId(final String originId) {
    this.originId = originId;
  }
  
  @Pure
  @NonNull
  public Integer getErrors() {
    return this.errors;
  }
  
  public void setErrors(@NonNull final Integer errors) {
    this.errors = errors;
  }
  
  @Pure
  @NonNull
  public Integer getWarnings() {
    return this.warnings;
  }
  
  public void setWarnings(@NonNull final Integer warnings) {
    this.warnings = warnings;
  }
  
  @Pure
  public Long getTime() {
    return this.time;
  }
  
  public void setTime(final Long time) {
    this.time = time;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("target", this.target);
    b.add("originId", this.originId);
    b.add("errors", this.errors);
    b.add("warnings", this.warnings);
    b.add("time", this.time);
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
    CompileReport other = (CompileReport) obj;
    if (this.target == null) {
      if (other.target != null)
        return false;
    } else if (!this.target.equals(other.target))
      return false;
    if (this.originId == null) {
      if (other.originId != null)
        return false;
    } else if (!this.originId.equals(other.originId))
      return false;
    if (this.errors == null) {
      if (other.errors != null)
        return false;
    } else if (!this.errors.equals(other.errors))
      return false;
    if (this.warnings == null) {
      if (other.warnings != null)
        return false;
    } else if (!this.warnings.equals(other.warnings))
      return false;
    if (this.time == null) {
      if (other.time != null)
        return false;
    } else if (!this.time.equals(other.time))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.target== null) ? 0 : this.target.hashCode());
    result = prime * result + ((this.originId== null) ? 0 : this.originId.hashCode());
    result = prime * result + ((this.errors== null) ? 0 : this.errors.hashCode());
    result = prime * result + ((this.warnings== null) ? 0 : this.warnings.hashCode());
    return prime * result + ((this.time== null) ? 0 : this.time.hashCode());
  }
}
