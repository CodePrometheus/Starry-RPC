package com.star.framework.transport.client.netty;

import com.star.common.domain.StarryRequest;
import com.star.common.domain.StarryResponse;
import com.star.common.factory.SingletonFactory;
import com.star.framework.compress.Compress;
import com.star.framework.serialization.Serialization;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @Author: zzStar
 * @Date: 05-27-2021 22:59
 */
public class NettyClientHandle extends SimpleChannelInboundHandler<StarryResponse> {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandle.class);

    private final UnprocessedRequests unprocessedRequests;

    public NettyClientHandle() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, StarryResponse msg) throws Exception {
        try {
            logger.info(String.format("客户端接收到消息: %s", msg));
            unprocessedRequests.complete(msg);
        } finally {
            // 引用计数 释放当前请求消息
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("调用过程有错误发生 : ", cause);
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                logger.info("发送心跳包 => [{}]", ctx.channel().remoteAddress());
            }

            Channel channel = ChannelProvider.get((InetSocketAddress) ctx.channel().remoteAddress(),
                    Serialization.getByCodecs(Serialization.DEFAULT_SERIALIZER),
                    Compress.getByCode(Compress.DEFAULT_COMPRESS));
            StarryRequest request = new StarryRequest();
            request.setHeartBeat(true);
            // 服务端发送消息到客户端失败则将连接关闭
            channel.writeAndFlush(request).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
