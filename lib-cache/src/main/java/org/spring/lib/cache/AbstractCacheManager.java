package org.spring.lib.cache;

import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 抽象缓存管理
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/30
 **/
public abstract class AbstractCacheManager implements CacheManager {

    private final ConcurrentMap<String, CacheFacade> cacheMap = new ConcurrentHashMap<>(16);

    /**
     * Early cache initialization on startup
     * @return All Cache initialized
     */
    protected abstract Collection<CacheFacade> loadCaches();

    /**
     * Lazy cache initialization on access
     * @param name cache-name
     * @return Cache initialized
     */
    protected abstract CacheFacade loadCache(String name);

    /**
     * search the designated cache
     * @param name cache name
     * @return cache provider
     */
    @Override
    public CacheFacade getCache(String name) {
        CacheFacade cache = this.cacheMap.get(name);
        if (cache != null) {
            return cache;
        }
        else {
            // fully synchronize now for missing cache creation
            synchronized (this.cacheMap) {
                cache = this.cacheMap.get(name);
                if (cache == null) {
                    cache = loadCache(name);
                    if (cache != null) {
                        this.cacheMap.put(name, cache);
                    }
                }
                return cache;
            }
        }
    }

    @Override
    public Collection<String> getCacheNames() {
        return this.cacheMap.keySet();
    }

    /**
     * Initialize the static configuration of caches.
     * can also be called to re-initialize at runtime.
     * @see #loadCaches()
     */
    public void initializeCaches() {
        Collection<? extends CacheFacade> caches = loadCaches();
        synchronized (this.cacheMap) {
            this.cacheMap.clear();
            for (CacheFacade cache : caches) {
                String name = cache.getName();
                this.cacheMap.put(name, cache);
            }
        }
    }

}
