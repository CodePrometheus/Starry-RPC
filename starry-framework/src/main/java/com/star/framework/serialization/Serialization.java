package com.star.framework.serialization;

import com.star.framework.serialization.impl.HessianSerialization;
import com.star.framework.serialization.impl.JsonSerialization;
import com.star.framework.serialization.impl.KryoSerialization;
import com.star.framework.serialization.impl.ProtobufSerializer;

/**
 * @Author: zzStar
 * @Date: 05-27-2021 13:37
 */
public interface Serialization {

    Integer KRYO_SERIALIZER = 0;

    Integer JSON_SERIALIZER = 1;

    Integer HESSIAN_SERIALIZER = 2;

    Integer PROTOBUF_SERIALIZER = 3;

    /**
     * 默认Kryo
     */
    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;

    /**
     * switch
     *
     * @param code
     * @return
     */
    static Serialization getByCodecs(int code) {
        switch (code) {
            case 0:
                return new KryoSerialization();
            case 1:
                return new JsonSerialization();
            case 2:
                return new HessianSerialization();
            case 3:
                return new ProtobufSerializer();
            default:
                return null;
        }
    }

    /**
     * 序列化
     *
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes
     * @param clazz
     * @return
     */
    Object deserialize(byte[] bytes, Class<?> clazz);

    /**
     * 选择序列化方式
     *
     * @return
     */
    int getCode();

}
