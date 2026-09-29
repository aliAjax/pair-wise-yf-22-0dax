package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.RoleConstants;
import com.generated.qualityTrace.constructors.AuditLogDtoFactory;
import com.generated.qualityTrace.middlewares.RequireRoles;
import com.generated.qualityTrace.services.AuditLogService;
import com.generated.qualityTrace.types.AuditLogResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 审计员/质量经理查看操作与追溯事件日志。 */
@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

  private final AuditLogService auditLogService;

  public AuditLogController(AuditLogService auditLogService) {
    this.auditLogService = auditLogService;
  }

  @GetMapping
  @RequireRoles({RoleConstants.QUALITY_MANAGER, RoleConstants.AUDITOR})
  public List<AuditLogResponse> recent(@RequestParam(defaultValue = "100") int limit) {
    return ControllerSupport.call(() -> auditLogService.listRecent(limit).stream()
        .map(AuditLogDtoFactory::toResponse)
        .toList());
  }

  @GetMapping("/{targetType}/{targetId}")
  @RequireRoles({RoleConstants.QUALITY_MANAGER, RoleConstants.AUDITOR})
  public List<AuditLogResponse> byTarget(@PathVariable String targetType,
                                         @PathVariable String targetId) {
    return ControllerSupport.call(() ->
        auditLogService.listByTarget(targetType, targetId).stream()
            .map(AuditLogDtoFactory::toResponse)
            .toList());
  }
}
