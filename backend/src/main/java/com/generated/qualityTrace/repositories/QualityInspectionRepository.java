package com.generated.qualityTrace.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.QualityInspection;

@Repository
public class QualityInspectionRepository extends InMemoryRepository<QualityInspection> {

  public List<QualityInspection> findByBatchId(Long batchId) {
    return store.values().stream()
        .filter(i -> batchId.equals(i.batchId))
        .collect(Collectors.toList());
  }

  public Optional<QualityInspection> findByRequestId(String requestId) {
    if (requestId == null) {
      return Optional.empty();
    }
    return store.values().stream().filter(i -> requestId.equals(i.requestId)).findFirst();
  }

  /** 取批次最新一张终检单（按检验时间倒序）。 */
  public Optional<QualityInspection> findLatestFinalByBatchId(Long batchId) {
    return store.values().stream()
        .filter(i -> batchId.equals(i.batchId) && "FINAL".equals(i.inspectionType))
        .max((a, b) -> {
          if (a.inspectedAt == null && b.inspectedAt == null) {
            return Long.compare(a.id, b.id);
          }
          if (a.inspectedAt == null) {
            return -1;
          }
          if (b.inspectedAt == null) {
            return 1;
          }
          return a.inspectedAt.compareTo(b.inspectedAt);
        });
  }

  @Override
  protected Long extractId(QualityInspection entity) {
    return entity.id;
  }

  @Override
  protected void assignId(QualityInspection entity, Long id) {
    entity.id = id;
  }
}
