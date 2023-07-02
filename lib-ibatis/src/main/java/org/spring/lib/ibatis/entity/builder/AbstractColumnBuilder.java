package org.spring.lib.ibatis.entity.builder;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.ibatis.type.UnknownTypeHandler;
import org.spring.lib.ibatis.annotation.ColumnConfig;
import org.spring.lib.ibatis.em.IgnoreType;
import org.spring.lib.ibatis.entity.Column;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/29
 **/
public abstract class AbstractColumnBuilder implements ColumnBuilder {

    @Override
    public Column build(Class<?> entityClass, Field field, Configuration configuration) {
        Class<?> javaType = field.getType();
        String columnName = this.getColumnName(entityClass, field);
        Column column = new Column(field.getName(), javaType, columnName);
        column.setIsId(this.isId(entityClass, field, columnName));
        column.setIgnoreScenes(this.getIgnoreTypes(entityClass, field, columnName));
        ColumnConfig columnConfig = field.getAnnotation(ColumnConfig.class);
        if (columnConfig != null) {
            Class<? extends TypeHandler<?>> typeHandlerClass = columnConfig.typeHandler();
            column.setTypeHandler(initTypeHandler(javaType, typeHandlerClass, configuration));
            column.setJdbcType(columnConfig.jdbcType());
        }
        return column;
    }

    private TypeHandler<?> initTypeHandler(Class<?> javaType, Class<? extends TypeHandler<?>> typeHandlerClass, Configuration configuration) {
        if (typeHandlerClass == null || typeHandlerClass == UnknownTypeHandler.class) {
            return null;
        }
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        TypeHandler<?> handler = typeHandlerRegistry.getMappingTypeHandler(typeHandlerClass);
        if (handler == null) {
            handler = typeHandlerRegistry.getInstance(javaType, typeHandlerClass);
        }
        return handler;
    }

    /**
     * 指明Column对应数据库表字段名
     * @param entityClass po类
     * @param field po类字段
     * @return 数据库表字段名
     */
    public abstract String getColumnName(Class<?> entityClass, Field field);

    /**
     * 指明Column对应数据库表字段是否主键
     * @param entityClass po类
     * @param field po类字段
     * @param columnName 数据库表字段名
     * @return true-是 false-否
     */
    public abstract boolean isId(Class<?> entityClass, Field field, String columnName);

    /**
     * 指明该Column可被忽略的场景
     * @param entityClass po类
     * @param field po类字段
     * @param columnName 数据库表字段名
     * @return 忽略场景类型集
     */
    public abstract Set<IgnoreType> getIgnoreTypes(Class<?> entityClass, Field field, String columnName);

}
