package com.generated.qualityTrace.exceptions;

/** 业务异常基类，携带集中定义的错误码（constants/ErrorCodes）。 */
public class BusinessException extends RuntimeException {
  private final String code;

  public BusinessException(String code, String message) {
    super(message);
    this.code = code;
  }

  public BusinessException(String code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
