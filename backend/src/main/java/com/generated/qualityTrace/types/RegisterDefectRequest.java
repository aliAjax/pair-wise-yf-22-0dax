package com.generated.qualityTrace.types;

import java.time.LocalDateTime;

/**
 * 不良登记请求。severity: MINOR/MAJOR/CRITICAL（见 constants/DefectSeverity）。
 * 同 batchNo + 同内容重复提交会按原记录返回（幂等），避免不良数量被重复累计。
 */
public record RegisterDefectRequest(String batchNo, String defectType, Integer defectQty,
                                   String severity, String rootCause, LocalDateTime createdAt) {
}
