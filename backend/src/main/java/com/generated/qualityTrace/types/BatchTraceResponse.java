package com.generated.qualityTrace.types;

import java.util.List;

/**
 * 按批号汇总的批次质量追溯树：工单信息 + 批次 + 检验单（含检验项）+ 不良记录（含处置进度）
 * + 当前结论 + 结论变化历史 + 汇总计数。
 */
public record BatchTraceResponse(
    String batchNo,
    ProductBatchResponse batch,
    WorkOrderResponse workOrder,
    List<InspectionResponse> inspections,
    List<DefectRecordResponse> defects,
    BatchConclusionResponse conclusion,
    List<ConclusionHistoryResponse> conclusionHistory,
    TraceSummary summary) {

  /** 汇总：不良处置进度 + 终检情况一览，帮助客户追查时直接看到风险敞口。 */
  public record TraceSummary(
      int inspectionCount,
      int finalInspectionCount,
      String finalInspectionStatus,
      String finalInspectionStatusText,
      int defectCount,
      int criticalOpen,
      int majorOpen,
      int minorOpen,
      int closedDefectCount,
      int totalDefectQty,
      boolean hasUnhandledSevereDefect,
      String currentConclusion,
      String currentConclusionText) {
  }
}
