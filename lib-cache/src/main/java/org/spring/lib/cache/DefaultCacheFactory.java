package org.spring.lib.cache;

import org.spring.lib.cache.constant.CacheSupport;
import org.spring.lib.cache.factory.CacheFacadeFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 默认缓存创建工厂
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/31
 **/
@Component
public class DefaultCacheFactory {

    @Resource
    private List<CacheFacadeFactory> supportedCacheFactory;

    /**
     * 根据不同的缓存厂商创建
     * @param cacheName 缓存名称
     * @param cacheSupport 缓存厂商
     * @return 缓存门面
     */
    public CacheFacade createCacheFacade(String cacheName, CacheSupport cacheSupport) {
        Optional<CacheFacadeFactory> hasSupportedOptional = supportedCacheFactory.stream().filter(
                cacheFactory -> Objects.equals(cacheSupport, cacheFactory.supportedCache())
        ).findFirst();
        if (hasSupportedOptional.isPresent()) {
            return hasSupportedOptional.get().initCacheFacade(cacheName);
        }
        throw new UnsupportedOperationException("当前不支持'"+cacheSupport+"'缓存工厂创建");
    }

}
