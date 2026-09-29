package com.generated.qualityTrace.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** 极简 HS256 JWT：header.payload.signature，仅承载 sub/username/role/exp，满足本地 RBAC 需求。 */
public final class JwtUtil {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
  private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

  private JwtUtil() {}

  public static String sign(String secret, long ttlSeconds,
                            Long userId, String username, String role) {
    try {
      long exp = System.currentTimeMillis() / 1000L + ttlSeconds;
      String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
      String payload = MAPPER.createObjectNode()
          .put("sub", String.valueOf(userId))
          .put("username", username)
          .put("role", role)
          .put("exp", exp)
          .toString();
      String h = encode(header.getBytes(StandardCharsets.UTF_8));
      String p = encode(payload.getBytes(StandardCharsets.UTF_8));
      String signingInput = h + "." + p;
      String sig = encode(hmac(secret, signingInput));
      return signingInput + "." + sig;
    } catch (Exception e) {
      throw new IllegalStateException("JWT 签发失败", e);
    }
  }

  public static JsonNode verify(String secret, String token) {
    try {
      String[] parts = token.split("\\.");
      if (parts.length != 3) {
        return null;
      }
      String signingInput = parts[0] + "." + parts[1];
      String expected = encode(hmac(secret, signingInput));
      if (!constantTimeEquals(expected, parts[2])) {
        return null;
      }
      JsonNode claims = MAPPER.readTree(URL_DECODER.decode(parts[1]));
      long exp = claims.path("exp").asLong(0L);
      if (exp > 0 && exp < System.currentTimeMillis() / 1000L) {
        return null;
      }
      return claims;
    } catch (Exception e) {
      return null;
    }
  }

  private static byte[] hmac(String secret, String input) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
    return mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
  }

  private static String encode(byte[] bytes) {
    return URL_ENCODER.encodeToString(bytes);
  }

  private static boolean constantTimeEquals(String a, String b) {
    if (a.length() != b.length()) {
      return false;
    }
    int result = 0;
    for (int i = 0; i < a.length(); i++) {
      result |= a.charAt(i) ^ b.charAt(i);
    }
    return result == 0;
  }
}
