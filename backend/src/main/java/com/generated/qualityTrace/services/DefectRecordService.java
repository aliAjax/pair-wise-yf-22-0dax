package com.generated.qualityTrace.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.generated.qualityTrace.constants.DispositionStatus;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.DefectRecordDtoFactory;
import com.generated.qualityTrace.exceptions.ServiceException;
import com.generated.qualityTrace.middlewares.CurrentUserContext;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.types.DefectRecordResponse;
import com.generated.qualityTrace.types.DisposeDefectRequest;
import com.generated.qualityTrace.types.RegisterDefectRequest;
import com.generated.qualityTrace.utils.IdGenerator;
import com.generated.qualityTrace.utils.IdempotencyHasher;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 不良记录服务：登记不良（幂等）、推进/关闭处置、按批次查询。
 * 登记的 MAJOR/CRITICAL 在关闭前会把批次结论压在“待处理”；关闭后触发结论重算。
 */
@Service
public class DefectRecordService {

  private final DefectRecordRepository defectRepository;
  private final ProductBatchService batchService;
  private final BatchConclusionService conclusionService;
  private final AuditLogService auditLogService;

  public DefectRecordService(DefectRecordRepository defectRepository,
                             ProductBatchService batchService,
                             BatchConclusionService conclusionService,
                             AuditLogService auditLogService) {
    this.defectRepository = defectRepository;
    this.batchService = batchService;
    this.conclusionService = conclusionService;
    this.auditLogService = auditLogService;
  }

  @Transactional
  public DefectRecordResponse register(RegisterDefectRequest request) {
    ProductBatch batch = batchService.requireByBatchNo(request.batchNo());
    String contentHash = IdempotencyHasher.hash(request);

    DefectRecord existing = defectRepository.selectOne(new LambdaQueryWrapper<DefectRecord>()
        .eq(DefectRecord::getContentHash, contentHash));
    if (existing != null) {
      auditLogService.record(CurrentUserContext.actorName(), "DEFECT_DUPLICATE",
          "DEFECT_RECORD", existing.getId(),
          LogTemplates.render(LogTemplates.DEFECT_DUPLICATE, existing.getId(),
              request.batchNo()));
      return DefectRecordDtoFactory.toResponse(existing, request.batchNo(), true);
    }

    DefectRecord defect = new DefectRecord();
    defect.setId(IdGenerator.nextId());
    defect.setBatchId(batch.getId());
    defect.setDefectType(request.defectType());
    defect.setDefectQty(request.defectQty());
    defect.setSeverity(request.severity());
    defect.setRootCause(request.rootCause());
    defect.setDispositionStatus(DispositionStatus.OPEN.name());
    defect.setRegisteredBy(CurrentUserContext.actorName());
    defect.setContentHash(contentHash);
    defect.setCreatedAt(request.createdAt() == null ? LocalDateTime.now() : request.createdAt());
    defectRepository.insert(defect);

    // 新不良（尤其严重不良）可能把可放行/待复检的批次重新压回待处理
    conclusionService.recalculate(batch.getId());

    auditLogService.record(CurrentUserContext.actorName(), "DEFECT_REGISTER", "DEFECT_RECORD",
        defect.getId(),
        LogTemplates.render(LogTemplates.DEFECT_REGISTER, request.batchNo(),
            defect.getDefectType(), defect.getSeverity(), defect.getDefectQty()));
    return DefectRecordDtoFactory.toResponse(defect, request.batchNo(), false);
  }

  @Transactional
  public DefectRecordResponse dispose(Long defectId, DisposeDefectRequest request) {
    DefectRecord defect = defectRepository.selectById(defectId);
    if (defect == null) {
      throw new ServiceException(ErrorCodes.NOT_FOUND,
          MessageFormat.format(ErrorMessages.DEFECT_NOT_FOUND, defectId));
    }
    ProductBatch batch = batchService.requireById(defect.getBatchId());
    DispositionStatus target = DispositionStatus.valueOf(request.dispositionStatus());

    // 已关闭记录重复/再次提交：按原记录幂等返回，不重复触发结论变化
    if (DispositionStatus.CLOSED.name().equals(defect.getDispositionStatus())) {
      auditLogService.record(CurrentUserContext.actorName(), "DEFECT_DUPLICATE",
          "DEFECT_RECORD", defect.getId(),
          LogTemplates.render(LogTemplates.DEFECT_DUPLICATE, defect.getId(),
              batch.getBatchNo()) + "（已关闭，重复处置请求）");
      return DefectRecordDtoFactory.toResponse(defect, batch.getBatchNo(), true);
    }

    String previous = defect.getDispositionStatus();
    defect.setDispositionStatus(target.name());
    if (request.dispositionNote() != null) {
      defect.setDispositionNote(request.dispositionNote());
    }
    if (target == DispositionStatus.CLOSED) {
      defect.setDisposedBy(CurrentUserContext.actorName());
      defect.setDisposedAt(LocalDateTime.now());
    }
    defectRepository.updateById(defect);

    // 只有关闭严重不良才可能让结论离开“待处理”；处置中仍然算未处置
    conclusionService.recalculate(batch.getId());

    String template = target == DispositionStatus.CLOSED
        ? LogTemplates.DEFECT_CLOSE : LogTemplates.DEFECT_DISPOSE;
    Object[] args = target == DispositionStatus.CLOSED
        ? new Object[]{defect.getId(), batch.getBatchNo(),
            String.valueOf(defect.getDispositionNote())}
        : new Object[]{defect.getId(), previous, target.name()};
    auditLogService.record(CurrentUserContext.actorName(), "DEFECT_DISPOSE", "DEFECT_RECORD",
        String.valueOf(defect.getId()), LogTemplates.render(template, args));
    return DefectRecordDtoFactory.toResponse(defect, batch.getBatchNo(), false);
  }

  public DefectRecord requireById(Long id) {
    DefectRecord defect = defectRepository.selectById(id);
    if (defect == null) {
      throw new ServiceException(ErrorCodes.NOT_FOUND,
          MessageFormat.format(ErrorMessages.DEFECT_NOT_FOUND, id));
    }
    return defect;
  }

  public List<DefectRecord> listByBatchId(Long batchId) {
    return defectRepository.selectList(new LambdaQueryWrapper<DefectRecord>()
        .eq(DefectRecord::getBatchId, batchId)
        .orderByDesc(DefectRecord::getCreatedAt));
  }
}
