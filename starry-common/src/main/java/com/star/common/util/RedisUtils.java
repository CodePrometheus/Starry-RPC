package com.star.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: zzStar
 * @Date: 05-29-2021 19:27
 */
public class RedisUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    private static final Jedis jedis;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;

    private static final String SERVER_ADDR = "127.0.0.1";
    private static final String SERVER_NAME_PREFIX = "starry-rpc:";

    static {
        jedis = getJedisInstance();
    }

    public static Jedis getJedisInstance() {
        try {
            Jedis jedis = new Jedis(SERVER_ADDR);
            jedis.ping();
            return jedis;
        } catch (Exception e) {
            logger.error("连接Redis出现错误: ", e);
            throw new StarryRpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    public static void registerServer(String serviceName, InetSocketAddress address) throws JsonProcessingException {
        RedisInstance redisInstance = RedisInstance.builder()
                .host(address.getHostString())
                .port(address.getPort()).build();
        jedis.hset(SERVER_NAME_PREFIX + serviceName, address.getHostName() + ":" + address.getPort(),
                new ObjectMapper().writeValueAsString(redisInstance));
        serviceNames.add(serviceName);
        RedisUtils.address = address;
    }

    public static List<RedisInstance> getAllInstances(String serviceName) {
        return jedis.hgetAll(SERVER_NAME_PREFIX + serviceName).values()
                .stream().map(json -> {
                    try {
                        return new ObjectMapper().readValue(json, RedisInstance.class);
                    } catch (JsonProcessingException ex) {
                        logger.error("反序列化 RedisInstance 对象失败: ", ex);
                        return null;
                    }
                }).collect(Collectors.toList());
    }

    public static List<String> getAllInstanceString(String serverName) {
        List<RedisInstance> allInstances = getAllInstances(serverName);
        return allInstances.stream().map(instance -> instance.getHost() + ":" + instance.getPort())
                .collect(Collectors.toList());
    }

    public static void clearRegistry() {
        if (!serviceNames.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            for (String serviceName : serviceNames) {
                try {
                    jedis.hdel(SERVER_NAME_PREFIX + serviceName, host + ":" + port);
                } catch (Exception e) {
                    logger.error("Redis 注销服务失败: {} ", serviceName, e);
                }
            }
        }
    }

}
