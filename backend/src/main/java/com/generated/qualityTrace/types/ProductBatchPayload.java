package com.generated.qualityTrace.types;

import java.time.OffsetDateTime;

/** 创建批次请求，必须挂到一张已登记工单上。 */
public record ProductBatchPayload(
    String requestId,
    String batchNo,
    String orderNo,
    Integer quantity,
    String materialLotNo,
    OffsetDateTime producedAt) {}
