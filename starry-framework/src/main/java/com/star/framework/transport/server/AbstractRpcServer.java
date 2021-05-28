package com.star.framework.transport.server;

import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import com.star.common.util.ReflectUtil;
import com.star.framework.annotation.StarryService;
import com.star.framework.annotation.StarryServiceScan;
import com.star.framework.properties.StarryRpcProperties;
import com.star.framework.provider.ServiceProvider;
import com.star.framework.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @Author: zzStar
 * @Date: 05-28-2021 08:51
 */
public abstract class AbstractRpcServer implements RpcServer {

    private Logger logger = LoggerFactory.getLogger(AbstractRpcServer.class);

    protected String host;
    protected int port;

    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    public void scanServices() {
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;

        try {
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(StarryServiceScan.class)) {
                logger.error("启动类缺少 @StarryServiceScan 注解");
                throw new StarryRpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException ex) {
            logger.error("包扫描出现错误");
            throw new StarryRpcException(RpcError.UNKNOWN_ERROR);
        }

        String[] packageNameNum = startClass.getAnnotation(StarryServiceScan.class).value();
        if (packageNameNum.length == 0) {
            String basePackage = mainClassName.substring(0, mainClassName.lastIndexOf("."));
            packageNameNum = new String[]{basePackage};
        }

        Set<Class<?>> classSet = ReflectUtil.getAllClasses(packageNameNum);
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(StarryService.class)) {
                StarryService starryService = clazz.getAnnotation(StarryService.class);
                Object obj;
                try {
                    obj = clazz.newInstance();
                } catch (Exception ex) {
                    logger.error("创建 " + clazz + " 时有错误发生");
                    continue;
                }

                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class<?> oneInterface : interfaces) {
                    StarryRpcProperties properties = StarryRpcProperties.builder()
                            .serviceName(oneInterface.getCanonicalName())
                            .group(starryService.group())
                            .version(starryService.version()).build();
                    String serviceName = properties.toString();
                    publishService(obj, serviceName);
                }
            }
        }
    }


    @Override
    public <T> void publishService(T service, String serviceName) {
        serviceProvider.registerService(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }

}
