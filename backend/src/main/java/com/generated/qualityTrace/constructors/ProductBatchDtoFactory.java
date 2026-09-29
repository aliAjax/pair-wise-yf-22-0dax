package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.BatchConclusion;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.types.ProductBatchResponse;
import com.generated.qualityTrace.utils.Formatters;

/** 批次响应 DTO 构造器，可附带当前结论一起展示。 */
public final class ProductBatchDtoFactory {

  private ProductBatchDtoFactory() {}

  public static ProductBatchResponse toResponse(ProductBatch batch, String orderNo,
                                                BatchConclusion conclusion) {
    if (batch == null) {
      return null;
    }
    String conclusionStatus = conclusion == null ? null : conclusion.getConclusionStatus();
    return new ProductBatchResponse(
        batch.getId(),
        batch.getBatchNo(),
        batch.getWorkOrderId(),
        orderNo,
        batch.getQuantity(),
        batch.getMaterialLotNo(),
        Formatters.dateTime(batch.getProducedAt()),
        batch.getBatchStatus(),
        conclusionStatus,
        Formatters.conclusion(conclusionStatus),
        conclusion == null ? null : conclusion.getReason(),
        Formatters.dateTime(batch.getCreatedAt()));
  }
}
