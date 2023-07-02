package org.spring.lib.ibatis.annotation;

import org.spring.lib.ibatis.em.IgnoreType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 插入更新时字段忽略标识
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/28
 **/
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Ignore({IgnoreType.INSERT, IgnoreType.UPDATE})
public @interface IgnoreInsertUpdate {
}
