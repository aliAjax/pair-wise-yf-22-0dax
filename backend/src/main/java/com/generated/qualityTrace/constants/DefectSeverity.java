package com.generated.qualityTrace.constants;

/** 不良严重度：轻微 / 主要 / 严重。MAJOR 与 CRITICAL 属于会拦住放行的严重不良。 */
public enum DefectSeverity {
  MINOR("轻微", false),
  MAJOR("主要", true),
  CRITICAL("严重", true);

  private final String label;
  private final boolean serious;

  DefectSeverity(String label, boolean serious) {
    this.label = label;
    this.serious = serious;
  }

  public String getLabel() {
    return label;
  }

  public boolean isSerious() {
    return serious;
  }

  public static DefectSeverity fromCode(String code) {
    for (DefectSeverity value : values()) {
      if (value.name().equals(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("unknown DefectSeverity: " + code);
  }
}
