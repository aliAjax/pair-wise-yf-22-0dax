package com.generated.qualityTrace.exceptions;

import com.generated.qualityTrace.constants.ErrorCodes;
import org.springframework.http.HttpStatus;

/** Service 错误码 -> HTTP 状态码的翻译表，供 controller 包装使用。 */
public final class ExceptionStatusMapping {

  private ExceptionStatusMapping() {}

  public static HttpStatus httpStatusOf(String code) {
    return switch (code) {
      case ErrorCodes.AUTH_REQUIRED, ErrorCodes.BAD_CREDENTIALS -> HttpStatus.UNAUTHORIZED;
      case ErrorCodes.RBAC_DENIED -> HttpStatus.FORBIDDEN;
      case ErrorCodes.NOT_FOUND -> HttpStatus.NOT_FOUND;
      case ErrorCodes.CONFLICT, ErrorCodes.DUPLICATE -> HttpStatus.CONFLICT;
      case ErrorCodes.RATE_LIMITED -> HttpStatus.TOO_MANY_REQUESTS;
      case ErrorCodes.INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }
}
