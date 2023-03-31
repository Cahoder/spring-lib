package org.spring.lib.distributedlock.utils;

import java.lang.reflect.Field;

/**
 * 反射操作工具类
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/8
 **/
public class ReflectUtils {

    private ReflectUtils() {}

    /**
     * 自底向上寻找field成员属性对象
     * @param object 被反射对象
     * @param fieldName 成员属性名称
     */
    public static Object getFieldObject(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            Field field;
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                continue;
            }
            Object fieldObj;
            if (field.isAccessible()) {
                try {
                    fieldObj = field.get(object);
                } catch (Exception e) {
                    return null;
                }
            } else {
                try {
                    field.setAccessible(true);
                    fieldObj = field.get(object);
                    field.setAccessible(false);
                } catch (Exception e) {
                    return null;
                }
            }
            return fieldObj;
        }
        return null;
    }

    /**
     * 通过表达式获取field成员属性对象
     * 要求表达式形如 eg: company.department.group...
     * 如果表达式形如 eg: company.department 则等同于{@linkplain #getFieldObject(Object, String)}
     * 如果表达式形如 eg: company 则返回参数object本身
     * @param object 被反射对象
     * @param fieldExpression 成员属性表达式
     */
    public static Object getSubFieldObject(Object object, String fieldExpression) {
        String[] subFields = fieldExpression.split("\\.");
        if(subFields.length == 1) {
            return object;
        }
        Object actualFieldObj = object;
        for (int i = 1; i < subFields.length && actualFieldObj != null; i++) {
            actualFieldObj = ReflectUtils.getFieldObject(actualFieldObj, subFields[i]);
        }
        return actualFieldObj;
    }

}
