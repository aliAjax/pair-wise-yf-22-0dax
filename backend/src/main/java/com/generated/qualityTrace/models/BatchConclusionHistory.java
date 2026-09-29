package com.generated.qualityTrace.models;

import java.time.OffsetDateTime;

/** 批次结论变化历史：只要结论发生变化就追加一条，终检通过也不会跳过未处置严重不良。 */
public class BatchConclusionHistory {
  public Long id;
  public Long batchId;
  public String fromConclusion;
  public String toConclusion;
  public String reason;
  public String triggerEvent;
  public String actor;
  public OffsetDateTime createdAt;
}
