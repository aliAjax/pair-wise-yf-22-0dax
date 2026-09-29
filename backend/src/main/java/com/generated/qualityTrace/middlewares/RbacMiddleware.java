package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.ControllerException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * rbacMiddleware：读取 handler 上的 @RequireRoles，当前角色不匹配即 403。
 */
@Component
public class RbacMiddleware implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler) {
    if (!(handler instanceof HandlerMethod handlerMethod)) {
      return true;
    }
    RequireRoles annotation = handlerMethod.getMethodAnnotation(RequireRoles.class);
    if (annotation == null) {
      annotation = handlerMethod.getBeanType().getAnnotation(RequireRoles.class);
    }
    if (annotation == null) {
      return true;
    }
    CurrentUserContext.CurrentUser user = CurrentUserContext.get();
    if (user == null || Arrays.stream(annotation.value()).noneMatch(r -> r.equals(user.role()))) {
      throw new ControllerException(ErrorCodes.RBAC_DENIED, ErrorMessages.RBAC_DENIED,
          HttpStatus.FORBIDDEN);
    }
    return true;
  }
}
