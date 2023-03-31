package org.spring.lib.redis.service.lock;

import org.spring.lib.distributedlock.pojo.DistributedLock;

import java.util.Set;

/**
 * 阻塞等待锁服务
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/9
 **/
public interface BlockingLockService {

    boolean doBlockingGetLock(String key, DistributedLock lock, Set<String> renewSet);

    boolean doBlockingReleaseLock(String key, DistributedLock lock);

}
