package com.star.framework.annotation;

import java.lang.annotation.*;

/**
 * 使用远程服务端提供的服务类，即用于客户端
 *
 * @Author: zzStar
 * @Date: 05-25-2021 23:14
 */
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StarryReference {

    /**
     * 服务版本号
     */
    String version() default "";

    /**
     * 服务分组
     */
    String group() default "";

}
