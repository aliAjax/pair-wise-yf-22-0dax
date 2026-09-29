package com.generated.qualityTrace.types;

import java.time.LocalDateTime;

/** 创建批次请求，挂到指定工单下并更新工单产出进度。 */
public record CreateProductBatchRequest(String batchNo, Long workOrderId, Integer quantity,
                                       String materialLotNo, LocalDateTime producedAt) {
}
