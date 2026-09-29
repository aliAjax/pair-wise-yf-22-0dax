package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.InspectionType;
import com.generated.qualityTrace.exceptions.ValidationException;
import com.generated.qualityTrace.types.InspectionItemResultPayload;
import com.generated.qualityTrace.types.QualityInspectionPayload;

/** 检验单入参校验：批号、检验类型必填；检验项至少一项；枚举值必须合法。 */
public final class QualityInspectionValidator {

  private QualityInspectionValidator() {}

  public static void validate(QualityInspectionPayload payload) {
    if (payload == null) {
      throw new ValidationException(ErrorMessages.format(ErrorMessages.FIELD_REQUIRED, "body"));
    }
    WorkOrderValidator.requireText(payload.batchNo(), "batchNo");
    WorkOrderValidator.requireText(payload.inspectionType(), "inspectionType");
    parseType(payload.inspectionType());
    if (payload.resultStatus() != null && !payload.resultStatus().isBlank()) {
      try {
        InspectionResultStatus.valueOf(payload.resultStatus());
      } catch (IllegalArgumentException ex) {
        throw new ValidationException(ErrorMessages.format(
            ErrorMessages.FIELD_INVALID, "resultStatus", payload.resultStatus()));
      }
    }
    if (payload.items() == null || payload.items().isEmpty()) {
      throw new ValidationException(ErrorMessages.format(ErrorMessages.FIELD_REQUIRED, "items"));
    }
    for (InspectionItemResultPayload item : payload.items()) {
      validateItem(item);
    }
  }

  static InspectionType parseType(String code) {
    try {
      return InspectionType.valueOf(code);
    } catch (IllegalArgumentException ex) {
      throw new ValidationException(
          ErrorMessages.format(ErrorMessages.FIELD_INVALID, "inspectionType", code));
    }
  }

  private static void validateItem(InspectionItemResultPayload item) {
    if (item == null) {
      throw new ValidationException(ErrorMessages.format(ErrorMessages.FIELD_REQUIRED, "items[]"));
    }
    WorkOrderValidator.requireText(item.itemCode(), "items[].itemCode");
    if (item.measuredValue() == null) {
      throw new ValidationException(
          ErrorMessages.format(ErrorMessages.FIELD_REQUIRED, "items[].measuredValue"));
    }
    if (item.itemStatus() != null && !item.itemStatus().isBlank()) {
      try {
        com.generated.qualityTrace.constants.InspectionItemStatus.valueOf(item.itemStatus());
      } catch (IllegalArgumentException ex) {
        throw new ValidationException(ErrorMessages.format(
            ErrorMessages.FIELD_INVALID, "items[].itemStatus", item.itemStatus()));
      }
    }
  }
}
