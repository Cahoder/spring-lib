package org.spring.lib.ibatis.entity.builder;

import org.spring.lib.ibatis.utils.EntityUtil;
import org.springframework.util.StringUtils;

import javax.persistence.Table;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/29
 **/
public class DefaultSqlEntityBuilder extends AbstractSqlEntityBuilder {

    private static final String TABLE_PREFIX = "t_";
    private static final Set<String> ENTITY_SUFFIX = new HashSet<>(Arrays.asList("PO", "Po", "po"));

    @Override
    public String getTableName(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null && !StringUtils.isEmpty(table.name())) {
            return table.name();
        }
        String tableName = entityClass.getSimpleName();
        int length = tableName.length();
        String tableSuffix = tableName.substring(length - 2);
        if (length > 2 && (ENTITY_SUFFIX.contains(tableSuffix))) {
            tableName = tableName.substring(0, length - 2);
        }
        return TABLE_PREFIX + EntityUtil.camel2UnderLine(tableName);
    }

}
