package org.spring.lib.redis.service;

import org.spring.lib.redis.config.RedisEnvProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * redis公共代码
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/9
 **/
public abstract class BaseRedisService implements InitializingBean {

    @Resource
    protected RedisEnvProperties redisEnvProperties;
    @Resource(name = "redisTemplate")
    protected RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 获取key序列化工具
     *
     * @return key序列化工具
     */
    public abstract RedisSerializer<String> getKeySerializer();

    /**
     * 获取value序列化工具
     *
     * @return value序列化工具
     */
    public abstract RedisSerializer<Object> getValueSerializer();

    /**
     * 序列化key
     *
     * @param key 键
     * @return 二进制字节数据
     */
    protected byte[] serializeKey(final String key) {
        return getKeySerializer().serialize(key);
    }

    /**
     * 反序列化key
     *
     * @param keyBytes 键二进制数据
     * @return 键字符串
     */
    protected String deserializeKey(final byte[] keyBytes) {
        return getKeySerializer().deserialize(keyBytes);
    }

    /**
     * 序列化多个key
     *
     * @param keys 键组
     * @return 二进制字节数据数组
     */
    protected byte[][] serializeKeys(final String... keys) {
        byte[][] keysBytes = new byte[keys.length][];
        for (int i = 0; i < keys.length; i++) {
            keysBytes[i] = this.serializeKey(keys[i]);
        }
        return keysBytes;
    }

    /**
     * 反序列化多个key
     *
     * @param keysBytes 键二进制数据数组
     * @return 键字符串数组
     */
    protected String[] deserializeKeys(final byte[][] keysBytes) {
        String[] keys = new String[keysBytes.length];
        for (int i = 0; i < keysBytes.length; i++) {
            keys[i] = this.deserializeKey(keysBytes[i]);
        }
        return keys;
    }

    /**
     * 序列化value
     *
     * @param value 值
     * @return 二进制字节数据
     */
    protected byte[] serializeValue(final Serializable value) {
        return getValueSerializer().serialize(value);
    }

    /**
     * 反序列化value
     *
     * @param valueBytes 值二进制数据
     * @return 值
     */
    @SuppressWarnings("unchecked")
    protected <V extends Serializable> V deserializeValue(final byte[] valueBytes) {
        return (V) getValueSerializer().deserialize(valueBytes);
    }

    /**
     * 序列化多个value
     *
     * @param values 值组
     * @return 二进制字节数据数组
     */
    protected byte[][] serializeValues(final Serializable... values) {
        byte[][] keysBytes = new byte[values.length][];
        for (int i = 0; i < values.length; i++) {
            keysBytes[i] = this.serializeValue(values[i]);
        }
        return keysBytes;
    }

    /**
     * 反序列化多个value
     *
     * @param valuesBytes 值二进制数据数组
     * @return 值组
     */
    @SuppressWarnings("unchecked")
    protected <V extends Serializable> V[] deserializeValues(final byte[][] valuesBytes, Class<V> clazz) {
        V[] values = (V[]) Array.newInstance(clazz, valuesBytes.length);
        for (int i = 0; i < valuesBytes.length; i++) {
            values[i] = this.deserializeValue(valuesBytes[i]);
        }
        return values;
    }

    /**
     * 反序列化多个value
     *
     * @param valuesBytes 值二进制数据集
     * @return 值集
     */
    protected <V extends Serializable> List<V> deserializeValues(final List<byte[]> valuesBytes) {
        List<V> values = new ArrayList<>(valuesBytes.size());
        for (byte[] valuesByte : valuesBytes) {
            values.add(this.deserializeValue(valuesByte));
        }
        return values;
    }

    /**
     * 反序列化多个value
     *
     * @param valuesBytes 值二进制数据去重集
     * @return 值去重集
     */
    protected <V extends Serializable> Set<V> deserializeValues(final Set<byte[]> valuesBytes) {
        Set<V> values = new HashSet<>(valuesBytes.size());
        for (byte[] valuesByte : valuesBytes) {
            values.add(this.deserializeValue(valuesByte));
        }
        return values;
    }

    @Override
    public void afterPropertiesSet() {
        redisTemplate.setKeySerializer(getKeySerializer());
        redisTemplate.setValueSerializer(getValueSerializer());
        redisTemplate.setHashKeySerializer(getKeySerializer());
        redisTemplate.setHashValueSerializer(getValueSerializer());
    }

}
