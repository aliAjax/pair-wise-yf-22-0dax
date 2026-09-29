package com.generated.qualityTrace.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.exceptions.ServiceException;
import com.generated.qualityTrace.middlewares.CurrentUserContext;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import com.generated.qualityTrace.types.CreateWorkOrderRequest;
import com.generated.qualityTrace.utils.IdGenerator;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

/** 生产工单服务：创建、开工/暂停/完工、查询。 */
@Service
public class WorkOrderService {

  private final WorkOrderRepository workOrderRepository;
  private final ProductBatchRepository productBatchRepository;
  private final AuditLogService auditLogService;

  public WorkOrderService(WorkOrderRepository workOrderRepository,
                          ProductBatchRepository productBatchRepository,
                          AuditLogService auditLogService) {
    this.workOrderRepository = workOrderRepository;
    this.productBatchRepository = productBatchRepository;
    this.auditLogService = auditLogService;
  }

  public WorkOrder create(CreateWorkOrderRequest request) {
    boolean duplicated = workOrderRepository.exists(new LambdaQueryWrapper<WorkOrder>()
        .eq(WorkOrder::getOrderNo, request.orderNo()));
    if (duplicated) {
      throw new ServiceException(ErrorCodes.CONFLICT,
          MessageFormat.format(ErrorMessages.WORK_ORDER_NO_DUPLICATED, request.orderNo()));
    }
    WorkOrder order = new WorkOrder();
    order.setId(IdGenerator.nextId());
    order.setOrderNo(request.orderNo());
    order.setProductCode(request.productCode());
    order.setProductName(request.productName());
    order.setPlannedQty(request.plannedQty());
    order.setLineCode(request.lineCode());
    order.setStartAt(request.startAt());
    order.setStatus(request.status() == null || request.status().isBlank()
        ? WorkOrderStatus.PLANNED.name() : request.status());
    order.setCreatedAt(LocalDateTime.now());
    workOrderRepository.insert(order);

    auditLogService.record(CurrentUserContext.actorName(), "WORK_ORDER_CREATE", "WORK_ORDER",
        order.getId(),
        LogTemplates.render(LogTemplates.WORK_ORDER_CREATE, order.getOrderNo(),
            order.getProductCode(), order.getPlannedQty()));
    return order;
  }

  public WorkOrder changeStatus(Long id, String action) {
    WorkOrder order = requireById(id);
    WorkOrderStatus current = WorkOrderStatus.valueOf(order.getStatus());
    WorkOrderStatus target = switch (action) {
      case "start" -> WorkOrderStatus.RUNNING;
      case "pause" -> WorkOrderStatus.PAUSED;
      case "finish" -> WorkOrderStatus.FINISHED;
      default -> throw new ServiceException(ErrorCodes.INVALID_ARGUMENT,
          MessageFormat.format(ErrorMessages.WORK_ORDER_STATUS_INVALID, action));
    };
    if (!isTransitionAllowed(current, target)) {
      throw new ServiceException(ErrorCodes.CONFLICT,
          MessageFormat.format(ErrorMessages.WORK_ORDER_STATUS_TRANSITION, current, target));
    }
    order.setStatus(target.name());
    workOrderRepository.updateById(order);

    String template = switch (action) {
      case "start" -> LogTemplates.WORK_ORDER_START;
      case "pause" -> LogTemplates.WORK_ORDER_PAUSE;
      default -> LogTemplates.WORK_ORDER_FINISH;
    };
    long batchCount = action.equals("finish")
        ? productBatchRepository.selectCount(new LambdaQueryWrapper<com.generated.qualityTrace.models.ProductBatch>()
            .eq(com.generated.qualityTrace.models.ProductBatch::getWorkOrderId, id))
        : 0L;
    auditLogService.record(CurrentUserContext.actorName(), "WORK_ORDER_" + action.toUpperCase(),
        "WORK_ORDER", order.getId(),
        LogTemplates.render(template, order.getOrderNo(), current.name(), target.name(),
            batchCount));
    return order;
  }

  public WorkOrder requireById(Long id) {
    WorkOrder order = workOrderRepository.selectById(id);
    if (order == null) {
      throw new ServiceException(ErrorCodes.NOT_FOUND,
          MessageFormat.format(ErrorMessages.WORK_ORDER_NOT_FOUND, id));
    }
    return order;
  }

  public WorkOrder findByOrderNo(String orderNo) {
    return workOrderRepository.selectOne(new LambdaQueryWrapper<WorkOrder>()
        .eq(WorkOrder::getOrderNo, orderNo));
  }

  public List<WorkOrder> list() {
    return workOrderRepository.selectList(new LambdaQueryWrapper<WorkOrder>()
        .orderByDesc(WorkOrder::getCreatedAt));
  }

  private boolean isTransitionAllowed(WorkOrderStatus from, WorkOrderStatus to) {
    return switch (to) {
      case RUNNING -> from == WorkOrderStatus.PLANNED || from == WorkOrderStatus.PAUSED;
      case PAUSED -> from == WorkOrderStatus.RUNNING;
      case FINISHED -> from == WorkOrderStatus.RUNNING;
      default -> false;
    };
  }
}
