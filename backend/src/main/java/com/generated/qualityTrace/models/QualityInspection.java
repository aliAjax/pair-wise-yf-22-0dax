package com.generated.qualityTrace.models;

import java.time.OffsetDateTime;

/** 质量检验单：首检 / 巡检 / 终检，包含多个检验项结果。 */
public class QualityInspection {
  public Long id;
  public Long batchId;
  public String inspectorId;
  public String inspectionType;
  public String standardVersion;
  public String resultStatus;
  public OffsetDateTime inspectedAt;
  /** 幂等键：同一 requestId 重复提交按原记录返回。 */
  public String requestId;
  public OffsetDateTime createdAt;
  public OffsetDateTime updatedAt;
}
