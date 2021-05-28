package com.star.framework.loadbalancer.impl;

import com.star.framework.loadbalancer.AbstractLoadBalancer;

import java.util.List;

/**
 * 轮询负载均衡
 *
 * @Author: zzStar
 * @Date: 05-28-2021 15:02
 */
public class PollingLoadBalancer extends AbstractLoadBalancer {

    private int index = 0;

    @Override
    public String doSelect(List<String> serviceAddresses, String serviceName) {
        if (index >= serviceAddresses.size()) {
            index %= serviceAddresses.size();
        }
        return serviceAddresses.get(index++);
    }

}
