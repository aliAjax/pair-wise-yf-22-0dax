package com.generated.qualityTrace.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.generated.qualityTrace.constants.InspectionItemStatus;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.InspectionType;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.InspectionItemResultDtoFactory;
import com.generated.qualityTrace.constructors.QualityInspectionDtoFactory;
import com.generated.qualityTrace.middlewares.CurrentUserContext;
import com.generated.qualityTrace.models.AppUser;
import com.generated.qualityTrace.models.InspectionItemResult;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.repositories.AppUserRepository;
import com.generated.qualityTrace.repositories.InspectionItemResultRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.types.InspectionResponse;
import com.generated.qualityTrace.types.RegisterInspectionRequest;
import com.generated.qualityTrace.utils.IdGenerator;
import com.generated.qualityTrace.utils.IdempotencyHasher;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 质量检验服务：登记检验单并逐项写入检验项。
 * 同批次 + 同请求内容重复提交时命中 content_hash，直接返回原记录，不重复累计。
 */
@Service
public class QualityInspectionService {

  private final QualityInspectionRepository inspectionRepository;
  private final InspectionItemResultRepository itemRepository;
  private final AppUserRepository userRepository;
  private final ProductBatchService batchService;
  private final BatchConclusionService conclusionService;
  private final AuditLogService auditLogService;

  public QualityInspectionService(QualityInspectionRepository inspectionRepository,
                                  InspectionItemResultRepository itemRepository,
                                  AppUserRepository userRepository,
                                  ProductBatchService batchService,
                                  BatchConclusionService conclusionService,
                                  AuditLogService auditLogService) {
    this.inspectionRepository = inspectionRepository;
    this.itemRepository = itemRepository;
    this.userRepository = userRepository;
    this.batchService = batchService;
    this.conclusionService = conclusionService;
    this.auditLogService = auditLogService;
  }

  @Transactional
  public InspectionResponse register(RegisterInspectionRequest request) {
    ProductBatch batch = batchService.requireByBatchNo(request.batchNo());
    String contentHash = IdempotencyHasher.hash(request);

    QualityInspection existing = inspectionRepository.selectOne(
        new LambdaQueryWrapper<QualityInspection>()
            .eq(QualityInspection::getContentHash, contentHash));
    if (existing != null) {
      List<InspectionItemResult> items = listItems(existing.getId());
      auditLogService.record(CurrentUserContext.actorName(), "INSPECTION_DUPLICATE",
          "QUALITY_INSPECTION", existing.getId(),
          LogTemplates.render(LogTemplates.INSPECTION_DUPLICATE, existing.getId(),
              request.batchNo()));
      return QualityInspectionDtoFactory.toResponse(existing, request.batchNo(), items, true);
    }

    String inspectorName = resolveInspectorName(request.inspectorId());
    QualityInspection inspection = new QualityInspection();
    inspection.setId(IdGenerator.nextId());
    inspection.setBatchId(batch.getId());
    inspection.setInspectorId(request.inspectorId());
    inspection.setInspectorName(inspectorName);
    inspection.setInspectionType(InspectionType.valueOf(request.inspectionType()).name());
    inspection.setStandardVersion(request.standardVersion());
    inspection.setInspectedAt(request.inspectedAt() == null
        ? LocalDateTime.now() : request.inspectedAt());
    inspection.setContentHash(contentHash);
    inspection.setCreatedAt(LocalDateTime.now());

    List<InspectionItemResult> items = new ArrayList<>();
    int ngCount = 0;
    for (RegisterInspectionRequest.ItemInput input : request.items()) {
      String itemStatus = resolveItemStatus(input);
      if (InspectionItemStatus.NG.name().equals(itemStatus)) {
        ngCount++;
      }
      items.add(InspectionItemResultDtoFactory.fromInput(inspection.getId(), input, itemStatus));
    }
    inspection.setResultStatus(resolveOverallStatus(request, ngCount, items.size()));

    inspectionRepository.insert(inspection);
    items.forEach(itemRepository::insert);

    // 检验结果（尤其终检）会影响批次当前结论
    conclusionService.recalculate(batch.getId());

    auditLogService.record(CurrentUserContext.actorName(), "INSPECTION_REGISTER",
        "QUALITY_INSPECTION", inspection.getId(),
        LogTemplates.render(LogTemplates.INSPECTION_REGISTER, request.batchNo(),
            inspection.getInspectionType(), inspection.getResultStatus(), items.size()));
    if (InspectionType.FINAL.name().equals(inspection.getInspectionType())) {
      String template = InspectionResultStatus.PASS.name().equals(inspection.getResultStatus())
          || InspectionResultStatus.CONDITIONAL_PASS.name().equals(inspection.getResultStatus())
          ? LogTemplates.INSPECTION_FINAL_PASS : LogTemplates.INSPECTION_FINAL_FAIL;
      auditLogService.record(CurrentUserContext.actorName(), "INSPECTION_FINAL",
          "QUALITY_INSPECTION", inspection.getId(),
          LogTemplates.render(template, request.batchNo(), inspection.getId(), ngCount));
    }
    return QualityInspectionDtoFactory.toResponse(inspection, request.batchNo(), items, false);
  }

  public List<InspectionItemResult> listItems(Long inspectionId) {
    return itemRepository.selectList(new LambdaQueryWrapper<InspectionItemResult>()
        .eq(InspectionItemResult::getInspectionId, inspectionId)
        .orderByAsc(InspectionItemResult::getId));
  }

  public List<QualityInspection> listByBatchId(Long batchId) {
    return inspectionRepository.selectList(new LambdaQueryWrapper<QualityInspection>()
        .eq(QualityInspection::getBatchId, batchId)
        .orderByDesc(QualityInspection::getInspectedAt));
  }

  private String resolveInspectorName(Long inspectorId) {
    if (inspectorId == null) {
      return CurrentUserContext.actorName();
    }
    AppUser inspector = userRepository.selectById(inspectorId);
    return inspector == null ? CurrentUserContext.actorName()
        : (inspector.getDisplayName() == null ? inspector.getUsername()
            : inspector.getDisplayName());
  }

  /** 逐项判定：显式状态优先；否则按测量值是否落在 [limitMin, limitMax] 内判定；无上下限记免检。 */
  private String resolveItemStatus(RegisterInspectionRequest.ItemInput input) {
    if (input.itemStatus() != null && !input.itemStatus().isBlank()) {
      return InspectionItemStatus.valueOf(input.itemStatus()).name();
    }
    if (input.measuredValue() == null
        || (input.limitMin() == null && input.limitMax() == null)) {
      return InspectionItemStatus.NA.name();
    }
    boolean belowMin = input.limitMin() != null
        && input.measuredValue().compareTo(input.limitMin()) < 0;
    boolean aboveMax = input.limitMax() != null
        && input.measuredValue().compareTo(input.limitMax()) > 0;
    return belowMin || aboveMax
        ? InspectionItemStatus.NG.name() : InspectionItemStatus.OK.name();
  }

  /** 汇总判定：显式结论优先；否则任一 NG 即 FAIL，全部免检时 PASS，其余全 OK 为 PASS。 */
  private String resolveOverallStatus(RegisterInspectionRequest request, int ngCount,
                                      int itemCount) {
    if (request.resultStatus() != null && !request.resultStatus().isBlank()) {
      return InspectionResultStatus.valueOf(request.resultStatus()).name();
    }
    if (ngCount > 0) {
      return InspectionResultStatus.FAIL.name();
    }
    return InspectionResultStatus.PASS.name();
  }
}
