package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.ControllerException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * rateLimitMiddleware：按 客户端 IP + 请求方法 + 路径 做固定窗口限流。
 * 默认每个窗口（60 秒）120 次，超出返回 429。
 */
@Component
public class RateLimitMiddleware implements HandlerInterceptor {

  private static final long WINDOW_MILLIS = 60_000L;
  private static final int MAX_REQUESTS = 120;

  private record Window(long startAt, int count) {
  }

  private final Map<String, Window> windows = new ConcurrentHashMap<>();

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler) {
    String key = clientIp(request) + "|" + request.getMethod() + "|" + request.getRequestURI();
    long now = System.currentTimeMillis();

    Window current = windows.compute(key, (k, old) -> {
      if (old == null || now - old.startAt() > WINDOW_MILLIS) {
        return new Window(now, 1);
      }
      return new Window(old.startAt(), old.count() + 1);
    });
    if (current.count() > MAX_REQUESTS) {
      throw new ControllerException(ErrorCodes.RATE_LIMITED, ErrorMessages.RATE_LIMITED,
          HttpStatus.TOO_MANY_REQUESTS);
    }
    return true;
  }

  private String clientIp(HttpServletRequest request) {
    String forwarded = request.getHeader("X-Forwarded-For");
    if (forwarded != null && !forwarded.isBlank()) {
      return forwarded.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}
