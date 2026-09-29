package com.generated.qualityTrace.constants;

import java.text.MessageFormat;

/**
 * 集中操作/追溯日志模板。每个核心实体至少 4 条，所有写操作都要落审计日志。
 * 占位符顺序见各服务调用处；新增字段或动作时必须同步本文件与调用处。
 */
public final class LogTemplates {

  private LogTemplates() {}

  // 工单
  public static final String WORK_ORDER_CREATE = "工单创建：orderNo={0}，productCode={1}，计划数量={2}";
  public static final String WORK_ORDER_START = "工单开工：orderNo={0}，状态 {1} -> {2}";
  public static final String WORK_ORDER_PAUSE = "工单暂停：orderNo={0}，状态 {1} -> {2}";
  public static final String WORK_ORDER_FINISH = "工单完工：orderNo={0}，状态 {1} -> {2}，关联批次 {3} 个";

  // 批次
  public static final String BATCH_CREATE = "批次创建：batchNo={0}，工单={1}，数量={2}";
  public static final String BATCH_STATUS_SYNC = "批次状态同步：batchNo={0}，状态 -> {1}";
  public static final String BATCH_TRACE_VIEW = "批次追溯查询：batchNo={0}";
  public static final String BATCH_PROGRESS_UPDATE = "工单进度更新：orderNo={0}，已产出批次 {1} 个，累计数量 {2}";

  // 检验单
  public static final String INSPECTION_REGISTER = "检验单登记：batchNo={0}，类型={1}，结论={2}，检验项 {3} 项";
  public static final String INSPECTION_DUPLICATE = "检验单重复提交，按原记录返回：inspectionId={0}，batchNo={1}";
  public static final String INSPECTION_FINAL_PASS = "终检通过：batchNo={0}，inspectionId={1}";
  public static final String INSPECTION_FINAL_FAIL = "终检未通过：batchNo={0}，inspectionId={1}，不合格项 {2} 个";

  // 不良记录
  public static final String DEFECT_REGISTER = "不良登记：batchNo={0}，类型={1}，严重度={2}，数量={3}";
  public static final String DEFECT_DUPLICATE = "不良记录重复提交，按原记录返回：defectId={0}，batchNo={1}";
  public static final String DEFECT_DISPOSE = "不良处置推进：defectId={0}，处置状态 {1} -> {2}";
  public static final String DEFECT_CLOSE = "不良关闭：defectId={0}，批次={1}，处置说明={2}";

  // 批次结论
  public static final String CONCLUSION_HOLD_PENDING = "批次结论保持待处理：batchNo={0}，未处置严重不良 {1} 条";
  public static final String CONCLUSION_CHANGE = "批次结论变更：batchNo={0}，{1} -> {2}，原因={3}";
  public static final String CONCLUSION_RELEASE = "批次可放行：batchNo={0}，终检结论={1}";
  public static final String CONCLUSION_RECHECK = "批次待复检：batchNo={0}，原因={1}";

  // 认证 / 审计
  public static final String AUTH_LOGIN = "用户登录：username={0}，角色={1}";
  public static final String AUDIT_EXPORT = "追溯/审计数据导出：targetType={0}，targetId={1}";

  public static String render(String template, Object... args) {
    if (args == null || args.length == 0) {
      return template;
    }
    return MessageFormat.format(template, args);
  }
}
