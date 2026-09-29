package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.InspectionItemStatus;
import com.generated.qualityTrace.models.InspectionItemResult;
import com.generated.qualityTrace.types.InspectionItemResultDto;

/** 检验项 model -> 响应 DTO 的唯一构造入口。 */
public final class InspectionItemResultDtoFactory {

  private InspectionItemResultDtoFactory() {}

  public static InspectionItemResultDto create(InspectionItemResult item) {
    return new InspectionItemResultDto(
        item.id,
        item.itemCode,
        item.itemName,
        item.measuredValue,
        item.limitMin,
        item.limitMax,
        item.itemStatus,
        itemStatusLabel(item.itemStatus));
  }

  public static String itemStatusLabel(String code) {
    if (code == null) {
      return null;
    }
    try {
      return InspectionItemStatus.valueOf(code).getLabel();
    } catch (IllegalArgumentException ex) {
      return code;
    }
  }
}
