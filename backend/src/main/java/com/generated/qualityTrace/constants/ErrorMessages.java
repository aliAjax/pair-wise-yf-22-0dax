package com.generated.qualityTrace.constants;

/** 集中错误消息模板，{0} 等占位符由 java.text.MessageFormat 填充。 */
public final class ErrorMessages {
  public static final String AUTH_REQUIRED = "缺少登录令牌或令牌已失效";
  public static final String RBAC_DENIED = "当前角色无权执行该操作";
  public static final String RATE_LIMITED = "请求过于频繁，请稍后再试";
  public static final String BAD_CREDENTIALS = "用户名或密码错误";
  public static final String WORK_ORDER_NOT_FOUND = "工单不存在：{0}";
  public static final String WORK_ORDER_NO_DUPLICATED = "工单号已存在：{0}";
  public static final String WORK_ORDER_STATUS_INVALID = "非法工单状态：{0}";
  public static final String WORK_ORDER_STATUS_TRANSITION = "工单不允许从 {0} 变更到 {1}";
  public static final String BATCH_NOT_FOUND = "批次不存在：{0}";
  public static final String BATCH_NO_DUPLICATED = "批号已存在：{0}";
  public static final String INSPECTION_ITEMS_EMPTY = "检验单至少需要一个检验项";
  public static final String INSPECTION_TYPE_INVALID = "非法检验类型：{0}";
  public static final String ITEM_LIMIT_INVALID = "检验项 {0} 的上下限不合法";
  public static final String DEFECT_QTY_INVALID = "不良数量必须大于 0";
  public static final String DEFECT_SEVERITY_INVALID = "非法不良严重度：{0}";
  public static final String DEFECT_NOT_FOUND = "不良记录不存在：{0}";
  public static final String DEFECT_ALREADY_CLOSED = "不良记录 {0} 已关闭，不能重复处置";
  public static final String DISPOSITION_STATUS_INVALID = "非法处置状态：{0}";
  public static final String FIELD_REQUIRED = "字段 {0} 不能为空";
  public static final String INTERNAL_ERROR = "服务内部错误";

  private ErrorMessages() {}
}
