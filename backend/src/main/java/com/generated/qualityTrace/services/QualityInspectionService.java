package com.generated.qualityTrace.services;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.BatchConclusionCode;
import com.generated.qualityTrace.constants.ConclusionTrigger;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.InspectionItemStatus;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.QualityInspectionDtoFactory;
import com.generated.qualityTrace.exceptions.NotFoundException;
import com.generated.qualityTrace.exceptions.ServiceLayerException;
import com.generated.qualityTrace.models.InspectionItemResult;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.repositories.InspectionItemResultRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.types.IdempotentResponse;
import com.generated.qualityTrace.types.InspectionItemResultDto;
import com.generated.qualityTrace.types.QualityInspectionDto;
import com.generated.qualityTrace.types.QualityInspectionPayload;
import com.generated.qualityTrace.validators.QualityInspectionValidator;

/** 检验单登记：逐项写入并判定，再按检验项汇总检验结论，最后触发批次结论重算。 */
@Service
public class QualityInspectionService {

  private static final Logger log = LoggerFactory.getLogger(QualityInspectionService.class);

  private final QualityInspectionRepository repository;
  private final InspectionItemResultRepository itemRepository;
  private final ProductBatchService batchService;
  private final IdempotencyService idempotencyService;
  private final AuditLogService auditLogService;
  private final BatchConclusionService conclusionService;

  public QualityInspectionService(
      QualityInspectionRepository repository,
      InspectionItemResultRepository itemRepository,
      ProductBatchService batchService,
      IdempotencyService idempotencyService,
      AuditLogService auditLogService,
      BatchConclusionService conclusionService) {
    this.repository = repository;
    this.itemRepository = itemRepository;
    this.batchService = batchService;
    this.idempotencyService = idempotencyService;
    this.auditLogService = auditLogService;
    this.conclusionService = conclusionService;
  }

  public List<QualityInspectionDto> listByBatch(ProductBatch batch) {
    return repository.findByBatchId(batch.id).stream()
        .sorted((a, b) -> Long.compare(a.id, b.id))
        .map(inspection -> QualityInspectionDtoFactory.create(
            inspection, batch, itemRepository.findByInspectionId(inspection.id)))
        .toList();
  }

  public QualityInspection requireById(Long inspectionId) {
    return repository
        .findById(inspectionId)
        .orElseThrow(
            () ->
                new NotFoundException(
                    ErrorCodes.INSPECTION_NOT_FOUND,
                    ErrorMessages.format(ErrorMessages.INSPECTION_NOT_FOUND, inspectionId)));
  }

  public List<InspectionItemResultDto> listItems(Long inspectionId) {
    requireById(inspectionId);
    return itemRepository.findByInspectionId(inspectionId).stream()
        .map(com.generated.qualityTrace.constructors.InspectionItemResultDtoFactory::create)
        .toList();
  }

  public IdempotentResponse<QualityInspectionDto> register(QualityInspectionPayload payload) {
    QualityInspectionValidator.validate(payload);
    try {
      ProductBatch batch = batchService.requireByBatchNo(payload.batchNo());

      // requestId 命中：重复提交按原检验单返回，检验项不会重复累计
      if (payload.requestId() != null && !payload.requestId().isBlank()) {
        var replay = repository.findByRequestId(payload.requestId());
        if (replay.isPresent()) {
          QualityInspection existing = replay.get();
          log.info(
              LogTemplates.INSPECTION_DUPLICATE_RETURN,
              payload.requestId(),
              existing.id);
          auditLogService.record(
              "INSPECTION_DUPLICATE_RETURN", "QualityInspection", existing.id,
              "requestId=" + payload.requestId());
          QualityInspectionDto dto =
              QualityInspectionDtoFactory.create(
                  existing, batch, itemRepository.findByInspectionId(existing.id));
          return new IdempotentResponse<>(dto, true, payload.requestId());
        }
      }

      IdempotencyService.IdempotentExecution<QualityInspectionDto> execution =
          idempotencyService.execute(
              payload.requestId(),
              payload,
              "QualityInspection",
              () -> persistInspection(payload, batch));

      return new IdempotentResponse<>(
          execution.data(), execution.deduplicated(), payload.requestId());
    } catch (RuntimeException ex) {
      if (ex instanceof com.generated.qualityTrace.exceptions.BusinessException) {
        throw ex;
      }
      throw new ServiceLayerException(
          ErrorMessages.format(ErrorMessages.SERVICE_ERROR, "register inspection"), ex);
    }
  }

  private IdempotencyService.IdempotentExecution<QualityInspectionDto> persistInspection(
      QualityInspectionPayload payload, ProductBatch batch) {
    QualityInspection inspection = new QualityInspection();
    OffsetDateTime now = OffsetDateTime.now();
    inspection.batchId = batch.id;
    inspection.inspectorId = payload.inspectorId();
    inspection.inspectionType = payload.inspectionType();
    inspection.standardVersion = payload.standardVersion();
    inspection.inspectedAt = now;
    inspection.requestId = payload.requestId();
    inspection.createdAt = now;
    inspection.updatedAt = now;

    List<InspectionItemResult> items = new ArrayList<>();
    boolean anyUnqualified = false;
    for (var itemPayload : payload.items()) {
      InspectionItemResult item = new InspectionItemResult();
      item.itemCode = itemPayload.itemCode();
      item.itemName = itemPayload.itemName();
      item.measuredValue = itemPayload.measuredValue();
      item.limitMin = itemPayload.limitMin();
      item.limitMax = itemPayload.limitMax();
      item.itemStatus = judgeItem(itemPayload);
      item.createdAt = now;
      items.add(item);
      if (InspectionItemStatus.UNQUALIFIED.name().equals(item.itemStatus)) {
        anyUnqualified = true;
      }
      log.info(
          LogTemplates.INSPECTION_ITEM_JUDGED,
          item.itemCode,
          item.measuredValue,
          item.limitMin,
          item.limitMax,
          item.itemStatus);
    }

    InspectionResultStatus result =
        payload.resultStatus() == null || payload.resultStatus().isBlank()
            ? (anyUnqualified ? InspectionResultStatus.FAIL : InspectionResultStatus.PASS)
            : InspectionResultStatus.valueOf(payload.resultStatus());
    inspection.resultStatus = result.name();
    repository.save(inspection);
    for (InspectionItemResult item : items) {
      item.inspectionId = inspection.id;
      itemRepository.save(item);
    }
    log.info(
        LogTemplates.INSPECTION_CREATE,
        batch.batchNo,
        inspection.inspectionType,
        inspection.resultStatus,
        items.size());
    log.info(
        LogTemplates.INSPECTION_RESULT_AUTO, inspection.id, inspection.resultStatus);

    BatchConclusionCode conclusion =
        conclusionService.recompute(batch, ConclusionTrigger.INSPECTION_REGISTERED);
    auditLogService.record(
        "INSPECTION_CREATE", "QualityInspection", inspection.id,
        "batchNo=" + batch.batchNo + ", type=" + inspection.inspectionType
            + ", result=" + result + ", conclusion=" + conclusion);

    QualityInspectionDto dto =
        QualityInspectionDtoFactory.create(inspection, batch, items);
    return new IdempotencyService.IdempotentExecution<>(dto, inspection.id, false);
  }

  /** 显式状态优先；未显式给定时按上下限判定，缺限值视为合格。 */
  static String judgeItem(com.generated.qualityTrace.types.InspectionItemResultPayload item) {
    if (item.itemStatus() != null && !item.itemStatus().isBlank()) {
      return InspectionItemStatus.valueOf(item.itemStatus()).name();
    }
    BigDecimal measured = item.measuredValue();
    if (item.limitMin() != null && measured.compareTo(item.limitMin()) < 0) {
      return InspectionItemStatus.UNQUALIFIED.name();
    }
    if (item.limitMax() != null && measured.compareTo(item.limitMax()) > 0) {
      return InspectionItemStatus.UNQUALIFIED.name();
    }
    return InspectionItemStatus.QUALIFIED.name();
  }
}
