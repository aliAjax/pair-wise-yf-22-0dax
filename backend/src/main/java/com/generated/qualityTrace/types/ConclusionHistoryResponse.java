package com.generated.qualityTrace.types;

public record ConclusionHistoryResponse(Long id, Long batchId, String fromStatus,
                                       String fromStatusText, String toStatus,
                                       String toStatusText, String reason, String changedBy,
                                       String changedAt) {
}
