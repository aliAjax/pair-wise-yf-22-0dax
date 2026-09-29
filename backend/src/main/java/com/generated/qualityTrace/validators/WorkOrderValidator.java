package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.ValidationException;
import com.generated.qualityTrace.types.WorkOrderPayload;

/** 工单入参校验：产品编码等关键字段不能为空。 */
public final class WorkOrderValidator {

  private WorkOrderValidator() {}

  public static void validate(WorkOrderPayload payload) {
    if (payload == null) {
      throw new ValidationException(ErrorMessages.format(ErrorMessages.FIELD_REQUIRED, "body"));
    }
    requireText(payload.orderNo(), "orderNo");
    requireText(payload.productCode(), "productCode");
    if (payload.plannedQty() == null || payload.plannedQty() <= 0) {
      throw new ValidationException(ErrorMessages.format(ErrorMessages.FIELD_INVALID,
          "plannedQty", payload.plannedQty()));
    }
  }

  static void requireText(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new ValidationException(ErrorMessages.format(ErrorMessages.FIELD_REQUIRED, field));
    }
  }
}
