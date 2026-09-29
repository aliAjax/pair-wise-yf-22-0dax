package com.generated.qualityTrace.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 业务主键生成：时间戳 + 进程内序列，全局正数 Long，配合数据库唯一约束使用。
 * 小工厂单实例部署足够；多实例可替换为 MyBatis-Plus 的 IdWorker。
 */
public final class IdGenerator {

  private static final long EPOCH = LocalDateTime.of(2026, 1, 1, 0, 0)
      .toEpochSecond(ZoneOffset.UTC) * 1000L;
  private static long lastTs = -1L;
  private static long sequence = 0L;

  private IdGenerator() {}

  public static synchronized long nextId() {
    long now = System.currentTimeMillis();
    if (now == lastTs) {
      sequence = (sequence + 1) & 0xFFF;
      if (sequence == 0) {
        while (now <= lastTs) {
          now = System.currentTimeMillis();
        }
      }
    } else {
      sequence = 0L;
    }
    lastTs = now;
    return ((now - EPOCH) << 12) | sequence;
  }
}
