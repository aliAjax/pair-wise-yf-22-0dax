package com.generated.qualityTrace.types;

/** 不良登记请求：严重度取 MINOR/MAJOR/CRITICAL，登记后默认处置状态为 PENDING。 */
public record DefectRecordPayload(
    String requestId,
    String batchNo,
    String defectType,
    Integer defectQty,
    String severity,
    String rootCause) {}
