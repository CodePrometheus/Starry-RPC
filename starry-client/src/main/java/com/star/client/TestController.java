package com.star.client;

import com.star.api.*;
import com.star.framework.annotation.StarryReference;
import org.springframework.stereotype.Component;

/**
 * @Author: zzStar
 * @Date: 05-30-2021 18:07
 */
@Component
public class TestController {

    @StarryReference(version = "version_1.0", group = "group_2")
    private HelloService helloService;

    @StarryReference(version = "version_1.0", group = "group_2")
    private UserService userService;

    @StarryReference(version = "version_1.0", group = "group_2")
    private ByeService byeService;

    public void test() {
        Hello msg = new Hello(2021, "msg");
        String res = helloService.starry(msg);
        System.out.println(res);

        User user = new User("starry", 21, "男");
        userService.printInfo(user);

        System.out.printf(byeService.sayBye("bye, starry"));
    }
}
