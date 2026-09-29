package com.generated.qualityTrace.constructors;

import java.util.List;
import com.generated.qualityTrace.constants.ConclusionTrigger;
import com.generated.qualityTrace.models.BatchConclusionHistory;
import com.generated.qualityTrace.types.ConclusionHistoryDto;

/** 结论变化历史 model -> 响应 DTO 的唯一构造入口。 */
public final class ConclusionHistoryDtoFactory {

  private ConclusionHistoryDtoFactory() {}

  public static ConclusionHistoryDto create(BatchConclusionHistory history) {
    return new ConclusionHistoryDto(
        history.id,
        history.fromConclusion,
        ProductBatchDtoFactory.conclusionLabel(history.fromConclusion),
        history.toConclusion,
        ProductBatchDtoFactory.conclusionLabel(history.toConclusion),
        history.reason,
        history.triggerEvent,
        triggerLabel(history.triggerEvent),
        history.actor,
        history.createdAt);
  }

  public static List<ConclusionHistoryDto> createList(List<BatchConclusionHistory> histories) {
    return histories.stream().map(ConclusionHistoryDtoFactory::create).toList();
  }

  private static String triggerLabel(String code) {
    if (code == null) {
      return null;
    }
    try {
      return ConclusionTrigger.valueOf(code).getLabel();
    } catch (IllegalArgumentException ex) {
      return code;
    }
  }
}
