package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.types.WorkOrderResponse;
import com.generated.qualityTrace.utils.Formatters;

/** 工单响应 DTO 构造器：service/controller 不得散写默认结构。 */
public final class WorkOrderDtoFactory {

  private WorkOrderDtoFactory() {}

  public static WorkOrderResponse toResponse(WorkOrder order) {
    if (order == null) {
      return null;
    }
    return new WorkOrderResponse(
        order.getId(),
        order.getOrderNo(),
        order.getProductCode(),
        order.getProductName(),
        order.getPlannedQty(),
        order.getLineCode(),
        Formatters.dateTime(order.getStartAt()),
        order.getStatus(),
        Formatters.workOrderStatus(order.getStatus()));
  }
}
