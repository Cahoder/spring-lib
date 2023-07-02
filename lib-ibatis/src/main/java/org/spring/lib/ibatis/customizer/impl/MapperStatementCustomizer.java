package org.spring.lib.ibatis.customizer.impl;

import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.lib.ibatis.annotation.CustomizerOrder;
import org.spring.lib.ibatis.annotation.SqlProvider;
import org.spring.lib.ibatis.context.DaoMapperContext;
import org.spring.lib.ibatis.customizer.MapperCustomizer;
import org.spring.lib.ibatis.dao.AbstractSqlProvider;
import org.spring.lib.ibatis.dao.MapperAware;
import org.spring.lib.ibatis.dao.ProvideSql;
import org.spring.lib.ibatis.entity.SqlEntity;
import org.spring.lib.ibatis.utils.MybatisUtil;
import org.spring.lib.ibatis.utils.ReflectUtil;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 根据SqlProvider生成MappedStatement
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/30
 **/
@CustomizerOrder(0)
public class MapperStatementCustomizer implements MapperCustomizer {

    private static final Logger log = LoggerFactory.getLogger(MapperStatementCustomizer.class);

    @Override
    public void process(DaoMapperContext daoMapperContext) {
        Configuration configuration = daoMapperContext.getConfiguration();
        Class<? extends MapperAware<?, ? extends Serializable>> mapperInterface = daoMapperContext.getMapperInterface();
        SqlEntity sqlEntity = daoMapperContext.getSqlEntityFactory().getSqlEntityByMapperClass(mapperInterface, configuration);

        Method[] mapperMethods = mapperInterface.getMethods();
        for (Method method : mapperMethods) {
            String id = mapperInterface.getName() + "." + method.getName();
            // 如果是桥接方法或已经存在mappedStatement了
            if (method.isBridge() || configuration.hasStatement(id)) {
                return;
            }

            // 没有sqlProvider
            SqlProvider sqlProvider = method.getDeclaringClass().getAnnotation(SqlProvider.class);
            if (sqlProvider == null) {
                return;
            }

            // 无法构造sqlProviderObject（可能因为接口非指定PO类型的DAO，sqlEntity无法获取）
            AbstractSqlProvider sqlProviderObj = ReflectUtil.getSqlProviderInstance(sqlProvider.value());
            sqlProviderObj.setSqlEntity(sqlEntity);

            // 没有对应的sql构造方法
            ProvideSql provideSql = this.getProvideSql(method, sqlProviderObj);
            if (provideSql == null) {
                return;
            }

            initMappedStatement(configuration, sqlEntity, id, provideSql);
            log.debug("statement:{}, type:{}, init sql:\n{}", id, provideSql.getSqlCommandType(), provideSql.getSql());
        }
    }

    private void initMappedStatement(Configuration configuration, SqlEntity sqlEntity, String id, ProvideSql provideSql) {
        SqlCommandType sqlCommandType = provideSql.getSqlCommandType();
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
        SqlSource sqlSource = MybatisUtil.buildSqlSource(configuration, provideSql.getSql());
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, id, sqlSource, sqlCommandType)
                .useCache(isSelect)
                .flushCacheRequired(!isSelect)
                .lang(MybatisUtil.getDefaultLanguageDriver());
        if (isSelect) {
            List<ResultMap> resultMap = MybatisUtil.getResultMap(configuration, sqlEntity);
            builder.resultMaps(resultMap);
        } else if (sqlCommandType == SqlCommandType.INSERT || sqlCommandType == SqlCommandType.UPDATE) {
            KeyGenerator keyGenerator;
            if (provideSql.isUseGeneratedKeys() || configuration.isUseGeneratedKeys()) {
                keyGenerator = Jdbc3KeyGenerator.INSTANCE;
            } else {
                keyGenerator = NoKeyGenerator.INSTANCE;
            }
            builder.keyGenerator(keyGenerator)
                    .keyProperty(provideSql.getKeyFieldName())
                    .keyColumn(provideSql.getKeyColumnName());
        }
        configuration.addMappedStatement(builder.build());
    }

    private ProvideSql getProvideSql(Method method, AbstractSqlProvider sqlProvider) {
        Class<?> providerClass = sqlProvider.getClass();
        String mapperMethodName = method.getName();
        Method sqlProviderMethod = ReflectionUtils.findMethod(providerClass, mapperMethodName);
        if (sqlProviderMethod == null) {
            log.debug(String.format("sqlProvider:%s 中找不到对应的sql构造方法: %s", sqlProvider.getClass(), mapperMethodName));
            return null;
        }
        return (ProvideSql) ReflectionUtils.invokeMethod(sqlProviderMethod, sqlProvider);
    }

}
