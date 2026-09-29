package com.generated.qualityTrace.types;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 按批号汇总的全链路追溯树：工单 + 批次 + 检验单(含检验项) + 不良处置进度 + 当前结论 + 结论变化历史。
 */
public record BatchTraceDto(
    String batchNo,
    WorkOrderDto workOrder,
    ProductBatchDto batch,
    List<QualityInspectionDto> inspections,
    QualityInspectionDto finalInspection,
    List<DefectRecordDto> defects,
    DispositionProgressDto dispositionProgress,
    String currentConclusion,
    String currentConclusionLabel,
    String conclusionReason,
    OffsetDateTime conclusionUpdatedAt,
    List<ConclusionHistoryDto> conclusionHistory) {}
