package com.generated.qualityTrace.constants;

/** 错误消息模板集中定义，参数通过 String.format 填充。 */
public final class ErrorMessages {
  public static final String AUTH_REQUIRED = "missing token";
  public static final String RBAC_DENIED = "role denied";
  public static final String FIELD_REQUIRED = "字段 %s 不能为空";
  public static final String FIELD_INVALID = "字段 %s 取值非法: %s";
  public static final String WORK_ORDER_NOT_FOUND = "工单不存在: %s";
  public static final String WORK_ORDER_DUPLICATE = "工单号已存在: %s";
  public static final String BATCH_NOT_FOUND = "批次不存在: %s";
  public static final String BATCH_DUPLICATE = "批号已存在: %s";
  public static final String INSPECTION_NOT_FOUND = "检验单不存在: %s";
  public static final String DEFECT_NOT_FOUND = "不良记录不存在: %s";
  public static final String IDEMPOTENCY_CONFLICT = "requestId=%s 已用于不同的请求内容，拒绝重复提交";
  public static final String DISPOSITION_INVALID = "不良记录 %s 当前处置状态为 %s，不能更新为 %s";
  public static final String SERVICE_ERROR = "服务层处理失败: %s";
  public static final String CONTROLLER_ERROR = "控制器处理失败: %s";

  private ErrorMessages() {}

  public static String format(String template, Object... args) {
    return String.format(template, args);
  }
}
