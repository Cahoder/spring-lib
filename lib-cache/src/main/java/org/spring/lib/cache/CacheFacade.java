package org.spring.lib.cache;

import org.spring.lib.cache.constant.CacheCategory;
import org.spring.lib.cache.constant.CacheSupport;
import org.springframework.cache.Cache;

/**
 * 缓存提供门面
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/30
 **/
public interface CacheFacade extends Cache {

    /**
     * 获取缓存类别
     * @return 缓存类别
     */
    CacheCategory getCacheCategory();

    /**
     * 获取缓存实现
     * @return 缓存实现
     */
    CacheSupport getCacheSupport();

}
