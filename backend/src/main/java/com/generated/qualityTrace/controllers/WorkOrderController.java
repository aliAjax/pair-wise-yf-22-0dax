package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.RoleConstants;
import com.generated.qualityTrace.constructors.WorkOrderDtoFactory;
import com.generated.qualityTrace.middlewares.RequireRoles;
import com.generated.qualityTrace.services.WorkOrderService;
import com.generated.qualityTrace.types.CreateWorkOrderRequest;
import com.generated.qualityTrace.types.WorkOrderActionRequest;
import com.generated.qualityTrace.types.WorkOrderResponse;
import com.generated.qualityTrace.validators.WorkOrderValidator;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

  private final WorkOrderService workOrderService;
  private final WorkOrderValidator workOrderValidator;

  public WorkOrderController(WorkOrderService workOrderService,
                             WorkOrderValidator workOrderValidator) {
    this.workOrderService = workOrderService;
    this.workOrderValidator = workOrderValidator;
  }

  @PostMapping
  @RequireRoles({RoleConstants.LINE_SUPERVISOR, RoleConstants.QUALITY_MANAGER})
  public WorkOrderResponse create(@RequestBody CreateWorkOrderRequest request) {
    return ControllerSupport.call(() -> {
      workOrderValidator.validateCreate(request);
      return WorkOrderDtoFactory.toResponse(workOrderService.create(request));
    });
  }

  @PostMapping("/{id}/actions")
  @RequireRoles({RoleConstants.LINE_SUPERVISOR, RoleConstants.QUALITY_MANAGER})
  public WorkOrderResponse changeStatus(@PathVariable Long id,
                                        @RequestBody WorkOrderActionRequest request) {
    return ControllerSupport.call(() -> {
      workOrderValidator.validateAction(request.action());
      return WorkOrderDtoFactory.toResponse(workOrderService.changeStatus(id, request.action()));
    });
  }

  @GetMapping
  @RequireRoles({RoleConstants.QUALITY_INSPECTOR, RoleConstants.LINE_SUPERVISOR,
      RoleConstants.QUALITY_MANAGER, RoleConstants.AUDITOR})
  public List<WorkOrderResponse> list() {
    return ControllerSupport.call(() -> workOrderService.list().stream()
        .map(WorkOrderDtoFactory::toResponse)
        .toList());
  }

  @GetMapping("/{id}")
  @RequireRoles({RoleConstants.QUALITY_INSPECTOR, RoleConstants.LINE_SUPERVISOR,
      RoleConstants.QUALITY_MANAGER, RoleConstants.AUDITOR})
  public WorkOrderResponse get(@PathVariable Long id) {
    return ControllerSupport.call(() ->
        WorkOrderDtoFactory.toResponse(workOrderService.requireById(id)));
  }
}
