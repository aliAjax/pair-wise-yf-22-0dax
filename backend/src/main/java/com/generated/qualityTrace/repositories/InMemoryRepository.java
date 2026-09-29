package com.generated.qualityTrace.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存仓储基类：本地数据库不可用时仍可完成登记、处置与追溯。
 * 换成 PostgreSQL + MyBatis-Plus 时只需替换子类实现，service 层不动。
 */
public abstract class InMemoryRepository<T> {

  protected final ConcurrentHashMap<Long, T> store = new ConcurrentHashMap<>();
  protected final AtomicLong sequence = new AtomicLong(0);

  public T save(T entity) {
    Long id = extractId(entity);
    if (id == null) {
      id = sequence.incrementAndGet();
      assignId(entity, id);
    } else {
      sequence.updateAndGet(current -> Math.max(current, id));
    }
    store.put(id, entity);
    return entity;
  }

  public Optional<T> findById(Long id) {
    return Optional.ofNullable(store.get(id));
  }

  public List<T> findAll() {
    return new ArrayList<>(store.values());
  }

  public void deleteAll() {
    store.clear();
    sequence.set(0);
  }

  protected abstract Long extractId(T entity);

  protected abstract void assignId(T entity, Long id);
}
