package com.star.client;

import com.star.api.ByeService;
import com.star.api.Hello;
import com.star.api.HelloService;
import com.star.framework.properties.StarryRpcProperties;
import com.star.framework.transport.client.RpcClientProxy;
import com.star.framework.transport.client.netty.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: zzStar
 * @Date: 05-29-2021 16:01
 */
public class NettyClientTest {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientTest.class);

    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient();
        // HelloServiceImpl2
        StarryRpcProperties properties = StarryRpcProperties.builder()
                .version("version_1.0")
                .group("group_2").build();
        RpcClientProxy proxy = new RpcClientProxy(nettyClient, properties);

        HelloService helloService = proxy.getProxy(HelloService.class);
        Hello test = new Hello(2021, "test");
        String res = helloService.starry(test);
        logger.info("res: {}", res);

        ByeService byeService = proxy.getProxy(ByeService.class);
        logger.info(byeService.sayBye("bye, starry"));
    }
}
