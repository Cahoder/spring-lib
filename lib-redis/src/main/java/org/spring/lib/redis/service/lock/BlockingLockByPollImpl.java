package org.spring.lib.redis.service.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.lib.distributedlock.pojo.DistributedLock;
import org.spring.lib.distributedlock.utils.LockUtils;
import org.spring.lib.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.Set;

import static org.spring.lib.distributedlock.utils.LockUtils.FAILURE;
import static org.spring.lib.distributedlock.utils.LockUtils.SUCCESS;
import static org.spring.lib.redis.constant.LockConstant.LOCK_FLAG_VALUE;
import static org.spring.lib.redis.constant.LockConstant.SET_IF_NOT_EXIST;
import static org.spring.lib.redis.constant.LockConstant.SET_MILLISECONDS_EXPIRE_TIME;

/**
 * 轮询方式实现阻塞锁
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/9
 **/
public class BlockingLockByPollImpl implements BlockingLockService {

    private static final Logger log = LoggerFactory.getLogger(BlockingLockByPollImpl.class);

    @Resource
    private RedisService redisService;
    @Value("${lock.block.wait.millis:60}")
    private int lockBlockWaitMillis;

    @Override
    public boolean doBlockingGetLock(String key, DistributedLock lock, Set<String> renewSet) {
        int startTime = (int) (System.currentTimeMillis() / 1000);
        int timeout = lock.getTimeout();
        boolean lockResult = redisService.set(key, LOCK_FLAG_VALUE, SET_IF_NOT_EXIST, SET_MILLISECONDS_EXPIRE_TIME, lock.getExpire() * 1000L);
        while (!lockResult) {
            //timeout=-1时才是永久阻塞锁
            if (timeout > 0) {
                int endTime = (int) (System.currentTimeMillis() / 1000);
                if (endTime - startTime >= timeout) {
                    lockResult = false;
                    break;
                }
            }
            sleepRandom();
            lockResult = redisService.set(key, LOCK_FLAG_VALUE, SET_IF_NOT_EXIST, SET_MILLISECONDS_EXPIRE_TIME, lock.getExpire() * 1000L);
        }
        log.debug("[REDIS分布式锁-阻塞] 线程{}对{}加锁:{}. 当前线程锁的上下文{}", Thread.currentThread().getName(), key, lockResult ? SUCCESS : FAILURE, LockUtils.getAllLockResult());
        return lockResult;
    }

    @Override
    public boolean doBlockingReleaseLock(String key, DistributedLock lock) {
        boolean unlockResult = redisService.remove(key);
        log.debug("[REDIS分布式锁-阻塞] 线程{}释放{}的锁:{}. 当前线程锁的上下文{} 完成", Thread.currentThread().getName(), key, unlockResult ? SUCCESS : FAILURE, LockUtils.getAllLockResult());
        return unlockResult;
    }

    /**
     * 随机休若干毫秒钟之内任意毫秒数
     */
    public void sleepRandom() {
        long time = (long) (Math.random() * lockBlockWaitMillis);
        try {
            Thread.sleep(time);
            log.debug("lock sleep time :{} ",time);
        } catch (InterruptedException ignored) {
            log.error("lock sleep time InterruptedException:{} ",time);
            Thread.currentThread().interrupt();
        }
    }

}
