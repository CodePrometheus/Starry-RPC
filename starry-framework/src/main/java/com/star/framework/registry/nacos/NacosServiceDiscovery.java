package com.star.framework.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import com.star.common.extension.ExtensionLoader;
import com.star.common.util.NacosUtils;
import com.star.framework.loadbalancer.LoadBalancer;
import com.star.framework.registry.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author: zzStar
 * @Date: 05-28-2021 14:39
 */
public class NacosServiceDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);
    private final LoadBalancer loadBalancer;

    public NacosServiceDiscovery() {
        this.loadBalancer = ExtensionLoader.getExtensionLoader(LoadBalancer.class).getExtension("loadBalancer");
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<String> serviceAddresses = NacosUtils.getAllInstanceString(serviceName);
            if (serviceAddresses.size() == 0) {
                logger.error("Nacos找不到对应的服务: " + serviceName);
                throw new StarryRpcException(RpcError.SERVICE_NOT_FOUND);
            }

            String targetServiceUrl = loadBalancer.select(serviceAddresses, serviceName);
            String[] addressArray = targetServiceUrl.split(":");
            String host = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            return new InetSocketAddress(host, port);
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生: ", e);
        }
        return null;
    }

    @Override
    public List<String> lookupServicesList(String serviceName) {
        try {
            List<String> serviceAddresses = NacosUtils.getAllInstanceString(serviceName);
            if (serviceAddresses.size() == 0) {
                logger.error("Nacos找不到服务: " + serviceName);
                throw new StarryRpcException(RpcError.SERVICE_NOT_FOUND);
            }
            return serviceAddresses;
        } catch (NacosException e) {
            logger.error("Nacos获取服务时有错误发生: ", e);
        }
        return null;
    }

}
