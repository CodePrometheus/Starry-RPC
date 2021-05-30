package com.star.framework.transport.client;

import com.star.common.domain.StarryRequest;
import com.star.common.extension.SPI;

import java.net.InetSocketAddress;

/**
 * 客户端通用接口
 *
 * @Author: zzStar
 * @Date: 05-27-2021 15:59
 */
@SPI
public interface RpcClient {

    /**
     * 客户端发送请求
     *
     * @param address
     * @param request
     * @return
     */
    Object sendRequest(InetSocketAddress address, StarryRequest request);

}
