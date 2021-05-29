package com.star.framework.provider;

import com.star.common.extension.SPI;

/**
 * @Author: zzStar
 * @Date: 05-28-2021 08:53
 */
@SPI
public interface ServiceProvider {

    /**
     * 将一个服务注册进注册表
     *
     * @param service
     * @param serviceName
     * @param <T>
     */
    <T> void registerService(T service, String serviceName);

    /**
     * 根据服务名称获取服务实体
     *
     * @param serviceName
     * @return
     */
    Object getService(String serviceName);

}
