package org.spring.lib.redis.config;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Redis环境自动注入
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @see org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
 * @see org.springframework.boot.autoconfigure.data.redis.RedisConnectionConfiguration
 * @since 2022/9/20
 * 下面参考自动装配搭配使用@AutoConfigureBefore实现
 **/
@Configuration
@EnableConfigurationProperties({RedisEnvProperties.class, RedisProperties.class})
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisEnvAutoConfiguration {

    @Bean(destroyMethod = "shutdown")
    public ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    @Bean
    public GenericObjectPoolConfig<?> poolConfig(RedisEnvProperties redisEnvProperties) {
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMinIdle(redisEnvProperties.getMinIdle());
        poolConfig.setMaxIdle(redisEnvProperties.getMaxIdle());
        poolConfig.setMaxTotal(redisEnvProperties.getMaxActive());
        poolConfig.setMaxWait(Duration.ofMillis(redisEnvProperties.getMaxWaitMillis()));
        return poolConfig;
    }

    @Bean
    public RedisTemplate<String, Serializable> redisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return stringRedisTemplate;
    }

    @Bean
    @ConditionalOnProperty(value = "spring.redis.mode", havingValue = "single", matchIfMissing = true)
    public RedisConnectionFactory redisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration,
                                                         @Qualifier("clientResources") ClientResources clientResources) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .clientResources(clientResources).build();
        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfiguration);
    }

    @Bean
    @ConditionalOnProperty(value = "spring.redis.mode", havingValue = "single", matchIfMissing = true)
    public RedisStandaloneConfiguration redisStandaloneConfiguration(RedisProperties properties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(properties.getHost());
        config.setPort(properties.getPort());
        config.setPassword(RedisPassword.of(properties.getPassword()));
        config.setDatabase(properties.getDatabase());
        return config;
    }

    @Bean
    @ConditionalOnProperty(value = "spring.redis.mode", havingValue = "sentinel")
    public RedisConnectionFactory redisConnectionFactory(RedisSentinelConfiguration redisSentinelConfiguration,
                                                         @Qualifier("poolConfig") GenericObjectPoolConfig<?> poolConfig,
                                                         @Qualifier("clientResources") ClientResources clientResources) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .clientResources(clientResources).poolConfig(poolConfig).build();
        return new LettuceConnectionFactory(redisSentinelConfiguration, clientConfiguration);
    }

    @Bean
    @ConditionalOnProperty(value = "spring.redis.mode", havingValue = "sentinel")
    public RedisSentinelConfiguration redisSentinelConfiguration(RedisProperties properties) {
        RedisProperties.Sentinel sentinelProperties = properties.getSentinel();
        Assert.notNull(sentinelProperties, "redis sentinel 配置信息缺失!");
        RedisSentinelConfiguration config = new RedisSentinelConfiguration();
        config.master(sentinelProperties.getMaster());
        config.setSentinels(createSentinels(sentinelProperties));
        if (properties.getPassword() != null) {
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }
        config.setDatabase(properties.getDatabase());
        return config;
    }

    private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<>();
        for (String node : sentinel.getNodes()) {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.notNull(parts, "redis sentinel node 配置信息有误!");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
            } catch (RuntimeException ex) {
                throw new IllegalStateException(
                        "Invalid redis sentinel " + "property '" + node + "'", ex);
            }
        }
        return nodes;
    }

    @Bean
    @ConditionalOnProperty(value = "spring.redis.mode", havingValue = "cluster")
    public RedisConnectionFactory redisConnectionFactory(RedisClusterConfiguration redisClusterConfiguration,
                                                         @Qualifier("poolConfig") GenericObjectPoolConfig<?> poolConfig,
                                                         @Qualifier("clientResources") ClientResources clientResources) {
        //开启自适应刷新和定时刷新-防止redis集群变更时导致连接异常
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh()
                .enableAllAdaptiveRefreshTriggers()
                .refreshPeriod(Duration.ofSeconds(5))
                .build();
        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(clusterTopologyRefreshOptions).build();
        LettuceClientConfiguration lettuceClientConfiguration = LettucePoolingClientConfiguration.builder()
                .clientResources(clientResources).poolConfig(poolConfig).clientOptions(clusterClientOptions).build();
        return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
    }

    @Bean
    @ConditionalOnProperty(value = "spring.redis.mode", havingValue = "cluster")
    public RedisClusterConfiguration redisClusterConfiguration(RedisProperties properties) {
        RedisProperties.Cluster clusterProperties = properties.getCluster();
        Assert.notNull(clusterProperties, "redis cluster 配置信息缺失!");
        RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());
        if (clusterProperties.getMaxRedirects() != null) {
            config.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        if (properties.getPassword() != null) {
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }
        return config;
    }

}
