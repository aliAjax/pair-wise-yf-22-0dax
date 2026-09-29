package com.generated.qualityTrace.types;

public record WorkOrderResponse(Long id, String orderNo, String productCode, String productName,
                               Integer plannedQty, String lineCode, String startAt, String status,
                               String statusText) {
}
