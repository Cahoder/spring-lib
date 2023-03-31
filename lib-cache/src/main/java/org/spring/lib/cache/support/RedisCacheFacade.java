package org.spring.lib.cache.support;

import org.spring.lib.cache.constant.CacheCategory;
import org.spring.lib.cache.constant.CacheSupport;
import org.spring.lib.redis.service.RedisService;

import java.util.Objects;

/**
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/31
 **/
public class RedisCacheFacade extends AbstractCacheFacadeAdapter {

    private final String name;
    private final RedisService redisService;

    protected RedisCacheFacade(String name, RedisService redisService) {
        super(false);
        this.name = name;
        this.redisService = redisService;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public CacheCategory getCacheCategory() {
        return CacheCategory.DISTRIBUTED_CACHE;
    }

    @Override
    public CacheSupport getCacheSupport() {
        return CacheSupport.REDIS;
    }

    @Override
    protected Object lookup(Object key) {
        return Objects.requireNonNull(redisService.get(String.valueOf(key)));
    }

}
