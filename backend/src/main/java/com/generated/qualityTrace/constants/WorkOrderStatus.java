package com.generated.qualityTrace.constants;

/** 工单状态：计划 / 生产中 / 暂停 / 完工 / 取消。 */
public enum WorkOrderStatus {
  PLANNED("待开工"),
  RUNNING("生产中"),
  PAUSED("已暂停"),
  FINISHED("已完工"),
  CANCELLED("已取消");

  private final String label;

  WorkOrderStatus(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static WorkOrderStatus fromCode(String code) {
    for (WorkOrderStatus value : values()) {
      if (value.name().equals(code)) {
        return value;
      }
    }
    throw new IllegalArgumentException("unknown WorkOrderStatus: " + code);
  }
}
