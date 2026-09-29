package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.types.DefectRecordResponse;
import com.generated.qualityTrace.utils.Formatters;

/** 不良记录响应 DTO 构造器，duplicated 标记重复提交命中原记录。 */
public final class DefectRecordDtoFactory {

  private DefectRecordDtoFactory() {}

  public static DefectRecordResponse toResponse(DefectRecord defect, String batchNo,
                                                boolean duplicated) {
    return new DefectRecordResponse(
        defect.getId(),
        defect.getBatchId(),
        batchNo,
        defect.getDefectType(),
        defect.getDefectQty(),
        defect.getSeverity(),
        Formatters.severity(defect.getSeverity()),
        defect.getSeverity() == null ? null
            : Formatters.riskLevel(DefectSeverity.valueOf(defect.getSeverity())),
        defect.getRootCause(),
        defect.getDispositionStatus(),
        Formatters.disposition(defect.getDispositionStatus()),
        defect.getDispositionNote(),
        defect.getRegisteredBy(),
        defect.getDisposedBy(),
        Formatters.dateTime(defect.getCreatedAt()),
        Formatters.dateTime(defect.getDisposedAt()),
        duplicated);
  }
}
