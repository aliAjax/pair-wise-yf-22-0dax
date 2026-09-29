package com.generated.qualityTrace.types;

/**
 * 不良处置推进请求。
 * dispositionStatus: IN_PROGRESS/CLOSED（OPEN 只能由登记产生），见 constants/DispositionStatus。
 * 已关闭的不良重复提交直接按原记录返回，不改变批次结论。
 */
public record DisposeDefectRequest(String dispositionStatus, String dispositionNote) {
}
