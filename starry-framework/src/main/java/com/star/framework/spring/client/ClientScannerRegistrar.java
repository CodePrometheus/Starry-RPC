package com.star.framework.spring.client;

import com.star.framework.annotation.StarryServiceClientScan;
import com.star.framework.spring.SpringScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * 客户端扫描过滤特定的注解
 *
 * @Author: zzStar
 * @Date: 05-30-2021 15:08
 */
public class ClientScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private static final Logger logger = LoggerFactory.getLogger(ClientScannerRegistrar.class);

    private static final String SPRING_BEAN_BASE_PACKAGE = "com.star.framework.spring.client";
    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";
    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        // 获取StarryServiceClientScan注解的所有属性和值
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(StarryServiceClientScan.class.getName()));
        String[] basePackages = new String[0];
        if (null != attributes) {
            // 获取basePackage属性的值，即指定所扫描的包
            basePackages = attributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }

        if (basePackages.length == 0) {
            // 若未指定，则默认扫描该注解标记类所在的包
            basePackages = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass()
                    .getPackage().getName()};
        }

        // 只扫描Component注解
        SpringScanner componentScan = new SpringScanner(registry, Component.class);

        if (null != resourceLoader) {
            componentScan.setResourceLoader(resourceLoader);
        }

        int componentCount = componentScan.scan(SPRING_BEAN_BASE_PACKAGE)
                + componentScan.scan(basePackages);
        logger.info("componentScan扫描的包数量: [{}]", componentCount);
    }
}
