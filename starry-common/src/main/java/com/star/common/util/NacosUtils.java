package com.star.common.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.star.common.enums.RpcConfig;
import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: zzStar
 * @Date: 05-28-2021 14:08
 */
public class NacosUtils {

    private static final Logger logger = LoggerFactory.getLogger(NacosUtils.class);

    private static final NamingService namingService;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;

    private static final String SERVER_ADDR;

    static {
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfig.RPC_CONFIG_PATH.getPropertyValue());
        SERVER_ADDR = properties.getProperty("nacos.address");
        namingService = getNacosNamingService();
    }

    public static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            logger.error("连接Nacos时有错误发生: ", e);
            throw new StarryRpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException {
        namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
        NacosUtils.address = address;
        serviceNames.add(serviceName);
    }

    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }


    public static List<String> getAllInstanceString(String serviceName) throws NacosException {
        List<Instance> allInstance = getAllInstance(serviceName);
        return allInstance.stream()
                .map(instance -> instance.getIp() + ":" + instance.getPort())
                .collect(Collectors.toList());
    }

    public static void clearRegistry() {
        if (!serviceNames.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            Iterator<String> iter = serviceNames.iterator();
            while (iter.hasNext()) {
                String serviceName = iter.next();
                try {
                    namingService.deregisterInstance(serviceName, host, port);
                    logger.info("Nacos 注销服务-> {} 成功", serviceName);
                } catch (NacosException e) {
                    logger.error("Nacos 注销服务-> {} 失败", serviceName, e);
                }
            }
        }
    }

}
