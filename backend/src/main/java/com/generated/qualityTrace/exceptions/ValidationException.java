package com.generated.qualityTrace.exceptions;

/** 入参校验失败，validator 统一抛这个，controller 层映射为 400。 */
public class ValidationException extends BusinessException {
  public ValidationException(String message) {
    super(com.generated.qualityTrace.constants.ErrorCodes.VALIDATION_FAILED, message);
  }
}
