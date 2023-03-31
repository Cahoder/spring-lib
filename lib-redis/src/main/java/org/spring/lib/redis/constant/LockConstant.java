package org.spring.lib.redis.constant;

/**
 * 锁常量
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/14
 **/
public class LockConstant {

    private LockConstant() {

    }

    public static final String SET_IF_NOT_EXIST = "NX";

    public static final String SET_IF_EXIST = "XX";

    public static final String SET_MILLISECONDS_EXPIRE_TIME = "PX";

    public static final String SET_SECONDS_EXPIRE_TIME = "EX";

    public static final String LOCK_FLAG_VALUE = "1";

}
