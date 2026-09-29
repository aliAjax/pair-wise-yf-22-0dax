package com.generated.qualityTrace.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.exceptions.ServiceException;
import com.generated.qualityTrace.models.AppUser;
import com.generated.qualityTrace.repositories.AppUserRepository;
import com.generated.qualityTrace.types.LoginRequest;
import com.generated.qualityTrace.types.LoginResponse;
import com.generated.qualityTrace.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** 登录认证服务：校验本地用户表，签发 JWT。禁止接入第三方身份源。 */
@Service
public class AuthService {

  private final AppUserRepository userRepository;
  private final AuditLogService auditLogService;
  private final String jwtSecret;
  private final long ttlSeconds;

  public AuthService(AppUserRepository userRepository, AuditLogService auditLogService,
                     @Value("${app.jwt.secret}") String jwtSecret,
                     @Value("${app.jwt.ttl-seconds:43200}") long ttlSeconds) {
    this.userRepository = userRepository;
    this.auditLogService = auditLogService;
    this.jwtSecret = jwtSecret;
    this.ttlSeconds = ttlSeconds;
  }

  public LoginResponse login(LoginRequest request) {
    if (request == null || request.username() == null || request.password() == null) {
      throw new ServiceException(ErrorCodes.BAD_CREDENTIALS, ErrorMessages.BAD_CREDENTIALS);
    }
    AppUser user = userRepository.selectOne(new LambdaQueryWrapper<AppUser>()
        .eq(AppUser::getUsername, request.username()));
    if (user == null || !Boolean.TRUE.equals(user.getEnabled())
        || !request.password().equals(user.getPassword())) {
      throw new ServiceException(ErrorCodes.BAD_CREDENTIALS, ErrorMessages.BAD_CREDENTIALS);
    }
    String token = JwtUtil.sign(jwtSecret, ttlSeconds, user.getId(), user.getUsername(),
        user.getRole());
    auditLogService.record(user.getUsername(), "AUTH_LOGIN", "USER", user.getId(),
        LogTemplates.render(LogTemplates.AUTH_LOGIN, user.getUsername(), user.getRole()));
    return new LoginResponse(token, user.getId(), user.getUsername(), user.getDisplayName(),
        user.getRole(), ttlSeconds);
  }
}
