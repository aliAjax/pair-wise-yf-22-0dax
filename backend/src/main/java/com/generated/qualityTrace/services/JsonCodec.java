package com.generated.qualityTrace.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/** service 层共享的 JSON 编解码器，供幂等指纹与响应快照使用。 */
public final class JsonCodec {

  static final ObjectMapper MAPPER =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private JsonCodec() {}

  public static String writeValueAsString(Object value) throws Exception {
    return MAPPER.writeValueAsString(value);
  }

  public static <T> T readValue(String json, Class<T> type) throws Exception {
    return MAPPER.readValue(json, type);
  }

  public static <T> T convert(Object value, Class<T> type) {
    return MAPPER.convertValue(value, type);
  }
}
