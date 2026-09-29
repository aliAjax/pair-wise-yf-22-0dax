package com.generated.qualityTrace.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/** 批次当前结论（每个批次一行），随检验登记、不良登记/处置实时重算。 */
@TableName("batch_conclusion")
public class BatchConclusion {
  @TableId(type = IdType.INPUT)
  private Long id;
  private Long batchId;
  private String conclusionStatus;
  private String reason;
  private Long finalInspectionId;
  private LocalDateTime updatedAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getBatchId() { return batchId; }
  public void setBatchId(Long batchId) { this.batchId = batchId; }
  public String getConclusionStatus() { return conclusionStatus; }
  public void setConclusionStatus(String conclusionStatus) { this.conclusionStatus = conclusionStatus; }
  public String getReason() { return reason; }
  public void setReason(String reason) { this.reason = reason; }
  public Long getFinalInspectionId() { return finalInspectionId; }
  public void setFinalInspectionId(Long finalInspectionId) { this.finalInspectionId = finalInspectionId; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
