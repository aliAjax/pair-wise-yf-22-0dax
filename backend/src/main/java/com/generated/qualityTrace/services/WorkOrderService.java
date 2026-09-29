package com.generated.qualityTrace.services;

import java.time.OffsetDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.constructors.WorkOrderDtoFactory;
import com.generated.qualityTrace.exceptions.NotFoundException;
import com.generated.qualityTrace.exceptions.ServiceLayerException;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import com.generated.qualityTrace.types.IdempotentResponse;
import com.generated.qualityTrace.types.WorkOrderDto;
import com.generated.qualityTrace.types.WorkOrderPayload;
import com.generated.qualityTrace.validators.WorkOrderValidator;

/** 工单登记：自然键 orderNo 天然防重，requestId 额外保证客户端重试幂等。 */
@Service
public class WorkOrderService {

  private static final Logger log = LoggerFactory.getLogger(WorkOrderService.class);

  private final WorkOrderRepository repository;
  private final IdempotencyService idempotencyService;
  private final AuditLogService auditLogService;

  public WorkOrderService(
      WorkOrderRepository repository,
      IdempotencyService idempotencyService,
      AuditLogService auditLogService) {
    this.repository = repository;
    this.idempotencyService = idempotencyService;
    this.auditLogService = auditLogService;
  }

  public List<WorkOrderDto> list() {
    try {
      return repository.findAll().stream()
          .sorted((a, b) -> Long.compare(b.id, a.id))
          .map(WorkOrderDtoFactory::create)
          .toList();
    } catch (RuntimeException ex) {
      throw new ServiceLayerException(
          ErrorMessages.format(ErrorMessages.SERVICE_ERROR, "list work orders"), ex);
    }
  }

  public WorkOrder requireByOrderNo(String orderNo) {
    return repository
        .findByOrderNo(orderNo)
        .orElseThrow(
            () ->
                new NotFoundException(
                    ErrorCodes.WORK_ORDER_NOT_FOUND,
                    ErrorMessages.format(ErrorMessages.WORK_ORDER_NOT_FOUND, orderNo)));
  }

  public IdempotentResponse<WorkOrderDto> register(WorkOrderPayload payload) {
    WorkOrderValidator.validate(payload);
    try {
      // 自然键先判重：重复提交按原记录返回，不再创建
      var duplicate = repository.findByOrderNo(payload.orderNo());
      if (duplicate.isPresent()) {
        WorkOrder existing = duplicate.get();
        log.info(LogTemplates.WORK_ORDER_DUPLICATE_RETURN, existing.orderNo, existing.id);
        auditLogService.record(
            "WORK_ORDER_DUPLICATE_RETURN", "WorkOrder", existing.id,
            "orderNo=" + existing.orderNo);
        return new IdempotentResponse<>(
            WorkOrderDtoFactory.create(existing), true, payload.requestId());
      }

      IdempotencyService.IdempotentExecution<WorkOrderDto> execution =
          idempotencyService.execute(
              payload.requestId(),
              payload,
              "WorkOrder",
              () -> {
                WorkOrder order = new WorkOrder();
                OffsetDateTime now = OffsetDateTime.now();
                order.orderNo = payload.orderNo();
                order.productCode = payload.productCode();
                order.productName = payload.productName();
                order.plannedQty = payload.plannedQty();
                order.lineCode = payload.lineCode();
                order.startAt = payload.startAt() == null ? now : payload.startAt();
                order.status = WorkOrderStatus.PLANNED.name();
                order.createdAt = now;
                order.updatedAt = now;
                repository.save(order);
                log.info(
                    LogTemplates.WORK_ORDER_CREATE,
                    order.orderNo,
                    order.productCode,
                    order.plannedQty);
                auditLogService.record(
                    "WORK_ORDER_CREATE", "WorkOrder", order.id,
                    "orderNo=" + order.orderNo + ", productCode=" + order.productCode);
                return new IdempotencyService.IdempotentExecution<>(
                    WorkOrderDtoFactory.create(order), order.id, false);
              });

      return new IdempotentResponse<>(
          execution.data(), execution.deduplicated(), payload.requestId());
    } catch (RuntimeException ex) {
      if (ex instanceof com.generated.qualityTrace.exceptions.BusinessException) {
        throw ex;
      }
      throw new ServiceLayerException(
          ErrorMessages.format(ErrorMessages.SERVICE_ERROR, "register work order"), ex);
    }
  }
}
