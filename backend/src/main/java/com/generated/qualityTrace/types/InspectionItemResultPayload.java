package com.generated.qualityTrace.types;

import java.math.BigDecimal;

/** 单个检验项录入：itemCode/itemName/measuredValue 必填，限值可缺省（缺省时按显式状态或合格处理）。 */
public record InspectionItemResultPayload(
    String itemCode,
    String itemName,
    BigDecimal measuredValue,
    BigDecimal limitMin,
    BigDecimal limitMax,
    String itemStatus) {}
