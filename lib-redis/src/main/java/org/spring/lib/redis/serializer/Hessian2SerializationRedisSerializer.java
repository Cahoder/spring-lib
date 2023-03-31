package org.spring.lib.redis.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian2Redis序列化
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/19
 **/
public class Hessian2SerializationRedisSerializer implements RedisSerializer<Object> {

    private static final byte[] EMPTY_ARRAY = new byte[0];

    @Override
    public byte[] serialize(Object obj) throws SerializationException {
        if (obj == null) {
            return EMPTY_ARRAY;
        }
        Hessian2Output out = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream(256);
            out = new Hessian2Output(bos);
            out.setSerializerFactory(Hessian2SerializerFactory.getInstance());
            out.startMessage();
            out.writeObject(obj);
            out.completeMessage();
            out.flush();
            return bos.toByteArray();
        } catch (Exception ex) {
            throw new SerializationException("Cannot serialize", ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw new SerializationException("Cannot close Hessian2Output", e);
            }
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                throw new SerializationException("Cannot close ByteArrayOutputStream", e);
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayInputStream bin = null;
        Hessian2Input in = null;
        try {
            bin = new ByteArrayInputStream(bytes);
            in = new Hessian2Input(bin);
            in.setSerializerFactory(Hessian2SerializerFactory.getInstance());
            in.startMessage();
            Object obj = in.readObject();
            in.completeMessage();
            return obj;
        } catch (Exception ex) {
            throw new SerializationException("Cannot deserialize", ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                throw new SerializationException("Cannot close Hessian2Input", e);
            }
            try {
                if (bin != null) {
                    bin.close();
                }
            } catch (IOException e) {
                throw new SerializationException("Cannot close ByteArrayInputStream", e);
            }
        }
    }

}
