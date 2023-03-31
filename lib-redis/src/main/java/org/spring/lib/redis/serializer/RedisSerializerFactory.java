package org.spring.lib.redis.serializer;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * redis序列化工厂类
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/19
 **/
public class RedisSerializerFactory {

    private static final RedisSerializer<String> STRING_SERIALIZATION = new StringRedisSerializer();
    private static final RedisSerializer<Object> JDK_SERIALIZATION = new JdkSerializationRedisSerializer();
    private static final RedisSerializer<Object> HESSIAN2_SERIALIZATION = new Hessian2SerializationRedisSerializer();

    private RedisSerializerFactory() {

    }

    /**
     * 获取key序列化实现
     * @return 默认String序列化实现
     */
    public static RedisSerializer<String> getRedisKeySerializer() {
        return STRING_SERIALIZATION;
    }

    /**
     * 获取value序列化实现
     * @param type 序列化类型标识
     * @return 序列化实现
     */
    public static RedisSerializer<Object> getRedisValueSerializer(String type) {
        switch(type) {
            case "hessian2":
                return HESSIAN2_SERIALIZATION;
            case "jdk":
            default:
                return JDK_SERIALIZATION;
        }
    }

}
