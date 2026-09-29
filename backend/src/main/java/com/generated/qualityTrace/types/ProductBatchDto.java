package com.generated.qualityTrace.types;

import java.time.OffsetDateTime;

public record ProductBatchDto(
    Long id,
    String batchNo,
    Long workOrderId,
    String orderNo,
    Integer quantity,
    String materialLotNo,
    OffsetDateTime producedAt,
    String batchStatus,
    String batchStatusLabel,
    String conclusionCode,
    String conclusionLabel,
    String conclusionReason,
    OffsetDateTime conclusionUpdatedAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
