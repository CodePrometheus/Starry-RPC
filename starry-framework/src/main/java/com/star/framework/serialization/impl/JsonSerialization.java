package com.star.framework.serialization.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.common.domain.StarryRequest;
import com.star.common.exception.SerializeException;
import com.star.framework.serialization.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 使用JSON格式的序列化器
 *
 * @Author: zzStar
 * @Date: 05-27-2021 13:43
 */
public class JsonSerialization implements Serialization {

    private static final Logger logger = LoggerFactory.getLogger(JsonSerialization.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("序列化时有错误发生: ", e);
            throw new SerializeException("序列化时有错误发生");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof StarryRequest) {
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            logger.error("反序列化时有错误发生: ", e);
            throw new SerializeException("反序列化时有错误发生");
        }
    }

    /**
     * 这里由于使用JSON序列化和反序列化Object数组，无法保证反序列化后仍然为原实例类型
     * 需要重新判断处理
     */
    public Object handleRequest(Object obj) throws IOException {
        StarryRequest starryRequest = (StarryRequest) obj;
        for (int i = 0; i < starryRequest.getParamTypes().length; i++) {
            Class<?> paramType = starryRequest.getParamTypes()[i];
            if (!paramType.isAssignableFrom(starryRequest.getParameters()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(starryRequest.getParameters()[i]);
                starryRequest.getParameters()[i] = objectMapper.readValue(bytes, paramType);
            }
        }
        return starryRequest;
    }

    @Override
    public int getCode() {
        return JSON_SERIALIZER;
    }
}
