package com.generated.qualityTrace.types;

import java.time.OffsetDateTime;
import java.util.List;

public record QualityInspectionDto(
    Long id,
    String batchNo,
    String inspectorId,
    String inspectionType,
    String inspectionTypeLabel,
    String standardVersion,
    String resultStatus,
    String resultStatusLabel,
    OffsetDateTime inspectedAt,
    List<InspectionItemResultDto> items,
    OffsetDateTime createdAt) {}
