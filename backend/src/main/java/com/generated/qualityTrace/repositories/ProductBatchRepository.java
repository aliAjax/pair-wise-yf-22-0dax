package com.generated.qualityTrace.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.ProductBatch;

@Repository
public class ProductBatchRepository extends InMemoryRepository<ProductBatch> {

  public Optional<ProductBatch> findByBatchNo(String batchNo) {
    return store.values().stream().filter(b -> batchNo.equals(b.batchNo)).findFirst();
  }

  public List<ProductBatch> findByWorkOrderId(Long workOrderId) {
    return store.values().stream()
        .filter(b -> workOrderId.equals(b.workOrderId))
        .collect(Collectors.toList());
  }

  @Override
  protected Long extractId(ProductBatch entity) {
    return entity.id;
  }

  @Override
  protected void assignId(ProductBatch entity, Long id) {
    entity.id = id;
  }
}
