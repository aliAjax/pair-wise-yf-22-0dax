package com.generated.qualityTrace.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.generated.qualityTrace.models.AuditLog;
import com.generated.qualityTrace.repositories.AuditLogRepository;
import com.generated.qualityTrace.utils.IdGenerator;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

/** 审计/追溯日志服务：所有 service 的写操作都通过它落库，日志模板来自 constants/LogTemplates。 */
@Service
public class AuditLogService {

  private final AuditLogRepository auditLogRepository;

  public AuditLogService(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  public void record(String actor, String action, String targetType, Object targetId,
                     String detail) {
    AuditLog log = new AuditLog();
    log.setId(IdGenerator.nextId());
    log.setActor(actor);
    log.setAction(action);
    log.setTargetType(targetType);
    log.setTargetId(targetId == null ? null : String.valueOf(targetId));
    log.setDetail(detail);
    log.setCreatedAt(LocalDateTime.now());
    auditLogRepository.insert(log);
  }

  public List<AuditLog> listRecent(int limit) {
    int safeLimit = limit <= 0 || limit > 500 ? 100 : limit;
    return auditLogRepository.selectList(new LambdaQueryWrapper<AuditLog>()
        .orderByDesc(AuditLog::getCreatedAt)
        .last("LIMIT " + safeLimit));
  }

  public List<AuditLog> listByTarget(String targetType, String targetId) {
    return auditLogRepository.selectList(new LambdaQueryWrapper<AuditLog>()
        .eq(AuditLog::getTargetType, targetType)
        .eq(AuditLog::getTargetId, targetId)
        .orderByDesc(AuditLog::getCreatedAt));
  }
}
