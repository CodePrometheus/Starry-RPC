package com.star.framework.transport.server.netty;

import com.star.common.domain.StarryRequest;
import com.star.common.domain.StarryResponse;
import com.star.common.factory.SingletonFactory;
import com.star.framework.transport.server.RequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: zzStar
 * @Date: 05-28-2021 13:16
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<StarryRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;

    public NettyServerHandler() {
        requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, StarryRequest msg) throws Exception {
        try {
            if (msg.getHeartBeat()) {
                logger.info("接收到客户端心跳包 ; ) ");
                return;
            }

            logger.info("服务器接收到请求: {}", msg);
            Object res = requestHandler.handle(msg);
            if (ctx.channel().isActive() &&
                    ctx.channel().isWritable()) {
                ctx.writeAndFlush(StarryResponse.success(res, msg.getRequestId()));
            } else {
                logger.error("通道不可写");
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("处理过程调用时有错误发生: {}", cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            // No data was received for a while
            if (state == IdleState.READER_IDLE) {
                logger.info("长时间未收到心跳包，断开连接 ❌");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
