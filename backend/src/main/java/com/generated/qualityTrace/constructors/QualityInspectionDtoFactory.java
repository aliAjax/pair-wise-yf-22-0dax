package com.generated.qualityTrace.constructors;

import java.util.List;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.InspectionType;
import com.generated.qualityTrace.models.InspectionItemResult;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.types.InspectionItemResultDto;
import com.generated.qualityTrace.types.QualityInspectionDto;

/** 检验单 model -> 响应 DTO 的唯一构造入口（含检验项嵌套）。 */
public final class QualityInspectionDtoFactory {

  private QualityInspectionDtoFactory() {}

  public static QualityInspectionDto create(
      QualityInspection inspection, ProductBatch batch, List<InspectionItemResult> items) {
    return new QualityInspectionDto(
        inspection.id,
        batch == null ? null : batch.batchNo,
        inspection.inspectorId,
        inspection.inspectionType,
        inspectionTypeLabel(inspection.inspectionType),
        inspection.standardVersion,
        inspection.resultStatus,
        resultStatusLabel(inspection.resultStatus),
        inspection.inspectedAt,
        items.stream().map(InspectionItemResultDtoFactory::create).toList(),
        inspection.createdAt);
  }

  public static InspectionItemResultDto createItem(InspectionItemResult item) {
    return InspectionItemResultDtoFactory.create(item);
  }

  public static String inspectionTypeLabel(String code) {
    if (code == null) {
      return null;
    }
    try {
      return InspectionType.valueOf(code).getLabel();
    } catch (IllegalArgumentException ex) {
      return code;
    }
  }

  public static String resultStatusLabel(String code) {
    if (code == null) {
      return null;
    }
    try {
      return InspectionResultStatus.valueOf(code).getLabel();
    } catch (IllegalArgumentException ex) {
      return code;
    }
  }
}
