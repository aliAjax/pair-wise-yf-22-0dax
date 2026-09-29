package com.generated.qualityTrace.models;

import java.time.OffsetDateTime;

/** 幂等请求记录：requestId -> 已成功响应快照 + 请求体指纹，防止重复累计。 */
public class IdempotentRequest {
  public String requestId;
  public String targetType;
  public Long targetId;
  public String payloadHash;
  public String responseJson;
  public OffsetDateTime createdAt;
}
