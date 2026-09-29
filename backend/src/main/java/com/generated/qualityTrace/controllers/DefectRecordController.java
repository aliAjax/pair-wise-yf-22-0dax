package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.RoleConstants;
import com.generated.qualityTrace.constructors.DefectRecordDtoFactory;
import com.generated.qualityTrace.middlewares.RequireRoles;
import com.generated.qualityTrace.services.DefectRecordService;
import com.generated.qualityTrace.services.ProductBatchService;
import com.generated.qualityTrace.types.DefectRecordResponse;
import com.generated.qualityTrace.types.DisposeDefectRequest;
import com.generated.qualityTrace.types.RegisterDefectRequest;
import com.generated.qualityTrace.validators.DefectValidator;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/defects")
public class DefectRecordController {

  private final DefectRecordService defectService;
  private final ProductBatchService batchService;
  private final DefectValidator defectValidator;

  public DefectRecordController(DefectRecordService defectService,
                                ProductBatchService batchService,
                                DefectValidator defectValidator) {
    this.defectService = defectService;
    this.batchService = batchService;
    this.defectValidator = defectValidator;
  }

  /** 登记不良。重复内容提交按原记录返回，duplicated=true，不良数量不重复累计。 */
  @PostMapping
  @RequireRoles({RoleConstants.QUALITY_INSPECTOR, RoleConstants.QUALITY_MANAGER})
  public DefectRecordResponse register(@RequestBody RegisterDefectRequest request) {
    return ControllerSupport.call(() -> {
      defectValidator.validateRegister(request);
      return defectService.register(request);
    });
  }

  /** 推进处置：IN_PROGRESS 处置中 / CLOSED 已关闭；关闭后批次结论才可能离开待处理。 */
  @PostMapping("/{id}/disposition")
  @RequireRoles({RoleConstants.QUALITY_INSPECTOR, RoleConstants.LINE_SUPERVISOR,
      RoleConstants.QUALITY_MANAGER})
  public DefectRecordResponse dispose(@PathVariable Long id,
                                      @RequestBody DisposeDefectRequest request) {
    return ControllerSupport.call(() -> {
      defectValidator.validateDispose(request);
      return defectService.dispose(id, request);
    });
  }

  @GetMapping("/batch/{batchNo}")
  @RequireRoles({RoleConstants.QUALITY_INSPECTOR, RoleConstants.LINE_SUPERVISOR,
      RoleConstants.QUALITY_MANAGER, RoleConstants.AUDITOR})
  public List<DefectRecordResponse> listByBatch(@PathVariable String batchNo) {
    return ControllerSupport.call(() -> {
      var batch = batchService.requireByBatchNo(batchNo);
      return defectService.listByBatchId(batch.getId()).stream()
          .map(defect -> DefectRecordDtoFactory.toResponse(defect, batchNo, false))
          .toList();
    });
  }
}
