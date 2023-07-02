package org.spring.lib.ibatis.config;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.spring.lib.ibatis.customizer.MapperCustomizerChain;
import org.spring.lib.ibatis.customizer.impl.MapperStatementCustomizer;
import org.spring.lib.ibatis.customizer.impl.SelectResultMapCustomizer;
import org.spring.lib.ibatis.customizer.impl.SqlAnnotationCustomizer;
import org.spring.lib.ibatis.dao.MapperAware;
import org.spring.lib.ibatis.entity.SqlEntityFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * dao层mapper相关bean增强
 * bean实例化&属性注入后,初始化前执行此钩子
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/27
 **/
@Component
public class MapperBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private boolean notFoundAnySqlSessionFactory;
    private static final Map<SqlSessionFactory, MapperCustomizerChain> MAPPER_CUSTOMIZER_CHAINS = new ConcurrentHashMap<>();
    private static final Set<Class<?>> LOADED_MAPPER = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 触发加载sqlSessionFactory
        Map<String, SqlSessionFactory> sqlSessionFactoryBeans = applicationContext.getBeansOfType(SqlSessionFactory.class);
        this.notFoundAnySqlSessionFactory = CollectionUtils.isEmpty(sqlSessionFactoryBeans);
        if (notFoundAnySqlSessionFactory) {
            return;
        }
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryBeans.values()) {
            MapperCustomizerChain mapperCustomizerChain = new MapperCustomizerChain(new SqlEntityFactory());
            mapperCustomizerChain
                    .addCustomizer(new MapperStatementCustomizer())
                    .addCustomizer(new SqlAnnotationCustomizer())
                    .addCustomizer(new SelectResultMapCustomizer());
            MAPPER_CUSTOMIZER_CHAINS.put(sqlSessionFactory, mapperCustomizerChain);
        }
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (notFoundAnySqlSessionFactory || !(bean instanceof MapperFactoryBean)) {
            return bean;
        }
        MapperFactoryBean<?> factoryBean = (MapperFactoryBean<?>) bean;
        Class<?> mapperInterface = factoryBean.getMapperInterface();
        if (!MapperAware.class.isAssignableFrom(mapperInterface) || !LOADED_MAPPER.add(mapperInterface)) {
            return bean;
        }
        Configuration configuration = factoryBean.getSqlSession().getConfiguration();
        // 提前加载Mapper
        if (!configuration.hasMapper(mapperInterface)) {
            configuration.addMapper(mapperInterface);
        }
        SqlSessionTemplate sqlSession = (SqlSessionTemplate) factoryBean.getSqlSession();
        SqlSessionFactory sqlSessionFactory = sqlSession.getSqlSessionFactory();
        MapperCustomizerChain mapperCustomizerChain = MAPPER_CUSTOMIZER_CHAINS.get(sqlSessionFactory);
        if (mapperCustomizerChain != null) {
            mapperCustomizerChain.customize(configuration, (Class<? extends MapperAware<?, ? extends Serializable>>) mapperInterface);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}