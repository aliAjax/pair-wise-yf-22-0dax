package com.generated.qualityTrace.repositories;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.IdempotentRequest;

/** requestId 独立序列仓储：键本身就是 requestId 字符串。 */
@Repository
public class IdempotentRequestRepository {

  private final java.util.concurrent.ConcurrentHashMap<String, IdempotentRequest> store =
      new java.util.concurrent.ConcurrentHashMap<>();

  public Optional<IdempotentRequest> find(String requestId) {
    return Optional.ofNullable(store.get(requestId));
  }

  public void save(IdempotentRequest entity) {
    store.put(entity.requestId, entity);
  }

  public void clear() {
    store.clear();
  }
}
