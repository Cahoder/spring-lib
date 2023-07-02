package org.spring.lib.ibatis.utils;

import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.spring.lib.ibatis.entity.Column;
import org.spring.lib.ibatis.entity.SqlEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/30
 **/
public class MybatisUtil {

    private static final LanguageDriver LANGUAGE_DRIVER = new XMLLanguageDriver();
    private static final String BASE_RESULT_MAP_NAME = "$.%s.BaseResultMap";

    public static final String SCRIPT_TAG_START = "<script>";
    public static final String SCRIPT_TAG_END = "</script>";

    private MybatisUtil () {}

    public static LanguageDriver getDefaultLanguageDriver() {
        return LANGUAGE_DRIVER;
    }

    public static SqlSource buildSqlSource(Configuration configuration, String sql) {
        String scriptSql = sql;
        if (!scriptSql.startsWith(SCRIPT_TAG_START)) {
            scriptSql = SCRIPT_TAG_START + scriptSql + SCRIPT_TAG_END;
        }
        return LANGUAGE_DRIVER.createSqlSource(configuration, scriptSql, null);
    }

    /**
     * 根据sqlEntity重新构造resultMap
     */
    public static List<ResultMap> getResultMap(Configuration configuration, SqlEntity sqlEntity) {
        String id = String.format(BASE_RESULT_MAP_NAME, sqlEntity.getEntityClass().getName());
        ResultMap resultMap = null;
        if (configuration.hasResultMap(id)) {
            resultMap = configuration.getResultMap(id);
        }
        if (resultMap == null) {
            Column idColumn = sqlEntity.getIdColumn();
            List<ResultMapping> resultMappingList = new ArrayList<>();
            resultMappingList.add(buildResultMapping(configuration, idColumn));
            for (Column sqlColumn : sqlEntity.getSqlColumnList()) {
                resultMappingList.add(buildResultMapping(configuration, sqlColumn));
            }
            resultMap = new ResultMap.Builder(configuration, id, sqlEntity.getEntityClass(), resultMappingList).build();
            configuration.addResultMap(resultMap);
        }
        return Collections.singletonList(resultMap);
    }

    private static ResultMapping buildResultMapping(Configuration configuration, Column sqlColumn) {
        List<ResultFlag> flags = sqlColumn.isId() ? Collections.singletonList(ResultFlag.ID) : Collections.emptyList();
        return new ResultMapping
                .Builder(configuration, sqlColumn.getFieldName(), sqlColumn.getColumnName(), sqlColumn.getJavaType())
                .jdbcType(sqlColumn.getJdbcType() != JdbcType.UNDEFINED ? sqlColumn.getJdbcType() : null)
                .flags(flags)
                .typeHandler(sqlColumn.getTypeHandler())
                .build();
    }

}
