package org.spring.lib.redis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.lib.redis.serializer.RedisSerializerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import static org.spring.lib.redis.constant.LockConstant.*;

/**
 * redis单机版
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/9
 **/
public class RedisSingleServiceImpl extends BaseRedisService implements RedisService {

    private final Logger log = LoggerFactory.getLogger(RedisSingleServiceImpl.class);

    @Override
    public RedisSerializer<String> getKeySerializer() {
        return RedisSerializerFactory.getRedisKeySerializer();
    }

    @Override
    public RedisSerializer<Object> getValueSerializer() {
        return RedisSerializerFactory.getRedisValueSerializer(redisEnvProperties.getSerializerType());
    }

    @Override
    public Boolean set(String key, String value, String nxxx, String expx, long timeout) {
        if (key == null || key.trim().length() == 0) {
            return false;
        }
        if (!SET_IF_EXIST.equalsIgnoreCase(nxxx) && !SET_IF_NOT_EXIST.equalsIgnoreCase(nxxx)) {
            return false;
        }
        if (!SET_MILLISECONDS_EXPIRE_TIME.equalsIgnoreCase(expx) && !SET_SECONDS_EXPIRE_TIME.equalsIgnoreCase(expx)) {
            return false;
        }
        try {
            return redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    return connection.set(serializeKey(key), serializeValue(value),
                            SET_SECONDS_EXPIRE_TIME.equalsIgnoreCase(expx) ?
                                    Expiration.seconds(timeout) : Expiration.milliseconds(timeout),
                            SET_IF_EXIST.equalsIgnoreCase(nxxx) ?
                                    RedisStringCommands.SetOption.ifPresent() : RedisStringCommands.SetOption.ifAbsent());
                }
            });
        } catch (Exception ex) {
            log.error("set error.key={}", key, ex);
        }
        return false;
    }

    @Override
    public <T extends Serializable> Boolean setNx(String key, T value) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value);
        } catch (Exception ex) {
            log.error("setNx error.key={}", key, ex);
        }
        return false;
    }

    @Override
    public <T extends Serializable> Boolean setNx(String key, T value, long timeout, TimeUnit timeunit) {
        try {
            return redisTemplate.execute(new RedisCallback<Boolean>() {
                @Override
                public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                    return connection.set(serializeKey(key), serializeValue(value),
                            Expiration.from(timeout, timeunit), RedisStringCommands.SetOption.ifAbsent());
                }
            });
        } catch (Exception ex) {
            log.error("setNxWithExpire error.key={}", key, ex);
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T get(String key) {
        try {
            return (T) redisTemplate.opsForValue().get(key);
        } catch (Exception ex) {
            log.error("get error.key={}", key, ex);
        }
        return null;
    }

    @Override
    public Long increment(String key) {
        try {
            return this.redisTemplate.execute(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(RedisConnection connection) throws DataAccessException {
                    return connection.incr(serializeKey(key));
                }
            });
        } catch (Exception ex) {
            log.error("increment error.key={}", key, ex);
        }
        return null;
    }

    @Override
    public Long increment(String key, long number) {
        try {
            return this.redisTemplate.execute(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(RedisConnection connection) throws DataAccessException {
                    return connection.incrBy(serializeKey(key), number);
                }
            });
        } catch (Exception ex) {
            log.error("increment error.key={}", key, ex);
        }
        return null;
    }

    @Override
    public Long decrement(String key) {
        try {
            return this.redisTemplate.execute(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(RedisConnection connection) throws DataAccessException {
                    return connection.decr(serializeKey(key));
                }
            });
        } catch (Exception ex) {
            log.error("decrement error.key={}", key, ex);
        }
        return null;
    }

    @Override
    public Long decrement(String key, long number) {
        try {
            return this.redisTemplate.execute(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(RedisConnection connection) throws DataAccessException {
                    return connection.decrBy(serializeKey(key), number);
                }
            });
        } catch (Exception ex) {
            log.error("decrement error.key={}", key, ex);
        }
        return null;
    }

    @Override
    public Boolean setExpire(String key, long timeout, TimeUnit timeunit) {
        return redisTemplate.expire(key, timeout, timeunit);
    }

    @Override
    public Boolean remove(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public <T extends Serializable> Long listLeftPushIfPresent(String key, T value) {
        return redisTemplate.opsForList().leftPushIfPresent(key, value);
    }

    @Override
    public <T extends Serializable> Long listLeftPush(String key, T value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public Long listLeftPushAll(String key, Serializable... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    @Override
    public Long listLeftPushString(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    @Override
    public Long listLeftPushStringAll(String key, String... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T listLeftPop(String key) {
        return (T) redisTemplate.opsForList().leftPop(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T listLeftPop(String key, long timeout, TimeUnit timeunit) {
        return (T) redisTemplate.opsForList().leftPop(key, timeout, timeunit);
    }

    @Override
    public String listLeftPopString(String key) {
        return (String) redisTemplate.opsForList().leftPop(key);
    }

    @Override
    public <T extends Serializable> Long listRightPushIfPresent(String key, T value) {
        return redisTemplate.opsForList().rightPushIfPresent(key, value);
    }

    @Override
    public <T extends Serializable> Long listRightPush(String key, T value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public Long listRightPushAll(String key, Serializable... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    @Override
    public Long listRightPushString(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public Long listRightPushStringAll(String key, String... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T listRightPop(String key) {
        return (T) redisTemplate.opsForList().rightPop(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T listRightPop(String key, long timeout, TimeUnit timeunit) {
        return (T) redisTemplate.opsForList().rightPop(key, timeout, timeunit);
    }

    @Override
    public String listRightPopString(String key) {
        return (String) redisTemplate.opsForList().rightPop(key);
    }

}
