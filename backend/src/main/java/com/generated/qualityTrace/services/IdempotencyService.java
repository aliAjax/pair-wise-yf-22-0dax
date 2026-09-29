package com.generated.qualityTrace.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.exceptions.IdempotencyConflictException;
import com.generated.qualityTrace.models.IdempotentRequest;
import com.generated.qualityTrace.repositories.IdempotentRequestRepository;

/**
 * requestId 幂等：
 * - 首次请求：执行 supplier，保存响应快照；
 * - 相同 requestId + 相同请求体指纹：直接返回原结果（deduplicated=true），不会重复累计；
 * - 相同 requestId + 不同请求体指纹：409 拒绝，防止幂等键复用掩盖误操作。
 */
@Service
public class IdempotencyService {

  private static final Logger log = LoggerFactory.getLogger(IdempotencyService.class);

  private final IdempotencyRequestRepository repository;

  public IdempotencyService(IdempotencyRequestRepository repository) {
    this.repository = repository;
  }

  /**
   * @param requestId  幂等键，为空则不启用幂等
   * @param payload    原始请求对象，参与指纹计算（requestId 本身先剔除避免自引用）
   * @param targetType 目标实体类型
   * @param supplier   首次执行动作，返回目标 id 与响应 JSON
   */
  @SuppressWarnings("unchecked")
  public <T> IdempotentExecution<T>> execute(
      String requestId,
      Object payload,
      String targetType,
      Supplier<IdempotentExecution<T>> supplier) {
    if (requestId == null || requestId.isBlank()) {
      return supplier.get();
    }
    String hash = fingerprint(payload);
    Optional<IdempotentRequest> existing = repository.find(requestId);
    if (existing.isPresent()) {
      IdempotentRequest saved = existing.get();
      if (!saved.payloadHash.equals(hash)) {
        log.warn(LogTemplates.IDEMPOTENCY_CONFLICT, requestId);
        throw new IdempotencyConflictException(
            ErrorMessages.format(ErrorMessages.IDEMPOTENCY_CONFLICT, requestId));
      }
      log.info(LogTemplates.IDEMPOTENCY_REPLAY, requestId, targetType, saved.targetId);
      T replayed;
      try {
        replayed = (T) JsonCodec.readValue(saved.responseJson, Object.class);
      } catch (Exception ex) {
        replayed = (T) Map.of("targetId", saved.targetId);
      }
      return new IdempotentExecution<>(replayed, saved.targetId, true);
    }

    IdempotentExecution<T> result = supplier.get();
    IdempotentRequest record = new IdempotentRequest();
    record.requestId = requestId;
    record.targetType = targetType;
    record.targetId = result.targetId();
    record.payloadHash = hash;
    try {
      record.responseJson = JsonCodec.writeValueAsString(result.data());
    } catch (Exception ex) {
      record.responseJson = "{\"targetId\":" + result.targetId() + "}";
    }
    record.createdAt = OffsetDateTime.now();
    repository.save(record);
    return result;
  }

  /** 规范化 JSON：TreeMap 对 key 排序后做 SHA-256，保证字段顺序不同也能识别为同一请求。 */
  static String fingerprint(Object payload) {
    try {
      Object normalized = normalize(JsonCodec.convert(payload, Object.class));
      String canonical = JsonCodec.writeValueAsString(normalized);
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] raw = digest.digest(canonical.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(raw);
    } catch (Exception ex) {
      throw new IllegalStateException("failed to build idempotency fingerprint", ex);
    }
  }

  @SuppressWarnings("unchecked")
  private static Object normalize(Object value) {
    if (value instanceof Map<?, ?> map) {
      TreeMap<String, Object> sorted = new TreeMap<>();
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        String key = String.valueOf(entry.getKey());
        if ("requestId".equals(key)) {
          continue;
        }
        sorted.put(key, normalize(entry.getValue()));
      }
      return sorted;
    }
    if (value instanceof List<?> list) {
      return list.stream().map(IdempotencyService::normalize).toList();
    }
    return value;
  }

  /** supplier 的返回体：data 为响应对象、targetId 为新实体 id、deduplicated 为是否命中旧记录。 */
  public record IdempotentExecution<T>(T data, Long targetId, boolean deduplicated) {}
}
