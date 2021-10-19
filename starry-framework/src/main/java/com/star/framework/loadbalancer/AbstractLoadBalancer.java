package com.star.framework.loadbalancer;

import java.util.List;

/**
 * @Author: zzStar
 * @Date: 05-28-2021 14:55
 */
public abstract class AbstractLoadBalancer implements LoadBalancer {

    @Override
    public String select(List<String> serviceAddresses, String serviceName) {
        if (serviceAddresses == null || serviceAddresses.size() == 0) {
            return null;
        }

        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses, serviceName);
    }

    /**
     * 具体由子类实现
     *
     * @param serviceAddress
     * @param serviceName
     * @return
     */
    protected abstract String doSelect(List<String> serviceAddress, String serviceName);
}
