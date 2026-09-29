package com.generated.qualityTrace.models;

import java.time.OffsetDateTime;

/** 生产工单：生成产品批次和检验单的源头。 */
public class WorkOrder {
  public Long id;
  public String orderNo;
  public String productCode;
  public String productName;
  public Integer plannedQty;
  public String lineCode;
  public OffsetDateTime startAt;
  public String status;
  public OffsetDateTime createdAt;
  public OffsetDateTime updatedAt;
}
