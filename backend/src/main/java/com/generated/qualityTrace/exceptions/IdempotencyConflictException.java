package com.generated.qualityTrace.exceptions;

import com.generated.qualityTrace.constants.ErrorCodes;

/** 幂等冲突：同一个 requestId 被不同内容复用，controller 层映射为 409。 */
public class IdempotencyConflictException extends BusinessException {
  public IdempotencyConflictException(String message) {
    super(ErrorCodes.IDEMPOTENCY_CONFLICT, message);
  }
}
