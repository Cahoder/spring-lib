package org.spring.lib.ibatis.entity.builder;

import org.spring.lib.ibatis.entity.Column;
import org.spring.lib.ibatis.entity.SqlEntity;

import java.util.List;

/**
 * SQL orm PO 对象建造者
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/29
 **/
public interface SqlEntityBuilder {

    /**
     * 构造器
     * @param entityClass po类
     * @param sqlColumnList sqlColumnList
     * @return SQL orm PO 对象封装
     */
    SqlEntity build(Class<?> entityClass, List<Column> sqlColumnList);

}
