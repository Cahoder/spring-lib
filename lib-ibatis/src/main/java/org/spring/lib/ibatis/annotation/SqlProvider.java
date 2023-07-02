package org.spring.lib.ibatis.annotation;

import org.spring.lib.ibatis.dao.AbstractSqlProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识Mapper接口对应的SQL提供器实现
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/28
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlProvider {
    Class<? extends AbstractSqlProvider> value();
}
