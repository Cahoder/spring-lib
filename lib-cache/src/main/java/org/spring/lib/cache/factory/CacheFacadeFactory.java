package org.spring.lib.cache.factory;

import org.spring.lib.cache.CacheFacade;
import org.spring.lib.cache.constant.CacheSupport;

/**
 * 抽象缓存创建工厂
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/31
 **/
public interface CacheFacadeFactory {

    /**
     * 当前支持创建的缓存
     * @return 缓存厂商
     */
    CacheSupport supportedCache();

    /**
     * 初始化缓存门面
     * @return 缓存门面
     * @param cacheName 缓存名称
     */
    CacheFacade initCacheFacade(String cacheName);

}
