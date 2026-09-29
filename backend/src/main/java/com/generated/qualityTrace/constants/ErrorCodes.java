package com.generated.qualityTrace.constants;

/** 业务错误码集中定义，service 与 controller 分别包装后再交给全局异常处理。 */
public final class ErrorCodes {
  public static final String AUTH_REQUIRED = "AUTH_REQUIRED";
  public static final String RBAC_DENIED = "RBAC_DENIED";
  public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
  public static final String WORK_ORDER_NOT_FOUND = "WORK_ORDER_NOT_FOUND";
  public static final String WORK_ORDER_DUPLICATE = "WORK_ORDER_DUPLICATE";
  public static final String BATCH_NOT_FOUND = "BATCH_NOT_FOUND";
  public static final String BATCH_DUPLICATE = "BATCH_DUPLICATE";
  public static final String INSPECTION_NOT_FOUND = "INSPECTION_NOT_FOUND";
  public static final String DEFECT_NOT_FOUND = "DEFECT_NOT_FOUND";
  public static final String IDEMPOTENCY_CONFLICT = "IDEMPOTENCY_CONFLICT";
  public static final String DISPOSITION_INVALID = "DISPOSITION_INVALID";
  public static final String SERVICE_ERROR = "SERVICE_ERROR";
  public static final String CONTROLLER_ERROR = "CONTROLLER_ERROR";

  private ErrorCodes() {}
}
