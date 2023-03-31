package org.spring.lib.distributedlock.pojo;

import static org.spring.lib.distributedlock.utils.TimeoutUtils.LOCKED_SECONDS;
import static org.spring.lib.distributedlock.utils.TimeoutUtils.NON_BLOCKING;

import java.io.Serializable;

/**
 * 分布式锁
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/8
 **/
public class DistributedLock implements Serializable {

    private static final String DEFAULT_SERVICE_ID = "";
    private static final long serialVersionUID = 6605569584173307035L;

    /**
     * 锁的实体
     */
    private LockBean lockBean;

    /**
     * 锁过期时间: 秒
     * （注：暂只作为redis缓存失效时间，对zk是没有作用的.）
     */
    private int expire;

    /**
     * 当使用阻塞锁时，阻塞超时的时间: 秒
     */
    private int timeout;

    /**
     * 定义公共锁
     * @param module 锁业务模块
     * @param lock 锁标识
     */
    public DistributedLock(String module, String lock) {
        this(module, lock, NON_BLOCKING, LOCKED_SECONDS);
    }

    public DistributedLock(String module, String lock, int timeout, int expire) {
        this(DEFAULT_SERVICE_ID, module, lock, timeout, expire);
    }

    /**
     * 定义私有锁
     * @param serviceId 私有锁项目id
     * @param module 锁业务模块
     * @param lock 锁标识
     */
    public DistributedLock(String serviceId, String module, String lock) {
        this(serviceId, module, lock, NON_BLOCKING, LOCKED_SECONDS);
    }

    public DistributedLock(String serviceId, String module, String lock, int timeout) {
        this(serviceId, module, lock, timeout, LOCKED_SECONDS);
    }

    /**
     * @param serviceId 定义私有锁或者公共锁
     * @param module 定义锁的业务模块
     * @param lock 业务模块下面的锁标识
     * @param expire 缓存过期时间[只对redis有效]
     * @param timeout 阻塞超时时间
     */
    public DistributedLock(String serviceId, String module, String lock, int timeout, int expire) {
        this(new LockBean(serviceId, module, lock), timeout, expire);
    }

    public DistributedLock(LockBean lockBean, int timeout) {
        this(lockBean, timeout, LOCKED_SECONDS);
    }

    public DistributedLock(LockBean lockBean, int timeout, int expire) {
        this.lockBean = lockBean;
        this.timeout = timeout;
        this.expire = expire;
    }

    public LockBean getLockBean() {
        return lockBean;
    }

    public void setLockBean(LockBean lockBean) {
        this.lockBean = lockBean;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
