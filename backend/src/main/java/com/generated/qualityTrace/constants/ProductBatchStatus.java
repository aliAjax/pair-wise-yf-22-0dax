package com.generated.qualityTrace.constants;

/**
 * 批次状态：与批次当前质量结论联动。
 * ACTIVE          生产中（尚无结论）
 * HOLD            暂扣（存在未处置严重不良）
 * PENDING_RECHECK 待复检
 * RELEASABLE      可放行
 * RELEASED        已放行
 */
public enum ProductBatchStatus {
  ACTIVE("生产中"),
  HOLD("暂扣待处理"),
  PENDING_RECHECK("待复检"),
  RELEASABLE("可放行"),
  RELEASED("已放行");

  private final String label;

  ProductBatchStatus(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static ProductBatchStatus fromConclusion(BatchConclusionCode conclusion) {
    if (conclusion == null) {
      return ACTIVE;
    }
    switch (conclusion) {
      case PENDING_DISPOSITION:
        return HOLD;
      case PENDING_RECHECK:
        return PENDING_RECHECK;
      case RELEASABLE:
        return RELEASABLE;
      default:
        return ACTIVE;
    }
  }
}
