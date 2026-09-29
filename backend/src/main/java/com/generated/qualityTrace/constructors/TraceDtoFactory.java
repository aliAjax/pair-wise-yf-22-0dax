package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.BatchConclusionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.DispositionStatus;
import com.generated.qualityTrace.constants.InspectionType;
import com.generated.qualityTrace.models.BatchConclusion;
import com.generated.qualityTrace.models.BatchConclusionHistory;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.InspectionItemResult;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.types.BatchTraceResponse;
import com.generated.qualityTrace.types.DefectRecordResponse;
import com.generated.qualityTrace.types.InspectionResponse;
import com.generated.qualityTrace.utils.Formatters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** 批次追溯树 DTO 构造器：把工单/批次/检验单/不良/结论按批号拼成一个响应。 */
public final class TraceDtoFactory {

  private TraceDtoFactory() {}

  public static BatchTraceResponse build(ProductBatch batch, WorkOrder workOrder,
                                         List<QualityInspection> inspections,
                                         Map<Long, List<InspectionItemResult>> itemsByInspection,
                                         List<DefectRecord> defects,
                                         BatchConclusion conclusion,
                                         List<BatchConclusionHistory> history) {
    String batchNo = batch.getBatchNo();

    List<InspectionResponse> inspectionResponses = inspections.stream()
        .sorted(Comparator.comparing(QualityInspection::getInspectedAt,
            Comparator.nullsLast(Comparator.naturalOrder())))
        .map(insp -> QualityInspectionDtoFactory.toResponse(insp, batchNo,
            itemsByInspection.getOrDefault(insp.getId(), List.of()), false))
        .toList();

    List<DefectRecordResponse> defectResponses = defects.stream()
        .sorted(Comparator.comparing(DefectRecord::getCreatedAt,
            Comparator.nullsLast(Comparator.naturalOrder())))
        .map(defect -> DefectRecordDtoFactory.toResponse(defect, batchNo, false))
        .toList();

    List<com.generated.qualityTrace.types.ConclusionHistoryResponse> historyResponses = history.stream()
        .sorted(Comparator.comparing(BatchConclusionHistory::getChangedAt,
            Comparator.nullsLast(Comparator.naturalOrder())))
        .map(BatchConclusionDtoFactory::toHistoryResponse)
        .toList();

    QualityInspection latestFinal = inspections.stream()
        .filter(i -> InspectionType.FINAL.name().equals(i.getInspectionType()))
        .max(Comparator.comparing(QualityInspection::getInspectedAt,
            Comparator.nullsFirst(Comparator.naturalOrder())))
        .orElse(null);

    int criticalOpen = countOpen(defects, DefectSeverity.CRITICAL);
    int majorOpen = countOpen(defects, DefectSeverity.MAJOR);
    int minorOpen = countOpen(defects, DefectSeverity.MINOR);
    int closed = (int) defects.stream()
        .filter(d -> DispositionStatus.CLOSED.name().equals(d.getDispositionStatus()))
        .count();
    int totalQty = defects.stream()
        .mapToInt(d -> d.getDefectQty() == null ? 0 : d.getDefectQty())
        .sum();
    boolean severeOpen = criticalOpen + majorOpen > 0;
    String currentConclusion = conclusion == null ? null : conclusion.getConclusionStatus();

    BatchTraceResponse.TraceSummary summary = new BatchTraceResponse.TraceSummary(
        inspections.size(),
        latestFinal == null ? 0 : 1,
        latestFinal == null ? null : latestFinal.getResultStatus(),
        latestFinal == null ? "未终检"
            : Formatters.inspectionResult(latestFinal.getResultStatus()),
        defects.size(),
        criticalOpen,
        majorOpen,
        minorOpen,
        closed,
        totalQty,
        severeOpen,
        currentConclusion,
        Formatters.conclusion(currentConclusion));

    return new BatchTraceResponse(
        batchNo,
        ProductBatchDtoFactory.toResponse(batch,
            workOrder == null ? null : workOrder.getOrderNo(), conclusion),
        WorkOrderDtoFactory.toResponse(workOrder),
        inspectionResponses,
        defectResponses,
        BatchConclusionDtoFactory.toResponse(conclusion, batchNo),
        historyResponses,
        summary);
  }

  private static int countOpen(List<DefectRecord> defects, DefectSeverity severity) {
    return (int) defects.stream()
        .filter(d -> severity.name().equals(d.getSeverity()))
        .filter(d -> !DispositionStatus.CLOSED.name().equals(d.getDispositionStatus()))
        .count();
  }

  /** 批次创建后的初始结论 model 装配。 */
  public static BatchConclusion initialConclusion(Long conclusionId, Long batchId, String reason) {
    BatchConclusion conclusion = new BatchConclusion();
    conclusion.setId(conclusionId);
    conclusion.setBatchId(batchId);
    conclusion.setConclusionStatus(BatchConclusionStatus.PENDING.name());
    conclusion.setReason(reason);
    conclusion.setUpdatedAt(java.time.LocalDateTime.now());
    return conclusion;
  }
}
