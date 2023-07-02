package org.spring.lib.ibatis.customizer.impl;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.lib.ibatis.annotation.CustomizerOrder;
import org.spring.lib.ibatis.context.DaoMapperContext;
import org.spring.lib.ibatis.customizer.MapperCustomizer;
import org.spring.lib.ibatis.dao.MapperAware;
import org.spring.lib.ibatis.entity.SqlEntity;
import org.spring.lib.ibatis.utils.EntityUtil;
import org.spring.lib.ibatis.utils.MybatisUtil;
import org.spring.lib.ibatis.utils.ReflectUtil;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 修改原来PO的映射方式，映射成对应于sqlEntity的字段和变量（ResultMap）
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/30
 **/
@CustomizerOrder(2000)
public class SelectResultMapCustomizer implements MapperCustomizer {

    private static final Logger log = LoggerFactory.getLogger(SelectResultMapCustomizer.class);

    @Override
    public void process(DaoMapperContext daoMapperContext) {
        Class<? extends MapperAware<?, ? extends Serializable>> mapperInterface = daoMapperContext.getMapperInterface();
        Configuration configuration = daoMapperContext.getConfiguration();

        Collection<MappedStatement> mappedStatements = configuration.getMappedStatements();
        for (MappedStatement mappedStatement : mappedStatements) {
            String mapperInterfaceName = EntityUtil.getMapperNameByMappedStatementId(mappedStatement.getId());
            if (!mapperInterface.getName().equals(mapperInterfaceName)) {
                return;
            }

            //要求用户未配置resultMap
            List<ResultMap> resultMaps = mappedStatement.getResultMaps();
            if (mappedStatement.getSqlCommandType() != SqlCommandType.SELECT || resultMaps.size() != 1
                    || !CollectionUtils.isEmpty(resultMaps.get(0).getResultMappings())) {
                return;
            }

            //要求存在相应po实体类
            Class<?> resultPoClass = resultMaps.get(0).getType();
            SqlEntity sqlEntity = daoMapperContext.getSqlEntityFactory()
                    .getSqlEntityByEntityClass(resultPoClass, configuration);
            if (sqlEntity == null) {
                log.debug("找不到对应的sqlEntity，mappedStatement：{}", mappedStatement.getId());
                return;
            }

            //重塑查询dao的返回结果映射为po
            List<ResultMap> resultMapList = MybatisUtil.getResultMap(configuration, sqlEntity);
            ReflectUtil.resetResultMap(mappedStatement, resultMapList);
        }
    }

}
