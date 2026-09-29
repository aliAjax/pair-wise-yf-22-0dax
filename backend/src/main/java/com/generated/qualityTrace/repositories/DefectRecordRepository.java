package com.generated.qualityTrace.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.DefectRecord;

@Repository
public class DefectRecordRepository extends InMemoryRepository<DefectRecord> {

  public List<DefectRecord> findByBatchId(Long batchId) {
    return store.values().stream()
        .filter(d -> batchId.equals(d.batchId))
        .sorted((a, b) -> Long.compare(a.id, b.id))
        .collect(Collectors.toList());
  }

  public Optional<DefectRecord> findByRequestId(String requestId) {
    if (requestId == null) {
      return Optional.empty();
    }
    return store.values().stream().filter(d -> requestId.equals(d.requestId)).findFirst();
  }

  @Override
  protected Long extractId(DefectRecord entity) {
    return entity.id;
  }

  @Override
  protected void assignId(DefectRecord entity, Long id) {
    entity.id = id;
  }
}
