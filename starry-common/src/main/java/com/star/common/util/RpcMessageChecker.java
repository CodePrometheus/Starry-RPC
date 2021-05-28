package com.star.common.util;

import com.star.common.domain.StarryRequest;
import com.star.common.domain.StarryResponse;
import com.star.common.enums.ResponseCode;
import com.star.common.enums.RpcError;
import com.star.common.exception.StarryRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检查响应与请求
 *
 * @Author: zzStar
 * @Date: 05-27-2021 16:50
 */
public class RpcMessageChecker {

    public static final String INTERFACE_NAME = "interfaceName";

    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);

    public static void check(StarryRequest request, StarryResponse response) {
        if (response == null) {
            logger.error("调用服务失败,serviceName:{}", request.getInterfaceName());
            throw new StarryRpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + request.getInterfaceName());
        }

        if (!request.getRequestId().equals(response.getRequestId())) {
            throw new StarryRpcException(RpcError.RESPONSE_NOT_MATCH, INTERFACE_NAME + ":" + request.getInterfaceName());
        }

        if (response.getStatusCode() == null || !response.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            logger.error("调用服务失败,serviceName:{},response:{}", request.getInterfaceName(), response);
            throw new StarryRpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + request.getInterfaceName());
        }
    }

}
