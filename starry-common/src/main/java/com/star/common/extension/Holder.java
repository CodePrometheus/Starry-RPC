package com.star.common.extension;

/**
 * 实际将扩展类放入Holder里
 *
 * @Author: zzStar
 * @Date: 05-27-2021 17:19
 */
public class Holder<T> {

    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

}
