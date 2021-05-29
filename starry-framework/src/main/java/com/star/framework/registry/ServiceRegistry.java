package com.star.framework.registry;

import com.star.common.extension.SPI;

import java.net.InetSocketAddress;

/**
 * 服务注册
 *
 * @Author: zzStar
 * @Date: 05-27-2021 14:39
 */
@SPI
public interface ServiceRegistry {

    /**
     * 将服务注册进注册表
     *
     * @param serviceName 服务名称
     * @param address     服务地址
     */
    void register(String serviceName, InetSocketAddress address);

}
