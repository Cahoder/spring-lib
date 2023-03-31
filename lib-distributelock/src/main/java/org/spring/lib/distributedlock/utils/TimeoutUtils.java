package org.spring.lib.distributedlock.utils;

/**
 * 默认超时工具类
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/7
 **/
public class TimeoutUtils {

    private TimeoutUtils() {}

    /**
     * 默认缓存1分钟-仅针对redis
     */
    public static final int LOCKED_SECONDS = 60;

    /**
     * 非阻塞锁的默认标识
     */
    public static final int NON_BLOCKING = 0;

    /**
     * 永久阻塞锁默认标识
     */
    public static final int PERPETUAL_BLOCKING = -1;

    /**
     * 判断是否阻塞锁
     * @param timeout 阻塞等待超时时间
     */
    public static boolean isBlocking(int timeout) {
        return timeout > 0 || isPerpetualBlocking(timeout);
    }

    /**
     * 判断是否非阻塞锁
     * @param timeout 阻塞等待超时时间
     */
    public static boolean isNonBlocking(int timeout) {
        return NON_BLOCKING == timeout;
    }

    /**
     * 判断是否永久阻塞锁
     * @param timeout 阻塞等待超时时间
     */
    public static boolean isPerpetualBlocking(int timeout) {
        return PERPETUAL_BLOCKING == timeout;
    }

}
