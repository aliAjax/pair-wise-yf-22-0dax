package com.generated.qualityTrace.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.generated.qualityTrace.constants.BatchConclusionStatus;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.TraceDtoFactory;
import com.generated.qualityTrace.exceptions.ServiceException;
import com.generated.qualityTrace.middlewares.CurrentUserContext;
import com.generated.qualityTrace.models.BatchConclusion;
import com.generated.qualityTrace.models.BatchConclusionHistory;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.InspectionItemResult;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.InspectionItemResultRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.types.BatchTraceResponse;
import com.generated.qualityTrace.types.CreateProductBatchRequest;
import com.generated.qualityTrace.utils.IdGenerator;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 产品批次服务：创建批次（挂工单+更新进度）、批号查询、按批号汇总追溯树。 */
@Service
public class ProductBatchService {

  private final ProductBatchRepository batchRepository;
  private final WorkOrderService workOrderService;
  private final QualityInspectionRepository inspectionRepository;
  private final InspectionItemResultRepository itemRepository;
  private final DefectRecordRepository defectRepository;
  private final BatchConclusionService conclusionService;
  private final AuditLogService auditLogService;

  public ProductBatchService(ProductBatchRepository batchRepository,
                             WorkOrderService workOrderService,
                             QualityInspectionRepository inspectionRepository,
                             InspectionItemResultRepository itemRepository,
                             DefectRecordRepository defectRepository,
                             BatchConclusionService conclusionService,
                             AuditLogService auditLogService) {
    this.batchRepository = batchRepository;
    this.workOrderService = workOrderService;
    this.inspectionRepository = inspectionRepository;
    this.itemRepository = itemRepository;
    this.defectRepository = defectRepository;
    this.conclusionService = conclusionService;
    this.auditLogService = auditLogService;
  }

  @Transactional
  public ProductBatch create(CreateProductBatchRequest request) {
    WorkOrder workOrder = workOrderService.requireById(request.workOrderId());
    boolean duplicated = batchRepository.exists(new LambdaQueryWrapper<ProductBatch>()
        .eq(ProductBatch::getBatchNo, request.batchNo()));
    if (duplicated) {
      throw new ServiceException(ErrorCodes.CONFLICT,
          MessageFormat.format(ErrorMessages.BATCH_NO_DUPLICATED, request.batchNo()));
    }
    ProductBatch batch = new ProductBatch();
    batch.setId(IdGenerator.nextId());
    batch.setBatchNo(request.batchNo());
    batch.setWorkOrderId(workOrder.getId());
    batch.setQuantity(request.quantity());
    batch.setMaterialLotNo(request.materialLotNo());
    batch.setProducedAt(request.producedAt() == null ? LocalDateTime.now() : request.producedAt());
    batch.setBatchStatus(BatchConclusionStatus.PENDING.name());
    batch.setCreatedAt(LocalDateTime.now());
    batchRepository.insert(batch);

    // 当前结论与初始结论变化流水由 BatchConclusionService 统一生成（初始为 PENDING）
    conclusionService.recalculate(batch.getId());

    auditLogService.record(CurrentUserContext.actorName(), "BATCH_CREATE", "PRODUCT_BATCH",
        batch.getId(),
        LogTemplates.render(LogTemplates.BATCH_CREATE, batch.getBatchNo(),
            workOrder.getOrderNo(), batch.getQuantity()));
    updateWorkOrderProgress(workOrder);
    return batch;
  }

  private void updateWorkOrderProgress(WorkOrder workOrder) {
    List<ProductBatch> batches = batchRepository.selectList(new LambdaQueryWrapper<ProductBatch>()
        .eq(ProductBatch::getWorkOrderId, workOrder.getId()));
    int totalQty = batches.stream()
        .mapToInt(b -> b.getQuantity() == null ? 0 : b.getQuantity())
        .sum();
    auditLogService.record(CurrentUserContext.actorName(), "BATCH_PROGRESS_UPDATE", "WORK_ORDER",
        workOrder.getId(),
        LogTemplates.render(LogTemplates.BATCH_PROGRESS_UPDATE, workOrder.getOrderNo(),
            batches.size(), totalQty));
  }

  public ProductBatch requireByBatchNo(String batchNo) {
    ProductBatch batch = findByBatchNo(batchNo);
    if (batch == null) {
      throw new ServiceException(ErrorCodes.NOT_FOUND,
          MessageFormat.format(ErrorMessages.BATCH_NOT_FOUND, batchNo));
    }
    return batch;
  }

  public ProductBatch requireById(Long batchId) {
    ProductBatch batch = batchRepository.selectById(batchId);
    if (batch == null) {
      throw new ServiceException(ErrorCodes.NOT_FOUND,
          MessageFormat.format(ErrorMessages.BATCH_NOT_FOUND, batchId));
    }
    return batch;
  }

  public ProductBatch findByBatchNo(String batchNo) {
    return batchRepository.selectOne(new LambdaQueryWrapper<ProductBatch>()
        .eq(ProductBatch::getBatchNo, batchNo));
  }

  public List<ProductBatch> list() {
    return batchRepository.selectList(new LambdaQueryWrapper<ProductBatch>()
        .orderByDesc(ProductBatch::getCreatedAt));
  }

  /** 按批号汇总：工单、检验项、不良处置进度、当前结论与结论变化一次性返回。 */
  public BatchTraceResponse trace(String batchNo) {
    ProductBatch batch = requireByBatchNo(batchNo);
    WorkOrder workOrder = workOrderService.requireById(batch.getWorkOrderId());

    List<QualityInspection> inspections = inspectionRepository.selectList(
        new LambdaQueryWrapper<QualityInspection>()
            .eq(QualityInspection::getBatchId, batch.getId()));
    List<Long> inspectionIds = inspections.stream().map(QualityInspection::getId).toList();
    List<InspectionItemResult> items = inspectionIds.isEmpty() ? List.of()
        : itemRepository.selectList(new LambdaQueryWrapper<InspectionItemResult>()
            .in(InspectionItemResult::getInspectionId, inspectionIds));
    Map<Long, List<InspectionItemResult>> itemsByInspection = new HashMap<>();
    items.forEach(item -> itemsByInspection
        .computeIfAbsent(item.getInspectionId(), k -> new java.util.ArrayList<>()).add(item));

    List<DefectRecord> defects = defectRepository.selectList(new LambdaQueryWrapper<DefectRecord>()
        .eq(DefectRecord::getBatchId, batch.getId()));
    BatchConclusion conclusion = conclusionService.getByBatchId(batch.getId());
    List<BatchConclusionHistory> history = conclusionService.listHistory(batch.getId());

    auditLogService.record(CurrentUserContext.actorName(), "BATCH_TRACE_VIEW", "PRODUCT_BATCH",
        batch.getId(), LogTemplates.render(LogTemplates.BATCH_TRACE_VIEW, batchNo));

    return TraceDtoFactory.build(batch, workOrder, inspections, itemsByInspection, defects,
        conclusion, history);
  }
}
