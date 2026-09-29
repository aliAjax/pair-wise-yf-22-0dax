package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.DispositionStatus;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.types.DisposeDefectRequest;
import com.generated.qualityTrace.types.RegisterDefectRequest;
import org.springframework.stereotype.Component;

/** 不良记录入参校验：批号、类型、数量必填，严重度合法；处置状态只允许 IN_PROGRESS/CLOSED。 */
@Component
public class DefectValidator {

  public void validateRegister(RegisterDefectRequest request) {
    ValidatorSupport.require("batchNo", request.batchNo());
    ValidatorSupport.require("defectType", request.defectType());
    if (request.defectQty() == null || request.defectQty() <= 0) {
      ValidatorSupport.fail(ErrorMessages.DEFECT_QTY_INVALID);
    }
    ValidatorSupport.require("severity", request.severity());
    try {
      DefectSeverity.valueOf(request.severity());
    } catch (IllegalArgumentException e) {
      ValidatorSupport.fail(ErrorMessages.DEFECT_SEVERITY_INVALID, request.severity());
    }
  }

  public void validateDispose(DisposeDefectRequest request) {
    ValidatorSupport.require("dispositionStatus", request.dispositionStatus());
    DispositionStatus status;
    try {
      status = DispositionStatus.valueOf(request.dispositionStatus());
    } catch (IllegalArgumentException e) {
      ValidatorSupport.fail(ErrorMessages.DISPOSITION_STATUS_INVALID, request.dispositionStatus());
      return;
    }
    if (status == DispositionStatus.OPEN) {
      ValidatorSupport.fail(ErrorMessages.DISPOSITION_STATUS_INVALID, request.dispositionStatus());
    }
  }
}
