package com.generated.qualityTrace.types;

/**
 * 不良处置进度更新请求。
 * dispositionStatus 取 PENDING/IN_PROGRESS/CLOSED；同一个 requestId 重复提交返回原结果。
 */
public record DefectDispositionPayload(
    String requestId,
    String dispositionStatus,
    String dispositionAction,
    String disposedBy) {}
