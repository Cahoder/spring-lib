package org.spring.lib.ibatis.entity;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.spring.lib.ibatis.em.IgnoreType;

import java.util.Set;

/**
 * SQL的column orm PO的field 对象封装
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/28
 **/
public class Column {

    private boolean isId;

    private String fieldName;
    private Class<?> javaType;

    private TypeHandler<?> typeHandler;

    private String columnName;
    private JdbcType jdbcType;

    private Set<IgnoreType> ignoreScenes;

    public Column() {

    }

    public Column(String fieldName, Class<?> javaType, String columnName) {
        this.fieldName = fieldName;
        this.javaType = javaType;
        this.columnName = columnName;
    }

    public boolean needSelect() {
        return this.ignoreScenes == null || !ignoreScenes.contains(IgnoreType.SELECT);
    }

    public boolean needInsert() {
        return this.ignoreScenes == null || !ignoreScenes.contains(IgnoreType.INSERT);
    }

    public boolean needUpdate() {
        return this.ignoreScenes == null || !ignoreScenes.contains(IgnoreType.UPDATE);
    }

    public boolean isId() {
        return isId;
    }

    public void setIsId(boolean isId) {
        this.isId = isId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(TypeHandler<?> typeHandler) {
        this.typeHandler = typeHandler;
    }

    public Set<IgnoreType> getIgnoreScenes() {
        return ignoreScenes;
    }

    public void setIgnoreScenes(Set<IgnoreType> ignoreScenes) {
        this.ignoreScenes = ignoreScenes;
    }

}
