package com.generated.qualityTrace.middlewares;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RBAC 标注：RbacMiddleware 读取后与当前用户角色比对。
 * 角色取值见 constants/RoleConstants。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRoles {
  String[] value();
}
