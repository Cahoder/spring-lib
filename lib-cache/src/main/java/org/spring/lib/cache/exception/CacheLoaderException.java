package org.spring.lib.cache.exception;

/**
 * 缓存加载异常
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/30
 **/
public class CacheLoaderException extends RuntimeException {

    private static final long serialVersionUID = -4404968524509958136L;

    public CacheLoaderException() {
    }

    public CacheLoaderException(String message) {
        super(message);
    }

    public CacheLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheLoaderException(Throwable cause) {
        super(cause);
    }

}
