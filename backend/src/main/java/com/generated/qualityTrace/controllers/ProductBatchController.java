package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.RoleConstants;
import com.generated.qualityTrace.constructors.ProductBatchDtoFactory;
import com.generated.qualityTrace.middlewares.RequireRoles;
import com.generated.qualityTrace.services.BatchConclusionService;
import com.generated.qualityTrace.services.ProductBatchService;
import com.generated.qualityTrace.services.WorkOrderService;
import com.generated.qualityTrace.types.BatchConclusionResponse;
import com.generated.qualityTrace.types.ConclusionHistoryResponse;
import com.generated.qualityTrace.types.CreateProductBatchRequest;
import com.generated.qualityTrace.types.ProductBatchResponse;
import com.generated.qualityTrace.validators.ProductBatchValidator;
import com.generated.qualityTrace.constructors.BatchConclusionDtoFactory;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/batches")
public class ProductBatchController {

  private final ProductBatchService batchService;
  private final WorkOrderService workOrderService;
  private final BatchConclusionService conclusionService;
  private final ProductBatchValidator batchValidator;

  public ProductBatchController(ProductBatchService batchService,
                                WorkOrderService workOrderService,
                                BatchConclusionService conclusionService,
                                ProductBatchValidator batchValidator) {
    this.batchService = batchService;
    this.workOrderService = workOrderService;
    this.conclusionService = conclusionService;
    this.batchValidator = batchValidator;
  }

  @PostMapping
  @RequireRoles({RoleConstants.LINE_SUPERVISOR, RoleConstants.QUALITY_MANAGER})
  public ProductBatchResponse create(@RequestBody CreateProductBatchRequest request) {
    return ControllerSupport.call(() -> {
      batchValidator.validateCreate(request);
      var batch = batchService.create(request);
      var workOrder = workOrderService.requireById(batch.getWorkOrderId());
      return ProductBatchDtoFactory.toResponse(batch, workOrder.getOrderNo(),
          conclusionService.getByBatchId(batch.getId()));
    });
  }

  @GetMapping
  @RequireRoles({RoleConstants.QUALITY_INSPECTOR, RoleConstants.LINE_SUPERVISOR,
      RoleConstants.QUALITY_MANAGER, RoleConstants.AUDITOR})
  public List<ProductBatchResponse> list() {
    return ControllerSupport.call(() -> batchService.list().stream().map(batch -> {
      var workOrder = workOrderService.requireById(batch.getWorkOrderId());
      return ProductBatchDtoFactory.toResponse(batch, workOrder.getOrderNo(),
          conclusionService.getByBatchId(batch.getId()));
    }).toList());
  }

  /** 按批号的全链路追溯树：工单、检验项、不良处置、当前结论与结论变化。 */
  @GetMapping("/trace/{batchNo}")
  @RequireRoles({RoleConstants.QUALITY_INSPECTOR, RoleConstants.LINE_SUPERVISOR,
      RoleConstants.QUALITY_MANAGER, RoleConstants.AUDITOR})
  public com.generated.qualityTrace.types.BatchTraceResponse trace(@PathVariable String batchNo) {
    return ControllerSupport.call(() -> batchService.trace(batchNo));
  }

  @GetMapping("/trace/{batchNo}/conclusion")
  @RequireRoles({RoleConstants.QUALITY_INSPECTOR, RoleConstants.LINE_SUPERVISOR,
      RoleConstants.QUALITY_MANAGER, RoleConstants.AUDITOR})
  public BatchConclusionResponse conclusion(@PathVariable String batchNo) {
    return ControllerSupport.call(() -> {
      var batch = batchService.requireByBatchNo(batchNo);
      return BatchConclusionDtoFactory.toResponse(conclusionService.getByBatchId(batch.getId()),
          batchNo);
    });
  }

  @GetMapping("/trace/{batchNo}/conclusion/history")
  @RequireRoles({RoleConstants.QUALITY_INSPECTOR, RoleConstants.LINE_SUPERVISOR,
      RoleConstants.QUALITY_MANAGER, RoleConstants.AUDITOR})
  public List<ConclusionHistoryResponse> conclusionHistory(@PathVariable String batchNo) {
    return ControllerSupport.call(() -> {
      var batch = batchService.requireByBatchNo(batchNo);
      return conclusionService.listHistory(batch.getId()).stream()
          .map(BatchConclusionDtoFactory::toHistoryResponse)
          .toList();
    });
  }
}
