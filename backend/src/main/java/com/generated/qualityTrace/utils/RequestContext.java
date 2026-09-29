package com.generated.qualityTrace.utils;

/**
 * 请求级上下文：AuthMiddleware 写入操作者，service 写操作日志 / 结论历史时读取。
 * 纯后端无前端登录态时默认操作者为 system。
 */
public final class RequestContext {

  private static final ThreadLocal<String> ACTOR = ThreadLocal.withInitial(() -> "system");

  private RequestContext() {}

  public static void setActor(String actor) {
    ACTOR.set(actor == null || actor.isBlank() ? "system" : actor);
  }

  public static String getActor() {
    return ACTOR.get();
  }

  public static void clear() {
    ACTOR.remove();
  }
}
