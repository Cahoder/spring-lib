package org.spring.lib.distributedlock.annotation;

import org.spring.lib.distributedlock.utils.TimeoutUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布锁注解配置
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/7
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributeLock {

    String DEFAULT_SERVICE_ID = "spring.application.name";

    /**
     * 项目唯一标识<p>
     * 用于项目内部私有锁使用区分<p>
     * 如需多个项目共享锁,可协商命名亦或者为"";
     * @return 默认微服务名
     */
    String serviceId() default DEFAULT_SERVICE_ID;

    /**
     * @return 定义锁的业务模块
     */
    String moduleId();

    /**
     * 指定锁的参数: 多个参数间拼接成一个字符串作为真正的锁<p>
     * 亦支持从锁资源中获取锁参数: {@linkplain Lock#value()}
     * @return eg: locks={"chd", "123456"} 拼接之后的锁名称为：chd_123456
     */
    String[] locks();

    /**
     * 锁等待超时时间[-1 <= 'timeout' <= expire]: 单位秒<p>
     * 如果想要阻塞锁的话,可以指定阻塞时间,也可以指定为永久阻塞
     * @see TimeoutUtils#NON_BLOCKING 非阻塞锁
     * @see TimeoutUtils#PERPETUAL_BLOCKING	永久阻塞锁
     * @return 默认是非阻塞锁,无等待时间
     */
    int timeout() default TimeoutUtils.NON_BLOCKING;

    /**
     * redis缓存失效时间
     * 要求不能大于超时时间 {@linkplain #timeout()}
     * @return 默认1分钟,此配置对zk无作用.
     */
    int expire() default TimeoutUtils.LOCKED_SECONDS;

}
