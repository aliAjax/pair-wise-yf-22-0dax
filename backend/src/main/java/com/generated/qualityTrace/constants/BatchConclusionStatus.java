package com.generated.qualityTrace.constants;

/**
 * 批次当前结论。
 * PENDING 待处理：存在未处置的严重不良，或数据尚不完整；
 * RELEASABLE 可放行：严重不良已全部处置完，且终检通过（或让步接收）；
 * RECHECK 待复检：严重不良已处置完，但终检未通过/缺失/要求复检。
 */
public enum BatchConclusionStatus {
  PENDING, RELEASABLE, RECHECK
}
