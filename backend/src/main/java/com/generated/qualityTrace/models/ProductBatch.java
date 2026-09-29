package com.generated.qualityTrace.models;

import java.time.OffsetDateTime;

/** 产品批次：检验与不良记录的汇聚点，batch_no 是追溯主键。 */
public class ProductBatch {
  public Long id;
  public String batchNo;
  public Long workOrderId;
  public Integer quantity;
  public String materialLotNo;
  public OffsetDateTime producedAt;
  public String batchStatus;
  /** 当前质量结论编码，取自 constants/BatchConclusionCode。 */
  public String conclusionCode;
  public String conclusionReason;
  public OffsetDateTime conclusionUpdatedAt;
  public OffsetDateTime createdAt;
  public OffsetDateTime updatedAt;
}
