package com.generated.qualityTrace.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.generated.qualityTrace.constants.BatchConclusionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.DispositionStatus;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.InspectionType;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.middlewares.CurrentUserContext;
import com.generated.qualityTrace.models.BatchConclusion;
import com.generated.qualityTrace.models.BatchConclusionHistory;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.repositories.BatchConclusionHistoryRepository;
import com.generated.qualityTrace.repositories.BatchConclusionRepository;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.utils.IdGenerator;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 批次结论规则引擎（本需求的核心）：
 * 1. 只要还存在未关闭的 MAJOR/CRITICAL 严重不良，结论一律停在 PENDING（待处理）；
 * 2. 严重不良全部处置（关闭）后，看最近一次终检：
 *    - 终检 PASS 或 CONDITIONAL_PASS -> RELEASABLE（可放行）
 *    - 没有终检、终检 FAIL 或 RECHECK -> RECHECK（待复检）
 * 3. 每次结论状态发生变化都追加 batch_conclusion_history 并写审计日志；
 *    状态不变（仍为待处理）时只更新原因，不写变化流水。
 */
@Service
public class BatchConclusionService {

  private final BatchConclusionRepository conclusionRepository;
  private final BatchConclusionHistoryRepository historyRepository;
  private final DefectRecordRepository defectRepository;
  private final QualityInspectionRepository inspectionRepository;
  private final ProductBatchRepository batchRepository;
  private final AuditLogService auditLogService;

  public BatchConclusionService(BatchConclusionRepository conclusionRepository,
                                BatchConclusionHistoryRepository historyRepository,
                                DefectRecordRepository defectRepository,
                                QualityInspectionRepository inspectionRepository,
                                ProductBatchRepository batchRepository,
                                AuditLogService auditLogService) {
    this.conclusionRepository = conclusionRepository;
    this.historyRepository = historyRepository;
    this.defectRepository = defectRepository;
    this.inspectionRepository = inspectionRepository;
    this.batchRepository = batchRepository;
    this.auditLogService = auditLogService;
  }

  @Transactional
  public BatchConclusion recalculate(Long batchId) {
    List<DefectRecord> defects = defectRepository.selectList(new LambdaQueryWrapper<DefectRecord>()
        .eq(DefectRecord::getBatchId, batchId));
    List<QualityInspection> inspections = inspectionRepository.selectList(
        new LambdaQueryWrapper<QualityInspection>()
            .eq(QualityInspection::getBatchId, batchId));

    long severeOpen = defects.stream()
        .filter(d -> isSevere(d.getSeverity()))
        .filter(d -> !DispositionStatus.CLOSED.name().equals(d.getDispositionStatus()))
        .count();

    QualityInspection latestFinal = inspections.stream()
        .filter(i -> InspectionType.FINAL.name().equals(i.getInspectionType()))
        .max(Comparator.comparing(QualityInspection::getInspectedAt,
            Comparator.nullsFirst(Comparator.naturalOrder())))
        .orElse(null);

    BatchConclusionStatus targetStatus;
    String reason;
    if (severeOpen > 0) {
      targetStatus = BatchConclusionStatus.PENDING;
      reason = "存在 " + severeOpen + " 条未处置的严重（主要/致命）不良，结论停在待处理";
    } else if (latestFinal == null) {
      if (defects.isEmpty()) {
        // 刚建批次：既没有不良也没有终检，保持待处理，等检验/不良数据进来
        targetStatus = BatchConclusionStatus.PENDING;
        reason = "批次尚无终检记录，等待检验与不良数据";
      } else {
        // 有不良（已处置完的严重不良或仅轻微不良）但没有终检，需要终检/复检兜底
        targetStatus = BatchConclusionStatus.RECHECK;
        reason = "不良已处置完，但批次尚无终检记录，需终检/复检";
      }
    } else {
      InspectionResultStatus finalResult = InspectionResultStatus.valueOf(
          latestFinal.getResultStatus());
      switch (finalResult) {
        case PASS, CONDITIONAL_PASS -> {
          targetStatus = BatchConclusionStatus.RELEASABLE;
          reason = (finalResult == InspectionResultStatus.CONDITIONAL_PASS
              ? "终检让步接收且严重不良已处置完，可放行"
              : "终检合格且严重不良已处置完，可放行");
        }
        case RECHECK -> {
          targetStatus = BatchConclusionStatus.RECHECK;
          reason = "终检结论为待复检，需复检后再判定";
        }
        case FAIL -> {
          targetStatus = BatchConclusionStatus.RECHECK;
          reason = "终检不合格，需复检后再判定";
        }
        default -> {
          targetStatus = BatchConclusionStatus.RECHECK;
          reason = "终检结论未明确，需复检后再判定";
        }
      }
    }

    BatchConclusion conclusion = conclusionRepository.selectOne(
        new LambdaQueryWrapper<BatchConclusion>()
            .eq(BatchConclusion::getBatchId, batchId));
    if (conclusion == null) {
      conclusion = new BatchConclusion();
      conclusion.setId(IdGenerator.nextId());
      conclusion.setBatchId(batchId);
      conclusion.setConclusionStatus(targetStatus.name());
      conclusion.setReason(reason);
      conclusion.setFinalInspectionId(latestFinal == null ? null : latestFinal.getId());
      conclusion.setUpdatedAt(LocalDateTime.now());
      conclusionRepository.insert(conclusion);
      appendHistory(batchId, null, targetStatus, reason);
      return conclusion;
    }

    String previous = conclusion.getConclusionStatus();
    conclusion.setConclusionStatus(targetStatus.name());
    conclusion.setReason(reason);
    conclusion.setFinalInspectionId(latestFinal == null ? null : latestFinal.getId());
    conclusion.setUpdatedAt(LocalDateTime.now());
    conclusionRepository.updateById(conclusion);

    // 同步批次表上的冗余状态，保持批次列表/追溯口径一致
    ProductBatch batch = batchRepository.selectById(batchId);
    if (batch != null && !targetStatus.name().equals(batch.getBatchStatus())) {
      batch.setBatchStatus(targetStatus.name());
      batchRepository.updateById(batch);
      auditLogService.record(CurrentUserContext.actorName(), "BATCH_STATUS_SYNC", "PRODUCT_BATCH",
          batchId, LogTemplates.render(LogTemplates.BATCH_STATUS_SYNC,
              batch.getBatchNo(), targetStatus.name()));
    }

    if (!targetStatus.name().equals(previous)) {
      appendHistory(batchId, previous, targetStatus, reason);
      ProductBatch ctx = batch != null ? batch : batchRepository.selectById(batchId);
      String batchNo = ctx == null ? String.valueOf(batchId) : ctx.getBatchNo();
      String template = switch (targetStatus) {
        case RELEASABLE -> LogTemplates.CONCLUSION_RELEASE;
        case RECHECK -> LogTemplates.CONCLUSION_RECHECK;
        case PENDING -> LogTemplates.CONCLUSION_HOLD_PENDING;
      };
      Object[] args = targetStatus == BatchConclusionStatus.PENDING
          ? new Object[]{batchNo, severeOpen}
          : new Object[]{batchNo,
              latestFinal == null ? reason : latestFinal.getResultStatus()};
      auditLogService.record(CurrentUserContext.actorName(),
          "CONCLUSION_" + targetStatus.name(), "PRODUCT_BATCH", batchId,
          LogTemplates.render(LogTemplates.CONCLUSION_CHANGE, batchNo,
              String.valueOf(previous), targetStatus.name(), reason)
              + " | " + LogTemplates.render(template, args));
    }
    return conclusion;
  }

  private void appendHistory(Long batchId, String from, BatchConclusionStatus to, String reason) {
    BatchConclusionHistory history = new BatchConclusionHistory();
    history.setId(IdGenerator.nextId());
    history.setBatchId(batchId);
    history.setFromStatus(from);
    history.setToStatus(to.name());
    history.setReason(reason);
    history.setChangedBy(CurrentUserContext.actorName());
    history.setChangedAt(LocalDateTime.now());
    historyRepository.insert(history);
  }

  private boolean isSevere(String severity) {
    return DefectSeverity.MAJOR.name().equals(severity)
        || DefectSeverity.CRITICAL.name().equals(severity);
  }

  public BatchConclusion getByBatchId(Long batchId) {
    return conclusionRepository.selectOne(new LambdaQueryWrapper<BatchConclusion>()
        .eq(BatchConclusion::getBatchId, batchId));
  }

  public List<BatchConclusionHistory> listHistory(Long batchId) {
    return historyRepository.selectList(new LambdaQueryWrapper<BatchConclusionHistory>()
        .eq(BatchConclusionHistory::getBatchId, batchId)
        .orderByAsc(BatchConclusionHistory::getChangedAt));
  }
}
