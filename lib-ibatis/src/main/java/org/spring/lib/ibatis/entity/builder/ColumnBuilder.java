package org.spring.lib.ibatis.entity.builder;

import org.apache.ibatis.session.Configuration;
import org.spring.lib.ibatis.entity.Column;

import java.lang.reflect.Field;

/**
 * SQL的column orm PO的field 对象建造者
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/29
 **/
public interface ColumnBuilder {

    /**
     * 构造器
     * @param entityClass po类
     * @param field sqlColumnList
     * @param configuration mapper配置
     * @return SQL的column orm PO的field 对象
     */
    Column build(Class<?> entityClass, Field field, Configuration configuration);

}
