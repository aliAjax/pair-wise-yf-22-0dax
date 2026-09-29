package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.AuditLog;
import com.generated.qualityTrace.types.AuditLogResponse;
import com.generated.qualityTrace.utils.Formatters;

/** 审计日志 DTO 构造器。 */
public final class AuditLogDtoFactory {

  private AuditLogDtoFactory() {}

  public static AuditLogResponse toResponse(AuditLog log) {
    return new AuditLogResponse(
        log.getId(),
        log.getActor(),
        log.getAction(),
        log.getTargetType(),
        log.getTargetId(),
        log.getDetail(),
        Formatters.dateTime(log.getCreatedAt()));
  }
}
