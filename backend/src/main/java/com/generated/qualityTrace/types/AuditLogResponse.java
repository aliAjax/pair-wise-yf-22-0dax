package com.generated.qualityTrace.types;

public record AuditLogResponse(Long id, String actor, String action, String targetType,
                              String targetId, String detail, String createdAt) {
}
