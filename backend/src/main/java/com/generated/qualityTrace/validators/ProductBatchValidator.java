package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.types.CreateProductBatchRequest;
import org.springframework.stereotype.Component;

/** 批次入参校验：批号、工单必填，数量为正。 */
@Component
public class ProductBatchValidator {

  public void validateCreate(CreateProductBatchRequest request) {
    ValidatorSupport.require("batchNo", request.batchNo());
    ValidatorSupport.require("workOrderId", request.workOrderId());
    if (request.quantity() == null || request.quantity() <= 0) {
      ValidatorSupport.require("quantity", null);
    }
  }
}
