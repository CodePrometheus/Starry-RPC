package com.star.framework.provider.impl;

import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import com.star.framework.provider.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: zzStar
 * @Date: 05-28-2021 15:20
 */
public class ServiceProviderImpl implements ServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredServices = ConcurrentHashMap.newKeySet();

    @Override
    public <T> void registerService(T service, String serviceName) {
        if (registeredServices.contains(serviceName)) {
            return;
        }
        registeredServices.add(serviceName);
        serviceMap.put(serviceName, service);
        logger.info("向接口: {} 注册服务: {}", service.getClass().getInterfaces(), serviceName);
    }

    @Override
    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new StarryRpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }

}
