package com.star.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: zzStar
 * @Date: 05-27-2021 14:50
 */
@Getter
@AllArgsConstructor
public enum PackageType {

    /**
     * request
     */
    REQUEST_PACK(0),

    /**
     * response
     */
    RESPONSE_PACK(1);

    private final int code;

}
