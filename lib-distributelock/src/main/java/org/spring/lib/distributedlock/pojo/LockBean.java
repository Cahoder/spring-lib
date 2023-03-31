package org.spring.lib.distributedlock.pojo;

import java.io.Serializable;

/**
 * 分布式锁空间
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/8
 **/
public class LockBean implements Serializable {

    private static final long serialVersionUID = 8078090504446557476L;

    /**
     * 项目唯一标识<p>
     * 用于项目内部私有锁使用区分<p>
     * 如需多个项目共享锁,可协商命名亦或者为"";
     */
    private String serviceId;

    /**
     * 定义锁的业务模块
     */
    private String module;

    /**
     * <pre>
     * 定义具体锁的资源
     * 1.宽泛锁：可以是一个常量
     * 2.细化锁：可以细化到锁某一个唯一标识，比如锁"用户id"
     * </pre>
     */
    private String lock;

    public LockBean(String serviceId, String module, String lock) {
        this.serviceId = serviceId;
        this.module = module;
        this.lock = lock;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }
}
