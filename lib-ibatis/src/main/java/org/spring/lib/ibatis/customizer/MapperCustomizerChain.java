package org.spring.lib.ibatis.customizer;

import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.lib.ibatis.annotation.CustomizerOrder;
import org.spring.lib.ibatis.context.DaoMapperContext;
import org.spring.lib.ibatis.dao.MapperAware;
import org.spring.lib.ibatis.entity.SqlEntityFactory;
import org.spring.lib.ibatis.utils.ReflectUtil;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

/**
 * Dao层Mapper增强定制器执行链
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/30
 **/
public class MapperCustomizerChain {

    private static final Logger log = LoggerFactory.getLogger(MapperCustomizerChain.class);

    private final SqlEntityFactory sqlEntityFactory;
    private final Set<MapperCustomizer> mapperCustomizerSet;

    public MapperCustomizerChain(SqlEntityFactory sqlEntityFactory) {
        this.sqlEntityFactory = sqlEntityFactory;
        this.mapperCustomizerSet = new TreeSet<>((c1, c2) -> {
            CustomizerOrder customizerOrder = c1.getClass().getAnnotation(CustomizerOrder.class);
            int c1Order = customizerOrder != null ? customizerOrder.value() : Integer.MAX_VALUE;
            customizerOrder = c2.getClass().getAnnotation(CustomizerOrder.class);
            int c2Order = customizerOrder != null ? customizerOrder.value() : Integer.MAX_VALUE;
            return c1Order - c2Order;
        });
    }

    /**
     * 执行增强链
     * @param configuration mapper对应配置
     * @param mapperInterface mapper接口类
     */
    public void customize(Configuration configuration, Class<? extends MapperAware<?,? extends Serializable>> mapperInterface) {
        Class<?> entityClass = ReflectUtil.getEntityClass(mapperInterface);
        if (entityClass == null) {
            log.debug("无法找到entity的Class，mapper: {}", mapperInterface.getName());
            return;
        }
        for (MapperCustomizer customizer : mapperCustomizerSet) {
            customizer.process(new DaoMapperContext(configuration, sqlEntityFactory, mapperInterface));
        }
    }

    /**
     * 添加链节点
     * @param mapperCustomizer 链节点
     */
    public MapperCustomizerChain addCustomizer(MapperCustomizer mapperCustomizer) {
        mapperCustomizerSet.add(mapperCustomizer);
        return this;
    }

}
