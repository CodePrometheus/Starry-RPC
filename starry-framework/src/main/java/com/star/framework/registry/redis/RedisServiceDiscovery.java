package com.star.framework.registry.redis;

import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import com.star.common.extension.ExtensionLoader;
import com.star.common.util.RedisUtils;
import com.star.framework.loadbalancer.LoadBalancer;
import com.star.framework.registry.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author: zzStar
 * @Date: 05-29-2021 19:25
 */
public class RedisServiceDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(RedisServiceDiscovery.class);

    public final LoadBalancer loadBalancer;

    public RedisServiceDiscovery() {
        this.loadBalancer = ExtensionLoader.getExtensionLoader(LoadBalancer.class).getExtension("loadBalancer");
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        List<String> address = RedisUtils.getAllInstanceString(serviceName);
        if (address.size() == 0) {
            logger.error("Redis找不到对应的服务: {}", serviceName);
            throw new StarryRpcException(RpcError.SERVICE_NOT_FOUND);
        }

        String targetServiceUrl = loadBalancer.select(address, serviceName);
        String[] addressArray = targetServiceUrl.split(":");
        String host = addressArray[0];
        int port = Integer.parseInt(addressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
