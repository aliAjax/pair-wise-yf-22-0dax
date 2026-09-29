package com.generated.qualityTrace.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/** 批次结论变化流水，每次结论状态变化追加一行（含创建时的初始结论）。 */
@TableName("batch_conclusion_history")
public class BatchConclusionHistory {
  @TableId(type = IdType.INPUT)
  private Long id;
  private Long batchId;
  private String fromStatus;
  private String toStatus;
  private String reason;
  private String changedBy;
  private LocalDateTime changedAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getBatchId() { return batchId; }
  public void setBatchId(Long batchId) { this.batchId = batchId; }
  public String getFromStatus() { return fromStatus; }
  public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }
  public String getToStatus() { return toStatus; }
  public void setToStatus(String toStatus) { this.toStatus = toStatus; }
  public String getReason() { return reason; }
  public void setReason(String reason) { this.reason = reason; }
  public String getChangedBy() { return changedBy; }
  public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
  public LocalDateTime getChangedAt() { return changedAt; }
  public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}
