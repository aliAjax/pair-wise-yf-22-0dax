package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.models.AuditLog;
import com.generated.qualityTrace.repositories.AuditLogRepository;
import com.generated.qualityTrace.utils.Formatters;
import com.generated.qualityTrace.utils.IdGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * auditLogMiddleware：对写操作（POST/PUT/PATCH/DELETE）记录接口级访问日志。
 * 业务实体级的追溯日志由各 service 用 LogTemplates 另写一条，两层互补。
 */
@Component
public class AuditLogMiddleware implements HandlerInterceptor {

  private final AuditLogRepository auditLogRepository;

  public AuditLogMiddleware(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler) {
    String method = request.getMethod();
    if ("GET".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)
        || "HEAD".equalsIgnoreCase(method)) {
      return true;
    }
    AuditLog log = new AuditLog();
    log.setId(IdGenerator.nextId());
    log.setActor(CurrentUserContext.actorName());
    log.setAction("HTTP_" + method.toUpperCase());
    log.setTargetType("API");
    log.setTargetId(request.getRequestURI());
    log.setDetail(Formatters.audit("API_REQUEST", method + " " + request.getRequestURI()));
    log.setCreatedAt(LocalDateTime.now());
    auditLogRepository.insert(log);
    return true;
  }
}
