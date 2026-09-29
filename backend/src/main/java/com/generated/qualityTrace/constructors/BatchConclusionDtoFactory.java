package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.BatchConclusion;
import com.generated.qualityTrace.models.BatchConclusionHistory;
import com.generated.qualityTrace.types.BatchConclusionResponse;
import com.generated.qualityTrace.types.ConclusionHistoryResponse;
import com.generated.qualityTrace.utils.Formatters;

/** 批次结论及结论变化历史的 DTO 构造器。 */
public final class BatchConclusionDtoFactory {

  private BatchConclusionDtoFactory() {}

  public static BatchConclusionResponse toResponse(BatchConclusion conclusion, String batchNo) {
    if (conclusion == null) {
      return null;
    }
    return new BatchConclusionResponse(
        conclusion.getId(),
        conclusion.getBatchId(),
        batchNo,
        conclusion.getConclusionStatus(),
        Formatters.conclusion(conclusion.getConclusionStatus()),
        conclusion.getReason(),
        conclusion.getFinalInspectionId(),
        Formatters.dateTime(conclusion.getUpdatedAt()));
  }

  public static ConclusionHistoryResponse toHistoryResponse(BatchConclusionHistory history) {
    return new ConclusionHistoryResponse(
        history.getId(),
        history.getBatchId(),
        history.getFromStatus(),
        Formatters.conclusion(history.getFromStatus()),
        history.getToStatus(),
        Formatters.conclusion(history.getToStatus()),
        history.getReason(),
        history.getChangedBy(),
        Formatters.dateTime(history.getChangedAt()));
  }
}
