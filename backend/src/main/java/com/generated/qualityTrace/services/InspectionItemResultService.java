package com.generated.qualityTrace.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.repositories.InspectionItemResultRepository;
import com.generated.qualityTrace.types.InspectionItemResultDto;

/** 检验项查询服务：登记随检验单一起完成，这里只提供按检验单读取。 */
@Service
public class InspectionItemResultService {

  private final InspectionItemResultRepository repository;
  private final QualityInspectionService inspectionService;

  public InspectionItemResultService(
      InspectionItemResultRepository repository, QualityInspectionService inspectionService) {
    this.repository = repository;
    this.inspectionService = inspectionService;
  }

  public List<InspectionItemResultDto> listByInspection(Long inspectionId) {
    return inspectionService.listItems(inspectionId);
  }
}
