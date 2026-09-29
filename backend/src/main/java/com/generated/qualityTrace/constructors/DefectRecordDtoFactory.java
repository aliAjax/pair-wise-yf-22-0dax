package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.types.DefectRecordDto;

/** 不良记录 model -> 响应 DTO 的唯一构造入口。 */
public final class DefectRecordDtoFactory {

  private DefectRecordDtoFactory() {}

  public static DefectRecordDto create(DefectRecord defect, ProductBatch batch) {
    DefectSeverity severity = severityOf(defect.severity);
    DefectDispositionStatus disposition = dispositionOf(defect.dispositionStatus);
    return new DefectRecordDto(
        defect.id,
        batch == null ? null : batch.batchNo,
        defect.defectType,
        defect.defectQty,
        defect.severity,
        severity == null ? defect.severity : severity.getLabel(),
        severity != null && severity.isSerious(),
        defect.rootCause,
        defect.dispositionStatus,
        disposition == null ? defect.dispositionStatus : disposition.getLabel(),
        disposition != null && disposition.isDone(),
        defect.dispositionAction,
        defect.disposedBy,
        defect.disposedAt,
        defect.createdAt,
        defect.updatedAt);
  }

  public static DefectSeverity severityOf(String code) {
    if (code == null) {
      return null;
    }
    try {
      return DefectSeverity.valueOf(code);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

  public static DefectDispositionStatus dispositionOf(String code) {
    if (code == null) {
      return null;
    }
    try {
      return DefectDispositionStatus.valueOf(code);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }
}
