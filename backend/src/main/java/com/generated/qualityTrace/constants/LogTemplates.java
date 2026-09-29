package com.generated.qualityTrace.constants;

/**
 * 日志模板集中管理。每个写操作都要：
 * 1) 用对应模板写应用日志（SLF4J）；
 * 2) 落一条 audit_log 操作日志。
 * 字段变更时必须同步改模板和调用处。
 */
public final class LogTemplates {

  // ---- 工单（4 条）----
  public static final String WORK_ORDER_CREATE = "工单创建: orderNo={}, productCode={}, plannedQty={}";
  public static final String WORK_ORDER_DUPLICATE_RETURN = "工单重复提交，按原记录返回: orderNo={}, id={}";
  public static final String WORK_ORDER_STATUS_CHANGED = "工单状态变更: orderNo={}, {} -> {}";
  public static final String WORK_ORDER_PROGRESS_UPDATED = "工单进度被批次更新: orderNo={}, batchNo={}";

  // ---- 批次（4 条）----
  public static final String BATCH_CREATE = "批次创建: batchNo={}, workOrderNo={}, quantity={}";
  public static final String BATCH_DUPLICATE_RETURN = "批次重复提交，按原记录返回: batchNo={}, id={}";
  public static final String BATCH_STATUS_CHANGED = "批次状态变更: batchNo={}, {} -> {}";
  public static final String BATCH_CONCLUSION_CHANGED = "批次结论变更: batchNo={}, {} -> {}, reason={}";

  // ---- 检验（4 条）----
  public static final String INSPECTION_CREATE = "检验单登记: batchNo={}, type={}, result={}, items={}";
  public static final String INSPECTION_ITEM_JUDGED = "检验项判定: itemCode={}, measured={}, limits=[{},{}], status={}";
  public static final String INSPECTION_RESULT_AUTO = "检验结论自动判定: inspectionId={}, result={}";
  public static final String INSPECTION_DUPLICATE_RETURN = "检验单重复提交，按原记录返回: requestId={}, id={}";

  // ---- 不良（4 条）----
  public static final String DEFECT_CREATE = "不良记录登记: batchNo={}, type={}, severity={}, qty={}";
  public static final String DEFECT_DUPLICATE_RETURN = "不良记录重复提交，按原记录返回: requestId={}, id={}";
  public static final String DEFECT_DISPOSITION_UPDATED = "不良处置进度更新: defectId={}, {} -> {}, action={}";
  public static final String DEFECT_CLOSED = "不良处置关闭: defectId={}, batchNo={}";

  // ---- 追溯 / 幂等 ----
  public static final String TRACE_QUERIED = "批次追溯查询: batchNo={}, conclusion={}";
  public static final String IDEMPOTENCY_REPLAY = "幂等命中: requestId={}, 目标={}#{}";
  public static final String IDEMPOTENCY_CONFLICT = "幂等冲突: requestId={} 被不同内容复用";

  private LogTemplates() {}
}
