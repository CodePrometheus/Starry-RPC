package com.star.framework.registry.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.star.common.util.RedisUtils;
import com.star.framework.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @Author: zzStar
 * @Date: 05-29-2021 19:25
 */
public class RedisServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(RedisServiceRegistry.class);

    @Override
    public void register(String serviceName, InetSocketAddress address) {
        try {
            RedisUtils.registerServer(serviceName, address);
        } catch (JsonProcessingException e) {
            logger.error("Redis注册服务时有错误发生: ", e);
        }
    }
}
