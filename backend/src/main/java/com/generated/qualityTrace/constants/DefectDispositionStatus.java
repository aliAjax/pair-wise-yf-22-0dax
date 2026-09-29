package com.generated.qualityTrace.constants;

/**
 * 不良处置状态：未处置 / 处置中 / 已处置。
 * 只有 CLOSED 才算“处置完”，PENDING、IN_PROGRESS 都会把批次结论压在待处理。
 */
public enum DefectDispositionStatus {
  PENDING("未处置", false),
  IN_PROGRESS("处置中", false),
  CLOSED("已处置", true);

  private final String label;
  private final boolean done;

  DefectDispositionStatus(String label, boolean done) {
    this.label = label;
    this.done = done;
  }

  public String getLabel() {
    return label;
  }

  public boolean isDone() {
    return done;
  }

  public static DefectDispositionStatus fromCode(String code) {
    for (DefectDispositionStatus value : values()) {
      if (value.name().equals(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("unknown DefectDispositionStatus: " + code);
  }
}
