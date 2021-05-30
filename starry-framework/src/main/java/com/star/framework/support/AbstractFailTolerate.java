package com.star.framework.support;

import com.star.common.domain.StarryRequest;
import com.star.common.enums.RpcConfig;
import com.star.common.extension.ExtensionLoader;
import com.star.common.util.PropertiesFileUtil;
import com.star.framework.loadbalancer.LoadBalancer;
import com.star.framework.registry.ServiceDiscovery;
import com.star.framework.transport.client.RpcClient;

import java.util.List;
import java.util.Properties;

/**
 * @Author: zzStar
 * @Date: 05-30-2021 11:49
 */
public abstract class AbstractFailTolerate implements FailTolerate {

    private final ServiceDiscovery serviceDiscovery;

    protected final LoadBalancer loadBalancer;
    protected final RpcClient rpcClient;
    protected int retry;

    protected AbstractFailTolerate() {
        serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("serviceDiscovery");
        loadBalancer = ExtensionLoader.getExtensionLoader(LoadBalancer.class).getExtension("loadBalancer");
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfig.RPC_CONFIG_PATH.getPropertyValue());
        String clientType = properties.getProperty("client.type");
        rpcClient = ExtensionLoader.getExtensionLoader(RpcClient.class).getExtension(clientType);
        String retryStr = properties.getProperty("retry");
        retry = Integer.parseInt(retryStr);
    }

    @Override
    public Object invoke(StarryRequest request) {
        List<String> serviceAddress = serviceDiscovery.lookupServicesList(request.getInterfaceName());
        return doInvoke(serviceAddress, request);
    }

    /**
     * 由子类具体实现
     *
     * @param serviceAddress
     * @param request
     * @return
     */
    protected abstract Object doInvoke(List<String> serviceAddress, StarryRequest request);

}
