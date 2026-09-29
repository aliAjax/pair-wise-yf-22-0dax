package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.ValidationException;
import com.generated.qualityTrace.types.DefectDispositionPayload;

/** 不良处置进度校验：目标状态必须是合法枚举。 */
public final class DefectDispositionValidator {

  private DefectDispositionValidator() {}

  public static DefectDispositionStatus validate(DefectDispositionPayload payload) {
    if (payload == null) {
      throw new ValidationException(ErrorMessages.format(ErrorMessages.FIELD_REQUIRED, "body"));
    }
    WorkOrderValidator.requireText(payload.dispositionStatus(), "dispositionStatus");
    try {
      return DefectDispositionStatus.valueOf(payload.dispositionStatus());
    } catch (IllegalArgumentException ex) {
      throw new ValidationException(ErrorMessages.format(
          ErrorMessages.FIELD_INVALID, "dispositionStatus", payload.dispositionStatus()));
    }
  }
}
