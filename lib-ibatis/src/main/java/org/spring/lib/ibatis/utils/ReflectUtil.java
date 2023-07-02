package org.spring.lib.ibatis.utils;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.spring.lib.ibatis.dao.AbstractSqlProvider;
import org.spring.lib.ibatis.dao.MapperAware;
import org.spring.lib.ibatis.exception.DaoMapperInitException;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/29
 **/
public class ReflectUtil {

    private ReflectUtil() {}

    private static final Field SQL_SOURCE_FIELD;
    private static final Field RESULT_MAP_FIELD;

    static {
        SQL_SOURCE_FIELD = ReflectionUtils.findField(MappedStatement.class, "sqlSource");
        assert SQL_SOURCE_FIELD != null;
        SQL_SOURCE_FIELD.setAccessible(true);
        RESULT_MAP_FIELD = ReflectionUtils.findField(MappedStatement.class, "resultMaps");
        assert RESULT_MAP_FIELD != null;
        RESULT_MAP_FIELD.setAccessible(true);
    }

    /**
     * 获取类的所有field（包含父类中的）
     */
    public static List<Field> getAllFields(Class<?> originClass) {
        Class<?> clazz = originClass;
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }

    /**
     * 利用反射构造一个实例
     */
    public static AbstractSqlProvider getSqlProviderInstance(Class<? extends AbstractSqlProvider> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new DaoMapperInitException(String.format("sqlProvider:%s, 请提供一个空的构造方法", clazz), e);
        }
    }

    /**
     * 根据DAO接口获取PO类型（泛型中获取）
     */
    public static Class<?> getEntityClass(Class<?> mapperInterface) {
        return getEntityClass(ResolvableType.forClass(mapperInterface));
    }

    private static Class<?> getEntityClass(ResolvableType resolvableType) {
        if (resolvableType == null) {
            return null;
        }
        if (resolvableType.getRawClass() == MapperAware.class) {
            return resolvableType.getGeneric(0).resolve();
        }
        for (ResolvableType parent : resolvableType.getInterfaces()) {
            Class<?> entityClass = getEntityClass(parent);
            if (entityClass != null) {
                return entityClass;
            }
        }
        return null;
    }

    /**
     * 重设sqlSource
     */
    public static void resetSqlSource(Configuration configuration, Class<?> daoInterface, Method method, String sql) {
        String mappedStatementId = daoInterface.getName() + "." + method.getName();
        MappedStatement mappedStatement = configuration.getMappedStatement(mappedStatementId);
        SqlSource sqlSource = MybatisUtil.buildSqlSource(configuration, sql);
        ReflectionUtils.setField(SQL_SOURCE_FIELD, mappedStatement, sqlSource);
    }

    /**
     * 重设ResultMap
     */
    public static void resetResultMap(MappedStatement mappedStatement, Collection<ResultMap> resultMapCollection) {
        ReflectionUtils.setField(RESULT_MAP_FIELD, mappedStatement, resultMapCollection);
    }

}
