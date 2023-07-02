package org.spring.lib.ibatis.entity;

import org.apache.ibatis.jdbc.SQL;

import java.util.List;

/**
 * SQL orm PO 对象封装
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/28
 **/
public class SqlEntity {

    private Class<?> entityClass;
    private String tableName;
    private Column idColumn;
    private List<Column> sqlColumnList;
    private String baseSelectSql;

    public SqlEntity() {
    }

    public SqlEntity(Class<?> entityClass, String tableName, Column idColumn, List<Column> sqlColumnList) {
        this.entityClass = entityClass;
        this.tableName = tableName;
        this.idColumn = idColumn;
        this.sqlColumnList = sqlColumnList;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public Column getIdColumn() {
        return idColumn;
    }

    public List<Column> getSqlColumnList() {
        return sqlColumnList;
    }

    public String getBaseSelectSql() {
        if (baseSelectSql != null) {
            return baseSelectSql;
        }
        SQL sql = new SQL().FROM(tableName);
        if (idColumn.needSelect()) {
            sql.SELECT(idColumn.getColumnName());
        }
        for (Column sqlColumn : sqlColumnList) {
            if (sqlColumn.needSelect()) {
                sql.SELECT(sqlColumn.getColumnName());
            }
        }
        baseSelectSql = sql.toString();
        return baseSelectSql;
    }

}
