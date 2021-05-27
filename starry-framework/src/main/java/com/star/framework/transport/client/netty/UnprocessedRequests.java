package com.star.framework.transport.client.netty;

import com.star.common.domain.StarryResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 未处理的response
 *
 * @Author: zzStar
 * @Date: 05-27-2021 22:26
 */
public class UnprocessedRequests {

    private static ConcurrentHashMap<String, CompletableFuture<StarryResponse>>
            unprocessedResponseFutures = new ConcurrentHashMap<>();


    public void put(String requestId, CompletableFuture<StarryResponse> future) {
        unprocessedResponseFutures.put(requestId, future);
    }

    public void remove(String requestId) {
        unprocessedResponseFutures.remove(requestId);
    }

    public void complete(StarryResponse response) {
        CompletableFuture<StarryResponse> future = unprocessedResponseFutures.remove(response.getRequestId());
        if (future != null) {
            future.complete(response);
        } else {
            throw new IllegalStateException();
        }
    }

}
