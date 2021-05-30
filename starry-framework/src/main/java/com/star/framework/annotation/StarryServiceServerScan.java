package com.star.framework.annotation;

import com.star.framework.spring.server.ServerScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 服务端包扫描
 *
 * @Author: zzStar
 * @Date: 05-30-2021 14:21
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Import(ServerScannerRegistrar.class)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface StarryServiceServerScan {

    String[] basePackage() default {};

}
