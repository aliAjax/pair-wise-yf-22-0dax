package com.generated.qualityTrace.constants;

/**
 * 批次当前质量结论：
 * PENDING_DISPOSITION 待处理（还有未处置的严重不良）
 * PENDING_RECHECK     待复检（严重不良已处置完，但终检未完成或终检未通过）
 * RELEASABLE          可放行（严重不良全部处置完且终检 PASS）
 */
public enum BatchConclusionCode {
  PENDING_DISPOSITION("待处理"),
  PENDING_RECHECK("待复检"),
  RELEASABLE("可放行");

  private final String label;

  BatchConclusionCode(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static BatchConclusionCode fromCode(String code) {
    for (BatchConclusionCode value : values()) {
      if (value.name().equals(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("unknown BatchConclusionCode: " + code);
  }
}
