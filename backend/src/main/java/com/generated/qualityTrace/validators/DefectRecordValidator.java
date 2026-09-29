package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.ValidationException;
import com.generated.qualityTrace.types.DefectRecordPayload;

/** 不良登记校验：批号、不良类型、严重度必填，数量必须为正。 */
public final class DefectRecordValidator {

  private DefectRecordValidator() {}

  public static void validate(DefectRecordPayload payload) {
    if (payload == null) {
      throw new ValidationException(ErrorMessages.format(ErrorMessages.FIELD_REQUIRED, "body"));
    }
    WorkOrderValidator.requireText(payload.batchNo(), "batchNo");
    WorkOrderValidator.requireText(payload.defectType(), "defectType");
    WorkOrderValidator.requireText(payload.severity(), "severity");
    try {
      DefectSeverity.valueOf(payload.severity());
    } catch (IllegalArgumentException ex) {
      throw new ValidationException(
          ErrorMessages.format(ErrorMessages.FIELD_INVALID, "severity", payload.severity()));
    }
    if (payload.defectQty() == null || payload.defectQty() <= 0) {
      throw new ValidationException(ErrorMessages.format(
          ErrorMessages.FIELD_INVALID, "defectQty", payload.defectQty()));
    }
  }
}
