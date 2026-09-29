package com.generated.qualityTrace.types;

import java.time.OffsetDateTime;

/** 追溯树中的结论变化条目。 */
public record ConclusionHistoryDto(
    Long id,
    String fromConclusion,
    String fromConclusionLabel,
    String toConclusion,
    String toConclusionLabel,
    String reason,
    String triggerEvent,
    String triggerEventLabel,
    String actor,
    OffsetDateTime createdAt) {}
