package org.spring.lib.ibatis.entity.builder;

import org.spring.lib.ibatis.entity.Column;
import org.spring.lib.ibatis.entity.SqlEntity;
import org.spring.lib.ibatis.exception.DaoMapperInitException;

import java.util.List;

/**
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/29
 **/
public abstract class AbstractSqlEntityBuilder implements SqlEntityBuilder {

    @Override
    public SqlEntity build(Class<?> entityClass, List<Column> sqlColumnList) {
        Column idColumn = null;
        for (Column sqlColumn : sqlColumnList) {
            if (sqlColumn.isId()) {
                if (idColumn != null) {
                    throw new DaoMapperInitException(
                            String.format("发现多个id字段：%s; %s, entity:%s",
                            idColumn.getFieldName(), sqlColumn.getFieldName(), entityClass.getName())
                    );
                }
                idColumn = sqlColumn;
            }
        }
        if (idColumn == null) {
            throw new DaoMapperInitException("请指定对应的Id字段，entity:" + entityClass.getName());
        }
        sqlColumnList.remove(idColumn);
        return new SqlEntity(entityClass, getTableName(entityClass), idColumn, sqlColumnList);
    }

    /**
     * 指明SqlEntity对应数据库表名
     * @param entityClass po类
     * @return 数据库表名
     */
    public abstract String getTableName(Class<?> entityClass);

}
