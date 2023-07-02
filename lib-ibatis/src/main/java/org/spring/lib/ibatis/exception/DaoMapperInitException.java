package org.spring.lib.ibatis.exception;

/**
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/29
 **/
public class DaoMapperInitException extends RuntimeException {

    private static final long serialVersionUID = -4753122783464114942L;

    public DaoMapperInitException(String message) {
        super(message);
    }

    public DaoMapperInitException(String message, Throwable cause) {
        super(message, cause);
    }

}
