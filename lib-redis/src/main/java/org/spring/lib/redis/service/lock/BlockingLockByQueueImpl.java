package org.spring.lib.redis.service.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.lib.distributedlock.pojo.DistributedLock;
import org.spring.lib.distributedlock.utils.LockUtils;
import org.spring.lib.redis.service.RedisService;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.spring.lib.distributedlock.utils.LockUtils.FAILURE;
import static org.spring.lib.distributedlock.utils.LockUtils.SUCCESS;
import static org.spring.lib.redis.constant.LockConstant.LOCK_FLAG_VALUE;
import static org.spring.lib.redis.constant.LockConstant.SET_IF_NOT_EXIST;
import static org.spring.lib.redis.constant.LockConstant.SET_MILLISECONDS_EXPIRE_TIME;

/**
 * 队列方式实现阻塞锁
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/9
 **/
public class BlockingLockByQueueImpl implements BlockingLockService {

    private static final Logger log = LoggerFactory.getLogger(BlockingLockByQueueImpl.class);

    @Resource
    private RedisService redisService;

    @Override
    public boolean doBlockingGetLock(String key, DistributedLock lock, Set<String> renewSet) {
        boolean lockResult = redisService.set(key, LOCK_FLAG_VALUE, SET_IF_NOT_EXIST, SET_MILLISECONDS_EXPIRE_TIME, lock.getExpire() * 1000L);
        if (!lockResult) {
            //在此阻塞等锁
            String blockingLock = redisService.listRightPop(appendListToKey(key), lock.getTimeout() + 1L, TimeUnit.SECONDS);
            lockResult = blockingLock != null && !blockingLock.isEmpty();
            log.debug("[REDIS分布式锁-获取] 线程{}对{}加锁:{}.通过brpop获取到锁", Thread.currentThread().getName(), key, lockResult ? SUCCESS : FAILURE);
        } else {
            // 获取到锁时,要保证list为空
            log.debug("[REDIS分布式锁-获取] 线程{}对{}加锁:{}.通过setIfNotExist获取到锁 ", Thread.currentThread().getName(), key, SUCCESS);
            redisService.remove(appendListToKey(key));
        }
        if(lockResult) {
            renewSet.add(appendListToKey(key));
        }
        log.debug("[REDIS分布式锁-阻塞] 线程{}对{}加锁:{}. 当前线程锁的上下文{}", Thread.currentThread().getName(), key, lockResult ? SUCCESS : FAILURE, LockUtils.getAllLockResult());
        return lockResult;
    }

    @Override
    public boolean doBlockingReleaseLock(String key, DistributedLock lock) {
        boolean unlockResult;
        // 释放锁前，需设置最后一次expire，不然可能在服务宕机之后一直存留。
        try {
            setKeyExpire(key, lock.getExpire());
            unlockResult = redisService.listRightPush(appendListToKey(key), LOCK_FLAG_VALUE) > 0;
            setKeyExpire(appendListToKey(key), lock.getExpire());
        } finally {
            setKeyExpire(key, lock.getExpire());
        }
        log.debug("[REDIS分布式锁-阻塞] 线程{}释放{}的锁:{}. 当前线程锁的上下文{} 完成", Thread.currentThread().getName(), key, unlockResult ? SUCCESS : FAILURE, LockUtils.getAllLockResult());
        return unlockResult;
    }

    private String appendListToKey(String key) {
        return String.format("%s_list", key);
    }

    private void setKeyExpire(String key, long timeout) {
        redisService.setExpire(appendListToKey(key), timeout, TimeUnit.SECONDS);
        redisService.setExpire(key, timeout, TimeUnit.SECONDS);
    }

}
