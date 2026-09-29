package com.generated.qualityTrace.types;

public record ProductBatchResponse(Long id, String batchNo, Long workOrderId, String orderNo,
                                  Integer quantity, String materialLotNo, String producedAt,
                                  String batchStatus, String conclusionStatus,
                                  String conclusionStatusText, String conclusionReason,
                                  String createdAt) {
}
