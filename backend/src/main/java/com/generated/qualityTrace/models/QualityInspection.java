package com.generated.qualityTrace.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("quality_inspection")
public class QualityInspection {
  @TableId(type = IdType.INPUT)
  private Long id;
  private Long batchId;
  private Long inspectorId;
  private String inspectorName;
  private String inspectionType;
  private String standardVersion;
  private String resultStatus;
  private LocalDateTime inspectedAt;
  private String contentHash;
  private LocalDateTime createdAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getBatchId() { return batchId; }
  public void setBatchId(Long batchId) { this.batchId = batchId; }
  public Long getInspectorId() { return inspectorId; }
  public void setInspectorId(Long inspectorId) { this.inspectorId = inspectorId; }
  public String getInspectorName() { return inspectorName; }
  public void setInspectorName(String inspectorName) { this.inspectorName = inspectorName; }
  public String getInspectionType() { return inspectionType; }
  public void setInspectionType(String inspectionType) { this.inspectionType = inspectionType; }
  public String getStandardVersion() { return standardVersion; }
  public void setStandardVersion(String standardVersion) { this.standardVersion = standardVersion; }
  public String getResultStatus() { return resultStatus; }
  public void setResultStatus(String resultStatus) { this.resultStatus = resultStatus; }
  public LocalDateTime getInspectedAt() { return inspectedAt; }
  public void setInspectedAt(LocalDateTime inspectedAt) { this.inspectedAt = inspectedAt; }
  public String getContentHash() { return contentHash; }
  public void setContentHash(String contentHash) { this.contentHash = contentHash; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
