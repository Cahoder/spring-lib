package org.spring.lib.cache.factory;

import org.spring.lib.cache.CacheFacade;
import org.spring.lib.cache.constant.CacheSupport;
import org.spring.lib.redis.service.RedisService;
import org.springframework.stereotype.Component;

/**
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/31
 **/
@Component
public class RedisCacheFactory implements CacheFacadeFactory {

    //@Resource
    /**
     * @see org.spring.lib.redis.service.RedisService
     */
    private RedisService redisService;

    @Override
    public CacheSupport supportedCache() {
        return CacheSupport.REDIS;
    }

    @Override
    public CacheFacade initCacheFacade(String cacheName) {
        throw new UnsupportedOperationException("当前还未实现redis缓存厂商");
    }

}
