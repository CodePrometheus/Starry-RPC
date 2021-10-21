package com.star.framework.spring.server;

import com.star.common.enums.RpcConfig;
import com.star.common.extension.ExtensionLoader;
import com.star.common.util.PropertiesFileUtil;
import com.star.framework.annotation.StarryService;
import com.star.framework.properties.StarryRpcProperties;
import com.star.framework.provider.ServiceProvider;
import com.star.framework.registry.ServiceRegistry;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * @Author: zzStar
 * @Date: 05-30-2021 08:50
 */
@Component
public class BeanPostProcessorOfServer implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(BeanPostProcessorOfServer.class);

    protected String host;
    protected int port;

    private final ServiceProvider serviceProvider;
    private final ServiceRegistry serviceRegistry;

    public BeanPostProcessorOfServer() {
        this.serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class).getExtension("serviceProvider");
        this.serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("serviceRegistry");
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfig.RPC_CONFIG_PATH.getPropertyValue());
        host = properties.getProperty("server.host");
        port = Integer.parseInt(properties.getProperty("server.port"));
    }


    @Override
    @SneakyThrows
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        if (clazz.isAnnotationPresent(StarryService.class)) {
            logger.info("[{}] 已被注解标记 [{}]", clazz.getName(), StarryService.class.getCanonicalName());
            // 获取StarryService注解
            StarryService starryService = bean.getClass().getAnnotation(StarryService.class);
            // 获取所有的接口
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> oneInterface : interfaces) {
                if (!starryService.register()) {
                    continue;
                }
                StarryRpcProperties rpcProperties = StarryRpcProperties.builder()
                        .version(starryService.version())
                        .group(starryService.group())
                        .serviceName(oneInterface.getCanonicalName()).build();
                String serviceName = rpcProperties.toString();
                serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
                serviceProvider.registerService(bean, serviceName);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
