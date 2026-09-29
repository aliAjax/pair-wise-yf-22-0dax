package com.generated.qualityTrace.middlewares;

import com.fasterxml.jackson.databind.JsonNode;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.ControllerException;
import com.generated.qualityTrace.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * authMiddleware：校验 Authorization: Bearer <token>，
 * 通过后把用户信息放进 CurrentUserContext，请求结束清理。
 */
@Component
public class AuthMiddleware implements HandlerInterceptor {

  private final String jwtSecret;

  public AuthMiddleware(@Value("${app.jwt.secret}") String jwtSecret) {
    this.jwtSecret = jwtSecret;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler) {
    String authorization = request.getHeader("Authorization");
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      throw new ControllerException(ErrorCodes.AUTH_REQUIRED, ErrorMessages.AUTH_REQUIRED,
          HttpStatus.UNAUTHORIZED);
    }
    String token = authorization.substring(7).trim();
    JsonNode claims = JwtUtil.verify(jwtSecret, token);
    if (claims == null) {
      throw new ControllerException(ErrorCodes.AUTH_REQUIRED, ErrorMessages.AUTH_REQUIRED,
          HttpStatus.UNAUTHORIZED);
    }
    CurrentUserContext.set(new CurrentUserContext.CurrentUser(
        Long.valueOf(claims.path("sub").asText()),
        claims.path("username").asText(),
        claims.path("username").asText(),
        claims.path("role").asText()));
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                              Object handler, Exception ex) {
    CurrentUserContext.clear();
  }
}
