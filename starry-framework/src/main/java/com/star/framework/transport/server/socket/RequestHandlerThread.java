package com.star.framework.transport.server.socket;

import com.star.common.domain.StarryRequest;
import com.star.common.domain.StarryResponse;
import com.star.framework.codec.ObjectReader;
import com.star.framework.codec.ObjectWriter;
import com.star.framework.compress.Compress;
import com.star.framework.serialization.Serialization;
import com.star.framework.transport.server.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 处理Request的工作线程
 *
 * @Author: zzStar
 * @Date: 05-28-2021 11:30
 */
public class RequestHandlerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private Serialization serialization;
    private Compress compress;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, Serialization serialization, Compress compress) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serialization = serialization;
        this.compress = compress;
    }

    @Override
    public void run() {
        try (InputStream input = socket.getInputStream();
             OutputStream output = socket.getOutputStream()) {
            StarryRequest starryRequest = (StarryRequest) ObjectReader.readObject(input);
            Object res = requestHandler.handle(starryRequest);
            StarryResponse<Object> response = StarryResponse.success(res, starryRequest.getRequestId());
            ObjectWriter.writeObject(output, response, serialization, compress);
        } catch (IOException e) {
            logger.error("调用或发送时有错误发生: {}", e.getMessage());
        }
    }

}
