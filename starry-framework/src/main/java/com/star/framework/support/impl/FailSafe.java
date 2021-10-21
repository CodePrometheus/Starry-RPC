package com.star.framework.support.impl;

import com.star.common.domain.StarryRequest;
import com.star.framework.support.AbstractFailTolerate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 失败安全，出现异常时，直接忽略。
 * 通常用于写入审计日志等操作
 *
 * @Author: zzStar
 * @Date: 05-30-2021 14:09
 */
public class FailSafe extends AbstractFailTolerate {

    private static final Logger logger = LoggerFactory.getLogger(FailSafe.class);

    @Override
    protected Object doInvoke(List<String> serviceAddress, StarryRequest request) {
        try {
            String serviceName = request.getInterfaceName();
            String targetUrl = loadBalancer.select(serviceAddress, serviceName);
            String[] address = targetUrl.split(":");
            String host = address[0];
            int port = Integer.parseInt(address[1]);
            InetSocketAddress socketAddress = new InetSocketAddress(host, port);
            Object res = rpcClient.sendRequest(socketAddress, request);
            return res;
        } catch (Exception e) {
            logger.error("doInvoke 有错误发生: ", e);
            return null;
        }
    }
}
