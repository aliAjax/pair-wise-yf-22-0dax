package com.generated.qualityTrace.models;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("defect_record")
public class DefectRecord {
  @TableId(type = IdType.INPUT)
  private Long id;
  private Long batchId;
  private String defectType;
  private Integer defectQty;
  private String severity;
  private String rootCause;
  private String dispositionStatus;
  private String dispositionNote;
  private String registeredBy;
  private String disposedBy;
  private String contentHash;
  private LocalDateTime createdAt;
  private LocalDateTime disposedAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getBatchId() { return batchId; }
  public void setBatchId(Long batchId) { this.batchId = batchId; }
  public String getDefectType() { return defectType; }
  public void setDefectType(String defectType) { this.defectType = defectType; }
  public Integer getDefectQty() { return defectQty; }
  public void setDefectQty(Integer defectQty) { this.defectQty = defectQty; }
  public String getSeverity() { return severity; }
  public void setSeverity(String severity) { this.severity = severity; }
  public String getRootCause() { return rootCause; }
  public void setRootCause(String rootCause) { this.rootCause = rootCause; }
  public String getDispositionStatus() { return dispositionStatus; }
  public void setDispositionStatus(String dispositionStatus) { this.dispositionStatus = dispositionStatus; }
  public String getDispositionNote() { return dispositionNote; }
  public void setDispositionNote(String dispositionNote) { this.dispositionNote = dispositionNote; }
  public String getRegisteredBy() { return registeredBy; }
  public void setRegisteredBy(String registeredBy) { this.registeredBy = registeredBy; }
  public String getDisposedBy() { return disposedBy; }
  public void setDisposedBy(String disposedBy) { this.disposedBy = disposedBy; }
  public String getContentHash() { return contentHash; }
  public void setContentHash(String contentHash) { this.contentHash = contentHash; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getDisposedAt() { return disposedAt; }
  public void setDisposedAt(LocalDateTime disposedAt) { this.disposedAt = disposedAt; }
}
