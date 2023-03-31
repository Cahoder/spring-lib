package org.spring.lib.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.lib.cache.config.CacheCentralConfiguration;
import org.spring.lib.cache.support.AbstractCacheFacadeAdapter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 默认缓存管理器
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/30
 **/
@Component
public class DefaultCacheManager extends AbstractCacheManager implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(DefaultCacheManager.class);

    @Resource
    private ListableBeanFactory beanFactory;

    @Resource
    private DefaultCacheFactory cacheFactory;

    @Resource
    private CacheCentralConfiguration cacheCentralConfiguration;

    @Override
    protected Collection<CacheFacade> loadCaches() {
        Collection<CacheFacade> cacheFacades = new ArrayList<>();
        beanFactory.getBeansOfType(AbstractCacheFacadeAdapter.class).values().forEach(
                cacheAdapter -> cacheFacades.add(cacheFactory.createCacheFacade(cacheAdapter.getName(), cacheAdapter.getCacheSupport()))
        );
        cacheCentralConfiguration.getCacheFactoryDefinite().forEach(
                (cacheName, cacheSupport) -> cacheFacades.add(cacheFactory.createCacheFacade(cacheName, cacheSupport))
        );
        return cacheFacades;
    }

    @Override
    protected CacheFacade loadCache(String name) {
        throw new UnsupportedOperationException("当前未实现懒加载缓存功能");
    }

    @Override
    public void afterPropertiesSet() {
        log.info("start initialize the static configuration of caches");
        initializeCaches();
        log.info("complete Initialize the static configuration of caches");
    }

}
