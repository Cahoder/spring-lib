package org.spring.lib.ibatis.annotation;

import org.spring.lib.ibatis.em.IgnoreType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段忽略标识注解
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/28
 **/
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {

    /**
     * 忽略类型，默认插入、更新、查询全部忽略
     */
    IgnoreType[] value() default {IgnoreType.INSERT, IgnoreType.UPDATE, IgnoreType.SELECT};

}
