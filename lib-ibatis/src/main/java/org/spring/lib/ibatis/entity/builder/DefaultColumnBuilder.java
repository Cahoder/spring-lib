package org.spring.lib.ibatis.entity.builder;

import org.spring.lib.ibatis.annotation.ColumnConfig;
import org.spring.lib.ibatis.annotation.Ignore;
import org.spring.lib.ibatis.annotation.IgnoreInsertUpdate;
import org.spring.lib.ibatis.annotation.RetainCreateUpdateTime;
import org.spring.lib.ibatis.em.IgnoreType;
import org.spring.lib.ibatis.utils.EntityUtil;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/29
 **/
public class DefaultColumnBuilder extends AbstractColumnBuilder {

    private static final String DEFAULT_ID_NAME = "id";
    private static final Set<String> DEFAULT_IGNORE_INSERT_UPDATE_COLUMN = new HashSet<>(
            Arrays.asList("create_time", "update_time")
    );

    @Override
    public String getColumnName(Class<?> entityClass, Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !StringUtils.isEmpty(column.name())) {
            return column.name();
        }
        ColumnConfig columnConfig = field.getAnnotation(ColumnConfig.class);
        if (columnConfig != null && !StringUtils.isEmpty(columnConfig.value())) {
            return columnConfig.value();
        }
        return EntityUtil.camel2UnderLine(field.getName());
    }

    @Override
    public boolean isId(Class<?> entityClass, Field field, String columnName) {
        if (field.isAnnotationPresent(Id.class)) {
            return true;
        }
        ColumnConfig columnConfig = field.getAnnotation(ColumnConfig.class);
        if (columnConfig != null && columnConfig.id()) {
            return true;
        }
        return DEFAULT_ID_NAME.equals(columnName);
    }

    @Override
    public Set<IgnoreType> getIgnoreTypes(Class<?> entityClass, Field field, String columnName) {
        Ignore ignoreAnnotation = field.getAnnotation(Ignore.class);
        if (ignoreAnnotation == null && field.isAnnotationPresent(IgnoreInsertUpdate.class)) {
            ignoreAnnotation = IgnoreInsertUpdate.class.getAnnotation(Ignore.class);
        }

        if (ignoreAnnotation != null) {
            IgnoreType[] ignoreTypes = ignoreAnnotation.value();
            if (ignoreTypes.length > 0) {
                Set<IgnoreType> ignoreTypeSet = new HashSet<>();
                Collections.addAll(ignoreTypeSet, ignoreTypes);
                return ignoreTypeSet;
            }
        }

        if (DEFAULT_IGNORE_INSERT_UPDATE_COLUMN.contains(columnName)
                && entityClass.getDeclaredAnnotation(RetainCreateUpdateTime.class) == null) {
            Set<IgnoreType> ignoreTypeSet = new HashSet<>();
            ignoreTypeSet.add(IgnoreType.INSERT);
            ignoreTypeSet.add(IgnoreType.UPDATE);
            return ignoreTypeSet;
        }

        return Collections.emptySet();
    }

}
