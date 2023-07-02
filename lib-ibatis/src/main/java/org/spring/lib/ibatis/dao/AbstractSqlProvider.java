package org.spring.lib.ibatis.dao;

import org.spring.lib.ibatis.entity.SqlEntity;

/**
 * Mapper对应抽象SQL提供器
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/28
 **/
public abstract class AbstractSqlProvider {

    protected SqlEntity sqlEntity;

    public void setSqlEntity(SqlEntity sqlEntity) {
        this.sqlEntity = sqlEntity;
    }

}
