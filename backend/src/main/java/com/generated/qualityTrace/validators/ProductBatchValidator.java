package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.ValidationException;
import com.generated.qualityTrace.types.ProductBatchPayload;

/** 批次入参校验：批号必填、数量必须为正。 */
public final class ProductBatchValidator {

  private ProductBatchValidator() {}

  public static void validate(ProductBatchPayload payload) {
    if (payload == null) {
      throw new ValidationException(ErrorMessages.format(ErrorMessages.FIELD_REQUIRED, "body"));
    }
    WorkOrderValidator.requireText(payload.batchNo(), "batchNo");
    WorkOrderValidator.requireText(payload.orderNo(), "orderNo");
    if (payload.quantity() == null || payload.quantity() <= 0) {
      throw new ValidationException(ErrorMessages.format(ErrorMessages.FIELD_INVALID,
          "quantity", payload.quantity()));
    }
  }
}
