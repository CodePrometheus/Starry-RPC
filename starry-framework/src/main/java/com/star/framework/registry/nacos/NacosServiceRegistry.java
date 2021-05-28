package com.star.framework.registry.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import com.star.common.util.NacosUtils;
import com.star.framework.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @Author: zzStar
 * @Date: 05-28-2021 14:07
 */
public class NacosServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    @Override
    public void register(String serviceName, InetSocketAddress address) {
        try {
            NacosUtils.registerService(serviceName, address);
        } catch (NacosException e) {
            logger.error("Nacos注册服务时有错误发生: ", e);
            throw new StarryRpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }

}
