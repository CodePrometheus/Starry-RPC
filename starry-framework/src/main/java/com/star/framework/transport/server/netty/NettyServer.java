package com.star.framework.transport.server.netty;

import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import com.star.common.extension.ExtensionLoader;
import com.star.framework.codec.Decoder;
import com.star.framework.codec.Encoder;
import com.star.framework.compress.Compress;
import com.star.framework.hook.ShutdownHook;
import com.star.framework.provider.ServiceProvider;
import com.star.framework.registry.ServiceRegistry;
import com.star.framework.serialization.Serialization;
import com.star.framework.transport.server.AbstractRpcServer;
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

import java.util.concurrent.TimeUnit;

/**
 * Netty方式远程方法调用的提供者（服务端）
 *
 * @Author: zzStar
 * @Date: 05-28-2021 12:05
 */
public class NettyServer extends AbstractRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private Serialization serialization;
    private Compress compress;

    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("serviceRegistry");
        this.serviceProvider = ExtensionLoader.getExtensionLoader(ServiceProvider.class).getExtension("serviceProvider");
        this.serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension("serialization");
        this.compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension("compress");
        scanServices();
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
                            pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new Encoder(serialization, compress))
                                    .addLast(new Decoder())
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
