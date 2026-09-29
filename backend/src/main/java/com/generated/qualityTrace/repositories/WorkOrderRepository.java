package com.generated.qualityTrace.repositories;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.WorkOrder;

@Repository
public class WorkOrderRepository extends InMemoryRepository<WorkOrder> {

  public Optional<WorkOrder> findByOrderNo(String orderNo) {
    return store.values().stream().filter(o -> orderNo.equals(o.orderNo)).findFirst();
  }

  @Override
  protected Long extractId(WorkOrder entity) {
    return entity.id;
  }

  @Override
  protected void assignId(WorkOrder entity, Long id) {
    entity.id = id;
  }
}
