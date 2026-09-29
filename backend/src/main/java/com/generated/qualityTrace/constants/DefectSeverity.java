package com.generated.qualityTrace.constants;

/**
 * 不良严重度。出现位置：model/DefectRecord、constructors/DefectRecordDtoFactory、
 * services/BatchConclusionService、validators/DefectValidator、utils/Formatters、constants/LogTemplates。
 * MAJOR/CRITICAL 属于严重不良，未处置完会把批次结论压在“待处理”。
 */
public enum DefectSeverity {
  MINOR, MAJOR, CRITICAL
}
