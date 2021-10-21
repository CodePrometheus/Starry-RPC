package com.star.framework.codec;

import com.star.common.domain.StarryRequest;
import com.star.common.enums.PackageType;
import com.star.common.util.GsonUtils;
import com.star.framework.compress.Compress;
import com.star.framework.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义Netty传输协议
 * 魔数 + 序列化 + 解压 + msg
 *
 * @Author: zzStar
 * @Date: 05-27-2021 15:44
 */
public class Encoder extends MessageToByteEncoder<Object> {

    private static final Logger logger = LoggerFactory.getLogger(Encoder.class);

    /**
     * 防止任何人随便向服务器的端口上发送数据。服务端在接收到数据时会解析出前几个固定字节的魔数
     * 然后做正确性比对。如果和约定的魔数不匹配，则认为是非法数据
     */
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final Serialization serialization;

    private final Compress compress;

    public Encoder(Serialization serialization, Compress compress) {
        this.serialization = serialization;
        this.compress = compress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        // magic_number
        out.writeInt(MAGIC_NUMBER);

        // request or response
        if (msg instanceof StarryRequest) {
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        } else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }

        // 序列化算法
        out.writeInt(serialization.getCode());
        // 解码方式
        out.writeInt(compress.getCode());

        byte[] bytes = serialization.serialize(msg);
        bytes = compress.compress(bytes);

        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        logger.info("Channel {} encoder message success, message content:{}",
                ctx.channel().id(), GsonUtils.getInstance().toJson(msg));
    }

}
