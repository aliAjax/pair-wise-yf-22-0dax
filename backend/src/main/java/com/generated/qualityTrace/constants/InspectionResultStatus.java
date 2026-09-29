package com.generated.qualityTrace.constants;

/** 检验结论：合格 / 不合格 / 条件放行 / 待复检。 */
public enum InspectionResultStatus {
  PASS("合格"),
  FAIL("不合格"),
  CONDITIONAL_PASS("条件放行"),
  RECHECK("待复检");

  private final String label;

  InspectionResultStatus(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  /** 只有终检明确 PASS 才满足放行条件，CONDITIONAL_PASS 仍需复检。 */
  public boolean isReleaseReady() {
    return this == PASS;
  }

  public static InspectionResultStatus fromCode(String code) {
    for (InspectionResultStatus value : values()) {
      if (value.name().equals(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("unknown InspectionResultStatus: " + code);
  }
}
