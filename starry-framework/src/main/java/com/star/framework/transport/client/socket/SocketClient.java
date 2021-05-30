package com.star.framework.transport.client.socket;

import com.star.common.domain.StarryRequest;
import com.star.common.domain.StarryResponse;
import com.star.common.enums.ResponseCode;
import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import com.star.common.extension.ExtensionLoader;
import com.star.common.util.RpcMessageChecker;
import com.star.framework.codec.ObjectReader;
import com.star.framework.codec.ObjectWriter;
import com.star.framework.compress.Compress;
import com.star.framework.serialization.Serialization;
import com.star.framework.transport.client.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket方式远程方法调用的消费者（客户端）
 *
 * @Author: zzStar
 * @Date: 05-27-2021 16:55
 */
public class SocketClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final Compress compress;
    private final Serialization serialization;

    public SocketClient() {
        this.serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension("serialization");
        this.compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension("compress");
    }

    @Override
    public Object sendRequest(InetSocketAddress address, StarryRequest request) {
        if (serialization == null) {
            logger.error("未设置序列化器");
            throw new StarryRpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        if (compress == null) {
            logger.error("未设置（解压）压缩方法");
            throw new StarryRpcException(RpcError.COMPRESS_NOT_FOUND);
        }

        try (Socket socket = new Socket()) {
            socket.connect(address);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            ObjectWriter.writeObject(out, request, serialization, compress);
            Object readData = ObjectReader.readObject(in);
            StarryResponse response = (StarryResponse) readData;

            if (response == null) {
                logger.error("服务调用失败，service: {}", request.getInterfaceName());
                throw new StarryRpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service : " + request.getInterfaceName());
            }

            if (response.getStatusCode() == null || response.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
                logger.error("调用服务失败, service: {}, response:{}", request.getInterfaceName(), response);
                throw new StarryRpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + request.getInterfaceName());
            }

            RpcMessageChecker.check(request, response);
            return response;
        } catch (IOException ex) {
            logger.error("调用时有错误发生: ", ex);
            throw new StarryRpcException("服务调用失败: ", ex);
        }
    }

}
