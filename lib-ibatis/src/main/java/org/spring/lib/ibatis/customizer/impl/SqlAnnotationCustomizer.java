package org.spring.lib.ibatis.customizer.impl;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.session.Configuration;
import org.spring.lib.ibatis.annotation.CustomizerOrder;
import org.spring.lib.ibatis.context.DaoMapperContext;
import org.spring.lib.ibatis.customizer.MapperCustomizer;
import org.spring.lib.ibatis.dao.MapperAware;
import org.spring.lib.ibatis.entity.SqlEntity;
import org.spring.lib.ibatis.utils.MybatisUtil;
import org.spring.lib.ibatis.utils.ReflectUtil;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 尝试重写mybatis原注解(@Select @Update @Delete)的sql，补充前缀和解析in语句
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/7/2
 **/
@CustomizerOrder(1000)
public class SqlAnnotationCustomizer implements MapperCustomizer {

    private static final String IN_TEMPLATE = "%n<foreach collection=\"%s\" item=\"item\" open=\"(\" close=\")\" separator=\",\">%n #{item} %n</foreach>%n";
    private static final Pattern IN_PATTERN = Pattern.compile("\\s+[iI][nN]\\s+(#\\{\\s*[\\w\\.]+\\s*\\})");
    private static final Pattern SQL_KEEP_BEFORE_PATTERN = Pattern.compile("^(<script>)?\\s*((select)|(update)|(delete)).*(</script>)?$");
    private static final Map<String, String> SENSITIVE_SQL_REPLACE_MAP = new HashMap<>();

    static {
        SENSITIVE_SQL_REPLACE_MAP.put("<", "<![CDATA[ < ]]>");
        SENSITIVE_SQL_REPLACE_MAP.put("<=", "<![CDATA[ <= ]]>");
        SENSITIVE_SQL_REPLACE_MAP.put(">", "<![CDATA[ > ]]>");
        SENSITIVE_SQL_REPLACE_MAP.put(">=", "<![CDATA[ >= ]]>");
    }

    @Override
    public void process(DaoMapperContext daoMapperContext) {
        Configuration configuration = daoMapperContext.getConfiguration();
        Class<? extends MapperAware<?, ? extends Serializable>> mapperInterface = daoMapperContext.getMapperInterface();
        SqlEntity sqlEntity = daoMapperContext.getSqlEntityFactory().getSqlEntityByMapperClass(mapperInterface, configuration);

        Method[] methods = mapperInterface.getMethods();
        for (Method method : methods) {
            String beforeSql = null;
            String newSql = null;
            if (method.isAnnotationPresent(Select.class) && !method.isAnnotationPresent(SelectProvider.class)) {
                Select select = method.getAnnotation(Select.class);
                beforeSql = getAnnotationSql(select.value());
                if (isNeedAppendSql(beforeSql)) {
                    newSql = String.format("%s %s", sqlEntity.getBaseSelectSql(), beforeSql);
                }
            } else if (method.isAnnotationPresent(Update.class) && !method.isAnnotationPresent(UpdateProvider.class)) {
                Update update = method.getAnnotation(Update.class);
                beforeSql = getAnnotationSql(update.value());
                if (isNeedAppendSql(beforeSql)) {
                    newSql = String.format("update %s %s", sqlEntity.getTableName(), beforeSql);
                }
            } else if (method.isAnnotationPresent(Delete.class) && !method.isAnnotationPresent(DeleteProvider.class)) {
                Delete delete = method.getAnnotation(Delete.class);
                beforeSql = getAnnotationSql(delete.value());
                if (isNeedAppendSql(beforeSql)) {
                    newSql = String.format("delete from %s %s", sqlEntity.getTableName(), beforeSql);
                }
            }

            // 表示无注解或已经存在相应[Select|Update|Delete]Provider，直接返回
            if (beforeSql == null) {
                return;
            }

            if (newSql != null) {
                newSql = parseInStr(newSql);
            } else {
                newSql = parseInStr(beforeSql);
            }

            if (!beforeSql.equals(newSql)) {
                ReflectUtil.resetSqlSource(configuration, mapperInterface, method, newSql);
            }
        }
    }


    /**
     * 将sql注解的value拼接起来
     */
    private String getAnnotationSql(String[] strings) {
        StringBuilder sqlBuilder = new StringBuilder();
        for (String fragment : strings) {
            sqlBuilder.append(fragment);
            sqlBuilder.append(" ");
        }
        String sql = sqlBuilder.toString().trim();
        // 非<script> 进行 敏感字符的替换
        if (!sql.startsWith(MybatisUtil.SCRIPT_TAG_START)) {
            for (Map.Entry<String, String> entry : SENSITIVE_SQL_REPLACE_MAP.entrySet()) {
                sql = sql.replace(entry.getKey(), entry.getValue());
            }
        }
        return sql;
    }

    /**
     * 是否需要补充原有sql
     */
    private boolean isNeedAppendSql(String sqlStr) {
        return !SQL_KEEP_BEFORE_PATTERN.matcher(sqlStr.toLowerCase()).matches();
    }

    /**
     * 解析 in #{list} => <foreach>#{item}</foreach>
     *
     * @return 如果不包含则返回原sql
     */
    private String parseInStr(String sql) {
        String newSql = sql;
        Matcher matcher = IN_PATTERN.matcher(sql);
        while (matcher.find()) {
            String matchStr = matcher.group(1);
            String listName = matchStr.substring(2).substring(0, matchStr.length() - 3);
            String forEachSql = String.format(IN_TEMPLATE, listName);
            newSql = newSql.replace(matchStr, forEachSql);
        }
        return newSql;
    }

}
