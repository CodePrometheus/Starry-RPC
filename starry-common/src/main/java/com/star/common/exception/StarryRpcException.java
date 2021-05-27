package com.star.common.exception;

import com.star.common.enums.RpcError;

/**
 * @Author: zzStar
 * @Date: 05-27-2021 15:10
 */
public class StarryRpcException extends RuntimeException {

    public StarryRpcException(RpcError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }

    public StarryRpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public StarryRpcException(RpcError error) {
        super(error.getMessage());
    }

}
