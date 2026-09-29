package com.generated.qualityTrace.types;

import java.time.LocalDateTime;

/**
 * 创建工单请求。status 取值见 constants/WorkOrderStatus，
 * 入参校验见 validators/WorkOrderValidator。
 */
public record CreateWorkOrderRequest(String orderNo, String productCode, String productName,
                                    Integer plannedQty, String lineCode, LocalDateTime startAt,
                                    String status) {
}
