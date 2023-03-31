package org.spring.lib.redis.service;

import org.spring.lib.redis.enums.DistanceUnit;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 封装redis操作
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/9
 **/
public interface RedisService {

    /**
     * 可用于分布式锁的原子实现.<p>
     * <dt>注意："PX"单位: 毫秒, "EX"单位：秒</dt>
     *
     * @param key     锁名称
     * @param value   请求唯一标识，用于锁释放
     * @param nxxx    用作分布式锁的话, 传值"NX":意思是SET IF NOT EXIST,
     *                即当key不存在时，我们进行set操作；若key已经存在，则不做任何操作
     * @param expx    传值"PX"，意思是我们要给这个key加一个过期的设置，具体时间由第五个参数决定。
     * @param timeout 代表key的过期时间. "PX"单位: 毫秒, "EX"单位：秒
     * @return 加锁结果
     */
    default Boolean set(String key, String value, String nxxx, String expx, long timeout) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 执行lua脚本
     *
     * @param key 分布式Jedis的key
     * @param script 一段Lua脚本程序,脚本不必(也不应该)定义为一个Lua函数
     * @param valueKeys 表示在脚本中所用到的那些Redis键(key)，
     *                  这些键名参数可以在Lua中通过全局变量KEYS数组，
     *                  以1为基址的形式访问(KEYS[1]，KEYS[2]，以此类推)。
     * @param valueArgs 附加参数，在Lua中通过全局变量ARGV数组访问，访问的形式和KEYS变量类似(ARGV[1]、ARGV[2]，诸如此类)
     * @return 脚本执行结果
     */
    default Object eval(String key, String script, List<String> valueKeys, List<String> valueArgs) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 生成SHA码.
     *
     * <p>这里的SHA码，是作为{@linkplain #evalsha(String, String, List, List) 的第二个参数}</p>
     *
     * @param key    主要用来hash分片，用一个固定的常量，让所有的操作都落在同一台redis节点上.
     * @param script lua脚本.
     * @return sha码.
     */
    default String scriptLoad(String key, String script) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 执行SHA码对应的lua脚本.
     *
     * @param key       主要用来hash分片，用一个固定的常量，让所有的操作都落在同一台redis节点上.
     * @param sha1      值来源{@linkplain #scriptLoad(String, String)}
     * @param valueKeys 可参考{@linkplain #eval}
     * @param valueArgs 可参考{@linkplain #eval}
     * @return 脚本执行结果
     */
    default Object evalsha(String key, String sha1, List<String> valueKeys, List<String> valueArgs) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 设置单个值
     *
     * @param key 键
     * @param value 值
     */
    default <T extends Serializable> void set(String key, T value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 设置带过期时间的单个值
     *
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param timeunit 时间单位
     */
    default <T extends Serializable> void set(String key, T value, long timeout, TimeUnit timeunit) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 设置多个值
     * @param entries 键值集
     */
    default <T extends Serializable> void multiSet(Map<String, T> entries) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 设置带过期时间的多个值
     *
     * @param tMap 键值集
     * @param timeout 过期时间
     * @param timeunit 时间单位
     */
    default <T extends Serializable> void multiSet(Map<String, T> tMap, long timeout, TimeUnit timeunit) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 为不存在的字段赋值
     * 存在则返回false-不修改
     *
     * @param key 键
     * @param value 值
     * @return true-成功 false-失败
     */
    default <T extends Serializable> Boolean setNx(String key, T value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 为不存在的字段赋值，并设置过期时间
     * 存在则返回false-不修改
     *
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param timeunit 时间单位
     * @return true-成功 false-失败
     */
    default <T extends Serializable> Boolean setNx(String key, T value, long timeout, TimeUnit timeunit) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 获取单个值
     *
     * @param key 键
     * @return 值
     */
    default <T extends Serializable> T get(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 获取多个值
     * @param keys 键集
     * @return 键对应值集
     * 如果键对应的值不存在或已过期则该值为null
     */
    default <T extends Serializable> Map<String, T> multiGet(Collection<String> keys) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 判断key是否存在.
     *
     * @param key 键
     * @return true-存在 false-不存在
     */
    default Boolean isExists(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 删除键对应的值.
     *
     * @param key 键
     * @return true-成功 false-失败
     */
    default Boolean remove(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 获取元素类型.
     *
     * @param key 键
     * @return none, string, list, set, zset, hash
     */
    default String getType(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 设置键在某段时间后失效.
     *
     * @param key 键
     * @param timeout 过期时间
     * @param timeunit 时间单位
     * @return true-成功 false-失败
     */
    default Boolean setExpire(String key, long timeout, TimeUnit timeunit) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 在某个时间点失效.
     *
     * @param key 键
     * @param date 失效时间点
     * @return true-成功 false-失败
     */
    default Boolean setExpireAt(String key, Date date) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 获取key存活时间，单位为秒.
     *
     * @param key 键
     * @return 存活时间（单位:秒）
     */
    default Long getTimeToLive(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Sets the bit at offset in value stored at key.
     *
     * @param key 键
     * @param offset 偏移量
     * @param value 值
     * @return true-成功 false-失败
     */
    default Boolean setBit(String key, long offset, boolean value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get the bit value at offset of value at key.
     *
     * @param key 键
     * @param offset 偏移量
     * @return true-成功 false-失败
     */
    default Boolean getBit(String key, long offset) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * decrement one the integer value.
     *
     * @param key 键
     * @return 递减1后key的值
     */
    default Long decrement(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * decrement the integer value of a key by the given number.
     *
     * @param key 键
     * @param number 递减量
     * @return 递减number后key的值
     */
    default Long decrement(String key, long number) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * decrement one the integer value.
     *
     * @param key 键
     * @return true-成功 false-失败
     */
    default Boolean isDecrement(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * decrement the integer value of a key by the given number.
     *
     * @param key 键
     * @param number 递减量
     * @return true-成功 false-失败
     */
    default Boolean isDecrement(String key, long number) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * increment one the integer value.
     *
     * @param key 键
     * @return 递增1后key的值
     */
    default Long increment(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * increment the integer value of a key by the given number.
     *
     * @param key 键
     * @param number 递增量
     * @return 递增number后key的值
     */
    default Long increment(String key, long number) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * increment one the integer value.
     *
     * @param key 键
     * @return true-成功 false-失败
     */
    default Boolean isIncrement(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * increment the integer value of a key by the given number.
     *
     * @param key 键
     * @param number 递增量
     * @return true-成功 false-失败
     */
    default Boolean isIncrement(String key, long number) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Append a string value to a key .
     *
     * @param key 键
     * @param value 追加字符串
     * @return 追加指定值之后，key中字符串的长度
     */
    default Integer append(String key, String value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get a substring of the string stored at a key.
     *
     * @param key 键
     * @param start 包含的起始位置-负数表示相对字符串结尾的偏移
     * @param end 包含的结束位置-负数表示相对字符串结尾的偏移
     * @return 子字符串
     */
    default String substr(String key, int start, int end) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Set the string value of a hash field.
     *
     * @param key 键
     * @param field 哈希表键
     * @param value 哈希表值
     * @return true-成功 false-失败
     */
    default <T extends Serializable> Boolean hashSet(String key, String field, T value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Set the value of a hash field, only if the field does not exist.
     *
     * @param key 键
     * @param field 哈希表键
     * @param value 哈希表值
     * @return true-成功 false-失败
     */
    default <T extends Serializable> Boolean hashSetNx(String key, String field, T value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 判断哈希表中field是否存在.
     *
     * @param key 键
     * @param field 哈希表键
     * @return true-成功 false-失败
     */
    default Boolean hashExists(String key, String field) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get the value of a hash field .
     *
     * @param key 键
     * @param field 哈希表键
     * @return 哈希表值
     */
    default <T extends Serializable> T hashGet(String key, String field) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 删除哈希表中单个键
     *
     * @param key 键
     * @param field 哈希表键
     * @return 被成功删除字段的数量，不包括被忽略的字段
     */
    default Long hashDelete(String key, String field) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get hash table of a key .
     *
     * @param key 键
     * @return 哈希表
     */
    default <T extends Serializable> Map<String, T> hashGetAll(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get hash table's keys of a key .
     *
     * @param key 键
     * @return 哈希表所有键
     */
    default Set<String> hashGetAllKeys(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get hash table's values of a key .
     *
     * @param key 键
     * @return 哈希表所有值
     */
    default <T extends Serializable> List<T> hashGetAllValues(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 删除哈希表中多个键
     *
     * @param key 键
     * @param fields 哈希表键
     * @return 被成功删除字段的数量，不包括被忽略的字段
     */
    default Long hashMapDelete(String key, String...fields) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 获取哈希表中多个键
     *
     * @param key 键
     * @param fields 哈希表键
     * @return 哈希表需要获取的部分
     */
    default <T extends Serializable> Map<String, T> hashMapGet(String key, String...fields) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 设置哈希表中多个键
     *
     * @param key 键
     * @param dataMap 待新增部分哈希表
     * @return true-成功 false-失败
     */
    default <T extends Serializable> Boolean hashMapSet(String key, Map<String, T> dataMap) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Append one value to a list.
     *
     * @param key 键
     * @param value 值
     * @return 执行操作后list的长度
     */
    default <T extends Serializable> Long listRightPush(String key, T value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Append one string value to a list.
     *
     * @param key 键
     * @param value 值
     * @return 执行操作后list的长度
     */
    default Long listRightPushString(String key, String value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Append one or multiple values to a list.
     *
     * @param key 键
     * @param values 多个值
     * @return 执行操作后list的长度
     */
    default Long listRightPushAll(String key, Serializable...values) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Append one or multiple string values to a list.
     *
     * @param key 键
     * @param values 多个值
     * @return 执行操作后list的长度
     */
    default Long listRightPushStringAll(String key, String...values) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Append values to key only if the list exists.
     *
     * @param key 键
     * @param value 值
     * @return 执行操作后list的长度
     */
    default <T extends Serializable> Long listRightPushIfPresent(String key, T value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Prepend one value to a list.
     *
     * @param key 键
     * @param value 值
     * @return 执行操作后list的长度
     */
    default <T extends Serializable> Long listLeftPush(String key, T value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Prepend one string value to a list.
     *
     * @param key 键
     * @param value 值
     * @return 执行操作后list的长度
     */
    default Long listLeftPushString(String key, String value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Prepend one or multiple values to a list.
     *
     * @param key 键
     * @param values 多个值
     * @return 执行操作后list的长度
     */
    default Long listLeftPushAll(String key, Serializable...values) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Prepend one or multiple string values to a list.
     *
     * @param key 键
     * @param values 多个值
     * @return 执行操作后list的长度
     */
    default Long listLeftPushStringAll(String key, String...values) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Prepend values to key only if the list exits.
     *
     * @param key 键
     * @param value 值
     * @return 执行操作后list的长度
     */
    default <T extends Serializable> Long listLeftPushIfPresent(String key, T value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get a range of elements from a list .
     *
     * @param key 键
     * @param start 包含的起始位置
     * @param end 包含的结束位置
     * @return list范围内元素列表
     */
    default <T extends Serializable> List<T> rangeList(String key, long start, long end) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Trim a list to the specified range .
     *
     * @param key 键
     * @param start 包含的起始位置
     * @param end 包含的结束位置
     */
    default void trimList(String key, long start, long end) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Removes the first count occurrences of value from the list stored at key.
     *
     * @param key 键
     * @param count count > 0: 从头到尾删除|count|个值为value的元素
     *              count < 0: 从尾到头删除|count|个值为value的元素
     *              count = 0: 移除所有值为value的元素
     * @param value 待删除值
     * @return 删除元素个数
     */
    default <T extends Serializable> Long removeList(String key, long count, T value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get element at index form list at key.
     *
     * @param key 键
     * @param index list下标
     * @return list[index]所在的元素
     */
    default <T extends Serializable> T indexList(String key, long index) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Removes and returns first element in list stored at key.
     *
     * @param key 键
     * @return 被移除元素
     */
    default <T extends Serializable> T listLeftPop(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Removes and returns first element in list stored at key.
     *
     * @param key 键
     * @return 被移除字符串
     */
    default String listLeftPopString(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Removes and returns first element from lists stored at keys (see: lPop(byte[])).
     * Blocks connection until element available or timeout reached.
     *
     * @param key 键
     * @param timeout 超时时间
     * @param timeunit 时间单位
     * @return 被移除元素
     */
    default <T extends Serializable> T listLeftPop(String key, long timeout, TimeUnit timeunit) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Removes and returns last element in list stored at key.
     *
     * @param key 键
     * @return 被移除元素
     */
    default <T extends Serializable> T listRightPop(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Removes and returns last string element in list stored at key.
     *
     * @param key 键
     * @return 被移除字符串
     */
    default String listRightPopString(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Removes and returns last element from lists stored at keys (see: rPop(byte[])).
     * Blocks connection until element available or timeout reached.
     *
     * @param key 键
     * @param timeout 超时时间
     * @param timeunit 时间单位
     * @return 被移除元素
     */
    default <T extends Serializable> T listRightPop(String key, long timeout, TimeUnit timeunit) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get the length of a list .
     *
     * @param key 键
     * @return list列表大小
     */
    default Long sizeList(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Add given values to set at key.
     *
     * @param key 键
     * @param values set值
     * @return 成功添加条数
     */
    default Long addSetValues(String key, Serializable...values) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Check if set at key contains value.
     *
     * @param key 键
     * @param value set值
     * @return true-存在 false-不存在
     */
    default <T extends Serializable> Boolean isSetContains(String key, T value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get all elements of set at key.
     *
     * @param key 键
     * @return set内所有值
     */
    default <T extends Serializable> Set<T> getSetAll(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Move value from srcKey to destKey.
     *
     * @param srcKey 原始键
     * @param value set值
     * @param destKey 目标键
     * @return true-成功 false-失败
     */
    default <T extends Serializable> Boolean moveSetValue(String srcKey, T value, String destKey) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get random element from set at key.
     *
     * @param key 键
     * @return set值
     */
    default <T extends Serializable> T randomSetValue(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get count random elements from set at key.
     *
     * @param key 键
     * @param count 获取数量
     * @return set值
     */
    default <T extends Serializable> Set<T> distinctRandomSetValues(String key, long count) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get count random elements from set at key.
     *
     * @param key 键
     * @param count 获取数量
     * @return set值
     */
    default <T extends Serializable> List<T> randomSetValues(String key, long count) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 比较两个集合的差集并返回
     *
     * @param key 键
     * @param compareKey 待比较键
     * @return 差集
     */
    default <T extends Serializable> Set<T> differenceSet(String key, String compareKey) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Remove given values from set at key
     * and return the number of removed elements.
     *
     * @param key 键
     * @param values 待删除set
     * @return 成功删除条数
     */
    default Long removeSetValues(String key, Serializable...values) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Remove and return a random member from set at key.
     *
     * @param key 键
     * @return 被删除的set值
     */
    default <T extends Serializable> T popSetValue(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get size of set at key.
     *
     * @param key 键
     * @return set集大小
     */
    default Long sizeSet(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Add value to a sorted set at key, or update its score if it already exists.
     *
     * @param key 键
     * @param value zset值
     * @param score 评分
     * @return true-成功 false-失败
     */
    default <T extends Serializable> Boolean addZSet(String key, T value, double score) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Increment the score of element with value in sorted set by increment.
     *
     * @param key 键
     * @param value zset值
     * @param delta 评分
     * @return 自增后评分
     */
    default <T extends Serializable> Double incrementZSetScore(String key, T value, double delta) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Determine the index of element with value in a sorted set.
     *
     * @param key 键
     * @param o zset成员
     * @return 该成员在zset中排名
     */
    default <T extends Serializable> Long rankZSet(String key, T o) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Redis reverseZSetRange
     *
     * @param key 键
     * @param start 包含的起始位置
     * @param end 包含的结束位置
     * @return 成员的位置按score值递减(从高到低)来排列。
     *         具有相同score值的成员按字典序的反序排列。
     */
    default <T extends Serializable> Set<T> reverseZSetRange(String key, Long start, Long end) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Redis reverseZSetRangeWithScore
     *
     * @param key 键
     * @param start 包含的起始位置
     * @param end 包含的结束位置
     * @return 成员的位置按score值递减(从高到低)来排列。
     *         具有相同score值的成员按字典序的反序排列。
     */
    default <T extends Serializable> Set<ZSetOperations.TypedTuple<T>> reverseZSetRangeWithScore(String key, Long start, Long end) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Redis scanZSet
     *
     * @param key 键
     * @param options 遍历配置
     * @return 游标
     */
    default <T extends Serializable> Cursor<ZSetOperations.TypedTuple<T>> scanZSet(String key, ScanOptions options) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Determine the index of element with value in a
     * sorted set when scored high to low.
     *
     * @param key 键
     * @param o zset成员
     * @return 该成员在zset中排名
     */
    default <T extends Serializable> Long reverseZSetRank(String key, T o) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Get the score of element with value from sorted set with key key.
     *
     * @param key 键
     * @param o zset成员
     * @return 该成员在zset中评分
     */
    default <T extends Serializable> Double scoreZSet(String key, T o) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Remove values from sorted set. Return number of removed elements.
     *
     * @param key 键
     * @param values zset成员
     * @return 成功删除数
     */
    default Long removeZSet(String key, Object...values) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Remove elements in range between begin and end from sorted set with key.
     *
     * @param key 键
     * @param start 包含的起始位置
     * @param end 包含的结束位置
     * @return 成功删除数
     */
    default Long removeZSetRange(String key, long start, long end) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Remove elements with scores between min and max from sorted set with key.
     *
     * @param key 键
     * @param min 包含的最低评分
     * @param max 包含的最高评分
     * @return 成功删除数
     */
    default Long removeZSetRangeByScore(String key, double min, double max) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Count number of elements within sorted set with scores between min and max.
     *
     * @param key 键
     * @param min 包含的最低评分
     * @param max 包含的最高评分
     * @return 评分内的元素个数
     */
    default Long countZSet(String key, double min, double max) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * Count number of elements within sorted set.
     *
     * @param key 键
     * @return 有序集成员个数
     */
    default Long zsetCard(String key) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 坐标添加值
     *
     * @param key 键
     * @param longitude 经度
     * @param latitude 纬度
     * @param value 值
     */
    default <T extends Serializable> void addGeoData(String key, double longitude, double latitude, T value) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

    /**
     * 获取坐标范围内符合数据
     *
     * @param key 键
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 半径
     * @param distUnit 距离单位
     * @return 数据映射
     */
    default <T extends Serializable> Map<T, Double> radiusWithDistance(String key, double longitude, double latitude, double radius, DistanceUnit distUnit) {
        throw new UnsupportedOperationException("该方法当前未实现");
    }

}
