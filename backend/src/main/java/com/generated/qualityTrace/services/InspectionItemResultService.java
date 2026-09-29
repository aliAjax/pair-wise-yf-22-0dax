package com.generated.qualityTrace.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.generated.qualityTrace.models.InspectionItemResult;
import com.generated.qualityTrace.repositories.InspectionItemResultRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/** 检验项结果查询服务（写入随检验登记一起完成，见 QualityInspectionService）。 */
@Service
public class InspectionItemResultService {

  private final InspectionItemResultRepository itemRepository;

  public InspectionItemResultService(InspectionItemResultRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  public List<InspectionItemResult> listByInspection(Long inspectionId) {
    return itemRepository.selectList(new LambdaQueryWrapper<InspectionItemResult>()
        .eq(InspectionItemResult::getInspectionId, inspectionId)
        .orderByAsc(InspectionItemResult::getId));
  }
}
