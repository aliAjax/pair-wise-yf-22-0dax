package com.generated.qualityTrace.models;

import java.time.OffsetDateTime;

/** 不良记录：严重度 + 处置状态共同决定批次能否放行。 */
public class DefectRecord {
  public Long id;
  public Long batchId;
  public String defectType;
  public Integer defectQty;
  public String severity;
  public String rootCause;
  public String dispositionStatus;
  public String dispositionAction;
  public String disposedBy;
  public OffsetDateTime disposedAt;
  /** 幂等键：同一 requestId 重复提交按原记录返回。 */
  public String requestId;
  public OffsetDateTime createdAt;
  public OffsetDateTime updatedAt;
}
