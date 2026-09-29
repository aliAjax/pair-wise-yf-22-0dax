package com.generated.qualityTrace.types;

/**
 * 不良记录响应。duplicated=true 表示重复提交命中原记录，不良数量未再次累计。
 */
public record DefectRecordResponse(Long id, Long batchId, String batchNo, String defectType,
                                  Integer defectQty, String severity, String severityText,
                                  String riskLevel, String rootCause, String dispositionStatus,
                                  String dispositionStatusText, String dispositionNote,
                                  String registeredBy, String disposedBy, String createdAt,
                                  String disposedAt, Boolean duplicated) {
}
