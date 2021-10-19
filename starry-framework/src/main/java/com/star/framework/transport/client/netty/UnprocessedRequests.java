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
            unprocessedResponseFutureMaps = new ConcurrentHashMap<>();


    /**
     * put
     *
     * @param requestId 请求Id
     * @param future    任务Future
     */
    public void put(String requestId, CompletableFuture<StarryResponse> future) {
        unprocessedResponseFutureMaps.put(requestId, future);
    }

    public void remove(String requestId) {
        unprocessedResponseFutureMaps.remove(requestId);
    }

    public void complete(StarryResponse response) {
        CompletableFuture<StarryResponse> future = unprocessedResponseFutureMaps.remove(response.getRequestId());
        if (future != null) {
            future.complete(response);
        } else {
            throw new IllegalStateException();
        }
    }

}
