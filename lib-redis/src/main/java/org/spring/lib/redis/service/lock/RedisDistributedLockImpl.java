package org.spring.lib.redis.service.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.lib.distributedlock.pojo.DistributedLock;
import org.spring.lib.distributedlock.service.DistributedLockService;
import org.spring.lib.distributedlock.utils.LockUtils;
import org.spring.lib.distributedlock.utils.TimeoutUtils;
import org.spring.lib.redis.service.RedisService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static org.spring.lib.distributedlock.utils.LockUtils.FAILURE;
import static org.spring.lib.distributedlock.utils.LockUtils.SUCCESS;
import static org.spring.lib.redis.constant.LockConstant.*;

/**
 * redis分布式锁操作实现
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/9
 **/
public class RedisDistributedLockImpl implements DistributedLockService {

    private static final Logger log = LoggerFactory.getLogger(RedisDistributedLockImpl.class);

    @Resource
    private RedisService redisService;
    @Resource
    private BlockingLockService blockingLockService;

    private static final Timer timer = new Timer();
    private static final ThreadLocal<Map<String, ExpiredKeyRenewalTask>>
            expiredKeyRenewalTaskThreadLocal = ThreadLocal.withInitial(HashMap::new);

    @Override
    public boolean tryAcquire(DistributedLock lock) {
        String key = LockUtils.formatLockKey(lock);
        String reenterKey = LockUtils.formatToReenterKey(key);
        if (LockUtils.isOwner(key)) {
            // 增加重入次数并续期
            Long reenterCount = redisService.increment(reenterKey);
            redisService.setExpire(reenterKey, lock.getExpire(), TimeUnit.SECONDS);
            log.debug("[REDIS分布式锁] 线程{}对{}重入加锁 最新重入次数:{}", Thread.currentThread().getName(), key, reenterCount);
            return true;
        }
        Set<String> renewSet = new HashSet<>();
        boolean lockResult = TimeoutUtils.isNonBlocking(lock.getTimeout()) ?
                doNonBlockingGetLock(key, lock) :
                blockingLockService.doBlockingGetLock(key, lock, renewSet);
        // 拿到锁后要进行续期
        if (lockResult) {
            renewSet.add(key);
            renewSet.add(reenterKey);
            redisService.increment(reenterKey);
            redisService.setExpire(reenterKey, lock.getExpire(), TimeUnit.SECONDS);
            prolongKeyRenewal(key, renewSet, lock.getExpire());
            LockUtils.addLockResult(key, Boolean.TRUE);
        }
        return lockResult;
    }

    @Override
    public boolean tryRelease(DistributedLock lock) {
        String key = LockUtils.formatLockKey(lock);
        boolean unlockResult = false;
        if (LockUtils.isOwner(key)) {
            log.debug("[REDIS分布式锁-阻塞] 线程{}释放{}的锁. 当前线程锁的上下文{} 开始", Thread.currentThread().getName(), key, LockUtils.getAllLockResult());
            String reenterKey = LockUtils.formatToReenterKey(key);
            Long reenterCount = redisService.decrement(reenterKey);
            log.debug("[REDIS分布式锁] 线程{}对{}重入解锁 剩余重入次数：{}", Thread.currentThread().getName(), key, reenterCount);
            if(reenterCount > 0) {
                return true;
            }
            try {
                unlockResult = TimeoutUtils.isNonBlocking(lock.getTimeout()) ?
                        doNonBlockingReleaseLock(key, lock) :
                        blockingLockService.doBlockingReleaseLock(key, lock);
            } finally {
                // 释放锁后取消续期
                cancelKeyRenewal(key);
            }
        }
        return unlockResult;
    }

    /**
     * 非阻塞方式获取锁
     * @param key 锁唯一标识
     * @param lock 锁实体
     */
    private boolean doNonBlockingGetLock(String key, DistributedLock lock) {
        boolean lockResult = redisService.set(key, LOCK_FLAG_VALUE, SET_IF_NOT_EXIST, SET_MILLISECONDS_EXPIRE_TIME, lock.getExpire() * 1000L);
        log.debug("[REDIS分布式锁-非阻塞] 线程{}对{}加锁:{}. 当前线程锁的上下文{}", Thread.currentThread().getName(), lock, lockResult ? SUCCESS : FAILURE, LockUtils.getAllLockResult());
        return lockResult;
    }

    /**
     * 非阻塞方式释放锁
     * @param key 锁唯一标识
     * @param lock 锁实体
     */
    private boolean doNonBlockingReleaseLock(String key, DistributedLock lock) {
        boolean unlockResult = redisService.remove(key);
        log.debug("[REDIS分布式锁-非阻塞] 线程{}释放{}的锁:{}. 当前线程锁的上下文{}", Thread.currentThread().getName(), lock, unlockResult ? SUCCESS : FAILURE, LockUtils.getAllLockResult());
        return unlockResult;
    }

    /**
     * 定时锁续期任务
     */
    private void prolongKeyRenewal(String key, Set<String>renewSet, long timeout) {
        ExpiredKeyRenewalTask renewalTask = new ExpiredKeyRenewalTask(key, renewSet, timeout);
        expiredKeyRenewalTaskThreadLocal.get().put(key, renewalTask);
        timer.schedule(renewalTask, 100L, 5 * 1000L);
    }

    /**
     * 取消锁续期任务
     */
    private void cancelKeyRenewal(String key) {
        ExpiredKeyRenewalTask expiredKeyRenewalTask = expiredKeyRenewalTaskThreadLocal.get().get(key);
        if (expiredKeyRenewalTask != null) {
            try {
                log.debug("cancelKeyRenewal expiredKeyRenewalTask={}", expiredKeyRenewalTask);
                expiredKeyRenewalTask.cancel();
            } catch (Exception e) {
                log.error("cancelKeyRenewal error, expiredKeyRenewalTask={}", expiredKeyRenewalTask, e);
            } finally {
                expiredKeyRenewalTaskThreadLocal.get().remove(key);
                LockUtils.deleteLockResult(key);
            }
        }
    }

    private class ExpiredKeyRenewalTask extends TimerTask {
        private final String key;
        private final Set<String> renewSet;
        private final long timeout;
        private final long startTime;

        public ExpiredKeyRenewalTask(String key, Set<String> renewSet, long timeout) {
            this.key = key;
            this.renewSet = renewSet;
            this.timeout = timeout;
            this.startTime = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "ExpiredKeyRenewalTask{" +
                    "key='" + key + '\'' +
                    ", renewSet=" + renewSet +
                    ", timeout=" + timeout +
                    ", startTime=" + startTime +
                    '}';
        }

        @Override
        public void run() {
            try {
                long elapsedTime = System.currentTimeMillis() - startTime;
                // 续期时间大于过期时间
                if (elapsedTime > TimeUnit.SECONDS.toMillis(timeout)) {
                    log.warn("renewal key, key={}, timeout={}s, elapsedTime={}ms you should focus the expiredTime config!", key, timeout, elapsedTime);
                }
                for(String renewKey : renewSet) {
                    redisService.setExpire(renewKey, timeout, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                log.error("renewal key, key={}, timeout={}s", key, timeout, e);
            }
            log.debug("renewal key, thread={}, key={}, timeout={}s", Thread.currentThread().getName(), key, timeout);
        }
    }

}
