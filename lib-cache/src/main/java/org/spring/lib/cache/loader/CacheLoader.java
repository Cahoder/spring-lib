package org.spring.lib.cache.loader;

import org.spring.lib.cache.CacheFacade;

/**
 * 缓存加载器
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/30
 **/
public interface CacheLoader {

    /**
     * 获取缓存加载器名称
     * @return 缓存加载器名称
     */
    String getName();

    /**
     * 执行加载缓存
     */
    void loadCache();

    /**
     * 获取缓存加载器对应缓存
     * @return 缓存提供门面
     */
    CacheFacade getCache();

}
