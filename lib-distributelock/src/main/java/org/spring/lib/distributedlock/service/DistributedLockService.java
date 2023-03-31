package org.spring.lib.distributedlock.service;

import org.spring.lib.distributedlock.pojo.DistributedLock;

/**
 * 分布式锁操作
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/8
 **/
public interface DistributedLockService {

    /**
     * 尝试获取锁
     * @return true成功 false失败
     * @param lock 分布式锁实体
     */
    boolean tryAcquire(DistributedLock lock);

    /**
     * 尝试释放锁
     * @return true成功 false失败
     * @param lock 分布式锁实体
     */
    boolean tryRelease(DistributedLock lock);

}
