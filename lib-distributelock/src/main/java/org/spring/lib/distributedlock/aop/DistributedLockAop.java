package org.spring.lib.distributedlock.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.lib.distributedlock.annotation.DistributeLock;
import org.spring.lib.distributedlock.annotation.Lock;
import org.spring.lib.distributedlock.exception.DistributedLockException;
import org.spring.lib.distributedlock.exception.LockParamException;
import org.spring.lib.distributedlock.pojo.DistributedLock;
import org.spring.lib.distributedlock.service.DistributedLockService;
import org.spring.lib.distributedlock.utils.JoinPointUtils;
import org.spring.lib.distributedlock.utils.ReflectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 分布式锁注解AOP实现
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/7
 **/
@Aspect
@Order(2)
public class DistributedLockAop {

    private static final Logger log = LoggerFactory.getLogger(DistributedLockAop.class);
    private final DistributedLockService distributedLockService;
    @Value("${spring.application.name:}")
    private String defaultServiceId;

    public DistributedLockAop(DistributedLockService distributedLockService) {
        this.distributedLockService = distributedLockService;
    }

    /**
     * 切入点
     */
    @Pointcut("@annotation(org.spring.lib.distributedlock.annotation.DistributeLock)")
    public void distributedLockPointcut() {
        //goto around advice
    }

    /**
     * 切面环绕通知
     * @param joinPoint 静态连接点
     * @throws Throwable 异常
     */
    @Around("distributedLockPointcut()")
    public Object distributedLockAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        String execMethod = JoinPointUtils.getClassTypeName(joinPoint) + "." + JoinPointUtils.getMethodName(joinPoint);
        try {
            //提取分布锁注解信息-组装锁实体
            DistributedLock lock = this.createDistributedLock(joinPoint);
            //取锁-执行业务-还锁
            return this.distributedLockProcess(joinPoint, lock);
        } catch (DistributedLockException e) {
            log.warn("[分布式锁AOP]获取锁失败,执行方法={}", execMethod, e);
            throw e;
        } catch (LockParamException e) {
            log.error("[分布式锁AOP]获取锁参数失败,执行方法={}", execMethod, e);
            throw e;
        } catch (Throwable e) {
            log.error("[分布式锁AOP]系统未知异常,执行方法={}", execMethod, e);
            throw e;
        }
    }

    /**
     * 提取分布锁注解信息-组装锁实体
     * @param joinPoint 静态连接点
     */
    private DistributedLock createDistributedLock(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取方法上锁注解
        Method sourceMethod = JoinPointUtils.getSourceMethod(joinPoint);
        DistributeLock lockAnnotation = sourceMethod.getAnnotation(DistributeLock.class);
        //提取信息-组装锁实体
        String serviceId = DistributeLock.DEFAULT_SERVICE_ID.equals(lockAnnotation.serviceId())
                ? defaultServiceId : lockAnnotation.serviceId();
        String module = lockAnnotation.moduleId();
        if (module.trim().length() == 0) {
            throw new LockParamException("@DistributeLock param moduleId is illegal, it should not be blank");
        }
        String locks = this.joinLockArgs(lockAnnotation.locks(), sourceMethod, joinPoint.getArgs());
        int timeout = lockAnnotation.timeout();
        int expired = lockAnnotation.expire();
        if (timeout < -1) {
            throw new LockParamException("@DistributeLock param timeout is illegal, it should timeout >= -1");
        }
        if (timeout > expired) {
            throw new LockParamException("@DistributeLock param timeout&expired is illegal, it should timeout <= expired");
        }
        return new DistributedLock(serviceId, module, locks, timeout, expired);
    }

    /**
     * 拼接锁参数-作为锁标识
     * @param lockArgs 锁参数列表
     * @param sourceMethod 被切点方法
     * @param methodArgs 被切点方法参数列表
     */
    private String joinLockArgs(String[] lockArgs, Method sourceMethod, Object[] methodArgs) throws LockParamException {
        StringJoiner locks = new StringJoiner("_");
        Parameter[] parameters = sourceMethod.getParameters();
        Map<String, Integer> lockFlagArgsIdxMap = new HashMap<>(methodArgs.length);
        for (int i = 0; i < parameters.length; i++) {
            for (Annotation annotation : parameters[i].getAnnotations()) {
                if (annotation instanceof Lock) {
                    Lock lock = (Lock) annotation;
                    String lockFlag = lock.value().trim();
                    if (lockFlag.length() == 0) {
                        throw new LockParamException("@Lock param value is illegal, it should not be blank");
                    }
                    if (lockFlagArgsIdxMap.containsKey(lockFlag)) {
                        throw new LockParamException("@Lock param value is illegal, it should be unique");
                    }
                    lockFlagArgsIdxMap.put(lockFlag, i);
                }
            }
        }
        for (String lockArg : lockArgs) {
            lockArg = lockArg.trim();
            if (lockArg.length() == 0) {
                throw new LockParamException("@DistributeLock param locks is illegal, it should not contains blank");
            }
            String[] lockArgGrades = lockArg.split("\\.");
            if (!lockFlagArgsIdxMap.containsKey(lockArgGrades[0])) {
                locks.add(lockArg);
            } else {
                Object fieldObject = ReflectUtils.getSubFieldObject(methodArgs[lockFlagArgsIdxMap.get(lockArgGrades[0])], lockArg);
                if (fieldObject == null) {
                    throw new LockParamException("@DistributeLock param locks \""+ lockArg +"\" is illegal, check the expression validity");
                }
                locks.add(fieldObject.toString());
            }
        }
        return locks.toString();
    }

    /**
     * 取锁-执行业务-还锁
     * @param joinPoint 静态连接点
     * @param lock 锁实体
     * @throws Throwable 异常
     */
    private Object distributedLockProcess(ProceedingJoinPoint joinPoint, DistributedLock lock) throws Throwable {
        try {
            if (!distributedLockService.tryAcquire(lock)) {
                log.warn("[分布式锁AOP]获取分布式锁失败,锁实体={}", lock);
                throw new DistributedLockException("[分布式锁]获取失败.");
            }
            return joinPoint.proceed();
        } catch (Throwable e) {
            log.error("[分布式锁AOP]分布式加锁流程失败,锁实体={}", lock);
            throw e;
        } finally {
            distributedLockService.tryRelease(lock);
        }
    }

}
