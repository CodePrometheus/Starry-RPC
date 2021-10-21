package com.star.framework.transport.server.netty;

import com.star.common.enums.RpcConfig;
import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import com.star.common.extension.ExtensionLoader;
import com.star.common.util.PropertiesFileUtil;
import com.star.framework.codec.Decoder;
import com.star.framework.codec.Encoder;
import com.star.framework.compress.Compress;
import com.star.framework.hook.ShutdownHook;
import com.star.framework.serialization.Serialization;
import com.star.framework.transport.server.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Netty方式远程方法调用的提供者（服务端）
 * NIO方式服务提供侧
 *
 * @Author: zzStar
 * @Date: 05-28-2021 12:05
 */
@Component
public class NettyServer implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private Serialization serialization;
    private Compress compress;

    private String host;
    private int port;


    public NettyServer() {
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
            logger.error("未设置(解压)压缩方法");
            throw new StarryRpcException(RpcError.COMPRESS_NOT_FOUND);
        }

        ShutdownHook.getShutdownHook().addClearAllHook();
        // 负责处理TCP/IP连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 负责处理Channel（通道）的I/O事件
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            // readerIdleTime: 读超时时间 (多长时间没有接受到客户端发送数据)
                            // writerIdleTime: 写超时时间(即多长时间没有向客户端发送数据)
                            // allIdleTime: 所有类型的超时时间
                            pipeline.addLast(new IdleStateHandler(1, 0, 0, TimeUnit.MINUTES))
                                    // 解码和编码
                                    .addLast(new Encoder(serialization, compress))
                                    .addLast(new Decoder())
                                    // 心跳
                                    .addLast(new NettyServerHandler());
                        }
                    });

            // 绑定对应ip和端口,同步等待
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
