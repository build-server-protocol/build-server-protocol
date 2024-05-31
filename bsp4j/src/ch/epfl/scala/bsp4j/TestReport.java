package ch.epfl.scala.bsp4j;

import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.eclipse.lsp4j.util.Preconditions;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

@SuppressWarnings("all")
public class TestReport {
  private String originId;

  @NonNull private BuildTargetIdentifier target;

  @NonNull private Integer passed;

  @NonNull private Integer failed;

  @NonNull private Integer ignored;

  @NonNull private Integer cancelled;

  @NonNull private Integer skipped;

  private Long time;

  public TestReport(
      @NonNull final BuildTargetIdentifier target,
      @NonNull final Integer passed,
      @NonNull final Integer failed,
      @NonNull final Integer ignored,
      @NonNull final Integer cancelled,
      @NonNull final Integer skipped) {
    this.target = target;
    this.passed = passed;
    this.failed = failed;
    this.ignored = ignored;
    this.cancelled = cancelled;
    this.skipped = skipped;
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
  public BuildTargetIdentifier getTarget() {
    return this.target;
  }

  public void setTarget(@NonNull final BuildTargetIdentifier target) {
    this.target = Preconditions.checkNotNull(target, "target");
  }

  @Pure
  @NonNull
  public Integer getPassed() {
    return this.passed;
  }

  public void setPassed(@NonNull final Integer passed) {
    this.passed = Preconditions.checkNotNull(passed, "passed");
  }

  @Pure
  @NonNull
  public Integer getFailed() {
    return this.failed;
  }

  public void setFailed(@NonNull final Integer failed) {
    this.failed = Preconditions.checkNotNull(failed, "failed");
  }

  @Pure
  @NonNull
  public Integer getIgnored() {
    return this.ignored;
  }

  public void setIgnored(@NonNull final Integer ignored) {
    this.ignored = Preconditions.checkNotNull(ignored, "ignored");
  }

  @Pure
  @NonNull
  public Integer getCancelled() {
    return this.cancelled;
  }

  public void setCancelled(@NonNull final Integer cancelled) {
    this.cancelled = Preconditions.checkNotNull(cancelled, "cancelled");
  }

  @Pure
  @NonNull
  public Integer getSkipped() {
    return this.skipped;
  }

  public void setSkipped(@NonNull final Integer skipped) {
    this.skipped = Preconditions.checkNotNull(skipped, "skipped");
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
    b.add("originId", this.originId);
    b.add("target", this.target);
    b.add("passed", this.passed);
    b.add("failed", this.failed);
    b.add("ignored", this.ignored);
    b.add("cancelled", this.cancelled);
    b.add("skipped", this.skipped);
    b.add("time", this.time);
    return b.toString();
  }

  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    TestReport other = (TestReport) obj;
    if (this.originId == null) {
      if (other.originId != null) return false;
    } else if (!this.originId.equals(other.originId)) return false;
    if (this.target == null) {
      if (other.target != null) return false;
    } else if (!this.target.equals(other.target)) return false;
    if (this.passed == null) {
      if (other.passed != null) return false;
    } else if (!this.passed.equals(other.passed)) return false;
    if (this.failed == null) {
      if (other.failed != null) return false;
    } else if (!this.failed.equals(other.failed)) return false;
    if (this.ignored == null) {
      if (other.ignored != null) return false;
    } else if (!this.ignored.equals(other.ignored)) return false;
    if (this.cancelled == null) {
      if (other.cancelled != null) return false;
    } else if (!this.cancelled.equals(other.cancelled)) return false;
    if (this.skipped == null) {
      if (other.skipped != null) return false;
    } else if (!this.skipped.equals(other.skipped)) return false;
    if (this.time == null) {
      if (other.time != null) return false;
    } else if (!this.time.equals(other.time)) return false;
    return true;
  }

  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.originId == null) ? 0 : this.originId.hashCode());
    result = prime * result + ((this.target == null) ? 0 : this.target.hashCode());
    result = prime * result + ((this.passed == null) ? 0 : this.passed.hashCode());
    result = prime * result + ((this.failed == null) ? 0 : this.failed.hashCode());
    result = prime * result + ((this.ignored == null) ? 0 : this.ignored.hashCode());
    result = prime * result + ((this.cancelled == null) ? 0 : this.cancelled.hashCode());
    result = prime * result + ((this.skipped == null) ? 0 : this.skipped.hashCode());
    return prime * result + ((this.time == null) ? 0 : this.time.hashCode());
  }
}
