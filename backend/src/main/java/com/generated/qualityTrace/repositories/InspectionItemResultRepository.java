package com.generated.qualityTrace.repositories;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.InspectionItemResult;

@Repository
public class InspectionItemResultRepository extends InMemoryRepository<InspectionItemResult> {

  public List<InspectionItemResult> findByInspectionId(Long inspectionId) {
    return store.values().stream()
        .filter(r -> inspectionId.equals(r.inspectionId))
        .sorted((a, b) -> Long.compare(a.id, b.id))
        .collect(Collectors.toList());
  }

  @Override
  protected Long extractId(InspectionItemResult entity) {
    return entity.id;
  }

  @Override
  protected void assignId(InspectionItemResult entity, Long id) {
    entity.id = id;
  }
}
