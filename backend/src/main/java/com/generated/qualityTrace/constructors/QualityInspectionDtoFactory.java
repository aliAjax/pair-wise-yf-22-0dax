package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.InspectionItemResult;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.types.InspectionItemResponse;
import com.generated.qualityTrace.types.InspectionResponse;
import com.generated.qualityTrace.utils.Formatters;
import java.util.List;

/** 检验单响应 DTO 构造器（含检验项），duplicated 标记幂等命中。 */
public final class QualityInspectionDtoFactory {

  private QualityInspectionDtoFactory() {}

  public static InspectionItemResponse toItemResponse(InspectionItemResult item) {
    return new InspectionItemResponse(
        item.getId(),
        item.getInspectionId(),
        item.getItemCode(),
        item.getItemName(),
        Formatters.measured(item.getMeasuredValue()),
        Formatters.measured(item.getLimitMin()),
        Formatters.measured(item.getLimitMax()),
        item.getItemStatus(),
        Formatters.itemStatus(item.getItemStatus()));
  }

  public static InspectionResponse toResponse(QualityInspection inspection, String batchNo,
                                              List<InspectionItemResult> items,
                                              boolean duplicated) {
    List<InspectionItemResponse> itemResponses = items.stream()
        .map(QualityInspectionDtoFactory::toItemResponse)
        .toList();
    return new InspectionResponse(
        inspection.getId(),
        inspection.getBatchId(),
        batchNo,
        inspection.getInspectorId(),
        inspection.getInspectorName(),
        inspection.getInspectionType(),
        Formatters.inspectionType(inspection.getInspectionType()),
        inspection.getStandardVersion(),
        inspection.getResultStatus(),
        Formatters.inspectionResult(inspection.getResultStatus()),
        Formatters.dateTime(inspection.getInspectedAt()),
        duplicated,
        itemResponses);
  }
}
