package org.spring.lib.distributedlock.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * aop切点信息获取工具类
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/7
 **/
public class JoinPointUtils {

    private JoinPointUtils() {}

    /**
     * 获取进行aop切点的原方法
     */
    public static Method getSourceMethod(JoinPoint joinPoint) throws NoSuchMethodException {
        Method proxyMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return joinPoint.getTarget().getClass().getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
    }

    /**
     * 获取被aop切点方法返回类型
     */
    public static Class getProceedResultClass(JoinPoint joinPoint) throws ClassNotFoundException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        return methodSignature.getReturnType();
    }

    /**
     * 获取进行aop切点的方法名
     */
    public static String getMethodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }

    /**
     * 获取进行aop切点的类型名
     */
    public static String getClassTypeName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringTypeName();
    }

    /**
     * 获取进行aop切点的spring-bean名称
     */
    public static String getBeanName(JoinPoint joinPoint) throws NoSuchMethodException {
        Class<?> declaringClass = JoinPointUtils.getSourceMethod(joinPoint).getDeclaringClass();
        if (declaringClass.getAnnotation(Service.class) != null) {
            return declaringClass.getAnnotation(Service.class).value();
        }
        if (declaringClass.getAnnotation(Component.class) != null) {
            return declaringClass.getAnnotation(Component.class).value();
        }
        if (declaringClass.getAnnotation(Repository.class) != null) {
            return declaringClass.getAnnotation(Repository.class).value();
        }
        if (declaringClass.getAnnotation(Configuration.class) != null) {
            return declaringClass.getAnnotation(Configuration.class).value();
        }
        if (declaringClass.getAnnotation(Controller.class) != null) {
            return declaringClass.getAnnotation(Controller.class).value();
        }
        return null;
    }

}
