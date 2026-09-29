package com.generated.qualityTrace.services;

import java.time.OffsetDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.models.AuditLog;
import com.generated.qualityTrace.repositories.AuditLogRepository;
import com.generated.qualityTrace.utils.Formatters;
import com.generated.qualityTrace.utils.RequestContext;

/** 操作日志：所有写操作统一登记，审计员可按时间倒序追查。 */
@Service
public class AuditLogService {

  private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

  private final AuditLogRepository repository;

  public AuditLogService(AuditLogRepository repository) {
    this.repository = repository;
  }

  public void record(String action, String targetType, Long targetId, String detail) {
    AuditLog entry = new AuditLog();
    entry.actor = RequestContext.getActor();
    entry.action = action;
    entry.targetType = targetType;
    entry.targetId = targetId == null ? null : String.valueOf(targetId);
    entry.detail = detail;
    entry.createdAt = OffsetDateTime.now();
    repository.save(entry);
    log.info(
        "audit: {} {} actor={}",
        action,
        Formatters.audit(targetType, targetId == null ? 0L : targetId),
        entry.actor);
  }

  /** 按业务键（如批号）记录，方便追溯查询直接关联。 */
  public void recordByKey(String action, String targetType, String targetKey, String detail) {
    AuditLog entry = new AuditLog();
    entry.actor = RequestContext.getActor();
    entry.action = action;
    entry.targetType = targetType;
    entry.targetId = targetKey;
    entry.detail = detail;
    entry.createdAt = OffsetDateTime.now();
    repository.save(entry);
    log.info("audit: {} {} actor={}", action, Formatters.audit(targetType, targetKey), entry.actor);
  }

  public List<AuditLog> recent(int limit) {
    return repository.findRecent(limit);
  }
}
