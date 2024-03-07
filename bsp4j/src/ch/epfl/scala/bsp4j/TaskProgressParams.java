package ch.epfl.scala.bsp4j;

import com.google.gson.annotations.JsonAdapter;
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class TaskProgressParams {
  @NonNull
  private TaskId taskId;

  private String originId;

  private Long eventTime;

  private String message;

  private Long total;

  private Long progress;

  private String unit;

  private String dataKind;

  @JsonAdapter(JsonElementTypeAdapter.Factory.class)
  private Object data;

  public TaskProgressParams(@NonNull final TaskId taskId) {
    this.taskId = taskId;
  }

  @Pure
  @NonNull
  public TaskId getTaskId() {
    return this.taskId;
  }

  public void setTaskId(@NonNull final TaskId taskId) {
    this.taskId = Preconditions.checkNotNull(taskId, "taskId");
  }

  @Pure
  public String getOriginId() {
    return this.originId;
  }

  public void setOriginId(final String originId) {
    this.originId = originId;
  }

  @Pure
  public Long getEventTime() {
    return this.eventTime;
  }

  public void setEventTime(final Long eventTime) {
    this.eventTime = eventTime;
  }

  @Pure
  public String getMessage() {
    return this.message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  @Pure
  public Long getTotal() {
    return this.total;
  }

  public void setTotal(final Long total) {
    this.total = total;
  }

  @Pure
  public Long getProgress() {
    return this.progress;
  }

  public void setProgress(final Long progress) {
    this.progress = progress;
  }

  @Pure
  public String getUnit() {
    return this.unit;
  }

  public void setUnit(final String unit) {
    this.unit = unit;
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
    b.add("taskId", this.taskId);
    b.add("originId", this.originId);
    b.add("eventTime", this.eventTime);
    b.add("message", this.message);
    b.add("total", this.total);
    b.add("progress", this.progress);
    b.add("unit", this.unit);
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
    TaskProgressParams other = (TaskProgressParams) obj;
    if (this.taskId == null) {
      if (other.taskId != null)
        return false;
    } else if (!this.taskId.equals(other.taskId))
      return false;
    if (this.originId == null) {
      if (other.originId != null)
        return false;
    } else if (!this.originId.equals(other.originId))
      return false;
    if (this.eventTime == null) {
      if (other.eventTime != null)
        return false;
    } else if (!this.eventTime.equals(other.eventTime))
      return false;
    if (this.message == null) {
      if (other.message != null)
        return false;
    } else if (!this.message.equals(other.message))
      return false;
    if (this.total == null) {
      if (other.total != null)
        return false;
    } else if (!this.total.equals(other.total))
      return false;
    if (this.progress == null) {
      if (other.progress != null)
        return false;
    } else if (!this.progress.equals(other.progress))
      return false;
    if (this.unit == null) {
      if (other.unit != null)
        return false;
    } else if (!this.unit.equals(other.unit))
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
    result = prime * result + ((this.taskId== null) ? 0 : this.taskId.hashCode());
    result = prime * result + ((this.originId== null) ? 0 : this.originId.hashCode());
    result = prime * result + ((this.eventTime== null) ? 0 : this.eventTime.hashCode());
    result = prime * result + ((this.message== null) ? 0 : this.message.hashCode());
    result = prime * result + ((this.total== null) ? 0 : this.total.hashCode());
    result = prime * result + ((this.progress== null) ? 0 : this.progress.hashCode());
    result = prime * result + ((this.unit== null) ? 0 : this.unit.hashCode());
    result = prime * result + ((this.dataKind== null) ? 0 : this.dataKind.hashCode());
    return prime * result + ((this.data== null) ? 0 : this.data.hashCode());
  }
}
