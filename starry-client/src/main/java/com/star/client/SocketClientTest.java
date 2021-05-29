package com.star.client;

import com.star.api.*;
import com.star.framework.properties.StarryRpcProperties;
import com.star.framework.transport.client.RpcClientProxy;
import com.star.framework.transport.client.socket.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: zzStar
 * @Date: 05-29-2021 16:01
 */
public class SocketClientTest {

    private static final Logger logger = LoggerFactory.getLogger(SocketClientTest.class);

    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient();
        StarryRpcProperties properties = StarryRpcProperties.builder()
                .version("version_1.0")
                .group("group_1").build();
        RpcClientProxy proxy = new RpcClientProxy(socketClient, properties);

        HelloService helloService = proxy.getProxy(HelloService.class);
        Hello hello = new Hello(2021, "socket");
        String res = helloService.starry(hello);
        logger.info("res: {}", res);

        UserService userService = proxy.getProxy(UserService.class);
        User user = new User("star", 21, "man");
        userService.printInfo(user);

        ByeService byeService = proxy.getProxy(ByeService.class);
        logger.info(byeService.sayBye("bye, star"));
    }

}
