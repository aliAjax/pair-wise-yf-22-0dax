package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.exceptions.ControllerException;
import com.generated.qualityTrace.exceptions.ExceptionStatusMapping;
import com.generated.qualityTrace.exceptions.ServiceException;
import java.util.function.Supplier;

/**
 * 控制器层统一入口：service 抛出的 ServiceException 在这里被重新包装成
 * ControllerException（带上 HTTP 状态码），符合“service 与 controller 分别包装异常”的要求。
 */
final class ControllerSupport {

  private ControllerSupport() {}

  static <T> T call(Supplier<T> action) {
    try {
      return action.get();
    } catch (ServiceException ex) {
      throw new ControllerException(ex.getCode(), ex.getMessage(),
          ExceptionStatusMapping.httpStatusOf(ex.getCode()), ex);
    }
  }

  static void run(Runnable action) {
    try {
      action.run();
    } catch (ServiceException ex) {
      throw new ControllerException(ex.getCode(), ex.getMessage(),
          ExceptionStatusMapping.httpStatusOf(ex.getCode()), ex);
    }
  }
}
