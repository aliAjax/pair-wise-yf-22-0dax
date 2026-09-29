package com.generated.qualityTrace.constants;

/** 触发批次结论重算并记录结论变化的事件来源。 */
public enum ConclusionTrigger {
  BATCH_CREATED("批次登记"),
  INSPECTION_REGISTERED("检验单登记"),
  DEFECT_REGISTERED("不良登记"),
  DEFECT_DISPOSITION_UPDATED("不良处置进度更新");

  private final String label;

  ConclusionTrigger(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
