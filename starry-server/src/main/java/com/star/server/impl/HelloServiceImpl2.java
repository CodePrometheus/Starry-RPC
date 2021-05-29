package com.star.server.impl;

import com.star.api.Hello;
import com.star.api.HelloService;
import com.star.framework.annotation.StarryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: zzStar
 * @Date: 05-29-2021 15:37
 */
@StarryService(version = "version_1.0", group = "group_2")
public class HelloServiceImpl2 implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl2.class);

    @Override
    public String starry(Hello hello) {
        logger.info("接收到: {}", hello.getMessage());
        return "调用的返回值，id = " + hello.getId();
    }

}
