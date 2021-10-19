package com.star.framework.hook;

import com.star.common.extension.ExtensionLoader;
import com.star.common.factory.ThreadPoolFactory;
import com.star.common.util.CuratorUtils;
import com.star.common.util.NacosUtils;
import com.star.common.util.RedisUtils;
import com.star.framework.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: zzStar
 * @Date: 05-28-2021 10:24
 */
public class ShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    private final ServiceRegistry serviceRegistry;
    public ShutdownHook() {
        this.serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("serviceRegistry");
    }


    public void addClearAllHook() {
        logger.info("Server 关闭后将自动注销所有的服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            serviceRegistry.unRegister();
            ThreadPoolFactory.shutDownAll();
        }));
    }

}
