package com.star.framework.annotation;

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
public @interface StarryServiceScan {

    String[] value() default {};

}
