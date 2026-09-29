package com.generated.qualityTrace.types;

import java.time.OffsetDateTime;

/** 创建工单请求。requestId 用于幂等，重复提交按原记录返回。 */
public record WorkOrderPayload(
    String requestId,
    String orderNo,
    String productCode,
    String productName,
    Integer plannedQty,
    String lineCode,
    OffsetDateTime startAt) {}
