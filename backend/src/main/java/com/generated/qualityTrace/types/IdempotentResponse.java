package com.generated.qualityTrace.types;

/**
 * 所有写接口的统一外层：
 * data          原始响应
 * deduplicated  true 表示命中幂等、按原记录返回，本次没有产生新数据，避免重复累计
 * requestId     实际使用的幂等键（可用于追查）
 */
public record IdempotentResponse<T>(T data, boolean deduplicated, String requestId) {}
