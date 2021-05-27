package com.star.common.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例工厂实现
 *
 * @Author: zzStar
 * @Date: 05-27-2021 22:34
 */
public class SingletonFactory {

    private static Map<Class, Object> map = new HashMap<>();

    private SingletonFactory() {
    }

    public static <T> T getInstance(Class<T> clazz) {
        Object instance = map.get(clazz);
        synchronized (clazz) {
            if (instance == null) {
                try {
                    instance = clazz.newInstance();
                    map.put(clazz, instance);
                } catch (Exception ex) {
                    throw new RuntimeException(ex.getMessage(), ex);
                }
            }
        }
        // 将对象装换为类或者接口
        return clazz.cast(instance);
    }
}
