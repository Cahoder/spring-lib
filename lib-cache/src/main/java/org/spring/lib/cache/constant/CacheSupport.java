package org.spring.lib.cache.constant;

/**
 * 缓存厂商
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/30
 **/
public enum CacheSupport {

    /**
     * Redis
     */
    REDIS,
    /**
     * Memcached
     */
    MEMCACHED,
    /**
     * GuavaCache
     */
    GUAVA_CACHE,
    /**
     * EhCache
     */
    EH_CACHE,
    /**
     * Caffeine
     */
    CAFFEINE,
    /**
     * ConcurrentMap
     */
    CONCURRENT_MAP,
    /**
     * JCache
     */
    JCACHE;

}
