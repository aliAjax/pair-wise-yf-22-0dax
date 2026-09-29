package com.generated.qualityTrace.exceptions;

import org.springframework.http.HttpStatus;

/**
 * 控制器层异常：controller 捕获 ServiceException 后按 HTTP 语义再包一层，
 * 禁止只在全局处理器里吞掉全部异常。
 */
public class ControllerException extends RuntimeException {
  private final String code;
  private final HttpStatus httpStatus;

  public ControllerException(String code, String message, HttpStatus httpStatus) {
    super(message);
    this.code = code;
    this.httpStatus = httpStatus;
  }

  public ControllerException(String code, String message, HttpStatus httpStatus, Throwable cause) {
    super(message, cause);
    this.code = code;
    this.httpStatus = httpStatus;
  }

  public String getCode() {
    return code;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }
}
