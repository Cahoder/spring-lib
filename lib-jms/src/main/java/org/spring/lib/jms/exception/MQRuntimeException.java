package org.spring.lib.jms.exception;

/**
 * MQ运行异常
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/2/16
 **/
public class MQRuntimeException extends Exception {

    private static final long serialVersionUID = 7104509331186785928L;

    public MQRuntimeException() {}

    public MQRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MQRuntimeException(String message) {
        super(message);
    }

    public MQRuntimeException(Throwable cause) {
        super(cause);
    }

}
