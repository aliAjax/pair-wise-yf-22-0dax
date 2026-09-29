package com.generated.qualityTrace.repositories;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.BatchConclusionHistory;

@Repository
public class BatchConclusionHistoryRepository extends InMemoryRepository<BatchConclusionHistory> {

  public List<BatchConclusionHistory> findByBatchId(Long batchId) {
    return store.values().stream()
        .filter(h -> batchId.equals(h.batchId))
        .sorted((a, b) -> Long.compare(a.id, b.id))
        .collect(Collectors.toList());
  }

  @Override
  protected Long extractId(BatchConclusionHistory entity) {
    return entity.id;
  }

  @Override
  protected void assignId(BatchConclusionHistory entity, Long id) {
    entity.id = id;
  }
}
