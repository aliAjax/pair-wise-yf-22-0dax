package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.RoleConstants;
import com.generated.qualityTrace.constructors.QualityInspectionDtoFactory;
import com.generated.qualityTrace.middlewares.RequireRoles;
import com.generated.qualityTrace.services.InspectionItemResultService;
import com.generated.qualityTrace.types.InspectionItemResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 检验项结果只读查询（写入在检验登记接口内逐项完成）。 */
@RestController
@RequestMapping("/api/inspection-item-results")
public class InspectionItemResultController {

  private final InspectionItemResultService itemService;

  public InspectionItemResultController(InspectionItemResultService itemService) {
    this.itemService = itemService;
  }

  @GetMapping("/inspection/{inspectionId}")
  @RequireRoles({RoleConstants.QUALITY_INSPECTOR, RoleConstants.LINE_SUPERVISOR,
      RoleConstants.QUALITY_MANAGER, RoleConstants.AUDITOR})
  public List<InspectionItemResponse> listByInspection(@PathVariable Long inspectionId) {
    return ControllerSupport.call(() -> itemService.listByInspection(inspectionId).stream()
        .map(QualityInspectionDtoFactory::toItemResponse)
        .toList());
  }
}
