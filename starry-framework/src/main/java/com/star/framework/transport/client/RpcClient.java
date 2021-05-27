package com.star.framework.transport.client;

import com.star.common.domain.StarryRequest;

/**
 * 客户端通用接口
 *
 * @Author: zzStar
 * @Date: 05-27-2021 15:59
 */
public interface RpcClient {

    /**
     * 客户端发送请求
     *
     * @param request
     * @return
     */
    Object sendRequest(StarryRequest request);

}
