package org.spring.lib.cache.support;

import org.spring.lib.cache.CacheFacade;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.util.concurrent.Callable;

/**
 * 抽象缓存实现适配器
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/30
 **/
public abstract class AbstractCacheFacadeAdapter extends AbstractValueAdaptingCache implements CacheFacade {

    /**
     * Create an {@code AbstractValueAdaptingCache} with the given setting.
     * @param allowNullValues whether to allow for {@code null} values
     */
    protected AbstractCacheFacadeAdapter(boolean allowNullValues) {
        super(allowNullValues);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        throw new UnsupportedOperationException("当前不支持此获取操作");
    }

    @Override
    public void put(Object key, Object value) {
        throw new UnsupportedOperationException("当前不支持此添加操作");
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        throw new UnsupportedOperationException("当前不支持此添加操作");
    }

    @Override
    public void evict(Object key) {
        throw new UnsupportedOperationException("当前不支持此删除操作");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("当前不支持此清空操作");
    }

}
