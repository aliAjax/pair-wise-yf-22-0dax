package com.generated.qualityTrace.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

/** 统一错误响应体，由 ErrorHandlerMiddleware（@RestControllerAdvice）组装。 */
public record ApiError(String code, String message, String path, LocalDateTime timestamp,
                       Map<String, Object> extra) {
}
