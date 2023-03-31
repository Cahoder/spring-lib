package org.spring.lib.jms.exception;

/**
 * MQ异常
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/2/16
 **/
public class MQException extends Exception {

    private static final long serialVersionUID = -3712206922243793322L;

    public MQException() {}

    public MQException(String message, Throwable cause) {
        super(message, cause);
    }

    public MQException(String message) {
        super(message);
    }

    public MQException(Throwable cause) {
        super(cause);
    }

}
