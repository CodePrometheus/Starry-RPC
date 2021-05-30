package com.star.framework.annotation;

import com.star.framework.spring.client.ClientScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 服务扫描的基包
 *
 * @Author: zzStar
 * @Date: 05-28-2021 09:45
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ClientScannerRegistrar.class)
public @interface StarryServiceClientScan {

    String[] basePackage() default {};

}
