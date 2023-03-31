package org.spring.lib.cache.config;

import org.spring.lib.cache.constant.CacheSupport;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存配置中心
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/31
 **/
@Component
@ConfigurationProperties("cache.config")
public class CacheCentralConfiguration {

    /**
     * key-》缓存名称，value-》缓存厂商
     */
    private Map<String, CacheSupport> cacheFactoryDefinite = new HashMap<>(0);

    public Map<String, CacheSupport> getCacheFactoryDefinite() {
        return cacheFactoryDefinite;
    }

    public void setCacheFactoryDefinite(Map<String, CacheSupport> cacheFactoryDefinite) {
        this.cacheFactoryDefinite = cacheFactoryDefinite;
    }

}
