package org.spring.lib.distributedlock.exception;

/**
 * 锁参数获取异常
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/7
 **/
public class LockParamException extends RuntimeException {

    private static final long serialVersionUID = -6457504708833027809L;

    public LockParamException(String message) {
        super(message);
    }

    public LockParamException(Throwable cause) {
        super(cause);
    }

}
