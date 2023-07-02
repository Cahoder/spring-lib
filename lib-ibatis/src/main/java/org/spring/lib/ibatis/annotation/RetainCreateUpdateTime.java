package org.spring.lib.ibatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 默认create_time和update_time会被忽略插入和更新（db建表时自行维护）
 * 如需不忽略请在PO上加上此注解即可
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/29
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RetainCreateUpdateTime {
}