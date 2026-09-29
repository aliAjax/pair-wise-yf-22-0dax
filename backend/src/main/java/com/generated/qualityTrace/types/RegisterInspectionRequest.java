package com.generated.qualityTrace.types;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 检验登记请求（检验单 + 检验项一起提交）。
 * inspectionType: FIRST/PATROL/FINAL，见 constants/InspectionType。
 * 可显式给 resultStatus；不给则按检验项逐项判定自动汇总。
 * 同 batchNo + 同内容重复提交会按原记录返回（幂等）。
 */
public record RegisterInspectionRequest(String batchNo, Long inspectorId, String inspectionType,
                                       String standardVersion, String resultStatus,
                                       LocalDateTime inspectedAt,
                                       List<ItemInput> items) {

  public record ItemInput(String itemCode, String itemName, BigDecimal measuredValue,
                          BigDecimal limitMin, BigDecimal limitMax, String itemStatus) {
  }
}
