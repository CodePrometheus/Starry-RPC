package com.star.framework.spring.client;

import com.star.common.enums.RpcConfig;
import com.star.common.extension.ExtensionLoader;
import com.star.common.util.PropertiesFileUtil;
import com.star.framework.annotation.StarryReference;
import com.star.framework.properties.StarryRpcProperties;
import com.star.framework.support.FailTolerate;
import com.star.framework.transport.client.RpcClientProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * @Author: zzStar
 * @Date: 05-29-2021 22:24
 */
@Component
public class BeanPostProcessorOfClient implements BeanPostProcessor {

    private final FailTolerate failTolerate;

    public BeanPostProcessorOfClient() {
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfig.RPC_CONFIG_PATH.getPropertyValue());
        String failType = properties.getProperty("failTolerate.type");
        this.failTolerate = ExtensionLoader.getExtensionLoader(FailTolerate.class).getExtension(failType);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();

        for (Field declaredField : declaredFields) {
            StarryReference starryReference = declaredField.getAnnotation(StarryReference.class);
            if (null != starryReference) {
                StarryRpcProperties refProperties = StarryRpcProperties.builder()
                        .version(starryReference.version())
                        .group(starryReference.group()).build();

                RpcClientProxy rpcClientProxy = new RpcClientProxy(failTolerate, refProperties);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }


}
