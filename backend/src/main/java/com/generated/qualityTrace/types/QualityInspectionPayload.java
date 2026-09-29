package com.generated.qualityTrace.types;

import java.util.List;

/**
 * 检验单登记请求：按批号定位批次，逐项写入检验结果。
 * resultStatus 留空时按检验项自动判定（任一项不合格 -> FAIL，全部合格 -> PASS）。
 */
public record QualityInspectionPayload(
    String requestId,
    String batchNo,
    String inspectorId,
    String inspectionType,
    String standardVersion,
    String resultStatus,
    List<InspectionItemResultPayload> items) {}
