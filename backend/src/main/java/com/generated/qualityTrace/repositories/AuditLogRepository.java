package com.generated.qualityTrace.repositories;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.AuditLog;

@Repository
public class AuditLogRepository extends InMemoryRepository<AuditLog> {

  public List<AuditLog> findRecent(int limit) {
    return store.values().stream()
        .sorted((a, b) -> Long.compare(b.id, a.id))
        .limit(limit)
        .toList();
  }

  @Override
  protected Long extractId(AuditLog entity) {
    return entity.id;
  }

  @Override
  protected void assignId(AuditLog entity, Long id) {
    entity.id = id;
  }
}
