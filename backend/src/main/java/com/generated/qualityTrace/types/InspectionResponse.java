package com.generated.qualityTrace.types;

import java.util.List;

/**
 * 检验单响应。duplicated=true 表示命中幂等：本次未新写数据，返回的是原记录。
 */
public record InspectionResponse(Long id, Long batchId, String batchNo, Long inspectorId,
                                String inspectorName, String inspectionType,
                                String inspectionTypeText, String standardVersion,
                                String resultStatus, String resultStatusText, String inspectedAt,
                                Boolean duplicated, List<InspectionItemResponse> items) {
}
