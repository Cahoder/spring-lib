package org.spring.lib.distributedlock.exception;

/**
 * 分布式锁获取异常
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/7
 **/
public class DistributedLockException extends RuntimeException {

    private static final long serialVersionUID = 8316449228265383970L;

    public DistributedLockException(String message) {
        super(message);
    }

}
