package com.star.framework.transport.server.socket;

import com.star.common.enums.RpcConfig;
import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import com.star.common.extension.ExtensionLoader;
import com.star.common.factory.ThreadPoolFactory;
import com.star.common.util.PropertiesFileUtil;
import com.star.framework.compress.Compress;
import com.star.framework.hook.ShutdownHook;
import com.star.framework.serialization.Serialization;
import com.star.framework.transport.server.RequestHandler;
import com.star.framework.transport.server.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

/**
 * Socket方式远程方法调用的提供者（服务端）
 *
 * @Author: zzStar
 * @Date: 05-28-2021 10:15
 */
@Component
public class SocketServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private final ExecutorService threadPool;
    private Serialization serialization;
    private Compress compress;
    private RequestHandler requestHandler = new RequestHandler();

    private String host;
    private int port;

    public SocketServer() {
        threadPool = ThreadPoolFactory.createDefaultThreadPool("starry-socket");
        this.serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension("serialization");
        this.compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension("compress");
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfig.RPC_CONFIG_PATH.getPropertyValue());
        host = properties.getProperty("server.host");
        port = Integer.parseInt(properties.getProperty("server.port"));
    }

    @Override
    public void start() {
        if (serialization == null) {
            logger.error("未设置序列化器");
            throw new StarryRpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        if (compress == null) {
            logger.error("未设置（解压）压缩方法");
            throw new StarryRpcException(RpcError.COMPRESS_NOT_FOUND);
        }

        ShutdownHook.getShutdownHook().addClearAllHook();
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(host, port));
            logger.info("服务器启动 ; ) ");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接: {} : {}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(
                        socket, requestHandler, serialization, compress
                ));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生:", e);
        }
    }
}
