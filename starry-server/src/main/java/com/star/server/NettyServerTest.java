package com.star.server;

import com.star.framework.annotation.StarryServiceScan;
import com.star.framework.transport.server.netty.NettyServer;

/**
 * @Author: zzStar
 * @Date: 05-28-2021 15:36
 */
@StarryServiceScan
public class NettyServerTest {

    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer("127.0.0.1", 9000);
        nettyServer.start();
    }

}
