package com.star.framework.transport.client.netty;

import com.star.common.domain.StarryRequest;
import com.star.common.domain.StarryResponse;
import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import com.star.common.extension.ExtensionLoader;
import com.star.common.factory.SingletonFactory;
import com.star.framework.compress.Compress;
import com.star.framework.registry.ServiceDiscovery;
import com.star.framework.serialization.Serialization;
import com.star.framework.transport.client.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * NIO方式消费侧客户端类
 *
 * @Author: zzStar
 * @Date: 05-27-2021 22:17
 */
public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    /**
     * 每一个 channel 绑定了一个thread 线程， 一个 thread 线程，封装到一个 EventLoop，多个EventLoop ，组成一个线程组 EventLoopGroup
     */
    private static final EventLoopGroup group;

    /**
     * 完成netty客户端的初始化，设置Nio类型的channel
     */
    private static final Bootstrap bootstrap;

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    private final ServiceDiscovery serviceDiscovery;
    private final Compress compress;
    private final Serialization serialization;

    private final UnprocessedRequests unprocessedRequests;

    public NettyClient() {
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("serviceDiscovery");
        this.serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension("serialization");
        this.compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension("compress");
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }


    @Override
    public Object sendRequest(StarryRequest request) {
        if (serialization == null) {
            logger.error("未设置序列化器");
            throw new StarryRpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        if (compress == null) {
            logger.error("未设置（解压）压缩方法");
            throw new StarryRpcException(RpcError.COMPRESS_NOT_FOUND);
        }

        CompletableFuture<StarryResponse> resultFuture = new CompletableFuture<>();
        try {
            InetSocketAddress address = serviceDiscovery.lookupService(request.getInterfaceName());
            Channel channel = ChannelProvider.get(address, serialization, compress);
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }

            unprocessedRequests.put(request.getRequestId(), resultFuture);
            channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", request.toString()));
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    logger.error("发送消息时有错误发生: ", future.cause());
                }
            });
        } catch (Exception ex) {
            unprocessedRequests.remove(request.getRequestId());
            logger.error(ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }

}
