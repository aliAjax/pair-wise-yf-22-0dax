package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.types.CreateWorkOrderRequest;
import org.springframework.stereotype.Component;

/** 工单入参校验：工单号/产品编码必填，计划数量为正，状态必须落在 WorkOrderStatus 内。 */
@Component
public class WorkOrderValidator {

  public void validateCreate(CreateWorkOrderRequest request) {
    ValidatorSupport.require("orderNo", request.orderNo());
    ValidatorSupport.require("productCode", request.productCode());
    if (request.plannedQty() == null || request.plannedQty() <= 0) {
      ValidatorSupport.require("plannedQty", null);
    }
    String status = request.status();
    if (status != null && !status.isBlank()) {
      try {
        WorkOrderStatus.valueOf(status);
      } catch (IllegalArgumentException e) {
        ValidatorSupport.fail(ErrorMessages.WORK_ORDER_STATUS_INVALID, status);
      }
    }
  }

  public void validateAction(String action) {
    if (action == null || action.isBlank()
        || (!action.equals("start") && !action.equals("pause") && !action.equals("finish"))) {
      ValidatorSupport.fail(ErrorMessages.WORK_ORDER_STATUS_INVALID, String.valueOf(action));
    }
  }
}
