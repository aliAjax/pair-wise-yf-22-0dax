package com.generated.qualityTrace.types;

import java.time.OffsetDateTime;

public record WorkOrderDto(
    Long id,
    String orderNo,
    String productCode,
    String productName,
    Integer plannedQty,
    String lineCode,
    OffsetDateTime startAt,
    String status,
    String statusLabel,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
