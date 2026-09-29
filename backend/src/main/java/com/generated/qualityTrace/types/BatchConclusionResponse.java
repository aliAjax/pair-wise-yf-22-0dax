package com.generated.qualityTrace.types;

public record BatchConclusionResponse(Long id, Long batchId, String batchNo,
                                     String conclusionStatus, String conclusionStatusText,
                                     String reason, Long finalInspectionId, String updatedAt) {
}
