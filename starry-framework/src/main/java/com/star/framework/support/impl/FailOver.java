package com.star.framework.support.impl;

import com.star.common.domain.StarryRequest;
import com.star.common.exception.StarryRpcException;
import com.star.framework.support.AbstractFailTolerate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 失败自动切换
 * 通常用于读操作，但是重试会带来更长延迟。
 *
 * @Author: zzStar
 * @Date: 05-30-2021 13:58
 */
public class FailOver extends AbstractFailTolerate {

    private static final Logger logger = LoggerFactory.getLogger(FailOver.class);

    @Override
    protected Object doInvoke(List<String> serviceAddress, StarryRequest request) {
        // 未调用的服务地址
        ArrayList<Object> unusedAddresses = new ArrayList<>(serviceAddress);
        int size = unusedAddresses.size();
        retry = Math.min(size, retry);

        // 最后一次调用异常
        StarryRpcException lastEx = null;
        String serviceName = request.getInterfaceName();
        for (int i = 0; i < retry; i++) {
            try {
                logger.info("第 {} 次尝试调用服务: {}", i + 1, serviceName);
                String targetUrl = loadBalancer.select(serviceAddress, serviceName);
                String[] address = targetUrl.split(":");
                String host = address[0];
                int port = Integer.parseInt(address[1]);
                InetSocketAddress socketAddress = new InetSocketAddress(host, port);
                Object res = rpcClient.sendRequest(socketAddress, request);
                return res;
            } catch (Exception ex) {
                logger.error("调用时有错误发生: ", ex);
                lastEx = new StarryRpcException("服务调用失败: ", ex);
            }
        }
        throw lastEx;
    }
}
