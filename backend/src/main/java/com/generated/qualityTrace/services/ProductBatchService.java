package com.generated.qualityTrace.services;

import java.time.OffsetDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.BatchConclusionCode;
import com.generated.qualityTrace.constants.ConclusionTrigger;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.ProductBatchStatus;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.constructors.ProductBatchDtoFactory;
import com.generated.qualityTrace.exceptions.NotFoundException;
import com.generated.qualityTrace.exceptions.ServiceLayerException;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import com.generated.qualityTrace.types.IdempotentResponse;
import com.generated.qualityTrace.types.ProductBatchDto;
import com.generated.qualityTrace.types.ProductBatchPayload;
import com.generated.qualityTrace.validators.ProductBatchValidator;

/** 批次登记：挂到工单上，创建后立即参与结论重算（新批次无检验 -> 待复检）。 */
@Service
public class ProductBatchService {

  private static final Logger log = LoggerFactory.getLogger(ProductBatchService.class);

  private final ProductBatchRepository repository;
  private final WorkOrderRepository workOrderRepository;
  private final WorkOrderService workOrderService;
  private final IdempotencyService idempotencyService;
  private final AuditLogService auditLogService;
  private final BatchConclusionService conclusionService;

  public ProductBatchService(
      ProductBatchRepository repository,
      WorkOrderRepository workOrderRepository,
      WorkOrderService workOrderService,
      IdempotencyService idempotencyService,
      AuditLogService auditLogService,
      BatchConclusionService conclusionService) {
    this.repository = repository;
    this.workOrderRepository = workOrderRepository;
    this.workOrderService = workOrderService;
    this.idempotencyService = idempotencyService;
    this.auditLogService = auditLogService;
    this.conclusionService = conclusionService;
  }

  public List<ProductBatchDto> list() {
    try {
      return repository.findAll().stream()
          .sorted((a, b) -> Long.compare(b.id, a.id))
          .map(batch -> ProductBatchDtoFactory.create(batch, resolveOrder(batch)))
          .toList();
    } catch (RuntimeException ex) {
      throw new ServiceLayerException(
          ErrorMessages.format(ErrorMessages.SERVICE_ERROR, "list batches"), ex);
    }
  }

  public ProductBatch requireByBatchNo(String batchNo) {
    return repository
        .findByBatchNo(batchNo)
        .orElseThrow(
            () ->
                new NotFoundException(
                    ErrorCodes.BATCH_NOT_FOUND,
                    ErrorMessages.format(ErrorMessages.BATCH_NOT_FOUND, batchNo)));
  }

  public WorkOrder resolveOrder(ProductBatch batch) {
    if (batch.workOrderId == null) {
      return null;
    }
    return workOrderRepository.findById(batch.workOrderId).orElse(null);
  }

  public IdempotentResponse<ProductBatchDto> register(ProductBatchPayload payload) {
    ProductBatchValidator.validate(payload);
    try {
      var duplicate = repository.findByBatchNo(payload.batchNo());
      if (duplicate.isPresent()) {
        ProductBatch existing = duplicate.get();
        log.info(LogTemplates.BATCH_DUPLICATE_RETURN, existing.batchNo, existing.id);
        auditLogService.record(
            "BATCH_DUPLICATE_RETURN", "ProductBatch", existing.id,
            "batchNo=" + existing.batchNo);
        return new IdempotentResponse<>(
            ProductBatchDtoFactory.create(existing, resolveOrder(existing)),
            true,
            payload.requestId());
      }

      WorkOrder order = workOrderService.requireByOrderNo(payload.orderNo());

      IdempotencyService.IdempotentExecution<ProductBatchDto> execution =
          idempotencyService.execute(
              payload.requestId(),
              payload,
              "ProductBatch",
              () -> {
                ProductBatch batch = new ProductBatch();
                OffsetDateTime now = OffsetDateTime.now();
                batch.batchNo = payload.batchNo();
                batch.workOrderId = order.id;
                batch.quantity = payload.quantity();
                batch.materialLotNo = payload.materialLotNo();
                batch.producedAt = payload.producedAt() == null ? now : payload.producedAt();
                batch.batchStatus = ProductBatchStatus.ACTIVE.name();
                batch.createdAt = now;
                batch.updatedAt = now;
                repository.save(batch);

                // 批次创建同步更新工单进度：待开工工单进入生产中
                if (WorkOrderStatus.PLANNED.name().equals(order.status)) {
                  String oldStatus = order.status;
                  order.status = WorkOrderStatus.RUNNING.name();
                  order.updatedAt = now;
                  workOrderRepository.save(order);
                  log.info(
                      LogTemplates.WORK_ORDER_STATUS_CHANGED,
                      order.orderNo,
                      oldStatus,
                      order.status);
                  log.info(
                      LogTemplates.WORK_ORDER_PROGRESS_UPDATED,
                      order.orderNo,
                      batch.batchNo);
                }

                BatchConclusionCode conclusion =
                    conclusionService.recompute(batch, ConclusionTrigger.BATCH_CREATED);
                auditLogService.record(
                    "BATCH_CREATE", "ProductBatch", batch.id,
                    "batchNo=" + batch.batchNo + ", conclusion=" + conclusion);
                return new IdempotencyService.IdempotentExecution<>(
                    ProductBatchDtoFactory.create(batch, order), batch.id, false);
              });

      return new IdempotentResponse<>(
          execution.data(), execution.deduplicated(), payload.requestId());
    } catch (RuntimeException ex) {
      if (ex instanceof com.generated.qualityTrace.exceptions.BusinessException) {
        throw ex;
      }
      throw new ServiceLayerException(
          ErrorMessages.format(ErrorMessages.SERVICE_ERROR, "register batch"), ex);
    }
  }
}
