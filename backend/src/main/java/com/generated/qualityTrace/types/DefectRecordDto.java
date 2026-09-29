package com.generated.qualityTrace.types;

import java.time.OffsetDateTime;

public record DefectRecordDto(
    Long id,
    String batchNo,
    String defectType,
    Integer defectQty,
    String severity,
    String severityLabel,
    boolean serious,
    String rootCause,
    String dispositionStatus,
    String dispositionStatusLabel,
    boolean dispositionDone,
    String dispositionAction,
    String disposedBy,
    OffsetDateTime disposedAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
