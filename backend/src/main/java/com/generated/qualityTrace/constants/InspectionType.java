package com.generated.qualityTrace.constants;

/** 检验类型：首检 / 巡检 / 终检。 */
public enum InspectionType {
  FIRST("首检"),
  IN_PROCESS("巡检"),
  FINAL("终检");

  private final String label;

  InspectionType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static InspectionType fromCode(String code) {
    for (InspectionType value : values()) {
      if (value.name().equals(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("unknown InspectionType: " + code);
  }
}
