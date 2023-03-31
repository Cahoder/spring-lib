package org.spring.lib.distributedlock.utils;

import org.spring.lib.distributedlock.pojo.DistributedLock;

import java.util.HashMap;
import java.util.Map;

/**
 * 锁工具类
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/14
 **/
public class LockUtils {

    private LockUtils() {

    }

    private static final String FORMAT = "%s_%s_%s";
    private static final String COMMON = "COMMON";
    private static final String REENTER_KEY_PATTERN ="enter_cnt_%s";
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";

    /**
     * 保存锁记录：支持多重锁（锁A里面可能还有锁B、锁C）
     */
    private static final ThreadLocal<Map<String, Boolean>> LOCK_THREAD_LOCAL = ThreadLocal.withInitial(HashMap::new);

    /**
     * 添加锁的结果
     *
     * @param key 锁唯一标识
     * @param isLockSuccess 锁结果：成功或失败
     */
    public static void addLockResult(String key, Boolean isLockSuccess) {
        LOCK_THREAD_LOCAL.get().put(key, isLockSuccess);
    }

    /**
     * 获取当前线程当前锁的结果.
     *
     * @param key 锁唯一标识
     * @return 锁结果：成功或失败
     */
    public static Boolean getLockResult(String key) {
        return LOCK_THREAD_LOCAL.get().get(key);
    }

    /**
     * 锁释放之后，需要删除锁记录
     *
     * @param key 锁唯一标识
     */
    public static void deleteLockResult(String key) {
        LOCK_THREAD_LOCAL.get().remove(key);
    }

    /**
     * 清除所有锁结果记录
     */
    public static void clearLockResult() {
        LOCK_THREAD_LOCAL.remove();
    }

    /**
     * 获取当前线程所有的锁结果.
     * @return 锁结果列表
     */
    public static Map<String, Boolean> getAllLockResult() {
        return LOCK_THREAD_LOCAL.get();
    }

    /**
     * 生成锁唯一标识
     * @param lock 锁实体
     * @return 锁唯一标识
     */
    public static String formatLockKey(DistributedLock lock) {
        if ("".equals(lock.getLockBean().getServiceId())) {
            return String.format(FORMAT, COMMON, lock.getLockBean().getModule(), lock.getLockBean().getLock());
        }
        return String.format(FORMAT, lock.getLockBean().getServiceId(), lock.getLockBean().getModule(), lock.getLockBean().getLock());
    }

    /**
     * 装饰成可重入锁唯一标识
     * @param key 锁唯一标识
     * @return 可重入锁唯一标识
     */
    public static String formatToReenterKey(String key) {
        return String.format(REENTER_KEY_PATTERN, key);
    }

    /**
     * 判断当前是否锁持有者
     * @param key 锁唯一标识
     * @return true-是 false-否
     */
    public static boolean isOwner(String key) {
        Boolean result = LockUtils.getLockResult(key);
        return result != null && result;
    }

}
