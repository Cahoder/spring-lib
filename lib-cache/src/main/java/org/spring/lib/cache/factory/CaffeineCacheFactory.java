package org.spring.lib.cache.factory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import org.spring.lib.cache.CacheFacade;
import org.spring.lib.cache.config.CaffeineCacheConfiguration;
import org.spring.lib.cache.constant.CacheSupport;
import org.spring.lib.cache.support.CaffeineCacheFacade;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/31
 **/
@Component
public class CaffeineCacheFactory implements CacheFacadeFactory {

    @Resource
    private CaffeineCacheConfiguration caffeineCacheConfiguration;

    @Override
    public CacheSupport supportedCache() {
        return CacheSupport.CAFFEINE;
    }

    @Override
    public CacheFacade initCacheFacade(String cacheName) {
        Cache<Object, Object> caffeineCache = createCaffeineCache(caffeineCacheConfiguration.getCacheSpecification()
                .getOrDefault(cacheName, caffeineCacheConfiguration.getDefaultCacheSpecification()));
        return new CaffeineCacheFacade(true, cacheName, new CaffeineCache(cacheName, caffeineCache,true));
    }

    private com.github.benmanes.caffeine.cache.Cache<Object, Object> createCaffeineCache(String caffeineSpec) {
        return Caffeine.from(CaffeineSpec.parse(caffeineSpec)).build();
    }

    //@Scheduled(fixedRate = 60 * 60 * 1000)
    //private void printCacheRecordStats() {log.info("{}缓存使用情况统计：{}", name, caffeineCache.getNativeCache().stats());}

}
