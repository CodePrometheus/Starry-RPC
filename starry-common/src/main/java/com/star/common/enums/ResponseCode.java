package com.star.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: zzStar
 * @Date: 05-27-2021 14:04
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {

    /**
     * 成功
     */
    SUCCESS(200, "调用方法成功"),

    /**
     * 失败
     */
    FAIL(500, "调用方法失败"),

    /**
     * 未找到指定方法
     */
    METHOD_NOT_FOUND(500, "未找到指定方法"),

    /**
     * 未找到指定类
     */
    CLASS_NOT_FOUND(500, "未找到指定类");

    private final int code;
    private final String message;

}
