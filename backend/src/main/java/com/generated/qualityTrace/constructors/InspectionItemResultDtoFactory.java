package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.InspectionItemResult;
import com.generated.qualityTrace.types.RegisterInspectionRequest;
import com.generated.qualityTrace.utils.IdGenerator;

/** 检验项 model 构造器：负责主键与父检验单 id 装配。 */
public final class InspectionItemResultDtoFactory {

  private InspectionItemResultDtoFactory() {}

  public static InspectionItemResult fromInput(Long inspectionId,
                                               RegisterInspectionRequest.ItemInput input,
                                               String itemStatus) {
    InspectionItemResult item = new InspectionItemResult();
    item.setId(IdGenerator.nextId());
    item.setInspectionId(inspectionId);
    item.setItemCode(input.itemCode());
    item.setItemName(input.itemName());
    item.setMeasuredValue(input.measuredValue());
    item.setLimitMin(input.limitMin());
    item.setLimitMax(input.limitMax());
    item.setItemStatus(itemStatus);
    return item;
  }
}
