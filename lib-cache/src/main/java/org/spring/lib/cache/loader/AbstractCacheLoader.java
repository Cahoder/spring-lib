package org.spring.lib.cache.loader;

import com.github.rholder.retry.AttemptTimeLimiters;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.spring.lib.cache.exception.CacheLoaderException;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 抽象缓存加载器
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/3/31
 **/
public abstract class AbstractCacheLoader implements CacheLoader {

    private static final int DEFAULT_ATTEMPT_TIME = 1;

    /**
     * 执行缓存加载
     * @return true|false
     */
    protected abstract boolean doLoadCache();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 是否存在多级缓存
     * @return true|false
     */
    public boolean hasNext() {
        return false;
    }

    /**
     * 获取下级缓存加载器
     * @return 下级缓存加载器
     */
    public CacheLoader getNextLoader() {
        throw new UnsupportedOperationException("当前未实现多级缓存");
    }

    /**
     * 加载失败最大重试次数
     * @return 默认要求≥1
     */
    protected int maxRetryTime() {
        return DEFAULT_ATTEMPT_TIME;
    }

    @Override
    public final void loadCache() {
        try {
            Retryer<Boolean> retryDoLoad = RetryerBuilder.<Boolean>newBuilder()
                    .retryIfException()
                    .retryIfResult(result -> Objects.equals(result, false))
                    //等待策略: 每次请求间隔2s
                    .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                    //停止策略: 尝试请求次数
                    .withStopStrategy(StopStrategies.stopAfterAttempt(maxRetryTime()))
                    //时间限制: 某次请求不得超过1min
                    .withAttemptTimeLimiter(AttemptTimeLimiters.fixedTimeLimit(1, TimeUnit.MINUTES))
                    .build();
            retryDoLoad.call(this::doLoadCache);
        } catch (ExecutionException e) {
            throw new CacheLoaderException("执行加载缓存任务失败: " + getName(), e);
        } catch (RetryException e) {
            throw new CacheLoaderException("执行加载缓存任务重试后失败: " + getName(), e);
        }
    }

    /**
     * 初始化加载缓存
     * 不包含重试机制
     */
    public final void initLoadCache() {
        if (!doLoadCache()) {
            throw new CacheLoaderException("初始化加载缓存任务失败: " + getName());
        }
    }

}
