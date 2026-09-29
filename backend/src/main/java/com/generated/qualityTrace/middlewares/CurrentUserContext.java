package com.generated.qualityTrace.middlewares;

/** 由 AuthMiddleware 写入、service/controller 读取的当前登录人（线程隔离）。 */
public final class CurrentUserContext {

  public record CurrentUser(Long userId, String username, String displayName, String role) {
  }

  private static final ThreadLocal<CurrentUser> HOLDER = new ThreadLocal<>();

  private CurrentUserContext() {}

  public static void set(CurrentUser user) {
    HOLDER.set(user);
  }

  public static CurrentUser get() {
    return HOLDER.get();
  }

  public static String actorName() {
    CurrentUser user = HOLDER.get();
    return user == null ? "anonymous" : user.username();
  }

  public static void clear() {
    HOLDER.remove();
  }
}
