package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.ServiceException;
import java.text.MessageFormat;

/** 校验小工具：统一抛 ServiceException(INVALID_ARGUMENT)。 */
public final class ValidatorSupport {

  private ValidatorSupport() {}

  public static void require(String field, Object value) {
    if (value == null || (value instanceof String s && s.isBlank())) {
      throw new ServiceException(ErrorCodes.INVALID_ARGUMENT,
          MessageFormat.format(ErrorMessages.FIELD_REQUIRED, field));
    }
  }

  public static void fail(String template, Object... args) {
    throw new ServiceException(ErrorCodes.INVALID_ARGUMENT,
        MessageFormat.format(template, args));
  }
}
