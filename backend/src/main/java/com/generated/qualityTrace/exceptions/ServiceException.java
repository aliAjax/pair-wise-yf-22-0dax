package com.generated.qualityTrace.exceptions;

/** 服务层业务异常基类：service 内所有可预期错误统一抛它。 */
public class ServiceException extends RuntimeException {
  private final String code;

  public ServiceException(String code, String message) {
    super(message);
    this.code = code;
  }

  public ServiceException(String code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
