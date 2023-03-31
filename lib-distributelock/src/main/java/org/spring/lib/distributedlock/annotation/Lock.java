package org.spring.lib.distributedlock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 锁资源标识
 * 反射从被标识锁资源中获取真正的锁参数
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/7
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Lock {

    /**
     * {@linkplain DistributeLock#locks()}
     * 1. 基本类型
     * <pre>@DistributeLock(locks={"name"})
     * public void demo1(@Lock("name") String name) {
     *
     * }</pre>
     * 2. 引用类型
     * <pre>@DistributeLock(locks={"user.name"})
     * public void demo2(@Lock("user") User user) {
     *
     * }</pre>
     * 3. 复合型
     * <pre>@DistributeLock(locks={"user.name", "other"})
     * public void demo3(@Lock("user") User user, @Lock("other") String other) {
     *
     * }</pre>
     * @return 锁资源标识-支持基本类型和引用类型
     */
    String value();

}
