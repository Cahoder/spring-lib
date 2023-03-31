package org.spring.lib.redis.serializer;

import com.caucho.hessian.io.SerializerFactory;

/**
 * Hessian2序列化工厂类
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/19
 **/
public class Hessian2SerializerFactory extends SerializerFactory {

    private static final Hessian2SerializerFactory SERIALIZER = new Hessian2SerializerFactory();

    private Hessian2SerializerFactory() {

    }

    public static Hessian2SerializerFactory getInstance() {
        return SERIALIZER;
    }

    @Override
    public ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

}
