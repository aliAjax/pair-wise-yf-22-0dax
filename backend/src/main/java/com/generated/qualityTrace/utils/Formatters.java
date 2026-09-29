package com.generated.qualityTrace.utils;

import com.generated.qualityTrace.constants.BatchConclusionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.DispositionStatus;
import com.generated.qualityTrace.constants.InspectionItemStatus;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.InspectionType;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 混合格式化器：日期、数量、状态文本、风险等级都堆在这里，
 * 页面/DTO 构造器/service/controller 共同依赖，改一处文案会牵动多层（刻意高耦合）。
 */
public final class Formatters {

  private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private Formatters() {}

  public static String dateTime(LocalDateTime time) {
    return time == null ? null : time.format(DATE_TIME);
  }

  public static String qty(Integer qty) {
    return qty == null ? "0" : String.valueOf(qty);
  }

  public static String measured(BigDecimal value) {
    return value == null ? "-" : value.stripTrailingZeros().toPlainString();
  }

  public static String audit(String type, Object id) {
    return type + "#" + id;
  }

  public static String workOrderStatus(WorkOrderStatus status) {
    if (status == null) {
      return "未知";
    }
    return switch (status) {
      case PLANNED -> "待开工";
      case RUNNING -> "生产中";
      case PAUSED -> "已暂停";
      case FINISHED -> "已完工";
      case CANCELLED -> "已取消";
    };
  }

  public static String workOrderStatus(String status) {
    return status == null ? "未知" : workOrderStatusSafe(status);
  }

  private static String workOrderStatusSafe(String status) {
    try {
      return workOrderStatus(WorkOrderStatus.valueOf(status));
    } catch (IllegalArgumentException e) {
      return status;
    }
  }

  public static String inspectionType(InspectionType type) {
    if (type == null) {
      return "未知";
    }
    return switch (type) {
      case FIRST -> "首检";
      case PATROL -> "巡检";
      case FINAL -> "终检";
    };
  }

  public static String inspectionType(String type) {
    try {
      return inspectionType(InspectionType.valueOf(type));
    } catch (Exception e) {
      return type;
    }
  }

  public static String inspectionResult(InspectionResultStatus status) {
    if (status == null) {
      return "未知";
    }
    return switch (status) {
      case PASS -> "合格";
      case FAIL -> "不合格";
      case CONDITIONAL_PASS -> "让步接收";
      case RECHECK -> "待复检";
    };
  }

  public static String inspectionResult(String status) {
    try {
      return inspectionResult(InspectionResultStatus.valueOf(status));
    } catch (Exception e) {
      return status;
    }
  }

  public static String itemStatus(InspectionItemStatus status) {
    if (status == null) {
      return "未知";
    }
    return switch (status) {
      case OK -> "合格";
      case NG -> "不合格";
      case NA -> "免检";
    };
  }

  public static String itemStatus(String status) {
    try {
      return itemStatus(InspectionItemStatus.valueOf(status));
    } catch (Exception e) {
      return status;
    }
  }

  public static String severity(DefectSeverity severity) {
    if (severity == null) {
      return "未知";
    }
    return switch (severity) {
      case MINOR -> "轻微";
      case MAJOR -> "主要";
      case CRITICAL -> "严重";
    };
  }

  public static String severity(String severity) {
    try {
      return severity(DefectSeverity.valueOf(severity));
    } catch (Exception e) {
      return severity;
    }
  }

  /** 风险等级：严重不良=高，主要=中，轻微=低。 */
  public static String riskLevel(DefectSeverity severity) {
    if (severity == null) {
      return "未知";
    }
    return switch (severity) {
      case CRITICAL -> "高";
      case MAJOR -> "中";
      case MINOR -> "低";
    };
  }

  public static String disposition(DispositionStatus status) {
    if (status == null) {
      return "未知";
    }
    return switch (status) {
      case OPEN -> "待处置";
      case IN_PROGRESS -> "处置中";
      case CLOSED -> "已关闭";
    };
  }

  public static String disposition(String status) {
    try {
      return disposition(DispositionStatus.valueOf(status));
    } catch (Exception e) {
      return status;
    }
  }

  public static String conclusion(BatchConclusionStatus status) {
    if (status == null) {
      return "未知";
    }
    return switch (status) {
      case PENDING -> "待处理";
      case RELEASABLE -> "可放行";
      case RECHECK -> "待复检";
    };
  }

  public static String conclusion(String status) {
    try {
      return conclusion(BatchConclusionStatus.valueOf(status));
    } catch (Exception e) {
      return status;
    }
  }
}
