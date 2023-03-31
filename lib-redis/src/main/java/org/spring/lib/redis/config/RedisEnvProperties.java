package org.spring.lib.redis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redis环境属性配置
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/19
 **/
@ConfigurationProperties(prefix = "spring.redis")
public class RedisEnvProperties {

    private int minIdle = 0;

    private int maxIdle = 8;

    private int maxActive = 8;

    private long maxWaitMillis = -1;

    private String mode;

    private String serializerType;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSerializerType() {
        return serializerType;
    }

    public void setSerializerType(String serializerType) {
        this.serializerType = serializerType;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }
}
