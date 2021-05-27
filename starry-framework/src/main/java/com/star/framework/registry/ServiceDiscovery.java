package com.star.framework.registry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author: zzStar
 * @Date: 05-27-2021 14:39
 */
public interface ServiceDiscovery {

    /**
     * 根据服务名查找具体的服务实体
     *
     * @param serviceName
     * @return
     */
    InetSocketAddress lookupService(String serviceName);

    /**
     * 根据服务名称查找服务实体列表
     *
     * @param serviceName
     * @return
     */
    List<String> lookupServicesList(String serviceName);

}
