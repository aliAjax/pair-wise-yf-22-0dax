package com.generated.qualityTrace.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.TreeMap;

/**
 * 幂等键：把请求体规范化（JSON 字段排序）后取 SHA-256。
 * 同一批次 + 同样内容重复提交检验单/不良记录时命中同一 content_hash，按原记录返回。
 */
public final class IdempotencyHasher {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private IdempotencyHasher() {}

  public static String hash(Object payload) {
    try {
      String canonical = MAPPER.writeValueAsString(MAPPER.convertValue(payload, TreeMap.class));
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] bytes = digest.digest(canonical.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder(64);
      for (byte b : bytes) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();
    } catch (Exception e) {
      throw new IllegalStateException("无法生成幂等键", e);
    }
  }
}
