package com.generated.qualityTrace.types;

public record InspectionItemResponse(Long id, Long inspectionId, String itemCode, String itemName,
                                    String measuredValue, String limitMin, String limitMax,
                                    String itemStatus, String itemStatusText) {
}
