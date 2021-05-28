package com.star.framework.loadbalancer;

import java.util.List;

/**
 * @Author: zzStar
 * @Date: 05-28-2021 13:53
 */
public interface LoadBalancer {

    /**
     * url + serviceName
     *
     * @param serviceAddresses
     * @param serviceName
     * @return
     */
    String select(List<String> serviceAddresses,
                  String serviceName);
}
