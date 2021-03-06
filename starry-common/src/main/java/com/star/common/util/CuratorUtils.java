package com.star.common.util;

import com.star.common.enums.RpcConfig;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * zookeeper客户端工具类
 *
 * @Author: zzStar
 * @Date: 05-28-2021 10:33
 */
public class CuratorUtils {

    private final static Logger log = LoggerFactory.getLogger(CuratorUtils.class);

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    public static final String ZK_REGISTER_ROOT_PATH = "/starry-rpc";
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";
    private static InetSocketAddress ADDRESS;

    private CuratorUtils() {
    }

    /**
     * 持久性节点
     */
    public static void createPersistentNode(CuratorFramework zkClient,
                                            String serviceName, InetSocketAddress inetSocketAddress) {
        String path = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + serviceName + inetSocketAddress.toString();
        ADDRESS = inetSocketAddress;
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("该节点已经存在: [{}]", path);
            } else {
                //eg: /starry-rpc/com.star.*Service/127.0.0.1:9999
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("节点创建成功 :[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            log.error("创建持久性节点 [{}] 失败", path);
        }
    }

    /**
     * 获取子节点
     *
     * @param zkClient
     * @param serviceName
     * @return
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String serviceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(serviceName)) {
            return SERVICE_ADDRESS_MAP.get(serviceName);
        }
        List<String> result = null;
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + serviceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(serviceName, result);
            registerWatcher(serviceName, zkClient);
        } catch (Exception e) {
            log.error("获取子节点的路径 [{}] 异常", servicePath);
        }
        return result;
    }

    /**
     * Empty the registry of data
     */
    public static void clearRegistry() {
        if (zkClient != null && REGISTERED_PATH_SET != null && ADDRESS != null) {
            REGISTERED_PATH_SET.stream().parallel().forEach(p -> {
                try {
                    if (p.endsWith(ADDRESS.toString())) {
                        zkClient.delete().forPath(p);
                    }
                } catch (Exception e) {
                    log.error("zk 清理节点路径 [{}] 异常", p);
                }
            });
            log.info("zk 所有注册路径已清除: [{}]", REGISTERED_PATH_SET);
        }
    }

    public static CuratorFramework getZkClient() {
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfig.RPC_CONFIG_PATH.getPropertyValue());
        // 集群模式则是多个ip
        String zookeeperAddress = properties != null &&
                properties.getProperty(RpcConfig.ZK_ADDRESS.getPropertyValue()) != null ?
                properties.getProperty(RpcConfig.ZK_ADDRESS.getPropertyValue()) : DEFAULT_ZOOKEEPER_ADDRESS;
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        // 重试
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        return zkClient;
    }


    /**
     * watch机制
     *
     * @param serviceName
     * @param zkClient
     * @throws Exception
     */
    private static void registerWatcher(String serviceName, CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + serviceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(serviceName, serviceAddresses);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

}
