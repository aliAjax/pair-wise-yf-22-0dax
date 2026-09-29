package com.generated.qualityTrace.exceptions;

import com.generated.qualityTrace.constants.ErrorCodes;

/** controller 层二次包装，和 service 包装区分开，满足分层异常处理要求。 */
public class ControllerLayerException extends BusinessException {
  public ControllerLayerException(String message, Throwable cause) {
    super(ErrorCodes.CONTROLLER_ERROR, message, cause);
  }
}
