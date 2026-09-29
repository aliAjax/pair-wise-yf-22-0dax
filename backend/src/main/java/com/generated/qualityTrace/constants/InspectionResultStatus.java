package com.generated.qualityTrace.constants;

/**
 * 检验结论。出现位置：model/QualityInspection、constructors/QualityInspectionDtoFactory、
 * services/QualityInspectionService、validators/InspectionValidator、utils/Formatters、constants/LogTemplates。
 * CONDITIONAL_PASS 为让步接收，仍需结合不良处置与终检结果决定批次能否放行。
 */
public enum InspectionResultStatus {
  PASS, FAIL, CONDITIONAL_PASS, RECHECK
}
