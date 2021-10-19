package com.star.framework.codec;

import com.star.common.domain.StarryRequest;
import com.star.common.domain.StarryResponse;
import com.star.common.enums.PackageType;
import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import com.star.framework.compress.Compress;
import com.star.framework.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Netty方式从输入流中读取字节并反序列化
 *
 * @Author: zzStar
 * @Date: 05-27-2021 15:28
 */
public class Decoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(Decoder.class);

    private static final int MAGIC_NUMBER = 0xCAFEBABE;


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        int magic = in.readInt();
        if (magic != MAGIC_NUMBER) {
            logger.error("不识别的协议包: {}", magic);
            throw new StarryRpcException(RpcError.UNKNOWN_PROTOCOL);
        }

        int packageCode = in.readInt();
        Class<?> packageClass;
        // request or response
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = StarryRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = StarryResponse.class;
        } else {
            logger.error("不识别的数据包: {}", packageCode);
            throw new StarryRpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }

        int serializationCode = in.readInt();
        Serialization serialization = Serialization.getByCodecs(serializationCode);
        if (serialization == null) {
            logger.error("不识别的反序列化器: {}", serializationCode);
            throw new StarryRpcException(RpcError.UNKNOWN_SERIALIZER);
        }

        int compressCode = in.readInt();
        Compress compress = Compress.getByCode(compressCode);
        if (compress == null) {
            logger.error("不识别的解压(压缩)类型: {}", compressCode);
            throw new StarryRpcException(RpcError.UNKNOWN_COMPRESS);
        }

        int len = in.readInt();
        byte[] bytes = new byte[len];
        in.readBytes(bytes);
        bytes = compress.decompress(bytes);
        Object obj = serialization.deserialize(bytes, packageClass);
        list.add(obj);
    }
}
