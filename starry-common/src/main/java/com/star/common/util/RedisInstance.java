package com.star.common.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zzStar
 * @Date: 05-29-2021 19:41
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisInstance {

    private String host;

    private int port;

    public RedisInstance toInstance() {
        return RedisInstance.builder().host(host)
                .port(port).build();
    }

}
