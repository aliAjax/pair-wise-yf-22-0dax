package com.generated.qualityTrace.types;

import java.math.BigDecimal;

public record InspectionItemResultDto(
    Long id,
    String itemCode,
    String itemName,
    BigDecimal measuredValue,
    BigDecimal limitMin,
    BigDecimal limitMax,
    String itemStatus,
    String itemStatusLabel) {}
