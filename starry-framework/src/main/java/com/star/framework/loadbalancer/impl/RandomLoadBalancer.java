package com.star.framework.loadbalancer.impl;

import com.star.framework.loadbalancer.AbstractLoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * 随机负载均衡
 *
 * @Author: zzStar
 * @Date: 05-28-2021 15:03
 */
public class RandomLoadBalancer extends AbstractLoadBalancer {

    @Override
    public String doSelect(List<String> serviceAddresses, String serviceName) {
        return serviceAddresses.get(new Random().nextInt(serviceAddresses.size()));
    }

}
