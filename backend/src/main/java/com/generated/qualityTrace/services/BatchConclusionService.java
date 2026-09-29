package com.generated.qualityTrace.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.generated.qualityTrace.constants.BatchConclusionCode;
import com.generated.qualityTrace.constants.ConclusionTrigger;
import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.ProductBatchStatus;
import com.generated.qualityTrace.models.BatchConclusionHistory;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.repositories.BatchConclusionHistoryRepository;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.types.DispositionProgressDto;
import com.generated.qualityTrace.utils.Formatters;
import com.generated.qualityTrace.utils.RequestContext;

/**
 * 批次质量结论引擎，规则：
 * 1. 只要还有未处置（PENDING/IN_PROGRESS）的 MAJOR/CRITICAL 严重不良，结论停在 PENDING_DISPOSITION；
 * 2. 严重不良全部 CLOSED 后，结合终检结果：
 *    - 没有终检单 / 终检不是 PASS -> PENDING_RECHECK；
 *    - 终检 PASS -> RELEASABLE；
 * 3. 只有结论真正发生变化才追加结论变化历史（终检通过也不能跳过未处置严重不良）。
 */
@Service
public class BatchConclusionService {

  private static final Logger log = LoggerFactory.getLogger(BatchConclusionService.class);

  private final ProductBatchRepository batchRepository;
  private final DefectRecordRepository defectRepository;
  private final QualityInspectionRepository inspectionRepository;
  private final BatchConclusionHistoryRepository historyRepository;

  public BatchConclusionService(
      ProductBatchRepository batchRepository,
      DefectRecordRepository defectRepository,
      QualityInspectionRepository inspectionRepository,
      BatchConclusionHistoryRepository historyRepository) {
    this.batchRepository = batchRepository;
    this.defectRepository = defectRepository;
    this.inspectionRepository = inspectionRepository;
    this.historyRepository = historyRepository;
  }

  public DispositionProgressDto progressOf(Long batchId) {
    List<DefectRecord> defects = defectRepository.findByBatchId(batchId);
    int open = 0;
    int closed = 0;
    int seriousTotal = 0;
    int seriousOpen = 0;
    List<String> openSeriousIds = new ArrayList<>();
    for (DefectRecord defect : defects) {
      DefectSeverity severity = parseSeverity(defect.severity);
      DefectDispositionStatus disposition = parseDisposition(defect.dispositionStatus);
      boolean serious = severity != null && severity.isSerious();
      boolean done = disposition != null && disposition.isDone();
      if (serious) {
        seriousTotal++;
      }
      if (done) {
        closed++;
      } else {
        open++;
        if (serious) {
          seriousOpen++;
          openSeriousIds.add(String.valueOf(defect.id));
        }
      }
    }
    return new DispositionProgressDto(
        defects.size(), open, closed, seriousTotal, seriousOpen, openSeriousIds);
  }

  /** 重算指定批次结论并在变化时落历史；trigger 标记本次重算由哪个业务动作触发。 */
  @Transactional
  public synchronized BatchConclusionCode recompute(
      ProductBatch batch, ConclusionTrigger trigger) {
    DispositionProgressDto progress = progressOf(batch.id);

    BatchConclusionCode next;
    String reason;
    if (progress.hasOpenSerious()) {
      next = BatchConclusionCode.PENDING_DISPOSITION;
      reason =
          "还有 "
              + progress.seriousOpenCount()
              + " 条严重不良未处置（"
              + String.join(",", progress.openSeriousDefectIds())
              + "）";
    } else {
      Optional<QualityInspection> latestFinal =
          inspectionRepository.findLatestFinalByBatchId(batch.id);
      if (latestFinal.isEmpty()) {
        next = BatchConclusionCode.PENDING_RECHECK;
        reason = "严重不良已处置完，尚无终检记录，需复检";
      } else {
        QualityInspection fin = latestFinal.get();
        InspectionResultStatus result = parseInspectionResult(fin.resultStatus);
        if (result != null && result.isReleaseReady()) {
          next = BatchConclusionCode.RELEASABLE;
          reason = "严重不良全部处置完成，终检合格可放行";
        } else {
          next = BatchConclusionCode.PENDING_RECHECK;
          reason = "严重不良已处置完，终检结论为 "
              + (result == null ? fin.resultStatus : result.getLabel())
              + "，需复检";
        }
      }
    }

    String previous = batch.conclusionCode;
    boolean changed = previous == null || !previous.equals(next.name());

    OffsetDateTime now = OffsetDateTime.now();
    batch.conclusionCode = next.name();
    batch.conclusionReason = reason;
    batch.conclusionUpdatedAt = now;
    batch.updatedAt = now;
    applyBatchStatus(batch, next, progress);
    batchRepository.save(batch);

    if (changed) {
      BatchConclusionHistory history = new BatchConclusionHistory();
      history.batchId = batch.id;
      history.fromConclusion = previous;
      history.toConclusion = next.name();
      history.reason = reason;
      history.triggerEvent = trigger.name();
      history.actor = RequestContext.getActor();
      history.createdAt = now;
      historyRepository.save(history);
      log.info(
          LogTemplates.BATCH_CONCLUSION_CHANGED,
          batch.batchNo,
          previous,
          next.name(),
          reason);
    }
    return next;
  }

  /** 结论联动批次状态；已放行(RELEASED)批次在出现新不良时回退到暂扣。 */
  private void applyBatchStatus(
      ProductBatch batch, BatchConclusionCode conclusion, DispositionProgressDto progress) {
    String oldStatus = batch.batchStatus;
    String newStatus;
    if (ProductBatchStatus.RELEASED.name().equals(oldStatus) && conclusion
        != BatchConclusionCode.RELEASABLE) {
      newStatus = ProductBatchStatus.fromConclusion(conclusion).name();
    } else if (ProductBatchStatus.RELEASED.name().equals(oldStatus) && conclusion
        == BatchConclusionCode.RELEASABLE) {
      newStatus = ProductBatchStatus.RELEASED.name();
    } else {
      newStatus = ProductBatchStatus.fromConclusion(conclusion).name();
    }
    if (!newStatus.equals(oldStatus)) {
      log.info(
          LogTemplates.BATCH_STATUS_CHANGED,
          batch.batchNo,
          oldStatus,
          newStatus);
      batch.batchStatus = newStatus;
    }
  }

  static DefectSeverity parseSeverity(String code) {
    try {
      return code == null ? null : DefectSeverity.valueOf(code);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

  static DefectDispositionStatus parseDisposition(String code) {
    try {
      return code == null ? null : DefectDispositionStatus.valueOf(code);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

  static InspectionResultStatus parseInspectionResult(String code) {
    try {
      return code == null ? null : InspectionResultStatus.valueOf(code);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

  public static String progressText(DispositionProgressDto progress) {
    return Formatters.dispositionProgress(
        progress.totalDefects(),
        progress.openCount(),
        progress.seriousOpenCount());
  }
}
