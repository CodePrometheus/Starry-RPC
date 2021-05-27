package com.star.framework.transport.client;

import com.star.common.domain.StarryRequest;
import com.star.common.domain.StarryResponse;
import com.star.common.util.RpcMessageChecker;
import com.star.framework.properties.StarryRpcProperties;
import com.star.framework.transport.client.netty.NettyClient;
import com.star.framework.transport.client.socket.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * RPC客户端动态代理
 *
 * @Author: zzStar
 * @Date: 05-27-2021 16:02
 */
public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final StarryRpcProperties properties;

    private final RpcClient client;

    public RpcClientProxy(RpcClient client, StarryRpcProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        properties.setServiceName(method.getDeclaringClass().getName());
        // serviceName = serviceName + "_" + group + "_" + version
        String serviceName = properties.toString();
        logger.info("调用方法: {}#{}", serviceName, method.getName());

        StarryRequest request = new StarryRequest(UUID.randomUUID().toString(), serviceName,
                method.getName(), args, method.getParameterTypes(), false);

        StarryResponse response = null;
        if (client instanceof NettyClient) {
            try {
                // 异步回调
                CompletableFuture<StarryResponse> completableFuture = (CompletableFuture) client.sendRequest(request);
                response = completableFuture.get();
            } catch (Exception ex) {
                logger.error("方法调用请求发送失败 : ", ex);
                return null;
            }
        }

        if (client instanceof SocketClient) {
            response = (StarryResponse) client.sendRequest(request);
        }

        RpcMessageChecker.check(request, response);
        return response.getData();
    }

}
