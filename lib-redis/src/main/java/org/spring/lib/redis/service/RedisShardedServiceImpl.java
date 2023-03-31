package org.spring.lib.redis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.lib.redis.serializer.RedisSerializerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;


/**
 * redis集群分片版
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/9
 **/
public class RedisShardedServiceImpl extends BaseRedisService implements RedisService {

    private final Logger log = LoggerFactory.getLogger(RedisShardedServiceImpl.class);

    @Override
    public RedisSerializer<String> getKeySerializer() {
        return RedisSerializerFactory.getRedisKeySerializer();
    }

    @Override
    public RedisSerializer<Object> getValueSerializer() {
        return RedisSerializerFactory.getRedisValueSerializer(redisEnvProperties.getSerializerType());
    }

    @Override
    public <T extends Serializable> void multiSet(Map<String, T> entries) {
        //通过hash分片到多个redis节点上
    }

    @Override
    public <T extends Serializable> Map<String, T> multiGet(Collection<String> keys) {
        //通过hash分片从多个redis节点获取
        return null;
    }

}
