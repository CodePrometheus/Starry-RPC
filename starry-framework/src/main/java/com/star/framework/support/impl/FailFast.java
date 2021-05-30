package com.star.framework.support.impl;

import com.star.common.domain.StarryRequest;
import com.star.common.exception.StarryRpcException;
import com.star.framework.support.AbstractFailTolerate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 快速失败,只会发起一次调用,失败立即报错。
 * 通常用于非幂等性的写操作，比如：新增记录
 *
 * @Author: zzStar
 * @Date: 05-30-2021 13:09
 */
public class FailFast extends AbstractFailTolerate {

    private static final Logger logger = LoggerFactory.getLogger(FailFast.class);

    @Override
    protected Object doInvoke(List<String> serviceAddress, StarryRequest request) {
        try {
            String serviceName = request.getInterfaceName();
            // 通过负载均衡选择一个服务提供者
            String targetUrl = loadBalancer.select(serviceAddress, serviceName);
            String[] address = targetUrl.split(":");
            String host = address[0];
            int port = Integer.parseInt(address[1]);
            InetSocketAddress socketAddress = new InetSocketAddress(host, port);
            Object res = rpcClient.sendRequest(socketAddress, request);
            return res;
        } catch (Exception e) {
            logger.error("doInvoke 有错误发生: ", e);
            throw new StarryRpcException("服务调用失败: ", e);
        }
    }

}
