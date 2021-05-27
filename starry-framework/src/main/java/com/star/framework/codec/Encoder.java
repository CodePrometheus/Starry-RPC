package com.star.framework.codec;

import com.star.common.domain.StarryRequest;
import com.star.common.enums.PackageType;
import com.star.framework.compress.Compress;
import com.star.framework.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Netty方式将msg转换字节并序列化
 *
 * @Author: zzStar
 * @Date: 05-27-2021 15:44
 */
public class Encoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final Serialization serialization;

    private final Compress compress;

    public Encoder(Serialization serialization, Compress compress) {
        this.serialization = serialization;
        this.compress = compress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeInt(MAGIC_NUMBER);
        if (msg instanceof StarryRequest) {
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        } else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }

        out.writeInt(serialization.getCode());
        out.writeInt(compress.getCode());
        byte[] bytes = serialization.serialize(msg);
        bytes = compress.compress(bytes);

        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

}
