package com.star.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: zzStar
 * @Date: 05-28-2021 10:58
 */
@Getter
@AllArgsConstructor
public enum RpcConfig {

    /**
     * path
     */
    RPC_CONFIG_PATH("rpc.properties"),

    ZK_ADDRESS("rpc.zookeeper.address");

    private final String propertyValue;

}
