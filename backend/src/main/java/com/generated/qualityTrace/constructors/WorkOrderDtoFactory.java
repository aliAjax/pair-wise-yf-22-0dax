package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.types.WorkOrderDto;

/** 工单 model -> 响应 DTO 的唯一构造入口，service/controller 不得散写字段映射。 */
public final class WorkOrderDtoFactory {

  private WorkOrderDtoFactory() {}

  public static WorkOrderDto create(WorkOrder order) {
    return new WorkOrderDto(
        order.id,
        order.orderNo,
        order.productCode,
        order.productName,
        order.plannedQty,
        order.lineCode,
        order.startAt,
        order.status,
        labelOf(order.status),
        order.createdAt,
        order.updatedAt);
  }

  public static String labelOf(String status) {
    if (status == null) {
      return null;
    }
    try {
      return WorkOrderStatus.valueOf(status).getLabel();
    } catch (IllegalArgumentException ex) {
      return status;
    }
  }
}
