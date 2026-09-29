package com.generated.qualityTrace.models;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** 检验项结果：实测值与上下限比较后判定合格/不合格，决定检验单结论。 */
public class InspectionItemResult {
  public Long id;
  public Long inspectionId;
  public String itemCode;
  public String itemName;
  public BigDecimal measuredValue;
  public BigDecimal limitMin;
  public BigDecimal limitMax;
  public String itemStatus;
  public OffsetDateTime createdAt;
}
