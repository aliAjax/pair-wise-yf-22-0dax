package com.generated.qualityTrace.models;

import java.time.OffsetDateTime;

/** 操作日志：所有写操作登记一条，供审计员追查谁在什么时候动了什么数据。 */
public class AuditLog {
  public Long id;
  public String actor;
  public String action;
  public String targetType;
  public String targetId;
  public String detail;
  public OffsetDateTime createdAt;
}
