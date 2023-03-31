package org.spring.lib.cache.support;

import org.spring.lib.cache.constant.CacheCategory;
import org.spring.lib.cache.constant.CacheSupport;
import org.springframework.cache.caffeine.CaffeineCache;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/30
 **/
public class CaffeineCacheFacade extends AbstractCacheFacadeAdapter {

    private final String name;
    private final CaffeineCache caffeineCache;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public CacheCategory getCacheCategory() {
        return CacheCategory.LOCAL_CACHE;
    }

    @Override
    public CacheSupport getCacheSupport() {
        return CacheSupport.CAFFEINE;
    }

    public CaffeineCacheFacade(boolean allowNullValues, String name, CaffeineCache caffeineCache) {
        super(allowNullValues);
        this.name = name;
        this.caffeineCache = caffeineCache;
    }

    @Override
    public Object getNativeCache() {
        return this.caffeineCache.getNativeCache();
    }

    @Override
    protected Object lookup(Object key) {
        return Objects.nonNull(caffeineCache.get(key)) ?
                Objects.requireNonNull(caffeineCache.get(key)).get() : null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return caffeineCache.get(key, valueLoader);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return caffeineCache.get(key, type);
    }

    @Override
    public void put(Object key, Object value) {
        caffeineCache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return caffeineCache.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        caffeineCache.evict(key);
    }

    @Override
    public void clear() {
        caffeineCache.clear();
    }

}
