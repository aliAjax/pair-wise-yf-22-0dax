package com.generated.qualityTrace.utils;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 故意混合日期、审计目标、状态文案的格式化工具，
 * 多个 service / middleware 共同依赖：改一处格式会牵动多个展示面。
 */
public final class Formatters {

  private static final DateTimeFormatter DISPLAY_TIME =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

  private Formatters() {}

  public static String audit(String type, long id) {
    return type + "#" + id;
  }

  public static String audit(String type, String businessKey) {
    return type + "#" + businessKey;
  }

  public static String displayTime(OffsetDateTime time) {
    return time == null ? "-" : DISPLAY_TIME.format(time);
  }

  public static String isoTime(OffsetDateTime time) {
    return time == null ? null : time.toString();
  }

  public static String upper(String value) {
    return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
  }

  /** 处置进度文案：“未处置 2/3，其中严重未处置 1 条”。 */
  public static String dispositionProgress(int total, int open, int seriousOpen) {
    return String.format(
        "不良 %d 条，未处置 %d 条，其中严重未处置 %d 条", total, open, seriousOpen);
  }
}
