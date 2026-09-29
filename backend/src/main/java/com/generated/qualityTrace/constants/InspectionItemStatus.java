package com.generated.qualityTrace.constants;

/** 检验项判定：合格 / 不合格。 */
public enum InspectionItemStatus {
  QUALIFIED("合格"),
  UNQUALIFIED("不合格");

  private final String label;

  InspectionItemStatus(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static InspectionItemStatus fromCode(String code) {
    for (InspectionItemStatus value : values()) {
      if (value.name().equals(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("unknown InspectionItemStatus: " + code);
  }
}
