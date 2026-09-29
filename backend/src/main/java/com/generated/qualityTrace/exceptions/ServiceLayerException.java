package com.generated.qualityTrace.exceptions;

import com.generated.qualityTrace.constants.ErrorCodes;

/** service 层对底层异常的统一包装，禁止把细节直接抛到 controller。 */
public class ServiceLayerException extends BusinessException {
  public ServiceLayerException(String message, Throwable cause) {
    super(ErrorCodes.SERVICE_ERROR, message, cause);
  }
}
