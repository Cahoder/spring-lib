package org.spring.lib.ibatis.dao;

import org.apache.ibatis.mapping.SqlCommandType;
import org.spring.lib.ibatis.utils.SqlUtil;

/**
 * SQL提供者
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/30
 **/
public class ProvideSql {

    private String sql;
    private SqlCommandType sqlCommandType;
    private boolean useGeneratedKeys;
    private String keyFieldName = "id";
    private String keyColumnName;

    public ProvideSql(String sql, SqlCommandType sqlCommandType) {
        this.sql = SqlUtil.trimN(sql);
        this.sqlCommandType = sqlCommandType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }

    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }

    public void setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
    }

    public String getKeyFieldName() {
        return keyFieldName;
    }

    public void setKeyFieldName(String keyFieldName) {
        this.keyFieldName = keyFieldName;
    }

    public String getKeyColumnName() {
        return keyColumnName;
    }

    public void setKeyColumnName(String keyColumnName) {
        this.keyColumnName = keyColumnName;
    }

}
