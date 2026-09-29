package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.RoleConstants;
import com.generated.qualityTrace.constructors.QualityInspectionDtoFactory;
import com.generated.qualityTrace.middlewares.RequireRoles;
import com.generated.qualityTrace.services.ProductBatchService;
import com.generated.qualityTrace.services.QualityInspectionService;
import com.generated.qualityTrace.types.InspectionResponse;
import com.generated.qualityTrace.types.RegisterInspectionRequest;
import com.generated.qualityTrace.validators.InspectionValidator;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inspections")
public class QualityInspectionController {

  private final QualityInspectionService inspectionService;
  private final ProductBatchService batchService;
  private final InspectionValidator inspectionValidator;

  public QualityInspectionController(QualityInspectionService inspectionService,
                                     ProductBatchService batchService,
                                     InspectionValidator inspectionValidator) {
    this.inspectionService = inspectionService;
    this.batchService = batchService;
    this.inspectionValidator = inspectionValidator;
  }

  /** 登记检验（含检验项）。重复内容提交按原记录返回，duplicated=true。 */
  @PostMapping
  @RequireRoles({RoleConstants.QUALITY_INSPECTOR, RoleConstants.QUALITY_MANAGER})
  public InspectionResponse register(@RequestBody RegisterInspectionRequest request) {
    return ControllerSupport.call(() -> {
      inspectionValidator.validateRegister(request);
      return inspectionService.register(request);
    });
  }

  @GetMapping("/batch/{batchNo}")
  @RequireRoles({RoleConstants.QUALITY_INSPECTOR, RoleConstants.LINE_SUPERVISOR,
      RoleConstants.QUALITY_MANAGER, RoleConstants.AUDITOR})
  public List<InspectionResponse> listByBatch(@PathVariable String batchNo) {
    return ControllerSupport.call(() -> {
      var batch = batchService.requireByBatchNo(batchNo);
      return inspectionService.listByBatchId(batch.getId()).stream()
          .map(insp -> QualityInspectionDtoFactory.toResponse(insp, batchNo,
              inspectionService.listItems(insp.getId()), false))
          .toList();
    });
  }
}
