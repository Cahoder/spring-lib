package org.spring.lib.cache.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Caffeine缓存配置信息
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/30
 **/
@Component
@ConfigurationProperties("cache.config.local.caffeine")
public class CaffeineCacheConfiguration {

    /**
     * key-》缓存名称，value-》缓存规范 详情看CaffeineSpec cacheSpecification=initialCapacity=5,maximumSize=500,expireAfterWrite=10s
     */
    private Map<String, String> cacheSpecification = new HashMap<>(0);

    /**
     * 默认配置
     */
    private String defaultCacheSpecification = "initialCapacity=300,maximumSize=500,expireAfterWrite=2h,recordStats";

    public Map<String, String> getCacheSpecification() {
        return cacheSpecification;
    }

    public void setCacheSpecification(Map<String, String> cacheSpecification) {
        this.cacheSpecification = cacheSpecification;
    }

    public String getDefaultCacheSpecification() {
        return defaultCacheSpecification;
    }

    public void setDefaultCacheSpecification(String defaultCacheSpecification) {
        this.defaultCacheSpecification = defaultCacheSpecification;
    }

}
