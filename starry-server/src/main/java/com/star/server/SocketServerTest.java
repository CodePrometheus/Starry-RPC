package com.star.server;

import com.star.framework.annotation.StarryServiceScan;
import com.star.framework.transport.server.socket.SocketServer;

/**
 * socket调用
 *
 * @Author: zzStar
 * @Date: 05-29-2021 15:42
 */
@StarryServiceScan("com.star.server.impl")
public class SocketServerTest {

    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer("127.0.0.1", 9000);
        socketServer.start();
    }

}
