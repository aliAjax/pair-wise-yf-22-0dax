package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.InspectionType;
import com.generated.qualityTrace.types.RegisterInspectionRequest;
import org.springframework.stereotype.Component;

/** 检验单入参校验：批号、类型、检验项必填，类型枚举合法，显式结论合法，检验项上下限合法。 */
@Component
public class InspectionValidator {

  public void validateRegister(RegisterInspectionRequest request) {
    ValidatorSupport.require("batchNo", request.batchNo());
    ValidatorSupport.require("inspectionType", request.inspectionType());
    if (request.items() == null || request.items().isEmpty()) {
      ValidatorSupport.fail(ErrorMessages.INSPECTION_ITEMS_EMPTY);
    }
    try {
      InspectionType.valueOf(request.inspectionType());
    } catch (IllegalArgumentException e) {
      ValidatorSupport.fail(ErrorMessages.INSPECTION_TYPE_INVALID, request.inspectionType());
    }
    if (request.resultStatus() != null && !request.resultStatus().isBlank()) {
      try {
        InspectionResultStatus.valueOf(request.resultStatus());
      } catch (IllegalArgumentException e) {
        ValidatorSupport.fail(ErrorMessages.INSPECTION_TYPE_INVALID, request.resultStatus());
      }
    }
    request.items().forEach(this::validateItem);
  }

  private void validateItem(RegisterInspectionRequest.ItemInput item) {
    ValidatorSupport.require("itemCode", item.itemCode());
    if (item.limitMin() != null && item.limitMax() != null
        && item.limitMin().compareTo(item.limitMax()) > 0) {
      ValidatorSupport.fail(ErrorMessages.ITEM_LIMIT_INVALID, item.itemCode());
    }
    if (item.itemStatus() != null && !item.itemStatus().isBlank()) {
      try {
        com.generated.qualityTrace.constants.InspectionItemStatus.valueOf(item.itemStatus());
      } catch (IllegalArgumentException e) {
        ValidatorSupport.fail(ErrorMessages.ITEM_LIMIT_INVALID, item.itemCode());
      }
    }
  }
}
