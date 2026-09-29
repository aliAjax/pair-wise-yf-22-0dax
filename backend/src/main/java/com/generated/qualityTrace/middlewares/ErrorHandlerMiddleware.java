package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.ApiError;
import com.generated.qualityTrace.exceptions.ControllerException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * errorHandlerMiddleware：全局异常出口。
 * ControllerException（controller 已包装过的业务错误）按其 httpStatus 返回；
 * 参数解析失败归 400；其余未预期错误归 500，避免堆栈直接外泄。
 */
@RestControllerAdvice
public class ErrorHandlerMiddleware {

  private static final Logger log = LoggerFactory.getLogger(ErrorHandlerMiddleware.class);

  @ExceptionHandler(ControllerException.class)
  public ResponseEntity<ApiError> handleController(ControllerException ex,
                                                   HttpServletRequest request) {
    if (ex.getHttpStatus().is5xxServerError()) {
      log.error("控制器处理失败 path={} code={}", request.getRequestURI(), ex.getCode(), ex);
    } else {
      log.warn("业务请求被拒绝 path={} code={} message={}", request.getRequestURI(),
          ex.getCode(), ex.getMessage());
    }
    return ResponseEntity.status(ex.getHttpStatus()).body(body(ex.getCode(), ex.getMessage(),
        request.getRequestURI(), null));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex,
                                                   HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(body(ErrorCodes.INVALID_ARGUMENT, "请求体不是合法 JSON 或字段类型不正确",
            request.getRequestURI(), null));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
    log.error("未预期异常 path={}", request.getRequestURI(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(body(ErrorCodes.INTERNAL_ERROR, ErrorMessages.INTERNAL_ERROR,
            request.getRequestURI(), null));
  }

  private ApiError body(String code, String message, String path, Object ignored) {
    return new ApiError(code, message, path, LocalDateTime.now(), null);
  }
}
