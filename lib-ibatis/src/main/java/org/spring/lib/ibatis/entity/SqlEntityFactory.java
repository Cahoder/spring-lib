package org.spring.lib.ibatis.entity;

import org.apache.ibatis.session.Configuration;
import org.spring.lib.ibatis.entity.builder.ColumnBuilder;
import org.spring.lib.ibatis.entity.builder.DefaultColumnBuilder;
import org.spring.lib.ibatis.entity.builder.DefaultSqlEntityBuilder;
import org.spring.lib.ibatis.entity.builder.SqlEntityBuilder;
import org.spring.lib.ibatis.utils.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQL orm PO 对象创建工厂
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/29
 **/
public class SqlEntityFactory {

    private final ColumnBuilder columnBuilder;
    private final SqlEntityBuilder sqlEntityBuilder;

    private static final Map<Class<?>, SqlEntity> INITIALIZED_SQL_ENTITY = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Class<?>> DAO_CLASS_MAPPING_PO_CLASS = new ConcurrentHashMap<>();

    public SqlEntityFactory() {
        this.columnBuilder = new DefaultColumnBuilder();
        this.sqlEntityBuilder = new DefaultSqlEntityBuilder();
    }

    public SqlEntityFactory(ColumnBuilder columnBuilder, SqlEntityBuilder sqlEntityBuilder) {
        this.columnBuilder = columnBuilder != null ? columnBuilder : new DefaultColumnBuilder();
        this.sqlEntityBuilder = sqlEntityBuilder != null ? sqlEntityBuilder : new DefaultSqlEntityBuilder();
    }

    /**
     * 获取SqlEntity通过dao类对象
     * 不存在则创建该SqlEntity
     * @param mapperClass dao类对象
     * @param configuration mapper配置
     * @return SqlEntity对象
     */
    public SqlEntity getSqlEntityByMapperClass(Class<?> mapperClass, Configuration configuration) {
        Class<?> entityClass = DAO_CLASS_MAPPING_PO_CLASS
                .computeIfAbsent(Objects.requireNonNull(mapperClass), ReflectUtil::getEntityClass);
        return getSqlEntityByEntityClass(entityClass, configuration);
    }

    /**
     * 获取SqlEntity通过po类对象
     * 不存在则创建该SqlEntity
     * @param entityClass po类对象
     * @param configuration mapper配置
     * @return SqlEntity对象
     */
    public SqlEntity getSqlEntityByEntityClass(Class<?> entityClass, Configuration configuration) {
        SqlEntity sqlEntity = INITIALIZED_SQL_ENTITY.get(Objects.requireNonNull(entityClass));
        if (sqlEntity != null) {
            return sqlEntity;
        }
        List<Column> sqlColumnList = new ArrayList<>();
        List<Field> allFields = ReflectUtil.getAllFields(entityClass);

        for (Field field : allFields) {
            if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Column column = columnBuilder.build(entityClass, field, configuration);
            if (column != null) {
                sqlColumnList.add(column);
            }
        }
        sqlEntity = sqlEntityBuilder.build(entityClass, sqlColumnList);
        INITIALIZED_SQL_ENTITY.put(entityClass, sqlEntity);
        return sqlEntity;
    }

}
