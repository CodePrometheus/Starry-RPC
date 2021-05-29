package com.star.framework.registry.zookeeper;

import com.star.common.util.CuratorUtils;
import com.star.framework.registry.ServiceRegistry;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * zookeeper注册接口
 *
 * @Author: zzStar
 * @Date: 05-28-2021 13:44
 */
public class zkServiceRegistry implements ServiceRegistry {

    @Override
    public void register(String serviceName, InetSocketAddress address) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, serviceName, address);
    }

}
