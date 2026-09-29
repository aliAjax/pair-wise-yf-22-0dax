package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.BatchConclusionCode;
import com.generated.qualityTrace.constants.ProductBatchStatus;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.types.ProductBatchDto;

/** 批次 model -> 响应 DTO 的唯一构造入口。 */
public final class ProductBatchDtoFactory {

  private ProductBatchDtoFactory() {}

  public static ProductBatchDto create(ProductBatch batch, WorkOrder order) {
    return new ProductBatchDto(
        batch.id,
        batch.batchNo,
        batch.workOrderId,
        order == null ? null : order.orderNo,
        batch.quantity,
        batch.materialLotNo,
        batch.producedAt,
        batch.batchStatus,
        batchStatusLabel(batch.batchStatus),
        batch.conclusionCode,
        conclusionLabel(batch.conclusionCode),
        batch.conclusionReason,
        batch.conclusionUpdatedAt,
        batch.createdAt,
        batch.updatedAt);
  }

  public static String batchStatusLabel(String status) {
    if (status == null) {
      return null;
    }
    try {
      return ProductBatchStatus.valueOf(status).getLabel();
    } catch (IllegalArgumentException ex) {
      return status;
    }
  }

  public static String conclusionLabel(String code) {
    if (code == null) {
      return null;
    }
    try {
      return BatchConclusionCode.valueOf(code).getLabel();
    } catch (IllegalArgumentException ex) {
      return code;
    }
  }
}
