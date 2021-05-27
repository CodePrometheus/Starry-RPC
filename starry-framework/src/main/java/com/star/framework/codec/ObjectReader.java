package com.star.framework.codec;

import com.star.common.domain.StarryRequest;
import com.star.common.domain.StarryResponse;
import com.star.common.enums.PackageType;
import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import com.star.framework.compress.Compress;
import com.star.framework.serialization.Serialization;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Socket方式从输入流中读取字节并反序列化
 * <p>
 * 调用参数与返回值的传输采用了如下rpc协议以防止粘包：
 * <p>
 * +---------------+---------------+-----------------+-------------+-------------+
 * |  Magic Number |  Package Type | Serializer Type |Compress Type| Data Length |
 * |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |   4 bytes   |
 * +---------------+---------------+-----------------+-------------+-------------+
 * |                                    Data Bytes                               |
 * |                                Length: ${Data Length}                       |
 * +-----------------------------------------------------------------------------+
 * <p>
 * 字段             |    解释
 * Magic Number    |	魔数，表识一个rpc协议包，0xCAFEBABE
 * Package Type	   |    包类型，标明这是一个调用请求还是调用响应
 * Serializer Type |	序列化器类型，标明这个包的数据的序列化方式
 * Compress Type   |    压缩类型
 * Data Length	   |    数据字节的长度
 * Data Bytes	   |    传输的对象，通常是一个RpcRequest或RpcResponse对象，取决于Package Type字段，对象的序列化方式取决于Serializer Type字段。
 *
 * @Author: zzStar
 * @Date: 05-27-2021 15:02
 */
public class ObjectReader {

    private static final Logger logger = LoggerFactory.getLogger(ObjectReader.class);

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    /**
     * 解析
     *
     * @param in
     * @return
     */
    public static Object readObject(InputStream in) throws IOException {
        byte[] bytes = new byte[4];
        in.read(bytes);

        int magic = bytesToInt(bytes);
        // 判断魔术
        if (magic != MAGIC_NUMBER) {
            logger.error("无法识别的协议包: {}", magic);
            throw new StarryRpcException(RpcError.UNKNOWN_PROTOCOL);
        }

        in.read(bytes);
        int packageCode = bytesToInt(bytes);
        Class<?> packageClass;
        // request
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = StarryRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = StarryResponse.class;
        } else {
            logger.error("不识别的数据包: ", packageCode);
            throw new StarryRpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }

        in.read(bytes);
        int serializationCode = bytesToInt(bytes);
        Serialization serialization = Serialization.getByCodecs(serializationCode);
        if (serialization == null) {
            logger.error("不识别的反序列化器: ", serializationCode);
            throw new StarryRpcException(RpcError.UNKNOWN_SERIALIZER);
        }

        in.read(bytes);
        int compressCode = bytesToInt(bytes);
        Compress compress = Compress.getByCode(compressCode);
        if (compress == null) {
            logger.error("不识别的解压类型: ", compressCode);
            throw new StarryRpcException(RpcError.UNKNOWN_COMPRESS);
        }

        in.read(bytes);
        int len = bytesToInt(bytes);
        byte[] data = new byte[len];
        in.read(data);
        data = compress.decompress(data);
        return serialization.deserialize(data, packageClass);
    }


    public static int bytesToInt(byte[] bytes) {
        int value;
        value = (bytes[0] & 0xFF)
                | ((bytes[1] & 0xFF) << 8)
                | ((bytes[2] & 0xFF) << 16)
                | ((bytes[3] & 0xFF) << 24);
        return value;
    }

}
