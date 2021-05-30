package com.star.framework.registry.zookeeper;

import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import com.star.common.extension.ExtensionLoader;
import com.star.common.util.CuratorUtils;
import com.star.framework.loadbalancer.LoadBalancer;
import com.star.framework.registry.ServiceDiscovery;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author: zzStar
 * @Date: 05-28-2021 13:47
 */
public class zkServiceDiscovery implements ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(zkServiceDiscovery.class);

    private final LoadBalancer loadBalancer;

    public zkServiceDiscovery() {
        this.loadBalancer = ExtensionLoader.getExtensionLoader(LoadBalancer.class).getExtension("loadBalancer");
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, serviceName);
        if (serviceUrlList == null || serviceUrlList.size() == 0) {
            logger.error("找不到对应的服务: " + serviceName);
            throw new StarryRpcException(RpcError.SERVICE_NOT_FOUND);
        }

        // 负载策略
        String targetServiceUrl = loadBalancer.select(serviceUrlList, serviceName);
        logger.info("获取服务地址 : [{}] ", targetServiceUrl + " 成功");
        String[] addressArray = targetServiceUrl.split(":");
        String host = addressArray[0];
        int port = Integer.parseInt(addressArray[1]);
        return new InetSocketAddress(host, port);
    }

    @Override
    public List<String> lookupServicesList(String serviceName) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, serviceName);
        if (serviceUrlList == null || serviceUrlList.size() == 0) {
            logger.error("找不到对应的服务: " + serviceName);
            throw new StarryRpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return serviceUrlList;
    }

}
