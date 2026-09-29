package com.generated.qualityTrace.routes;

/** 批次与追溯接口路径常量。 */
public final class ProductBatchRoutes {
  public static final String PATH = "/api/batches";
  public static final String TRACE = "/api/batches/trace/{batchNo}";
  public static final String CONCLUSION = "/api/batches/trace/{batchNo}/conclusion";
  public static final String CONCLUSION_HISTORY = "/api/batches/trace/{batchNo}/conclusion/history";

  private ProductBatchRoutes() {}
}
