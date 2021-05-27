package com.star.framework.serialization.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.star.common.domain.StarryRequest;
import com.star.common.domain.StarryResponse;
import com.star.common.exception.SerializeException;
import com.star.framework.serialization.Serialization;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 由于kryo不是线程安全的，故用ThreadLocal保证线程安全
 *
 * @Author: zzStar
 * @Date: 05-27-2021 13:41
 */
public class KryoSerialization implements Serialization {

    private static final Logger logger = LoggerFactory.getLogger(KryoSerialization.class);

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(StarryRequest.class);
        kryo.register(StarryResponse.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception ex) {
            logger.error("序列化时有错误发生: ", ex);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object readObject = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return readObject;
        } catch (Exception ex) {
            logger.error("反序列化时有错误发生: ", ex);
            throw new SerializeException("反序列化时有错误发生");
        }
    }

    @Override
    public int getCode() {
        return KRYO_SERIALIZER;
    }


}
