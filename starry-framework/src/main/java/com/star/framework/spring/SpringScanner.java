package com.star.framework.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * 自定义包扫描器
 *
 * @Author: zzStar
 * @Date: 05-30-2021 14:26
 */
public class SpringScanner extends ClassPathBeanDefinitionScanner {

    public SpringScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annoType) {
        super(registry);
        // 添加过滤条件，添加了annoType的注解才会被扫描到
        super.addIncludeFilter(new AnnotationTypeFilter(annoType));
    }

    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }

}
